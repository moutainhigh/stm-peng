package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;


/**
 * <li>文件名称: MainResourceStrategyVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   pengl
 */
public class MainResourceStrategyVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6308787004638519079L;
	
	
	private long resourceId;
	
	private String lifeState;
	
	private String resourceShowName;
	
	private long selectStatus;

	private String resourceIp;
	
	private long strategyID;
	
	private String strategyName;
	
	private long domainId;
	
	public long getSelectStatus() {
		return selectStatus;
	}
	
	public void setSelectStatus(long selectStatus) {
		this.selectStatus = selectStatus;
	}

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceShowName() {
		return resourceShowName;
	}

	public void setResourceShowName(String resourceShowName) {
		this.resourceShowName =resourceShowName;
	}

	public String getResourceIp() {
		return resourceIp;
	}

	public void setResourceIp(String resourceIp) {
		this.resourceIp = resourceIp;
	}

	public long getStrategyID() {
		return strategyID;
	}

	public void setStrategyID(long strategyID) {
		this.strategyID = strategyID;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public String getLifeState() {
		return lifeState;
	}

	public void setLifeState(String lifeState) {
		this.lifeState = lifeState;
	}
	
}
