package com.mainsteam.stm.system.alarm.web.action;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.notify.SmsOrEmailNotifyTemplateService;
import com.mainsteam.stm.alarm.obj.NotifyTypeEnum;
import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplate;
import com.mainsteam.stm.alarm.obj.SmsOrEmailNotifyTemplateEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.message.MessageSendHelper;
import com.mainsteam.stm.message.MsgSettingInfo;
import com.mainsteam.stm.message.MsgSettingManager;
import com.mainsteam.stm.message.mail.MailClient;
import com.mainsteam.stm.message.mail.MailSenderInfo;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.alarm.web.vo.AlarmSelectorVo;
import com.mainsteam.stm.util.PropertiesFileUtil;

/**
 * <li>文件名称: AlarmAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月18日
 * @author   zhangjunfeng
 */
@Controller
@RequestMapping("system/alarm")
public class AlarmAction extends BaseAction {

	private MsgSettingManager manager = new MsgSettingManager();
	@Autowired
	private SmsOrEmailNotifyTemplateService alarmNotifyTemplateService;
	@Value("${stm.notifyName}")
	private String notifyName;
	@RequestMapping("getAlarmSetting")
	public JSONObject getAlarmSetting(){
		MsgSettingInfo info = manager.getMsgSetting();
		return toSuccess(info);
	}
	
	@RequestMapping("emailAlarmSetting")
	public JSONObject emailAlarmSetting(MsgSettingInfo settingInfo){
		return toSuccess(manager.alarmSetting(settingInfo,MsgSettingManager.SETTING_TYPE_MAIL));
	}
	@RequestMapping("messageAlarmSetting")
	public JSONObject messageAlarmSetting(MsgSettingInfo settingInfo){
		return toSuccess(manager.alarmSetting(settingInfo,MsgSettingManager.SETTING_TYPE_MESSAGE));
	}
	@RequestMapping("alarmWanrnSetting")
	public JSONObject alarmWanrnSetting(MsgSettingInfo settingInfo){
		return toSuccess(manager.alarmSetting(settingInfo,MsgSettingManager.SETTING_TYPE_WARN));
	}
	
	@RequestMapping("testSendEmail")
	public JSONObject testSendEmail(MsgSettingInfo settingInfo,String emailTestEmail){
		boolean result;
		try {
			manager.alarmSetting(settingInfo,MsgSettingManager.SETTING_TYPE_MAIL);
			MailSenderInfo mailInfo = new MailSenderInfo();
			String[] addrs = {emailTestEmail};
			mailInfo.setToAddress(addrs);
			mailInfo.setSubject("STMCenter Email告警信息设置测试");
			mailInfo.setContent("STMCenter Email告警信息设置测试;如果你收到此邮件说明你信息设置成功");
			result = MailClient.sendTextMail(mailInfo);
			return toSuccess(result);
		} catch (Exception e) {
			e.printStackTrace();
			return toJsonObject(401, e.getMessage());
		}
		
	}
	/** 
	* @Title: testSendMessage 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param @param settingInfo
	* @param @return    设定文件 
	* @return JSONObject    返回类型 
	* @throws 
	*/
	@RequestMapping("testSendMessage")
	public JSONObject testSendMessage(MsgSettingInfo settingInfo,String messageTestPhone){
		try {
			manager.alarmSetting(settingInfo,MsgSettingManager.SETTING_TYPE_MESSAGE);
			List<String> phone = new ArrayList<String>();
			phone.add(messageTestPhone);
			boolean result = MessageSendHelper.sendMessage(phone, "STMCenter 短信告警信息设置测试").isSuccess();
			return toSuccess(result);
		} catch (Exception e) {
			return toJsonObject(550, e.getMessage());
		}
	}
	
	

