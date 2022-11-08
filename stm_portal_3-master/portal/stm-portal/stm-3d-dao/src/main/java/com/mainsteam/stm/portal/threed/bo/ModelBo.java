package com.mainsteam.stm.portal.threed.bo;

import java.io.Serializable;

/**
 * 
 * <li>文件名称: ModelBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日
 * @author   liupeng
 */
public class ModelBo implements Serializable{
	private static final long serialVersionUID = 7269736882396339688L;
	/**
	 * 资源设备型号
	 */
	private String type;
	/**
	 * 3D机房定义型号
	 */
	private String model;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	
}
