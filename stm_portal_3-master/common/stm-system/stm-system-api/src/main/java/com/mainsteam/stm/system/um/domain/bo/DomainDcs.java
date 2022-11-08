package com.mainsteam.stm.system.um.domain.bo;

import java.io.Serializable;

/**
 * <li>文件名称: DomainDcs.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月20日
 * @author   ziwenwen
 */
public class DomainDcs implements Serializable {
	
	private static final long serialVersionUID = 5594470156962106244L;
	
	private Long domainId;
	private int dcsId;
	private String dcsName;
	private String dcsIp;
	private int dcsState;
	private String dhs;
	private int isChecked;
	
	
	
	
	public Long getDomainId() {
		return domainId;
	}
	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	public int getDcsId() {
		return dcsId;
	}
	public void setDcsId(int dcsId) {
		this.dcsId = dcsId;
	}
	public int getIsChecked() {
		return isChecked;
	}
	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}
	public String getDcsName() {
		return dcsName;
	}
	public void setDcsName(String dcsName) {
		this.dcsName = dcsName;
	}
	public String getDcsIp() {
		return dcsIp;
	}
	public void setDcsIp(String dcsIp) {
		this.dcsIp = dcsIp;
	}
	public int getDcsState() {
		return dcsState;
	}
	public void setDcsState(int dcsState) {
		this.dcsState = dcsState;
	}
	public String getDhs() {
		return dhs;
	}
	public void setDhs(String dhs) {
		this.dhs = dhs;
	}
	
}


