package com.mainsteam.stm.alarm.query;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;


public class AlarmEventQuery {
	public enum OrderField{
		LEVEL("ALARM_LEVEL"),SOURCE_IP,COLLECTION_TIME("COLLECTION_TIME"),CONTENT("content"),
		SOURCE_NAME("SOURCE_NAME"),
		EXT0,EXT1,EXT2,EXT3,EXT4,EXT5,EXT6,EXT7,EXT8,EXT9;
		
		private String name;
		OrderField(){
			this.name=this.name();
		}
		OrderField(String name){
			this.name=name;
		}
		
		@Override
		public String toString(){
			return name;
		}
	}

	public static void main(String[] args) {
		System.out.println(OrderField.SOURCE_IP);
	}


	private List<MetricStateEnum> states;
	private List<String> sourceIDes; 
	private String likeSourceIP;
	private String likeSourceName;
	private String likeSourceIpOrName;
	
	private Date start;
	private Date end;
	/**告警提供者：OC4、otherSys */
	private AlarmProviderEnum provider;
	/**告警模块 */
	private SysModuleEnum[] sysIDes; 
	private Boolean recovered;
	private Long recoveryEventID;
	private String ext0;
	private String ext1;
	private String ext2;
	private String ext3;
	private String ext4;
	private String ext5;
	private String ext6;
	private String ext7;
	private String ext8;
	private String ext9;
	private OrderField[] orderFieldes;
	private boolean orderASC;
	
	public List<MetricStateEnum> getStates() {
		return states;
	}
	public void setStates(List<MetricStateEnum> states) {
		this.states = states;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public String getExt0() {
		return ext0;
	}
	public void setExt0(String ext0) {
		this.ext0 = ext0;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
	public String getExt4() {
		return ext4;
	}
	public void setExt4(String ext4) {
		this.ext4 = ext4;
	}
	public String getExt5() {
		return ext5;
	}
	public void setExt5(String ext5) {
		this.ext5 = ext5;
	}
	public String getExt6() {
		return ext6;
	}
	public void setExt6(String ext6) {
		this.ext6 = ext6;
	}
	public String getExt7() {
		return ext7;
	}
	public void setExt7(String ext7) {
		this.ext7 = ext7;
	}
	public String getExt8() {
		return ext8;
	}
	public void setExt8(String ext8) {
		this.ext8 = ext8;
	}
	public String getExt9() {
		return ext9;
	}
	public void setExt9(String ext9) {
		this.ext9 = ext9;
	}
	public AlarmProviderEnum getProvider() {
		return provider;
	}
	public void setProvider(AlarmProviderEnum provider) {
		this.provider = provider;
	}
	public Boolean getRecovered() {
		return recovered;
	}
	public void setRecovered(Boolean recovered) {
		this.recovered = recovered;
	}
	public List<String> getSourceIDes() {
		return sourceIDes;
	}
	public void setSourceIDes(List<String> sourceIDes) {
		this.sourceIDes = sourceIDes;
	}
	public String getLikeSourceIP() {
		return likeSourceIP;
	}
	public void setLikeSourceIP(String likeSourceIP) {
		this.likeSourceIP = likeSourceIP;
	}
	public Long getRecoveryEventID() {
		return recoveryEventID;
	}
	public void setRecoveryEventID(Long recoveryEventID) {
		this.recoveryEventID = recoveryEventID;
	}
	public SysModuleEnum[] getSysIDes() {
		return sysIDes;
	}
	public void setSysIDes(SysModuleEnum[] sysIDes) {
		this.sysIDes = sysIDes;
	}
	public boolean isOrderASC() {
		return orderASC;
	}
	public void setOrderASC(boolean orderASC) {
		this.orderASC = orderASC;
	}
	public OrderField[] getOrderFieldes() {
		return orderFieldes;
	}
	public void setOrderFieldes(OrderField[] orderFieldes) {
		this.orderFieldes = orderFieldes;
	}
	public String getLikeSourceName() {
		return likeSourceName;
	}
	public void setLikeSourceName(String likeSourceName) {
		this.likeSourceName = likeSourceName;
	}
	public String getLikeSourceIpOrName() {
		return likeSourceIpOrName;
	}
	public void setLikeSourceIpOrName(String likeSourceIpOrName) {
		this.likeSourceIpOrName = likeSourceIpOrName;
	}
}
