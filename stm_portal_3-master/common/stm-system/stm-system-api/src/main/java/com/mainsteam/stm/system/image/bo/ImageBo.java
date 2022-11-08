package com.mainsteam.stm.system.image.bo;

import java.io.Serializable;

/**
 * <li>文件名称: ImageBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月5日
 * @author   ziwenwen
 */
public class ImageBo implements Serializable{
	private static final long serialVersionUID = 8431205347978435549L;
	private String systemDefaultLogo;
	private String systemCurrentLogo;
	private String loginDefaultLogo;
	private String loginCurrentLogo;
	private String defaultCopyRight;
	private String currentCopyRight;
	private String logoPsdName;
	
	public String getSystemDefaultLogo() {
		return systemDefaultLogo;
	}
	public void setSystemDefaultLogo(String systemDefaultLogo) {
		this.systemDefaultLogo = systemDefaultLogo;
	}
	public String getSystemCurrentLogo() {
		return systemCurrentLogo;
	}
	public void setSystemCurrentLogo(String systemCurrentLogo) {
		this.systemCurrentLogo = systemCurrentLogo;
	}
	public String getLoginDefaultLogo() {
		return loginDefaultLogo;
	}
	public void setLoginDefaultLogo(String loginDefaultLogo) {
		this.loginDefaultLogo = loginDefaultLogo;
	}
	public String getLoginCurrentLogo() {
		return loginCurrentLogo;
	}
	public void setLoginCurrentLogo(String loginCurrentLogo) {
		this.loginCurrentLogo = loginCurrentLogo;
	}
	public String getDefaultCopyRight() {
		return defaultCopyRight;
	}
	public void setDefaultCopyRight(String defaultCopyRight) {
		this.defaultCopyRight = defaultCopyRight;
	}
	public String getCurrentCopyRight() {
		return currentCopyRight;
	}
	public void setCurrentCopyRight(String currentCopyRight) {
		this.currentCopyRight = currentCopyRight;
	}
	public String getLogoPsdName() {
		return logoPsdName;
	}
	public void setLogoPsdName(String logoPsdName) {
		this.logoPsdName = logoPsdName;
	}
}


