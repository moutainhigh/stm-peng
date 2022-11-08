/**
 * 
 */
package com.mainsteam.stm.errorcode;

/**
 * SERVER内组件的常量类
 * 
 * code的命名规则ERR_SERVER_模块简称_错误简称
 * 
 * 
 * 我们的错误编码范围是从700-799
 * 
 * @author ziw
 * 
 */
public class ServerErrorCodeConstant {

	/**
	 * 操作数据库异常
	 */
	public static final int ERR_SERVER_OPERATE_DATABASE_ERROR = 700;
	
	/**
	 * memcache 加载失败
	 */
	public static final int ERR_SERVER_OPERATE_CACHE_ERROR = 701;
	
	
	/**
	 * 未知异常
	 */
	public static final int ERR_SERVER_UNKOWN_ERROR = 702;
	
	/**
	 * 错误的输入参数
	 */
	public static final int ERR_SERVER_PARAMETER_ERROR = 703;
	
	
	/**
	 * License异常
	 */
	public static final int ERR_SERVER_LICENSE_ERROR=704;
	
	/*
	 * ******* *******************
	 * ******* 资源库模块 ******************* ******* *******************
	 */

	/**
	 * 资源实例重复
	 */
	public static final int ERR_SERVER_RESPO_REPEAT = 710;
	
	/**
	 * 资源库实例拦截器
	 */
	public static final int ERR_SERVER_RESPO_INTERCEPTOR = 711;
	
	/**
	 * 资源库实例属性拦截器
	 */
	public static final int ERR_SERVER_RESPO_PROP_INTERCEPTOR = 712;
	
	/*
	 * ******* *******************
	 * ******* 策略库模块 ******************* ******* *******************
	 */
	/**
	 * 资源已经添加到监控
	 */
	public static final int ERR_SERVER_PROFILE_INTANCE_REPEAT = 730;
	
	
	/**
	 * 资源策略不存在
	 */
	public static final int ERR_SERVER_PROFILE_NOT_FOUND = 734;
	

	/*
	 * ******* *******************
	 * ******* 采集器发现模块 ******************* ******* *******************
	 */
	/**
	 * 发现输入参数不正确
	 */
	public static final int ERROR_DISCOVERY_INVALID_PARAMETER = 731;

	/**
	 * 发现的资源模型在采集器不存在
	 */
	public static final int ERROR_DISCOVERY_RESOURCE_NOT_EXIST = 732;

	/**
	 * 发现的资源模型指标在采集器不存在
	 */
	public static final int ERROR_DISCOVERY_METRIC_NOT_EXIST = 733;

	/**
	 * 发现的资源的名称属性id对应的属性不存在
	 */
	public static final int ERROR_DISCOVERY_NAME_PROPERTY_NOT_EXIST = 721;

	/**
	 * 发现的资源的资源实例化依赖的ID属性id对应的属性不存在
	 */
	public static final int ERROR_DISCOVERY_ID_PROPERTY_NOT_EXIST = 722;

	/**
	 * 发现的资源的资源实例化依赖的ID属性不存在
	 */
	public static final int ERROR_DISCOVERY_ISNTANCE_ID_NOT_EXIST = 723;
	

	/**
	 * 发现的资源的资源实例化时，指标执行参数依赖的属性ID对应的属性定义不存在
	 */
	public static final int ERROR_DISCOVERY_PROPERTY_ID_FOR_PARAMETER_NOT_EXIST = 724;	
	
	
	/**
	 * 发现的资源的资源实例化时，资源实例化依赖的ID属性无值
	 */
	public static final int ERROR_DISCOVERY_PROPERTY_ID_VALUE_EMPTY = 725;	
	
	
	/**
	 * 发现的资源的资源实例化没有主资源实例。发现用的指标取不到值 
	 */
	public static final int ERROR_DISCOVERY_EMPTY_METRIC_VALUE_FOR_INSTANCE = 726;
	
	/**
	 * 发现的网络设备时，systemoid找不到对应的模型id
	 */
	public static final int ERROR_DISCOVERY_NO_RESOURCEID_BY_SYSTEMOID = 727;		
	
