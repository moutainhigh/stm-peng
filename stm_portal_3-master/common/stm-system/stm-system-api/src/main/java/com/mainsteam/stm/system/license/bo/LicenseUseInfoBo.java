package com.mainsteam.stm.system.license.bo;


public class LicenseUseInfoBo {
	// 相应枚举
	private String lmEnum;
	// 相应的中文名称
	private String LicenseNameCN;
	// 授权数
	private int authorCount;
	// 已使用数
	private int usedCount;
	// 剩余数
	private int remainCount;

	public String getLmEnum() {
		return lmEnum;
	}

	public void setLmEnum(String lmEnum) {
		this.lmEnum = lmEnum;
	}

	public String getLicenseNameCN() {
		return LicenseNameCN;
	}

	public void setLicenseNameCN(String licenseNameCN) {
		LicenseNameCN = licenseNameCN;
	}

	public int getAuthorCount() {
		return authorCount;
	}

	public void setAuthorCount(int authorCount) {
		this.authorCount = authorCount;
	}

	public int getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}

	public int getRemainCount() {
		return remainCount;
	}

	public void setRemainCount(int remainCount) {
		this.remainCount = remainCount;
	}

}
