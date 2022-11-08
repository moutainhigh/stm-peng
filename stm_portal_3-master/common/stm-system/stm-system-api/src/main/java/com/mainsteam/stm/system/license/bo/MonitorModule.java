package com.mainsteam.stm.system.license.bo;

import java.io.Serializable;

/**
 * <li>文件名称: MonitorModule.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   ziwenwen
 */
public class MonitorModule implements Serializable{
	
	private static final long serialVersionUID = -1299423686272612553L;


	public MonitorModule(int authorCount, int remainCount, int usedCount) {
		super();
		this.authorCount = authorCount;
		this.remainCount = remainCount;
		this.usedCount = usedCount;
	}
	
	/**
	 * 授权数量
	 */
	int authorCount;
	
	/**
	 * 资源管理剩余数量
	 */
	int remainCount;
	
	int usedCount;
	

	public int getAuthorCount() {
		return authorCount;
	}

	public int getRemainCount() {
		return remainCount;
	}
	
	public int getUsedCount() {
		return usedCount;
	}
}


