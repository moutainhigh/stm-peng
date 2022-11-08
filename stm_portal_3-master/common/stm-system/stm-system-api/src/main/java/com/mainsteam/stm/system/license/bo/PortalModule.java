package com.mainsteam.stm.system.license.bo;

import java.io.Serializable;

/**
 * <li>文件名称: PortalModule.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   ziwenwen
 */
public class PortalModule implements Serializable{
	
	private static final long serialVersionUID = -1893491634097012394L;

	/**
	 * 是否已经授权
	 */
	boolean isAuthor;
	
	/**
	 * 模块名称
	 */
	String name;
	
	/**
	 * 模块id
	 */
	long id;

	public long getId() {
		return id;
	}

	public PortalModule(Long id,String name,boolean isAuthor){
		this.id=id;
		this.name = name;
		this.isAuthor = isAuthor;
	}
	
	public boolean isAuthor() {
		return isAuthor;
	}


	public String getName() {
		return name;
	}
}


