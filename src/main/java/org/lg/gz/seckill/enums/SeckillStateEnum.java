package org.lg.gz.seckill.enums;

/**
 * 使用枚举表述常量数据字段
 * Created by zzq_eason on 2016/9/19.
 */
public enum SeckillStateEnum {

	SUCCESS(1, "秒杀成功"),
	END(0, "秒杀结束"),
	REPEATE_KILL(-1, "重复秒杀"),
	INNER_ERROR(-2, "系统异常"),
	DATA_REWRITE(-3, "数据篡改")	;

	private int state;

	private String stateInfo;

	SeckillStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public static SeckillStateEnum stateOf(int index) {
		for(SeckillStateEnum state : values()) {
			if(state.getState() == index) {
				return state;
			}
		}
		return null;
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
}
