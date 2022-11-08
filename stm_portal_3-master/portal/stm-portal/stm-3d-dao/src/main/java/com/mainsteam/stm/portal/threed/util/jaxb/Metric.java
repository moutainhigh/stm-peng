package com.mainsteam.stm.portal.threed.util.jaxb;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * 
 * <li>文件名称: Metric.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 要推送的指标</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   liupeng
 */
@XmlRootElement(name="Metric")
@XmlAccessorType(XmlAccessType.FIELD)
public class Metric implements Serializable{
	private static final long serialVersionUID = 9054428608494676236L;
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String unit;
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
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
}
