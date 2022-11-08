package com.mainsteam.stm.portal.vm.po;

public class VmResourceTreePo {

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
	private Long vcenterinstanceid;
	private String datacentername;
	private Long clusterinstanceid;
	private Long hostinstanceid;
	private String resourcepool;

	@Override
	public String toString() {
		return "RedisInfo{" + "uuid='" + uuid + '\'' + ", puuid='" + puuid + '\'' + ", vmtype='" + vmtype + '\''
				+ ", vmname='" + vmname + '\'' + ", vmfullname='" + vmfullname + '\''+ ", resourceid='" + resourceid + '\''
				+ ", vcenteruuid='" + vcenteruuid + '\''
				+ ", datacenteruuid='" + datacenteruuid + '\''
				+ ", clusteruuid='" + clusteruuid + '\''
				+ ", hostuuid='" + hostuuid + '\''
				+ ", instanceid='" + instanceid + '\''
				+ ", vcenterinstanceid='" + vcenterinstanceid + '\''
				+ ", datacentername='" + datacentername + '\''
				+ ", clusterinstanceid='" + clusterinstanceid + '\''
				+ ", hostinstanceid='" + hostinstanceid + '\''
				+ ", resourcepool='" + resourcepool + '\''+ '}';
	}
	
	public String getResourcepool() {
		return resourcepool;
	}

	public void setResourcepool(String resourcepool) {
		this.resourcepool = resourcepool;
	}

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

	public Long getVcenterinstanceid() {
		return vcenterinstanceid;
	}

	public void setVcenterinstanceid(Long vcenterinstanceid) {
		this.vcenterinstanceid = vcenterinstanceid;
	}

	public String getDatacentername() {
		return datacentername;
	}

	public void setDatacentername(String datacentername) {
		this.datacentername = datacentername;
	}

	public Long getClusterinstanceid() {
		return clusterinstanceid;
	}

	public void setClusterinstanceid(Long clusterinstanceid) {
		this.clusterinstanceid = clusterinstanceid;
	}

	public Long getHostinstanceid() {
		return hostinstanceid;
	}

	public void setHostinstanceid(Long hostinstanceid) {
		this.hostinstanceid = hostinstanceid;
	}


}
