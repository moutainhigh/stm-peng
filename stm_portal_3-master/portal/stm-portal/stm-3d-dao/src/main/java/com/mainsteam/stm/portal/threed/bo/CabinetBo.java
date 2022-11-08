package com.mainsteam.stm.portal.threed.bo;

import java.io.Serializable;

/**
 * 
 * <li>文件名称: CabinetBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日
 * @author   liupeng
 */
public class CabinetBo implements Serializable{
	private static final long serialVersionUID = 2418248592382422294L;
	/**
	 * 资源ID
	 */
	private Long id;
	/**
	 * 布局
	 */
	private String layout;
	/**
	 * U位
	 */
	private String uplace;
	/**
	 * 3D型号
	 */
	private String model;
	/**
	 * 所属机柜
	 */
	private String belong;
	/**
	 * 设备名称
	 */
	private String name;
	/**
	 * 型号品牌
	 */
	private String brand;
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public String getUplace() {
		return uplace;
	}
	public void setUplace(String uplace) {
		this.uplace = uplace;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getBelong() {
		return belong;
	}
	public void setBelong(String belong) {
		this.belong = belong;
	}
	
}
