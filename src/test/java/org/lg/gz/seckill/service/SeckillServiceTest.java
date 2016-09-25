package org.lg.gz.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lg.gz.seckill.dto.Exposer;
import org.lg.gz.seckill.dto.SeckillExecution;
import org.lg.gz.seckill.entity.Seckill;
import org.lg.gz.seckill.exception.RepeatKillException;
import org.lg.gz.seckill.exception.SeckillCloseException;
import org.lg.gz.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by zzq_eason on 2016/9/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(SeckillServiceTest.class);

	@Autowired
	private SeckillService seckillService;

	@Test
	public void testGetSeckillList() throws Exception {
		List<Seckill> list = seckillService.getSeckillList();
		LOGGER.info("list{}", list);
	}

	@Test
	public void testGetById() throws Exception {
		long id = 1000;
		Seckill seckill = seckillService.getById(id);
		LOGGER.info("1000 seckill{}", seckill);
	}

	// 集成测试的完整性，注意可重复执行
	@Test
	public void testSeckillLogic() throws Exception {
		long id = 10001;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()) {
			LOGGER.info("秒杀开启提示信息：exposer={}", exposer);
			long phone = 18306824330L;
			String md5 = exposer.getMd5();
			try {
				SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
				LOGGER.info("秒杀结果：{}", seckillExecution);
			}  catch (SeckillCloseException e) {
				LOGGER.error(e.getMessage());
			} catch (RepeatKillException e) {
				LOGGER.error(e.getMessage());
			}
		} else {
			LOGGER.warn("未开启时提示信息：exposer={}", exposer);
		}

	}

	@Test
	public void testExecuteSeckill() throws Exception {
		long id = 1000;
		long phone = 18306824330L;
		String md5 = "1f199d09d57de8715bc52334d775b6a6";
		try {
			SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
			LOGGER.info("秒杀结果：{}", seckillExecution);
		}  catch (SeckillCloseException e1) {
			LOGGER.error(e1.getMessage());
		} catch (RepeatKillException e2) {
			LOGGER.error(e2.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}

		/** 仔细观察sqlsession实例，属于单例，还要释放事务
		 09:51:00.311 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39]
		 09:51:00.323 [main] DEBUG o.m.s.t.SpringManagedTransaction - JDBC Connection [com.mchange.v2.c3p0.impl.NewProxyConnection@998c68e] will be managed by Spring
		 09:51:00.334 [main] DEBUG o.l.g.s.dao.SeckillDao.reduceNumber - ==>  Preparing: update seckill set number = number - 1 where seckill_id = ? and start_time <= ? and end_time >= ? and number > 0;
		 09:51:00.393 [main] DEBUG o.l.g.s.dao.SeckillDao.reduceNumber - ==> Parameters: 1000(Long), 2016-09-20 09:51:00.29(Timestamp), 2016-09-20 09:51:00.29(Timestamp)
		 09:51:00.640 [main] DEBUG o.l.g.s.dao.SeckillDao.reduceNumber - <==    Updates: 1
		 09:51:00.641 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39]
		 09:51:00.644 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39] from current transaction
		 09:51:00.645 [main] DEBUG o.l.g.s.d.S.insertSuccessKilled - ==>  Preparing: insert ignore into success_killed(seckill_id, user_phone, state) value (?, ?, 0);
		 09:51:00.648 [main] DEBUG o.l.g.s.d.S.insertSuccessKilled - ==> Parameters: 1000(Long), 18306824330(Long)
		 09:51:00.703 [main] DEBUG o.l.g.s.d.S.insertSuccessKilled - <==    Updates: 1
		 09:51:00.726 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39]
		 09:51:00.729 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39] from current transaction
		 09:51:00.734 [main] DEBUG o.l.g.s.d.S.queryByIdWithSeckill - ==>  Preparing: 根据id查询SuccessKilled并携带秒杀实体 * 如果告诉mybatis把结果映射到SuccessKilled同时映射Seckill的属性 * 可以控制sql，这是mybatis的优点  select sk.seckill_id, sk.user_phone, sk.create_time, sk.state, s.seckill_id "seckill.seckill_id", s.name "seckill.name", s.number "seckill.number", s.start_time "seckill.start_time", s.end_time "seckill.end_time", s.create_time "seckill.create_time" from success_killed sk inner join seckill s on sk.seckill_id = s.seckill_id where sk.seckill_id = ? and sk.user_phone = ?;
		 09:51:00.737 [main] DEBUG o.l.g.s.d.S.queryByIdWithSeckill - ==> Parameters: 1000(Long), 18306824330(Long)
		 09:51:01.021 [main] DEBUG o.l.g.s.d.S.queryByIdWithSeckill - <==      Total: 1
		 09:51:01.029 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39]
		 09:51:01.139 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization committing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39]
		 09:51:01.140 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization deregistering SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39]
		 09:51:01.140 [main] DEBUG org.mybatis.spring.SqlSessionUtils - Transaction synchronization closing SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@2a610a39]
		 09:51:01.616 [main] INFO  o.l.g.s.service.SeckillServiceTest - 秒杀结果：SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功', successKilled=SuccessKilled{seckillId=1000, userPhone=18306824330, state=0, createTime=Tue Sep 20 09:51:00 CST 2016}}
		 */
	}
}