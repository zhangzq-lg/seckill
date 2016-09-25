package org.lg.gz.seckill.exception;

/**
 * 重复秒杀异常(运行时异常)
 * Created by zzq_eason on 2016/9/19.
 */
public class RepeatKillException extends SeckillException {

	public RepeatKillException(String message) {
		super(message);
	}

	public RepeatKillException(String message, Throwable cause) {
		super(message, cause);
	}
}
