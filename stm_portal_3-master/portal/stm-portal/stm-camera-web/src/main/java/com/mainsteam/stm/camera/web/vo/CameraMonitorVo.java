package com.mainsteam.stm.camera.web.vo;

import java.io.Serializable;

import com.mainsteam.stm.portal.resource.web.vo.ResourceMonitorVo;

public class CameraMonitorVo extends ResourceMonitorVo implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//增加摄像头特有属性
	private String cameraType;
	private String cameraAddress;
	private String cameraGroup;
	private String loginUser;
	public String getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(String loginUser) {
		this.loginUser = loginUser;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public int getCameraPort() {
		return cameraPort;
	}

	public void setCameraPort(int cameraPort) {
		this.cameraPort = cameraPort;
	}

	private String loginPassword;
	private int cameraPort;
	
	public String getCameraType() {
		return cameraType;
	}

	public void setCameraType(String cameraType) {
		this.cameraType = cameraType;
	}

	public String getCameraAddress() {
		return cameraAddress;
	}

	public void setCameraAddress(String cameraAddress) {
		this.cameraAddress = cameraAddress;
	}

	public String getCameraGroup() {
		return cameraGroup;
	}

	public void setCameraGroup(String cameraGroup) {
		this.cameraGroup = cameraGroup;
	}
}
