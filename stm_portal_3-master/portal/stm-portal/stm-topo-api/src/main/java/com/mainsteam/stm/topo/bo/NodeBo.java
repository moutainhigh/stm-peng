package com.mainsteam.stm.topo.bo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.qwserv.itm.netprober.bean.DevType;



/**
 * 拓扑图元节点表
 * <li>文件名称: TopoAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 映射stm_topo_node表结构</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月7日
 * @author  xfq
 */
public class NodeBo {
	Logger logger = Logger.getLogger(NodeBo.class);
	//三层拓扑的id标示
	public static final Long THIRDTOPO_ID = 1l;
	//二层主拓扑id
	public static final Long SECONDTOPO_ID = null;
	private Long id;
	private String deviceId;
	private String oid;
	private Long instanceId;
	private String type;	//资源类型
	private String typeName;//设备类型名称
	private Double x=0d;
	private Double y=0d;
	private Double rx=0d;
	private Double ry=0d;
	private Double iconWidth=30d;	//系统默认图元宽带
	private Double iconHeight=30d;	//系统默认图元高度
	private Boolean visible=true;
	private String icon;
	private Long groupId;
	private String ip;
	private Long subTopoId;
	private Long parentId;
	private String attr;
	private Boolean deleteFlag;
	//拓扑发现产生的原始信息
	private JSONObject rawInfo;
	private Boolean flag;
	
	//以下字段是业务字段，不入库
	private String resourceId;
	private boolean isRepeat=false;
	/*扩展属性-用于查询*/
	private String searchVal;
	private String instanceName;	//资源名称
	private String uptimeString;	//运行时间-字符串
    private String instanceColor;	//资源状态(致命,严重,告警,正常,未知)
    private String cpuRate;			//CPU利用率
    private String cpuRateColor;	//CPU利用率展示颜色
    private String memeRate;		//内存利用率
    private String memeRateColor;	//内存利用率
    private String[] macAddress;	//mac地址列表
    private String[] ips=new String[0];
    private Set<Long> domainSet = new HashSet<Long>();	//登录用户所属域
    //以下两个属性用在单资源发现
    private String showName;//显示名称
    private String commonBody;//团体字
	
	public Set<Long> getDomainSet() {
		return domainSet;
	}
	public void setDomainSet(Set<Long> domainSet) {
		this.domainSet = domainSet;
	}
	public String getCpuRateColor() {
		return cpuRateColor;
	}
	public void setCpuRateColor(String cpuRateColor) {
		this.cpuRateColor = cpuRateColor;
	}
	public String getMemeRateColor() {
		return memeRateColor;
	}
	public void setMemeRateColor(String memeRateColor) {
		this.memeRateColor = memeRateColor;
	}

