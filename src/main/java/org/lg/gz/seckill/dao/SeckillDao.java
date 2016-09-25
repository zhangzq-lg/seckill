package org.lg.gz.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.lg.gz.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zzq_eason on 2016/6/29.
 * 当有多个参数时,使用@Param注释告诉mybatis的参数是什么
 */
public interface SeckillDao {

	/**
	 * 减库存操作
	 *
	 * @param seckillId
	 * @param killTime
	 * @return 如果影响行数 > 1，表示更新的记录行数
	 */
	int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

	/**
	 * 根据id查询
	 *
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(@Param("seckillId") long seckillId);

	/**
	 * 根据偏移量查询秒杀商品列表
	 * @param offset 从什么位置开始查询
	 * @param limit  限制查询多少条记录
	 * @return
	 */
	List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

	/**
	 * 使用存储过程执行秒杀
	 * 还需要在配置文件中配置语句
	 * @param paramMap
	 */
	void killByProcedure(Map<String, Object> paramMap);

}
