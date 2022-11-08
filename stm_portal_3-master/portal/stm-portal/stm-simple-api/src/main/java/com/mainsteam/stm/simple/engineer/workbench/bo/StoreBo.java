package com.mainsteam.stm.simple.engineer.workbench.bo;

import java.io.Serializable;

/**
 * <li>文件名称: StoreBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 店铺实体对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class StoreBo implements Serializable{
	
	private static final long serialVersionUID = 415884644663903924L;

	/**
	 * 店铺id
	 */
	private Long id;
	
	/**
	 * 店铺名称
	 */
	private String name;
	
	/**
	 * 店铺级别
	 */
	private int level;
	
	/**
	 * 联系电话
	 */
	private String phone;
	
	/**
	 * 店铺logo图标id
	 */
	private Long logoFileId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getLogoFileId() {
		return logoFileId;
	}

	public void setLogoFileId(Long logoFileId) {
		this.logoFileId = logoFileId;
	}
	
}