	public String getCpuRate() {
		return cpuRate;
	}
	public void setCpuRate(String cpuRate) {
		this.cpuRate = cpuRate;
	}
	public String getMemeRate() {
		return memeRate;
	}
	public void setMemeRate(String memeRate) {
		this.memeRate = memeRate;
	}
	public String getInstanceColor() {
		return instanceColor;
	}
	public void setInstanceColor(String instanceColor) {
		this.instanceColor = instanceColor;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getUptimeString() {
		return uptimeString;
	}
	public void setUptimeString(String uptimeString) {
		this.uptimeString = uptimeString;
	}
	public boolean isNetDevice(){
		if(this.type.equals("HOST") || this.type.equals("SERVER")){
			return false;
		}
		return true;
	}
	public JSONObject getAttrJson(){
		String attr = getAttr();
		return JSON.parseObject(attr);
	}
	public Boolean getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public String getSearchVal() {
		return searchVal;
	}
	public void setSearchVal(String searchVal) {
		this.searchVal = searchVal;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public String getType() {
		if(null == type || "".equals(type)){
			this.setType("OTHER");
		}
		if(type.equals(DevType.HOST.name())){//不区分主机服务器，主机一律归入服务器
			type=DevType.SERVER.name();
		}
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Double getX() {
		return x;
	}
	public void setX(Double x) {
		this.x = x;
	}
	public Double getY() {
		return y;
	}
	public void setY(Double y) {
		this.y = y;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Double getRx() {
		return rx;
	}
	public void setRx(Double rx) {
		this.rx = rx;
	}
	public Double getRy() {
		return ry;
	}
	public void setRy(Double ry) {
		this.ry = ry;
	}
	public Double getIconWidth() {
		return iconWidth;
	}
	public void setIconWidth(Double iconWidth) {
		this.iconWidth = iconWidth;
	}
	public Double getIconHeight() {
		return iconHeight;
	}
	public void setIconHeight(Double iconHeight) {
		this.iconHeight = iconHeight;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public Long getSubTopoId() {
		if(subTopoId==null) return 0l;
		return subTopoId;
	}
	public void setSubTopoId(Long subTopoId) {
		this.subTopoId = subTopoId;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	
	
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getCommonBody() {
		return commonBody;
	}
	public void setCommonBody(String commonBody) {
		this.commonBody = commonBody;
	}
	public NodeBo clone(){
		NodeBo newInst = new NodeBo();
		BeanUtils.copyProperties(this, newInst);
		return newInst;
	}
	public void mapAttr(){
		try {
			DevType type = DevType.valueOf(getType());
			//映射设备类型对应的图标
			switch(type){
				case ROUTER://路由器
					setIcon("themes/blue/images/topo/topoIcon/net/10_router.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("路由器");
					break;
				case STORAGE://存储设备
					setIcon("themes/blue/images/topo/topoIcon/net/storage.png");
					setIconWidth(30d);
					setIconHeight(35d);
					setTypeName("存储设备");
					break;
				case WLAN://wlan
					setIcon("themes/blue/images/topo/topoIcon/net/wlan.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("WLAN");
					break;
				case VIRTUAL://virtual
					setIcon("themes/blue/images/topo/topoIcon/net/virtual.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("virtual");
					break;
				case ROUTER_SWITCH:
					setIcon("themes/blue/images/topo/topoIcon/net/10_switch3.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("三层交换机");
				break;
				case FIREWALL://firewall
					setIcon("themes/blue/images/topo/topoIcon/net/10_firewall.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("防火墙");
				break;
				case HOST:
					setIcon("themes/blue/images/topo/topoIcon/net/10_server.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("主机");
				break;
				case LOAD_BALANCE://load_balance
					setIcon("themes/blue/images/topo/topoIcon/net/other.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("负载均衡");
				break;
				case SERVER:
					setIcon("themes/blue/images/topo/topoIcon/net/10_server.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("服务器");
				break;
				case SWITCH:
					setIcon("themes/blue/images/topo/topoIcon/net/10_switch2.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("二层交换机");
				break;	
				case WIRELESS_AP://wireless_ap
					setIcon("themes/blue/images/topo/topoIcon/net/10_ap.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("无线AP");
				break;
				case WIRELESS_AC:
					setIcon("themes/blue/images/topo/topoIcon/net/10_ap.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("无线AC");
				break;
				case PRINTER://打印机 printer
					setIcon("themes/blue/images/topo/topoIcon/net/printer.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("打印机");
					break;
				case OTHER://其他设备
					setIcon("themes/blue/images/topo/topoIcon/net/10_host.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("其他设备");
					break;
				default:
					setIcon("themes/blue/images/topo/topoIcon/net/10_router.png");
					setIconWidth(30d);
					setIconHeight(30d);
					setTypeName("未知类型");
				break;
			}
		} catch (Exception e) {
			setTypeName("子网");
		}
	}
	public static  List<NodeBo> parseForTopo(String itemsStr){
		JSONArray items = (JSONArray)JSON.parse(itemsStr);
		List<NodeBo> retn = new ArrayList<NodeBo>();
		for(Object itemObject:items){
			NodeBo tmp = new NodeBo();
			JSONObject item = (JSONObject) itemObject;
			tmp.setId(item.getLong("id"));
			tmp.setIcon(item.getString("icon"));
			tmp.setX(item.getDouble("x"));
			tmp.setY(item.getDouble("y"));
			tmp.setRx(item.getDouble("rx"));
			tmp.setRy(item.getDouble("ry"));
			tmp.setIconWidth(item.getDouble("iconWidth"));
			tmp.setIconHeight(item.getDouble("iconHeight"));
			tmp.setSubTopoId(item.getLong("subTopoId"));
			tmp.setVisible(item.getBoolean("visible"));
			tmp.setIp(item.getString("ip"));
			tmp.setShowName(item.getString("showName"));
			tmp.setCommonBody(item.getString("commonBody"));
			tmp.setAttr(item.getString("attr"));
			retn.add(tmp);
		}
		return retn;
	}
	public String[] getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String[] macAddress) {
		this.macAddress = macAddress;
	}
	public String getTypeName() {
		mapAttr();
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public JSONObject getRawInfo() {
		return rawInfo;
	}
	public void setRawInfo(JSONObject rawInfo) {
		this.rawInfo = rawInfo;
	}
	public boolean isRepeat() {
		return isRepeat;
	}
	public void setRepeat(boolean isRepeat) {
		this.isRepeat = isRepeat;
	}
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public Long getParentId() {
		if(parentId==null) return 0l;
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getAttr() {
		if(null==attr){
			return "{}";
		}
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String[] getIps() {
		return ips;
	}
	public void setIps(String[] ips) {
		this.ips = ips;
	}
	/**
	 * 判断是否为硬件资源
	 * @return
	 */
	public boolean isHardDevice(){
		String typeStr = getType();
		try{
			DevType type = DevType.valueOf(typeStr);
			//映射设备类型对应的图标
			switch(type){
			case ROUTER://路由器
				return true;
			case STORAGE://存储设备
				return true;
			case WLAN://wlan
				return false;
			case VIRTUAL://virtual
				return false;
			case ROUTER_SWITCH:
				return true;
			case FIREWALL://firewall
				return true;
			case HOST:
				return false;
			case LOAD_BALANCE://load_balance
				return false;
			case SERVER:
				return false;
			case SWITCH:
				return true;
			case WIRELESS_AP://wireless_ap
				return true;
			case WIRELESS_AC:
				return true;
			case PRINTER://打印机 printer
				return true;
			case OTHER://其他设备
				return true;
			default:
				return true;
			}
		}catch(IllegalArgumentException e){
			return 	true;
		}
	}
	public String getCategoryId() {
		String typeStr = getType();
		try{
			DevType type = DevType.valueOf(typeStr);
			//映射设备类型对应的图标
			switch(type){
			case ROUTER://路由器
				return CapacityConst.ROUTER;
			case STORAGE://存储设备
				return CapacityConst.STORAGE;
			case WLAN://wlan
				return CapacityConst.SNMPOTHERS;
			case VIRTUAL://virtual
				return CapacityConst.SNMPOTHERS;
			case ROUTER_SWITCH:
				return CapacityConst.ROUTER;
			case FIREWALL://firewall
				return CapacityConst.FIREWALL;
			case HOST:
				return CapacityConst.HOST;
			case LOAD_BALANCE://load_balance
				return CapacityConst.NETWORK_DEVICE;
			case SERVER:
				return CapacityConst.HOST;
			case SWITCH:
				return CapacityConst.SWITCH;
			case WIRELESS_AP://wireless_ap
				return CapacityConst.NETWORK_DEVICE;
			case WIRELESS_AC:
				return CapacityConst.NETWORK_DEVICE;
			case PRINTER://打印机 printer
				return CapacityConst.SNMPOTHERS;
			case OTHER://其他设备
				return CapacityConst.SNMPOTHERS;
			default:
				return CapacityConst.SNMPOTHERS;
			}
		}catch(IllegalArgumentException e){
			return 	typeStr;
		}
	}
	
}
