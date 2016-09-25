package org.lg.gz.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.lg.gz.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by zzq_eason on 2016/9/24.
 */
public class RedisDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisDao.class);

	private final JedisPool jedisPool;

	public RedisDao(String host, int port) {
		jedisPool = new JedisPool(host, port);
	}

	// 通过使用protostuff来获取对象序列化的scheme
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	// 获取缓存中的秒杀商品（即为一个反序列化过程）
	public Seckill getSeckill(long seckillId) {
		// redis操作逻辑
		Jedis jedis = jedisPool.getResource();
		try {
			String key = "seckill:" + seckillId;
			/**
			 * redis没有实现对象序列化，自己实现通过添加protostuff的jar依赖
			 * get--->byte[] -> 反序列化 -> Object(Seckill)
			 */
			byte[] bytes = jedis.get(key.getBytes());
			// 缓存中获取到
			if (bytes != null) {
				// 创建空对象
				Seckill seckill = schema.newMessage();
				// 使用工具类反序列化对象
				ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
				LOGGER.info("RedisDao反序列化成功时对象内容:{}", seckill);
				return seckill;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

	// 放入秒杀商品到缓存（即为序列化过程）
	public String putSeckill(Seckill seckill) {

		Jedis jedis = jedisPool.getResource();
		try {
			/**
			 * 先生成一个key，此过程是将Object -> 序列化 -> byte[]
			 */
			String key = "seckill:" + seckill.getSeckillId();
			byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
					LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
			// 超时缓存
			int timeout = 60 * 60; // 一小时
			String result = jedis.setex(key.getBytes(), timeout, bytes);
			LOGGER.info("RedisDao放入缓存时的返回结果:{}", result);
			return result;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
		return null;
	}

}
