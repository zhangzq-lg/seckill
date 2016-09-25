package org.lg.gz.seckill.dto;


/**
 * VO：封装json数据
 * 主要使用与ajax请求返回数据
 * Created by zzq_eason on 2016/9/20.
 */

public class SeckillResult<T> {

	private boolean success;
	private T data;
	private String error;

	public SeckillResult(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	public SeckillResult(boolean success, String error) {
		this.success = success;
		this.error = error;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "SeckillResult{" +
				"success=" + success +
				", data=" + data +
				", error='" + error + '\'' +
				'}';
	}
}
