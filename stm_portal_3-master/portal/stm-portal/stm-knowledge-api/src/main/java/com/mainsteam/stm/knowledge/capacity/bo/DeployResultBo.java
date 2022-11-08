package com.mainsteam.stm.knowledge.capacity.bo;

import java.io.Serializable;

/**
 * <li>文件名称: DeployResultBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 部署结果</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月2日
 * @author   ziwenwen
 */
public class DeployResultBo implements Serializable{
	private static final long serialVersionUID = -5018152133286321176L;

	/**
	 * 组件编号、id
	 */
	private int id;
	
	/**
	 * 组件名称
	 */
	private String name;
	
	/**
	 * 安装状态 false-失败 true-成功
	 */
	private boolean status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
}


