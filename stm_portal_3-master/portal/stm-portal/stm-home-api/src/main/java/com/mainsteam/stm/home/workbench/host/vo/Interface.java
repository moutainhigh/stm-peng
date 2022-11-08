package com.mainsteam.stm.home.workbench.host.vo;


/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.host.vo.HostInterface.java</li>
 * <li>文件描述: 主机接口信息</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月18日
 */
public class Interface {
	private String type;	//连接类型
	private String name;	//接口名称
	private String status;	//状态
	private String statusDescription;	//状态描述
	private String id;
	private Boolean isCheck=false;
//	private List<InterfaceIndicators> indicators;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
//	public List<InterfaceIndicators> getIndicators() {
//		return indicators;
//	}
//	public void setIndicators(List<InterfaceIndicators> indicators) {
//		this.indicators = indicators;
//	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getIsCheck() {
		return isCheck;
	}
	public void setIsCheck(Boolean isCheck) {
		this.isCheck = isCheck;
	}
	
}
