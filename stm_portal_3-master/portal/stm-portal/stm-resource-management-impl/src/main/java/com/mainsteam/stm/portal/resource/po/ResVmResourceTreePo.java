package com.mainsteam.stm.portal.resource.po;

public class ResVmResourceTreePo {

	private String uuid;
	private String puuid;
	private String vmtype;
	private String vmname;
	private String vmfullname;
	private String resourceid;
	private String vcenteruuid;
	private String datacenteruuid;
	private String clusteruuid;
	private String hostuuid;
	private Long instanceid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPuuid() {
		return puuid;
	}

	public void setPuuid(String puuid) {
		this.puuid = puuid;
	}

	public String getVmtype() {
		return vmtype;
	}

	public void setVmtype(String vmtype) {
		this.vmtype = vmtype;
	}

	public String getVmname() {
		return vmname;
	}

	public void setVmname(String vmname) {
		this.vmname = vmname;
	}

	public String getVmfullname() {
		return vmfullname;
	}

	public void setVmfullname(String vmfullname) {
		this.vmfullname = vmfullname;
	}

	public String getResourceid() {
		return resourceid;
	}

	public void setResourceid(String resourceid) {
		this.resourceid = resourceid;
	}

	public String getVcenteruuid() {
		return vcenteruuid;
	}

	public void setVcenteruuid(String vcenteruuid) {
		this.vcenteruuid = vcenteruuid;
	}

	public String getDatacenteruuid() {
		return datacenteruuid;
	}

	public void setDatacenteruuid(String datacenteruuid) {
		this.datacenteruuid = datacenteruuid;
	}

	public String getClusteruuid() {
		return clusteruuid;
	}

	public void setClusteruuid(String clusteruuid) {
		this.clusteruuid = clusteruuid;
	}

	public String getHostuuid() {
		return hostuuid;
	}

	public void setHostuuid(String hostuuid) {
		this.hostuuid = hostuuid;
	}

	public Long getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(Long instanceid) {
		this.instanceid = instanceid;
	}

}
