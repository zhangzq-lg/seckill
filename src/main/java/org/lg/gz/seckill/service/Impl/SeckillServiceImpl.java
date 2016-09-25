package org.lg.gz.seckill.service.Impl;

import org.apache.commons.collections.MapUtils;
import org.lg.gz.seckill.dao.SeckillDao;
import org.lg.gz.seckill.dao.SuccessKilledDao;
import org.lg.gz.seckill.dao.cache.RedisDao;
import org.lg.gz.seckill.dto.Exposer;
import org.lg.gz.seckill.dto.SeckillExecution;
import org.lg.gz.seckill.entity.Seckill;
import org.lg.gz.seckill.entity.SuccessKilled;
import org.lg.gz.seckill.enums.SeckillStateEnum;
import org.lg.gz.seckill.exception.RepeatKillException;
import org.lg.gz.seckill.exception.SeckillCloseException;
import org.lg.gz.seckill.exception.SeckillException;
import org.lg.gz.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzq_eason on 2016/9/19.
 */
// @Component:不明确时使用容器, @Service:service层使用， @Dao：Dao层使用, @Controller:控制层
@Service
public class SeckillServiceImpl implements SeckillService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SeckillServiceImpl.class);
	// 注入service依赖
	@Autowired
	private SeckillDao seckillDao;
	@Autowired
	private SuccessKilledDao successKilledDao;
	@Autowired
	private RedisDao redisDao;

	private final String slat = "zzq!@#$%^&*()_+124?";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	public Exposer exportSeckillUrl(long seckillId) {
		/**
		 * 优化策略：添加redis缓存
		 * 超时的基础上维护一致性
		 * 1.先从redis中获取
		 * 2.不存在则从数据库中获取
		 */
		Seckill seckill = redisDao.getSeckill(seckillId);
		if (seckill == null) {
			seckill = seckillDao.queryById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			}
			// 存在则放入到缓存中
			redisDao.putSeckill(seckill);
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		// 系统当前时间
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		String md5 = getMD5(seckillId); // TODO
		return new Exposer(true, md5, seckillId);
	}

	// 获取md5
	private String getMD5(long seckillId) {
		String base = seckillId + "/" + slat;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}


	/**
	 * 使用注解控制方法的优点说明：
	 * 1.开发团队达成一致的约定，明确标注事务的编程风格
	 * 2.保证事务方法的执行时间尽可能的短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务外部
	 * 3.不是所有的方法度需要事务，如只有一条修改操作，只读操作不需要事务控制
	 * 此处先执行insert ---> update 是为了降低网络延迟，另一个是为了获取行级锁
	 */
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException {
		if (md5 == null || !getMD5(seckillId).equals(md5)) {
			throw new SeckillException("seckill data rewrite...");
		}
		// 执行秒杀逻辑：减库存 + 记录购买行为
		Date nowTime = new Date();
		try {
			// 记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			/**
			 * 唯一性，使用联合组件结合ignore来返回结果，如果大于0则成功，否则重复秒杀
			 * insert ignore into success_killed(seckill_id, user_phone, state) value (#{seckillId}, #{userPhone}, 0);
			 */
			if (insertCount <= 0) {
				throw new RepeatKillException("sekill repeated...");
			} else {
				/**
				 * 秒杀成功
				 * 减库存，commit
				 */
				int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if (updateCount <= 0) {
					/**
					 * 没有更新到记录，秒杀结束，则执行rollback
					 */
					throw new SeckillCloseException("seckill is closed exception...");
				} else {
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
				}
			}
		} catch (SeckillCloseException e1) {
			throw e1;
		} catch (RepeatKillException e2) {
			throw e2;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			// 所有编译期异常，转化为运行时异常
			throw new SeckillException("seckilled inner error " + e.getMessage());
		}
	}

	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
		// 返回数据篡改
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		// 执行存储过程，result被复制
		try {
			seckillDao.killByProcedure(map);
			// 获取result
			int result = MapUtils.getIntValue(map, "result", -2);
			if (result == 1) {
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
			} else {
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}

}
