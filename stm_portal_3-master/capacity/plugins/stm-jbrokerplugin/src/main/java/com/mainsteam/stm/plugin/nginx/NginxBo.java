package com.mainsteam.stm.plugin.nginx;

public class NginxBo {

	/**
	 * Nginx访问页面
	 */
	private String pageName;

	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
		System.out.println(pageName);
	}
}
