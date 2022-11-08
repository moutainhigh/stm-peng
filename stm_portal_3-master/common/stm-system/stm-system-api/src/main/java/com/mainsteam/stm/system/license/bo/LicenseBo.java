package com.mainsteam.stm.system.license.bo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <li>文件名称: LicenseBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   ziwenwen
 */
public class LicenseBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9153426902741471272L;

	public LicenseBo(boolean isOverTime,boolean isTip,Date deadLine,String productName,String copyright,boolean isSimple,boolean ifMatch){
		this.isOverTime=isOverTime;
		this.isTip=isTip;
		this.deadLine=deadLine;
		this.productName=productName;
		this.copyright = copyright;
		this.isSimple=isSimple;
		this.ifMatch = ifMatch;
	}
	
	public LicenseBo(boolean isOverTime,boolean isTip,Date deadLine,String productName,String copyright,boolean isSimple,String version,boolean ifMatch){
		this.isOverTime=isOverTime;
		this.isTip=isTip;
		this.deadLine=deadLine;
		this.productName=productName;
		this.copyright = copyright;
		this.isSimple=isSimple;
		this.version=version;
		this.ifMatch = ifMatch;
	}
	
	/**
	 * 是否已经过期
	 */
	boolean isOverTime;
	
	/**
	 * 是否应该提醒license过期
	 */
	boolean isTip;
	
	/**
	 * license过期时间
	 */
	Date deadLine;
	
	String copyright;
	
	/**
	 * 版本
	 */
	String version;
	
	/**
	 * 是否匹配，false即为老版本
	 */
	boolean ifMatch;
	
	public String getCopyright() {
		return copyright;
	}
	
	public String getVersion() {
		return version;
	}

	/**
	 * license过期时间
	 */
	boolean isSimple;
	
	public boolean isSimple() {
		return isSimple;
	}

	/**
	 * portal模块授权列表
	 */
	List<PortalModule> portalModules;
	
	/**
	 * 监控授权列表
	 */
	MonitorModule monitorModule;
	
	/**
	 * 采集器
	 */
	Collector collector;
	
	/**
	 * 授权数量列表
	 */
	List<LicenseUseInfoBo> licenseUseInfoBo;
	
	public List<LicenseUseInfoBo> getLicenseUseInfoBo() {
		return licenseUseInfoBo;
	}

	public void setLicenseUseInfoBo(List<LicenseUseInfoBo> licenseUseInfoBo) {
		this.licenseUseInfoBo = licenseUseInfoBo;
	}

	public Collector getCollector() {
		return collector;
	}

	public void setCollector(Collector collector) {
		this.collector = collector;
	}
	/**
	 * 产品名称
	 */
	String productName;

	public boolean isOverTime() {
		return isOverTime;
	}

	public boolean isTip() {
		return isTip;
	}

	public Date getDeadLine() {
		return deadLine;
	}

	public List<PortalModule> getPortalModules() {
		return portalModules;
	}

	public void setPortalModules(List<PortalModule> portalModules) {
		this.portalModules = portalModules;
	}

	public MonitorModule getMonitorModule() {
		return monitorModule;
	}

	public void setMonitorModule(MonitorModule monitorModule) {
		this.monitorModule = monitorModule;
	}

	public String getProductName() {
		return productName;
	}

	public boolean isIfMatch() {
		return ifMatch;
	}

	public void setIfMatch(boolean ifMatch) {
		this.ifMatch = ifMatch;
	}
}


