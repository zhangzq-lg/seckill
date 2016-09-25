package org.lg.gz.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lg.gz.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by zzq_eason on 2016/7/3.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

	@Resource
	private SuccessKilledDao successKilledDao;

	@Test
	public void testInsertSuccessKilled() throws Exception {
		long id = 1000l;
		long phone = 18306824330L;
		int insertCount = successKilledDao.insertSuccessKilled(id, phone);
		System.out.println("秒杀成功：" + insertCount);
	}

	@Test
	public void testQueryByIdWithSeckill() throws Exception {
		long id = 1000l;
		long phone = 18306824330L;

		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
		System.out.println("秒杀成功的记录有：" + successKilled);
	}
}