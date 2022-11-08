package com.mainsteam.stm.topo.bo;


/*
 * 设备背板信息
 */
public class BackbordBo {
    private Long id;
    //基准背板id
    private Long baseId;
    //资源实例id
    private Long instanceId;
    // 厂商
	private String vendor;
	// 型号
	private String type;
	//背板信息
    private String info;
    
    /**扩展字段，用于查看显示*/
    //设备类型
    private String typeName;
    //设备厂商
    private String venderName;
    //设备型号
    private String series;
    //设备名称
    private String name;
    //设备IP
    private String ip;
    //
    private String osType;
    
    public Long getBaseId() {
		return baseId;
	}
	public void setBaseId(Long baseId) {
		this.baseId = baseId;
	}
    public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getVenderName() {
		return venderName;
	}
	public void setVenderName(String venderName) {
		this.venderName = venderName;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}

}
