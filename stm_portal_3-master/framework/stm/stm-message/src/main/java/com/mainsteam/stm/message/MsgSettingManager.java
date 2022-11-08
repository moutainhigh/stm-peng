package com.mainsteam.stm.message;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.util.SpringBeanUtil;


/**
 * <li>文件名称: AlarmManager.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 告警管理</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月18日
 * @author   zhangjunfeng
 */
public class MsgSettingManager {
	
	private ISystemConfigApi systemConfigApi;
	public void setSystemConfigApi(ISystemConfigApi systemConfigApi) {
		this.systemConfigApi = systemConfigApi;
	}
	
	public MsgSettingManager(){
		if(systemConfigApi==null){
			systemConfigApi = SpringBeanUtil.getBean(ISystemConfigApi.class);
		}
	}
	private static final long SYSTEM_CONFIG_ID=SystemConfigConstantEnum.SYSTEM_CONFIG_MESSAGE_SETTING_CFG.getCfgId();
	
	public static final int SETTING_TYPE_MAIL=1;
	public static final int SETTING_TYPE_MESSAGE=2;
	public static final int SETTING_TYPE_WARN=3;
	
	public boolean alarmSetting(MsgSettingInfo alarmSettingInfo,int type){
		SystemConfigBo config = systemConfigApi.getSystemConfigById(SYSTEM_CONFIG_ID);
		MsgSettingInfo settingInfo = new MsgSettingInfo();
		if(null!=config && config.getContent()!=null && !config.getContent().isEmpty()){
			settingInfo = JSONObject.parseObject(config.getContent(),MsgSettingInfo.class);
		}else{
			settingInfo.setManufacturer("WAVECOM MODEM");
			settingInfo.setModel("MULTIBAND  900E  1800");
			settingInfo.setTimeOut("10");
			settingInfo.setConnectCount("3");
			settingInfo.setTime("60");
		}
		if(type==SETTING_TYPE_MAIL){
			settingInfo.setEmailSmtp(alarmSettingInfo.getEmailSmtp()==null?"":alarmSettingInfo.getEmailSmtp());
			settingInfo.setEmailPort(alarmSettingInfo.getEmailPort()==null?"":alarmSettingInfo.getEmailPort());
			settingInfo.setEmailSendEmail(alarmSettingInfo.getEmailSendEmail()==null?"":alarmSettingInfo.getEmailSendEmail());
			if(alarmSettingInfo.getEmailAccount()!=null){
				settingInfo.setEmailAccount(alarmSettingInfo.getEmailAccount());
			}
			settingInfo.setEmailPassword(alarmSettingInfo.getEmailPassword()==null?"":alarmSettingInfo.getEmailPassword());
		}else if(type==SETTING_TYPE_MESSAGE){
			if(alarmSettingInfo.getMessageSendType()!=null){
				settingInfo.setMessageSendType(alarmSettingInfo.getMessageSendType()==null?"":alarmSettingInfo.getMessageSendType());
				settingInfo.setMessageType(alarmSettingInfo.getMessageType()==null?"":alarmSettingInfo.getMessageType());
				if(alarmSettingInfo.getMessageSendType().equals("SMSModem")){
					settingInfo.setClientIp(alarmSettingInfo.getClientIp()==null?"":alarmSettingInfo.getClientIp());
					settingInfo.setClientPort(alarmSettingInfo.getClientPort()==null?"":alarmSettingInfo.getClientPort());
//					settingInfo.setMessageCOMPort(alarmSettingInfo.getMessageCOMPort()==null?"":alarmSettingInfo.getMessageCOMPort());
//					settingInfo.setMessageBaudRate(alarmSettingInfo.getMessageBaudRate()==null?"":alarmSettingInfo.getMessageBaudRate());
//					settingInfo.setMessageSIMPIN(alarmSettingInfo.getMessageSIMPIN()==null?"":alarmSettingInfo.getMessageSIMPIN());
				}else if(alarmSettingInfo.getMessageSendType().equals("SMSGateway")){
					settingInfo.setMessageGatewayIp(alarmSettingInfo.getMessageGatewayIp()==null?"":alarmSettingInfo.getMessageGatewayIp());
					settingInfo.setMessagePort(alarmSettingInfo.getMessagePort()==null?"":alarmSettingInfo.getMessagePort());
					settingInfo.setMessageLoginAccount(alarmSettingInfo.getMessageLoginAccount()==null?"":alarmSettingInfo.getMessageLoginAccount());
					settingInfo.setMessagePassword(alarmSettingInfo.getMessagePassword()==null?"":alarmSettingInfo.getMessagePassword());
				}
			}
		}else if(type==SETTING_TYPE_WARN){
			settingInfo.setTime(alarmSettingInfo.getTime());
		}
		int result = 0;
		if(config==null){
			config = new SystemConfigBo();
			config.setId(SYSTEM_CONFIG_ID);
			config.setContent(JSONObject.toJSONString(settingInfo));
			config.setDescription("系统告警设置配置信息");
			result = systemConfigApi.insertSystemConfig(config);
		}else{
			config.setContent(JSONObject.toJSONString(settingInfo));
			result = systemConfigApi.updateSystemConfig(config);
		}
		return result>0?true:false;
	}
	
	public MsgSettingInfo getMsgSetting(){
		SystemConfigBo config = systemConfigApi.getSystemConfigById(SYSTEM_CONFIG_ID);
		MsgSettingInfo settingInfo = new MsgSettingInfo();
		if(null!=config){
			String configContent = config.getContent();
			if(configContent!=null && !configContent.isEmpty()){
				settingInfo = JSONObject.parseObject(configContent,MsgSettingInfo.class);
			}
		}
		return settingInfo;
	}
}
