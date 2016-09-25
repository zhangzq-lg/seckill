package org.lg.gz.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by zzq_eason on 2016/9/19.
 */
public class SeckillCloseException extends SeckillException {

	public SeckillCloseException(String message) {
		super(message);
	}

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}
}
