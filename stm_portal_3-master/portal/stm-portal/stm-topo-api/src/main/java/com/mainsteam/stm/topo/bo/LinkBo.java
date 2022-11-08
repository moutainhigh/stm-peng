package com.mainsteam.stm.topo.bo;

import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;

public class LinkBo {
	public static final String NODE="node";
	public static final String OTHER="other";
	public static final String GROUP="group";
	public static String NET = "net";
	private Long id;
	private String note;
	private String type;
	private Long instanceId;
	private Long from;
	private Long to;
	private String fromType;
	private String toType;
	private Long fromIfIndex;
	private Long toIfIndex;
	private Long parentId;
	private String attr;
	private boolean direction;
	//链路的原始发现信息-不入库
	private JSONObject rawInfo;
	//资源实例
	private ResourceInstance res;
	
	/*扩展属性-仅用于查询显示*/
	private String searchType;
	private String searchVal;
	
	private String srcMainInstIP;		//源设备IP地址
	private String destMainInstIP;		//目的设备IP地址
	private String srcMainInstName;		//源接口名称
	private String destMainInsName;		//目的接口名称
	private String insStatus;			//链路状态
	private String monitorStatus;		//监控状态
	private String getValInterface;		//取值接口
	private String downDirect;			//下行方向
	private String ifInOctetsSpeed;		//上行流量
	private String ifOutOctetsSpeed;	//下行流量
	private String ifInBandWidthUtil;	//上行带宽利用率
	private String ifOutBandWidthUtil;	//下行带宽利用率
	private String broadPktsRate;		//广播包率
	private String bandWidth;			//链路带宽
	private String srcIfName;			//源端端口名称
	private String destIfName;			//目的端端口名称
	private String srcIfColor;			//源接口状态颜色
	private String destIfColor;			//目的接口状态颜色
	private Long srcInstanceId;			//源端实例id
	private Long destInstanceId;		//目的端实例id
	private int multiNumber = 1;		//多链路条数
	private int deleteFlag = 0;			//0:未删除，1：已删除
	private Set<Long> domainSet = new HashSet<Long>();	//登录用户所属域
	
	public Set<Long> getDomainSet() {
		return domainSet;
	}

	public void setDomainSet(Set<Long> domainSet) {
		this.domainSet = domainSet;
	}

	public int getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getMonitorStatus() {
		return monitorStatus;
	}

	public void setMonitorStatus(String monitorStatus) {
		this.monitorStatus = monitorStatus;
	}

	public int getMultiNumber() {
		return multiNumber;
	}

	public void setMultiNumber(int multiNumber) {
		this.multiNumber = multiNumber;
	}

	public Long getDestInstanceId() {
		return destInstanceId;
	}

	public void setDestInstanceId(Long destInstanceId) {
		this.destInstanceId = destInstanceId;
	}

	public String getSrcIfName() {
		return srcIfName;
	}

	public Long getSrcInstanceId() {
		return srcInstanceId;
	}

	public void setSrcInstanceId(Long srcInstanceId) {
		this.srcInstanceId = srcInstanceId;
	}

	public void setSrcIfName(String srcIfName) {
		this.srcIfName = srcIfName;
	}
	public String getDestIfName() {
		return destIfName;
	}
	public void setDestIfName(String destIfName) {
		this.destIfName = destIfName;
	}
	public String getSrcIfColor() {
		return srcIfColor;
	}
	public void setSrcIfColor(String srcIfColor) {
		this.srcIfColor = srcIfColor;
	}
	public String getDestIfColor() {
		return destIfColor;
	}
	public void setDestIfColor(String destIfColor) {
		this.destIfColor = destIfColor;
	}
	public String getSrcMainInstIP() {
		return srcMainInstIP;
	}
	public void setSrcMainInstIP(String srcMainInstIP) {
		this.srcMainInstIP = srcMainInstIP;
	}
	public String getDestMainInstIP() {
		return destMainInstIP;
	}
	public void setDestMainInstIP(String destMainInstIP) {
		this.destMainInstIP = destMainInstIP;
	}
	public String getSrcMainInstName() {
		return srcMainInstName;
	}
	public void setSrcMainInstName(String srcMainInstName) {
		this.srcMainInstName = srcMainInstName;
	}
	public String getDestMainInsName() {
		return destMainInsName;
	}
	public void setDestMainInsName(String destMainInsName) {
		this.destMainInsName = destMainInsName;
	}
	public String getInsStatus() {
		return insStatus;
	}
	public void setInsStatus(String insStatus) {
		this.insStatus = insStatus;
	}
	public String getGetValInterface() {
		return getValInterface;
	}
	public void setGetValInterface(String getValInterface) {
		this.getValInterface = getValInterface;
	}
	public String getDownDirect() {
		return downDirect;
	}
	public void setDownDirect(String downDirect) {
		this.downDirect = downDirect;
	}
	public String getIfInOctetsSpeed() {
		return ifInOctetsSpeed;
	}
	public void setIfInOctetsSpeed(String ifInOctetsSpeed) {
		this.ifInOctetsSpeed = ifInOctetsSpeed;
	}
	public String getIfOutOctetsSpeed() {
		return ifOutOctetsSpeed;
	}
	public void setIfOutOctetsSpeed(String ifOutOctetsSpeed) {
		this.ifOutOctetsSpeed = ifOutOctetsSpeed;
	}
	public String getIfInBandWidthUtil() {
		return ifInBandWidthUtil;
	}
	public void setIfInBandWidthUtil(String ifInBandWidthUtil) {
		this.ifInBandWidthUtil = ifInBandWidthUtil;
	}
	public String getIfOutBandWidthUtil() {
		return ifOutBandWidthUtil;
	}
	public void setIfOutBandWidthUtil(String ifOutBandWidthUtil) {
		this.ifOutBandWidthUtil = ifOutBandWidthUtil;
	}
	public String getBroadPktsRate() {
		return broadPktsRate;
	}
	public void setBroadPktsRate(String broadPktsRate) {
		this.broadPktsRate = broadPktsRate;
	}
	public String getBandWidth() {
		return bandWidth;
	}
	public void setBandWidth(String bandWidth) {
		this.bandWidth = bandWidth;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(Long instanceId) {
		this.instanceId = instanceId;
	}
	public Long getFrom() {
		return from;
	}
	public void setFrom(Long from) {
		this.from = from;
	}
	public Long getTo() {
		return to;
	}
	public void setTo(Long to) {
		this.to = to;
	}
	public String getFromType() {
		return fromType;
	}
	public void setFromType(String fromType) {
		this.fromType = fromType;
	}
	public String getToType() {
		return toType;
	}
	public void setToType(String toType) {
		this.toType = toType;
	}
	public JSONObject getRawInfo() {
		return rawInfo;
	}
	public void setRawInfo(JSONObject rawInfo) {
		this.rawInfo = rawInfo;
	}
	public ResourceInstance getRes() {
		return res;
	}
	public void setRes(ResourceInstance res) {
		this.res = res;
	}
	public Long getFromIfIndex() {
		return fromIfIndex;
	}
	public void setFromIfIndex(Long fromIfIndex) {
		this.fromIfIndex = fromIfIndex;
	}
	public Long getToIfIndex() {
		return toIfIndex;
	}
	public void setToIfIndex(Long toIfIndex) {
		this.toIfIndex = toIfIndex;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public boolean isDirection() {
		return direction;
	}
	public void setDirection(boolean direction) {
		this.direction = direction;
	}
}
