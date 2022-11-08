package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;


/**
 * <li>文件名称: ChildResourceStrategyVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   pengl
 */
public class ChildResourceStrategyVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5732870808498751763L;

	private long resourceId;
	
	private String interfaceState;
	
	private String resourceName;
	
	private String strategyName;
	
	private long strategyID;

	public long getResourceId() {
		return resourceId;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getInterfaceState() {
		return interfaceState;
	}

	public void setInterfaceState(String interfaceState) {
		this.interfaceState = interfaceState;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public long getStrategyID() {
		return strategyID;
	}

	public void setStrategyID(long strategyID) {
		this.strategyID = strategyID;
	}
	
	
	
}
