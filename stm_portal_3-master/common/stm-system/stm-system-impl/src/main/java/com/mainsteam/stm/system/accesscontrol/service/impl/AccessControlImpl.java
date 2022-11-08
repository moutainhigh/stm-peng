package com.mainsteam.stm.system.accesscontrol.service.impl;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.system.accesscontrol.api.IAccessControlApi;
import com.mainsteam.stm.system.accesscontrol.bo.AccessControl;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.util.Util;

/**
 * <li>文件名称: AccessControlServiceImpl.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月6日
 * @author zjf
 */
public class AccessControlImpl implements IAccessControlApi {

	@Autowired
	@Qualifier("systemConfigApi")
	private ISystemConfigApi systemConfigApi;
	@Resource(name="stm_system_userApi")
	private IUserApi userApi;
	
	private static final long ACCESSCONTROL_CONFIG_ID = SystemConfigConstantEnum.SYSTEM_CONFIG_ACCESS_CONTROL_CFG.getCfgId();
	
	IMemcache<AccessControl> cache = MemCacheFactory
			.getLocalMemCache(AccessControl.class);
	
	/**缓存KEY名称*/
	private static final String IPCACHENAME = "ACCESSCONTROL";

	@Override
	public AccessControl getAccessControlIP() {
		AccessControl accessControl = cache.get(IPCACHENAME);
		if (accessControl == null) {
			accessControl = new AccessControl();
			SystemConfigBo configBo = systemConfigApi.getSystemConfigById(ACCESSCONTROL_CONFIG_ID);
			if(configBo!=null){
				if(!StringUtils.isEmpty(configBo.getContent())){
					accessControl =  JSONObject.parseObject(configBo.getContent(), AccessControl.class);
				}
			}
		}
		return accessControl;
	}

	@Override
	public boolean updateAccessControlIp(AccessControl accessControl) {
		SystemConfigBo configBo = systemConfigApi.getSystemConfigById(ACCESSCONTROL_CONFIG_ID);
		int result = 0;
		if(configBo != null){
			// 不开启各项开关时不恢复默认值
			AccessControl dbAccessControl = null;
			if(!StringUtils.isEmpty(configBo.getContent())){
				dbAccessControl =  JSONObject.parseObject(configBo.getContent(), AccessControl.class);
				if(dbAccessControl != null){
					if(!accessControl.getLoginFailTimeIsEnable()){
						accessControl.setLoginFailTime(dbAccessControl.getLoginFailTime());
					}
					if(!accessControl.getLoginDeblockIsEnable()){
						accessControl.setLoginDeblockMinutes(dbAccessControl.getLoginDeblockMinutes());
					}
					if(!accessControl.getLoginPassValidityIsEnable()){
						accessControl.setLoginPassValidityDays(dbAccessControl.getLoginPassValidityDays());
						accessControl.setLoginPassValidityAlertDays(dbAccessControl.getLoginPassValidityAlertDays());
					}
					// 初始化用户错误密码信息
					if(accessControl.getLoginFailTimeIsEnable() && !dbAccessControl.getLoginFailTimeIsEnable()){
						userApi.updatePassErrorCnt2Zero();
					}
					// 初始化用户密码修改信息
					if(accessControl.getLoginPassValidityIsEnable() && !dbAccessControl.getLoginPassValidityIsEnable()){
						userApi.updateUpPassTime2Now();
					}
				}
			}
			configBo.setContent(JSONObject.toJSONString(accessControl));
			result = systemConfigApi.updateSystemConfig(configBo);
		}else{
			configBo = new SystemConfigBo();
			configBo.setId(ACCESSCONTROL_CONFIG_ID);
			configBo.setContent(JSONObject.toJSONString(accessControl));
			configBo.setDescription("访问控制配置文件管理");
			result = systemConfigApi.insertSystemConfig(configBo);
		}
		if (cache.get(IPCACHENAME) == null) {
			cache.set(IPCACHENAME, accessControl);
		} else {
			cache.update(IPCACHENAME, accessControl);
		}
		return result>0?true:false;
	}

	@Override
	public boolean checkIpIsAllow(String ip) {
		if(this.isOpenIPFilter()){
			if(Util.isIp(ip)){
				AccessControl bo = this.getAccessControlIP();
				String ips = "";
				if(bo!=null){
					if (bo.getAccessType().equals("blackList")) {//判断过虑模式，仅阻止以下列表中IP访问本系统获取黑名单中IP
						ips = bo.getNotAllowed();
					} else if (bo.getAccessType().equals("whiteList")) {//  仅允许以下列表中IP访问本系统获取白名单中IP
						ips = bo.getAllow();
					}
				}
				String[] iparray = null;
				if (ips != null && ips.length() > 0) {
					iparray = ips.split(";");
				}
				List<String> singleIP = new ArrayList<String>();//独立IP
				List<String> netSectionIP = new ArrayList<String>();//IP网段
				List<String> addSetionIp = new ArrayList<String>();//IP地址区间
				if(iparray!=null){
					for (int i = 0; i < iparray.length; i++) {
						if (Util.isIp(iparray[i])) {
							singleIP.add(iparray[i]);
						}else if((iparray[i].split("\\.").length-1)<=2){
							netSectionIP.add(iparray[i]);
						}else if(iparray[i].indexOf("-")>0){
							addSetionIp.add(iparray[i]);
						}
					}
				}
				boolean flag = false;//IP在过虑列表中是否存在
				if(singleIP.contains(ip.trim())){//判断单个IP过虑
					flag=true;
				}else{
					//IP网段过虑
					for(int i=0;i<netSectionIP.size();i++){
						String nip = netSectionIP.get(i);
						if(this.isExistsInRange(ip, nip+".1-"+nip+".255")){
							flag=true;
							break;
						}else {
							continue;
						}
					}
					//IP地址段过虑
					for(int i=0;i<addSetionIp.size();i++){
						if(this.isExistsInRange(ip, addSetionIp.get(i))){
							flag=true;
							break;
						}else {
							continue;
						}
					}
				}
				
				if (bo.getAccessType().equals("blackList")) {//判断过虑模式，如果在黑名单中存在的，不允许通过
					return !flag;
				} else if (bo.getAccessType().equals("whiteList")) {// 如果在白名单中存的的，允许通过
					return flag;
				}
			}
			return false;
		}else{
			return true;
		}
	}

	@Override
	public boolean isOpenIPFilter() {
		return this.getAccessControlIP().getIsEnable();
	}

	

	
	/** 
     * 判断ip是否在指定网段中 
     * @author zhangjf 
     * @param iparea 
     * @param ip 
     * @return boolean 
     */  
    private boolean isExistsInRange(String ip,String ipSection){
    	ipSection = ipSection.trim();
    	ip = ip.trim();
    	int idx = ipSection.indexOf("-");
    	String beginIp = ipSection.substring(0,idx);
    	String endIp = ipSection.substring(idx+1);
    	return Util.ip2Long(beginIp)<=Util.ip2Long(ip) && Util.ip2Long(ip) <=Util.ip2Long(endIp);
    }
}
