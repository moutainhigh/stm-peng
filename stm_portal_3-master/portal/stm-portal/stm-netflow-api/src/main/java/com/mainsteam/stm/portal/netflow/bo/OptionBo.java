/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.io.Serializable;

/**
 * <li>文件名称: OptionBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月18日
 * @author   lil
 */
public class OptionBo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String ids;
	private long routerIp;
	
	/**
	 * @return the routerIp
	 */
	public long getRouterIp() {
		return routerIp;
	}

	/**
	 * @param routerIp the routerIp to set
	 */
	public void setRouterIp(long routerIp) {
		this.routerIp = routerIp;
	}

	/**
	 * @return the ids
	 */
	public String getIds() {
		return ids;
	}

	/**
	 * @param ids the ids to set
	 */
	public void setIds(String ids) {
		this.ids = ids;
	}
	
}