	/**
	 * 发现的网络设备时，systemoid取不到值
	 */
	public static final int ERROR_DISCOVERY_EMPTY_SYSTEMOID = 728;	
	
	/**
	 * 发现的资源的资源实例化依赖的属性指标不存在
	 */
	public static final int ERROR_DISCOVERY_ID_PROPERTY_METRIC_NOT_EXIST = 729;
	
	/*
	 * ******* *******************
	 * ******* executor模块 ******************* ******* *******************
	 */
	/**
	 * 处理器的类名为空
	 */
	public static final int ERR_SERVER_EXECUTOR_EMPTY_PROCESSER_CLASS_NAME = 735;

	/**
	 * 资源实例再采集器上查询为null
	 */
	public static final int ERR_SERVER_EXECUTOR_INSTANCE_NOT_EXIST = 736;

	/**
	 * 在采集器上查询资源实例异常
	 */
	public static final int ERR_SERVER_EXECUTOR_INSTANCE_SELECT_EXCEPTION = 737;

	/**
	 * 模型上的指标的采集协议没有定义
	 */
	public static final int ERR_SERVER_EXECUTOR_COLLECTOR_NOT_EXIST = 738;

	/**
	 * 模型上的指标的没有定义
	 */
	public static final int ERR_SERVER_EXECUTOR_METRIC_NOT_EXIST = 739;

	/**
	 * 绑定查询参数失败
	 */
	public static final int ERR_SERVER_EXECUTOR_BIND_PLUGINREQUEST = 740;
	
	/**
	 * 指标执行超时
	 */
	public static final int ERR_SERVER_EXECUTOR_TIMEOUT = 741;	

	/*
	 * ******* *******************
	 * ******* 调度模块 ******************* ******* *******************
	 */

	/*
	 * ******* *******************
	 * ******* PluginServer模块 ******************* ******* ******************
	 */
	/**
	 * plugin session执行异常
	 */
	public static final int ERR_SERVER_PLUGINSERVER_PLUGINSESSION_EXECUTE = 780;
	
	
	/**
	 * plugin server的executor执行异常
	 */
	public static final int ERR_SERVER_PLUGINSERVER_EXECUTE = 781;

	/**
	 * plugin server的processer执行异常
	 */
	public static final int ERR_SERVER_PLUGINSERVER_PROCESSER_EXECUTE = 782;

	/**
	 * plugin server的converter执行异常
	 */
	public static final int ERR_SERVER_PLUGINSERVER_CONVERTER_EXECUTE = 783;

	
	/**
	 * plugin session 初始化失败
	 */
	public static final int ERR_SERVER_PLUGINSERVER_PLUGINSESSION_INIT = 784;
	
	/*
	 * ******* *******************
	 * ******* 模块 ******************* ******* *******************
	 */

	/*
	 * ******* *******************
	 * ******* 路由模块 ******************* ******* *******************
	 */
	
	/**
	 * 未找到对应的（处理器/采集器）节点
	 */
	public static final int ERR_NODE_NOT_FIND = 790;
	
	/**
	 * 当前节点不存在。检查节点注册文件。
	 */
	public static final int ERR_NODE_CURRENT_NOT_EXIST_NODE_TABLES_XML = 791;

	/*
	 * ******* *******************
	 * ******* 数据传输模块 ******************* ******* *******************
	 */

	/*
	 * ******* *******************
	 * ******* RPCServer模块 ******************* ******* *******************
	 */

	/*
	 * ******* *******************
	 * ******* RPCClient模块 ******************* ******* *******************
	 */
	/**
	 * 获取MBean失败
	 */
	public static final int ERR_RCPCLIENT_GETMBEAN_FAIL = 795;
	
	/*
	 * ******* *******************
	 * ******* 指标数据模块 ******************* ******* *******************
	 */

	/*
	 * ******* *******************
	 * ******* 状态模块 ******************* ******* *******************
	 */

	/*
	 * ******* *******************
	 * ******* 事件模块 ******************* ******* *******************
	 */

}
