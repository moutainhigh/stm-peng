package com.mainsteam.stm.camera.api.obj;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;

public class CameraInstance extends ResourceInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int platformId;
	private String groupName;
	private String cameraName;
	private String platFormName;
	private String channelId;
	private String address;
	private String gisx;
	private String gisy;
	private String cameraIP;
	private int cameraPort;
	private String loginuser;
	private String loginpwd;
	
	public int getPlatformId() {
		return platformId;
	}
	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	public String getPlatFormName() {
		return platFormName;
	}
	public void setPlatFormName(String platFormName) {
		this.platFormName = platFormName;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getGisx() {
		return gisx;
	}
	public void setGisx(String gisx) {
		this.gisx = gisx;
	}
	public String getGisy() {
		return gisy;
	}
	public void setGisy(String gisy) {
		this.gisy = gisy;
	}
	public String getCameraIP() {
		return cameraIP;
	}
	public void setCameraIP(String cameraIP) {
		this.cameraIP = cameraIP;
	}
	public int getCameraPort() {
		return cameraPort;
	}
	public void setCameraPort(int cameraPort) {
		this.cameraPort = cameraPort;
	}
	public String getLoginuser() {
		return loginuser;
	}
	public void setLoginuser(String loginuser) {
		this.loginuser = loginuser;
	}
	public String getLoginpwd() {
		return loginpwd;
	}
	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
