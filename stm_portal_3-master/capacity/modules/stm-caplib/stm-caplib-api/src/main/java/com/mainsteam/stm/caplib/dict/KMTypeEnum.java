package com.mainsteam.stm.caplib.dict;

/**
 * 
 * 文件名称: KMTypeEnum
 * 
 * 文件描述: 采集接口
 * 
 * 版权所有: 版权所有(C)2005-2006
 * 
 * 公司: 美新翔盛
 * 
 * 内容摘要:故障知识和监控模型知识公用的枚举
 * 
 * 其他说明:
 * 
 * 完成日期：2019年11月7日
 * 
 * 修改记录1: 创建
 * 
 * @version 3
 * @author sunsht
 * 
 */
public enum KMTypeEnum {
	Category, // 模型分类（模型的分类，例如：主机-->Linux 网络设备--->交换机）

	MainResource, // 模型（例如LinuxSNMP)

	SubResource, // 子模型(例如CPU)

	Metric; // 指标(例如分区利用率)
}
