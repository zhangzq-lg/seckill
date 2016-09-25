package org.lg.gz.seckill.exception;

/**
 * 秒杀相关业务异常
 * Created by zzq_eason on 2016/9/19.
 */
public class SeckillException extends RuntimeException {

	public SeckillException(String message) {
		super(message);
	}

	public SeckillException(String message, Throwable cause) {
		super(message, cause);
	}
}
