package org.lg.gz.seckill.dto;

import org.lg.gz.seckill.entity.SuccessKilled;
import org.lg.gz.seckill.enums.SeckillStateEnum;

/**
 * 封装秒杀执行后的结果
 * Created by zzq_eason on 2016/9/19.
 */
public class SeckillExecution {

	private long seckillId;
	// 秒杀执行结果状态
	private int state;
	// 秒杀表示
	private String stateInfo;
	// 秒杀成功对象
	private SuccessKilled successKilled;

	// 成功时的初始化
	public SeckillExecution(long seckillId, SeckillStateEnum stateEnum, SuccessKilled successKilled) {
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.successKilled = successKilled;
	}

	// 失败时的初始化
	public SeckillExecution(long seckillId, SeckillStateEnum stateEnum) {
		this.seckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public long getSeckillId() {
		return seckillId;
	}

	public void setSeckillId(long seckillId) {
		this.seckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}

	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}

	@Override
	public String toString() {
		return "SeckillExecution{" +
				"seckillId=" + seckillId +
				", state=" + state +
				", stateInfo='" + stateInfo + '\'' +
				", successKilled=" + successKilled +
				'}';
	}
}
