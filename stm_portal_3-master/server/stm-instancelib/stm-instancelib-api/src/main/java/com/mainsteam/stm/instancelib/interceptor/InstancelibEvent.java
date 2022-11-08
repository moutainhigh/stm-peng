package com.mainsteam.stm.instancelib.interceptor;

import java.util.EventObject;

import com.mainsteam.stm.instancelib.objenum.EventEnum;

/**
 * 定义事件
 * @author xiaoruqiang
 *
 */
public class InstancelibEvent extends EventObject {

	private static final long serialVersionUID = 6489922885733742339L;
	
	/**
	 * 事件类型
	 */
	private EventEnum eventType;
	/**
	 * 事件发生后的数据
	 */
	private transient Object current;
	/**
	 * 
	 */
	private transient Object params;
	
	/**
	 * 
	 * @param source   事件源数据
	 * @param current  事件发生后的数据
	 * @param type     事件类型
	 */
	public InstancelibEvent(Object source,Object current,EventEnum eventType) {
		super(source);
		this.source = source;
		this.current = current;
		this.eventType = eventType;
	}
	
	/**
	 * 
	 * @param source   事件源数据
	 * @param current  事件发生后的数据
	 * @param type     事件类型
	 */
	public InstancelibEvent(Object source,Object current,Object params,EventEnum eventType) {
		super(source);
		this.source = source;
		this.current = current;
		this.eventType = eventType;
		this.params = params;
	}
	/**
	 * 事件类型
	 */
	public EventEnum getEventType() {
		return eventType;
	}

	/**
	 * 最新数据
	 */
	public Object getCurrent() {
		return current;
	}
	
	/**
	 * 参数
	 */
	public Object getParams() {
		return params;
	}
}
