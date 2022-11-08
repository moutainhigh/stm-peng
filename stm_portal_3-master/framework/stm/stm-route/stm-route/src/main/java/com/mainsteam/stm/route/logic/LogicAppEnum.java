/**
 * 
 */
package com.mainsteam.stm.route.logic;

/**
 * 
 * 不同类类型，可以适用于不同的业务
 * 
 * 逻辑连接的上层应用使用类型
 * 
 * 1.RPC_JMX_NODE_GROUP: jmx远程调用节点组 <br>
 * 使用场景： 策略分发，资源发现，指标即时取值，拓扑发现等等需要业务计算的。
 * 
 * 2. RPC_JMX_NODE: jmx远程调用节点 使用场景：分布式节点升级，部署，节点jvm运行状态监测。
 * 
 * 2. TRANSFER_TCP:使用TCP数据上传,TRANSFER_UDP:使用UDP数据上传 指标数据采集完成，上传到采集器
 * 
 * 3. PING_NODE:节点状态检测 单纯的探测端口是否可用
 * 
 * 
 * 
 * @author ziw
 * 
 */
public enum LogicAppEnum {
	RPC_JMX_NODE_GROUP((byte) 0, true), RPC_JMX_NODE((byte) 1, false), TRANSFER_TCP(
			(byte) 2, true), TRANSFER_UDP((byte) 3, true), PING_NODE((byte) 4,
			false),
	/** 文件传输 */
	FILE_TRANSFER_TCP((byte) 5, true), HTTP_EMBEDDED((byte) 10, false);
	private byte code;
	/**
	 * 是否使用于节点组
	 */
	private boolean groupable;

	private LogicAppEnum(byte code, boolean groupable) {
		this.code = code;
		this.groupable = groupable;
	}

	/**
	 * @return the code
	 */
	public final byte getCode() {
		return code;
	}

	public static LogicAppEnum valueOf(byte code) {
		LogicAppEnum[] values = LogicAppEnum.values();
		if (values.length > code) {
			return values[code];
		}
		return null;
	}

	public boolean isGroupable() {
		return groupable;
	}
}
