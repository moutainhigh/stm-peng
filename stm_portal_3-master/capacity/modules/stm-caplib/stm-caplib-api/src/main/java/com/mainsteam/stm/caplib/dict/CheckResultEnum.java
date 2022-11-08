package com.mainsteam.stm.caplib.dict;

public enum CheckResultEnum {
	OK,

	// 目录不存在
	DIRECTORY_NOT_FOUND,

	// 文件不存在
	FILE_NOT_FOUND,

	// XML文件格式错误
	FILE_FORMAT_ERROR,

	// 少了GLOBAL
	NO_GLOBALMETRICSETTING,

	// GLOBAL配置错误
	CONFIG_ERROR_GLOBALMETRICSETTING,

	// 没有模型元素
	NONE_RESOURCE_ELEMENTS,

	// 没有指标元素
	NONE_METRIC_ELEMENTS,

	// 指标元素配置错误
	CONFIG_ERROR_ELEMENTS,

	//数字或布尔格式错误
	CONFIG_ERROR_NUMBER_OR_BOOL,
	
	// 阈值配置错误
	CONFIG_ERROR_THRESHOLD,

	// 实力化配置错误
	CONFIG_ERROR_INSTANT,

	// 不支持的采集频度
	NONE_SUPPORT_PLUGINID,

	// 没有指标对应
	ERROR_METRICID,
	// 没有插件对应
	ERROR_PLUGINID,

	// 不支持的采集频度
	NONE_SUPPORT_FREQ,

	// 配置错误
	CONFIG_ERROR_SYSOID,

	// 有的指标没有配置采集命令
	NO_METRICCOLLECT;
}
