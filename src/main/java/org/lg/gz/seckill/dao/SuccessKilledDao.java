package org.lg.gz.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.lg.gz.seckill.entity.SuccessKilled;
import org.springframework.stereotype.Repository;

/**
 * Created by zzq_eason on 2016/6/29.
 */
public interface SuccessKilledDao {

	/**
	 * 插入购买明细，可过滤重复（数据库的联合主键即可完成此功能）
	 * @param seckillId
	 * @param userPhone
	 * @return 插入的结果集数量，即成功插入的条数
	 */
	int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

	/**
	 * 根据id查询SuccessKilled并携带秒杀产品对象实体
	 * @param seckillId
	 * @return
	 */
	SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

}
