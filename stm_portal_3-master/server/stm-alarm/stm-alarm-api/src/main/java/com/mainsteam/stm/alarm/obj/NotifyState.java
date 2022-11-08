package com.mainsteam.stm.alarm.obj;

public enum NotifyState {
	/** 不发送*/
	NOT_SEND,
	/** 等待发送*/
	WAITE_SEND,
	
	/**发送中*/
	SENDING,
	SUCCESS,
	NOT_SUCCESS;
}
