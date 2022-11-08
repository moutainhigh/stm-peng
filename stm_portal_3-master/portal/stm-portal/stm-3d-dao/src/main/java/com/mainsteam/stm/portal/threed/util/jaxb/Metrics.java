package com.mainsteam.stm.portal.threed.util.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * <li>文件名称: Metrics.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   liupeng
 */
@XmlRootElement(name="Metrics")
@XmlAccessorType(XmlAccessType.FIELD)
public class Metrics implements Serializable{
	
	private static final long serialVersionUID = 5274468227814126002L;
	@XmlElement(name="Cron")
	private String cron;
	@XmlElement(name="Metric")
	private List<Metric> metricList = new ArrayList<Metric>();
	public List<Metric> getMetricList() {
		return metricList;
	}
	public void setMetricList(List<Metric> metricList) {
		this.metricList = metricList;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}

}
