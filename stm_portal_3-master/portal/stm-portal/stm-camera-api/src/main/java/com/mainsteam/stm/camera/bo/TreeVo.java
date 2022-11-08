package com.mainsteam.stm.camera.bo;

import java.io.Serializable;
import java.util.List;

public class TreeVo implements Serializable{

	private static final long serialVersionUID = -3947844957177750528L;
	
	private String id;
	private String name;
	private String state;
	private int type;
	private String pid;
	private String isCamera;
	//在线数
	private int onlineNumber;
	//完好数
	private int normalNumber;
	//GIS数
	private int gisNumber;
	//已诊断总数
	private int dignoseNumber;
	
	
	private  List<CaremaMonitorBo> cameraList;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getIsCamera() {
		return isCamera;
	}
	public void setIsCamera(String isCamera) {
		this.isCamera = isCamera;
	}
	public List<CaremaMonitorBo> getCameraList() {
		return cameraList;
	}
	public void setCameraList(List<CaremaMonitorBo> cameraList) {
		this.cameraList = cameraList;
	}
	public int getOnlineNumber() {
		return onlineNumber;
	}
	public void setOnlineNumber(int onlineNumber) {
		this.onlineNumber = onlineNumber;
	}
	public int getNormalNumber() {
		return normalNumber;
	}
	public void setNormalNumber(int normalNumber) {
		this.normalNumber = normalNumber;
	}
	public int getGisNumber() {
		return gisNumber;
	}
	public void setGisNumber(int gisNumber) {
		this.gisNumber = gisNumber;
	}
	public int getDignoseNumber() {
		return dignoseNumber;
	}
	public void setDignoseNumber(int dignoseNumber) {
		this.dignoseNumber = dignoseNumber;
	}
	
	
	
	

}
