package org.lg.gz.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lg.gz.seckill.dao.SeckillDao;
import org.lg.gz.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by zzq_eason on 2016/9/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {

	private long id = 1001;

	@Autowired
	private RedisDao redisDao;

	@Autowired
	private SeckillDao seckillDao;

	@Test
	public void testSeckill() throws Exception {
		// get and put
		Seckill seckill = redisDao.getSeckill(id);
		System.out.println(seckill);
		if (seckill == null) {
			seckill = seckillDao.queryById(id);
			if(seckill != null) {
				String result = redisDao.putSeckill(seckill);
			}
		}
	}

}