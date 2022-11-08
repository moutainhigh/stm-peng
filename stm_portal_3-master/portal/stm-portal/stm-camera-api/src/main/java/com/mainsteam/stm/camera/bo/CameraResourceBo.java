package com.mainsteam.stm.camera.bo;

import java.io.Serializable;

public class CameraResourceBo implements Serializable{

	private static final long serialVersionUID = -7189552504731510081L;
	
	private Long id;
	
	private String sourceName;
	
	private String name;
	
	private String devIP;
	
	private String groupName;
	
	private String address;
	
	private String gisX;
	
	private String gisY;
	
	private String platForm;
	
	private String instanceState;
	
	private String instanceStatus;
	
	private String liablePerson;

	private String devUser;

	private String devPwd;

	private String devPort;

	private String cameraType;

	private String resourceStatus;

	//############### 条件查询用参数字段 ###############//
	
	private String iPorName;
	
	private String domainId;
	
	private String domainName;

    /**
     * DCS名称
     */
    private String dcsGroupName;

    private String ipAddress;

    private boolean hasRight;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDevIP() {
		return devIP;
	}

	public void setDevIP(String devIP) {
		this.devIP = devIP;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGisX() {
		return gisX;
	}

	public void setGisX(String gisX) {
		this.gisX = gisX;
	}

	public String getGisY() {
		return gisY;
	}

	public void setGisY(String gisY) {
		this.gisY = gisY;
	}

	public String getPlatForm() {
		return platForm;
	}

	public void setPlatForm(String platForm) {
		this.platForm = platForm;
	}

	public String getInstanceState() {
		return instanceState;
	}

	public void setInstanceState(String instanceState) {
		this.instanceState = instanceState;
	}
	
	public String getInstanceStatus() {
		return instanceStatus;
	}
	
	public void setInstanceStatus(String instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public String getLiablePerson() {
		return liablePerson;
	}

	public void setLiablePerson(String liablePerson) {
		this.liablePerson = liablePerson;
	}

	public String getiPorName() {
		return iPorName;
	}

	public void setiPorName(String iPorName) {
		this.iPorName = iPorName;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

    public String getDcsGroupName() {
        return dcsGroupName;
    }

    public void setDcsGroupName(String dcsGroupName) {
        this.dcsGroupName = dcsGroupName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isHasRight() {
        return hasRight;
    }

    public void setHasRight(boolean hasRight) {
        this.hasRight = hasRight;
    }

    public String getDevUser() {
        return devUser;
    }

    public void setDevUser(String devUser) {
        this.devUser = devUser;
    }

    public String getDevPwd() {
        return devPwd;
    }

    public void setDevPwd(String devPwd) {
        this.devPwd = devPwd;
    }

    public String getDevPort() {
        return devPort;
    }

    public void setDevPort(String devPort) {
        this.devPort = devPort;
    }

    public String getCameraType() {
        return cameraType;
    }

    public void setCameraType(String cameraType) {
        this.cameraType = cameraType;
    }

    public String getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(String resourceStatus) {
        this.resourceStatus = resourceStatus;
    }
}
