package com.mainsteam.stm.home.workbench.host.vo;

import java.util.List;
import java.util.Map;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.host.vo.BaseInfo.java</li>
 * <li>文件描述: 主机基本信息</li>
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
public class BaseInfo {
	private List<Map<String, Object>> ips;	//IP地址
	private String system;	//操作系统
	private String name;	//主机名
	private String status;	//状态
	public String getSystem() {
		return system;
	}
	public void setSystem(String system) {
		this.system = system;
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
	public List<Map<String, Object>> getIps() {
		return ips;
	}
	public void setIps(List<Map<String, Object>> ips) {
		this.ips = ips;
	}
}