	@RequestMapping("saveAlarmNotice")
	public JSONObject saveAlarmNotice(SmsOrEmailNotifyTemplate template){
		
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		template.setUpdateTime(new Date());
		template.setProvider(getLoginUser().getId());
		if(template.getTemplateID()!=0){//修改
			SmsOrEmailNotifyTemplate temp= new SmsOrEmailNotifyTemplate();
			temp.setTemplateID(template.getTemplateID());
			
			List<SmsOrEmailNotifyTemplate> list= alarmNotifyTemplateService.findNotifyTemplateByCondition(temp);
			if(list.size()!=0){
				template.setDefaultTemplate(list.get(0).isDefaultTemplate());
			
				return toSuccess(alarmNotifyTemplateService.updateNotifyTemplate(template));
			}
			
		}else{
			template.setDefaultTemplate(false);
			return toSuccess(alarmNotifyTemplateService.addNotifyTemplate(template));
		}
		return null;
	}
	@RequestMapping("getAll")
	public JSONObject getAll(){
		List<SmsOrEmailNotifyTemplate>
		list= alarmNotifyTemplateService.findNotifyTemplateByCondition(new SmsOrEmailNotifyTemplate());
		List<SmsOrEmailNotifyTemplate> templates= new ArrayList<SmsOrEmailNotifyTemplate>();
	if(list.size()!=0){
		for (int i=list.size()-1;i>=0;i--) {
			SmsOrEmailNotifyTemplate sms= list.get(i);
			templates.add(sms);
		}
	}
		return toSuccess(templates);
	}
	@RequestMapping("getAllById")
	public JSONObject getAllById(Long id){
		SmsOrEmailNotifyTemplate template = new SmsOrEmailNotifyTemplate();
		
		template.setTemplateID(id);
		List<SmsOrEmailNotifyTemplate> list= alarmNotifyTemplateService.findNotifyTemplateByCondition(template);
		if(list.size()!=0){
			return toSuccess(list.get(0));
		}
		return null;
		
	}
	@RequestMapping("delById")
	public JSONObject delById(Long id,String sysName){
		List<Long> ids= new ArrayList<Long>();
		ids.add(id);
		if(sysName.equals("MONITOR")){
			return toSuccess(alarmNotifyTemplateService.deleteNotifyTemplate(ids,SysModuleEnum.MONITOR));
		}else{
			return toSuccess(alarmNotifyTemplateService.deleteNotifyTemplate(ids,SysModuleEnum.BUSSINESS));
		}
		
	}
	@RequestMapping("getParamters")
	public JSONObject getParamters(){
		SmsOrEmailNotifyTemplateEnum[] data=		alarmNotifyTemplateService.findNotifyTemplateParameters();
		 JSONObject jsonObject = new JSONObject();
		if(data.length!=0){
		for (SmsOrEmailNotifyTemplateEnum alarmNotifyTemplateEnum : data) {
			jsonObject.put(alarmNotifyTemplateEnum.getKey(), alarmNotifyTemplateEnum.getValue());
		}
	}
		return toSuccess(jsonObject);
	}
	@RequestMapping("resetDefaultNotifyTemplate")
	public JSONObject resetDefaultNotifyTemplate(String type,String sysName){
		SmsOrEmailNotifyTemplate template= new SmsOrEmailNotifyTemplate();
		if(type.equals("sms")){//短信
			if(sysName.equals("MONITOR")){
				template=alarmNotifyTemplateService.resetDefaultNotifyTemplate(NotifyTypeEnum.sms,SysModuleEnum.MONITOR);
			}else{
				template=alarmNotifyTemplateService.resetDefaultNotifyTemplate(NotifyTypeEnum.sms,SysModuleEnum.BUSSINESS);
			}
			
		}else{//邮件
			if(sysName.equals("MONITOR")){
			template=	alarmNotifyTemplateService.resetDefaultNotifyTemplate(NotifyTypeEnum.email,SysModuleEnum.MONITOR);
			}else{
				template=	alarmNotifyTemplateService.resetDefaultNotifyTemplate(NotifyTypeEnum.email,SysModuleEnum.BUSSINESS);	
			}
		}
	
		return toSuccess(template);
	}
	
	@RequestMapping("getConfig")
	public JSONObject getConfig(HttpServletRequest request,HttpServletResponse response) throws Exception{
	Properties pp=PropertiesFileUtil.getProperties("properties/stm.properties");
		
	String notifyName=pp.getProperty("stm.notifyName");
	String configContent=notifyName;
		return toSuccess(configContent);
	}
	@RequestMapping("getSelector")
	public JSONObject getSelector(long type,long profileNameType) throws Exception{
		SmsOrEmailNotifyTemplate template = new SmsOrEmailNotifyTemplate();
		if(type==1){//sms
			template.setTemplateType(NotifyTypeEnum.sms);
		}else if(type==2){//email
			template.setTemplateType(NotifyTypeEnum.email);
		}
		if(profileNameType==3){//biz
			template.setSysModuleEnum(SysModuleEnum.BUSSINESS);
		}else if(profileNameType==2){
			template.setSysModuleEnum(SysModuleEnum.MONITOR);
		}
	
		List<SmsOrEmailNotifyTemplate>
		list= alarmNotifyTemplateService.findNotifyTemplateByCondition(template);
		List<AlarmSelectorVo> vos = new ArrayList<AlarmSelectorVo>();
		
		if(list.size()!=0){
			for (int i=list.size()-1;i>=0;i--) {
				AlarmSelectorVo selectorVo = new AlarmSelectorVo();
				selectorVo.setId(list.get(i).getTemplateID());
				selectorVo.setName(list.get(i).getTemplateName());
				selectorVo.setAlarmDefalut(list.get(i).isDefaultTemplate());
				selectorVo.setSysModuleEnum(list.get(i).getSysModuleEnum().name());
				vos.add(selectorVo);
			}
		}
		return toSuccess(vos);
	}
	@RequestMapping("checkNotifyTemplateEnabled")
	public JSONObject checkNotifyTemplateEnabled(Long id,String type) throws Exception{
	boolean bl=false;
		if(type.equals("sms")){
			bl=	alarmNotifyTemplateService.checkNotifyTemplateEnabled(id, NotifyTypeEnum.sms);
		}else{
		bl=alarmNotifyTemplateService.checkNotifyTemplateEnabled(id, NotifyTypeEnum.email);
		}
		return toSuccess(bl);
	}
	//checkNotifyTemplateEnabled
	
}
