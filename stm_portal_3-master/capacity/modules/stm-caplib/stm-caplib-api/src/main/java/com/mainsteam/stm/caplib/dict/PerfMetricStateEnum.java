package com.mainsteam.stm.caplib.dict;

/**
 * 
 * <li>文件名称: MetricStateEnum.java</li><br/>
 * <li>文件描述: 指标状态</li><br/>
 * <li>版权所有: 版权所有(C)2014</li><br/>
 * <li>公司: 美新翔盛</li> <br/>
 * <li>内容摘要:指标状态</li> <br/>
 * <li>其他说明:</li><br/>
 * <li>完成日期：2014.8.12</li><br/>
 * <li>修改记录1:新建</li>
 * 
 * @version 3
 * @author sunsht
 */
@Deprecated
public enum PerfMetricStateEnum {

	// 未知
	Indeterminate(-1),
	//正常
	Normal(1),
	// 警告
	Minor(2),
	//严重
	Major(3),
	//致命
	Critical(4);

	private int stateVal;

	PerfMetricStateEnum(int stateVal) {
		this.stateVal = stateVal;
	}

	public int getStateVal() {
		return this.stateVal;
	}
}
