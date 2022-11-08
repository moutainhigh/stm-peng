package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <li>文件名称: BusinessData</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 业务数据</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月20日 下午3:56:15
 * @author   俊峰
 */
public class BusinessData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2483872890475184337L;
	/**
	 * 业务ID
	 */
	private String id;
	/**
	 * 业务名称
	 */
	private String name;
	
	/**
	 * 业务指标数据集合（key:指标名称，value:指标值）
	 */
	private List<MetricData> metricDatas;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MetricData> getMetricDatas() {
		return metricDatas;
	}

	public void setMetricDatas(List<MetricData> metricDatas) {
		this.metricDatas = metricDatas;
	}

	
	
}
