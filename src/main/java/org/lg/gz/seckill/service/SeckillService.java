package org.lg.gz.seckill.service;

import org.lg.gz.seckill.dto.Exposer;
import org.lg.gz.seckill.dto.SeckillExecution;
import org.lg.gz.seckill.entity.Seckill;
import org.lg.gz.seckill.exception.RepeatKillException;
import org.lg.gz.seckill.exception.SeckillCloseException;
import org.lg.gz.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口：站在“使用者”的角度设计接口
 * 怎么设计：1.方法定义粒度，2.参数， 3.返回类型（return 类型/异常）
 * <p/>
 * Created by zzq_eason on 2016/9/19.
 */
public interface SeckillService {

	/**
	 * 查询全部秒杀
	 *
	 * @return
	 */
	public List<Seckill> getSeckillList();

	/**
	 * 查询单个秒杀
	 *
	 * @param seckillId
	 * @return
	 */
	public Seckill getById(long seckillId);

	/**
	 * 秒杀开启则输出秒杀接口地址（dto）
	 * 否则输出当前系统时间和秒杀时间
	 *
	 * @param seckillId
	 * @return
	 */
	public Exposer exportSeckillUrl(long seckillId);

	/**
	 * 执行秒杀操作
	 *
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws RepeatKillException, SeckillCloseException, SeckillException;

	/**
	 * 执行秒杀操作 by 存储过程
	 *
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 */
	public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);


}
