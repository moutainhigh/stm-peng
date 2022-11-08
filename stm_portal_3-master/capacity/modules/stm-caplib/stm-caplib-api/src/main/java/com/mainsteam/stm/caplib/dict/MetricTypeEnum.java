package com.mainsteam.stm.caplib.dict;

/**
 * 
 * <li>文件名称: MetricTypeEnum.java</li><br/>
 * <li>文件描述: 指标类型枚举</li><br/>
 * <li>版权所有: 版权所有(C)2014</li><br/>
 * <li>公司: 美新翔盛</li><br/>
 * <li>内容摘要: 指标类型枚举定义</li> <br/>
 * <li>其他说明:</li><br/>
 * <li>完成日期：2014.8.12</li><br/>
 * <li>修改记录1:新建</li>
 * 
 * @version 3
 * @author sunsht
 */
public enum MetricTypeEnum {
	// 可用性指标
	AvailabilityMetric(1),
	// 性能指标
	PerformanceMetric(2),
	// 信息指标
	InformationMetric(3);
	// ,
	// 配置指标
	// ConfigurationMetric(4)
	// ;
	private String chVal;
	private int stateVal;

	MetricTypeEnum(int stateVal) {
		this.stateVal = stateVal;
		switch (stateVal) {
		case 1:
			chVal = "可用性指标";
			break;
		case 2:
			chVal = "性能指标";
			break;
		case 3:
			chVal = "信息指标";
			break;
		}
	}

	public int getInnerVal() {
		return this.stateVal;
	}

	public String getChVal() {
		return this.chVal;
	}
}
