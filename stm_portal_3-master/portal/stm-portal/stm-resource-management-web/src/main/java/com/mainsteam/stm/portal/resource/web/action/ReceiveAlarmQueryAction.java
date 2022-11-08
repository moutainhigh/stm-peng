package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.IAlarmProfileQueryApi;
import com.mainsteam.stm.portal.resource.web.vo.AlarmRuleContentVo;
import com.mainsteam.stm.portal.resource.web.vo.AlarmRulePageVo;
import com.mainsteam.stm.portal.resource.web.vo.AlarmRuleSetVo;
import com.mainsteam.stm.portal.resource.web.vo.AlarmRuleVo;
import com.mainsteam.stm.portal.resource.web.vo.ReceiveAlarmQueryPageVo;
import com.mainsteam.stm.portal.resource.web.vo.ReceiveAlarmQueryVo;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmConditonEnableInfo;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmLevelEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmNotifyPeriodForDay;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmNotifyPeriodForWeek;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;
import com.mainsteam.stm.profilelib.alarm.obj.ContinusUnitEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
/**
 * <li>文件名称: ReceiveAlarmQueryAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 发现资源相关的操作</li>
 * <li>其他说明:</li>
 * 
 * @version ms.stm
 * @since 2019年8月20日
 * @author tpl
 */
@Controller
@RequestMapping("/portal/resource/rceceiveAlarmQuery")
public class ReceiveAlarmQueryAction extends BaseAction {
	private static Logger logger = Logger.getLogger(ReceiveAlarmQueryAction.class);
	
	@Resource
	private IAlarmProfileQueryApi alarmProfileQueryApi;
	@Resource
	private IDomainApi domainApi;
	@Resource
    private ResourceInstanceService resourceInstanceService;
	@Resource
	private IUserApi stm_system_userApi;
	@Resource
	private ProfileService profileService;
	
	/**
	 * 获取用户,用于告警规则添加
	 * 
	 * @return
	 */
	@RequestMapping(value="/getUser", method=RequestMethod.POST)
	public JSONObject userPage(Page<User, User> pageUser,Long[] domainId,int domainType){
		switch (domainType) {
		case 0://默认策略
			if(null!=domainId && domainId.length>0){
				List<UserRole> urList=null;
				for (Long id : domainId) {
				 urList = domainApi.getUserRolesByDomainId(id);
				}
				List<User> list = domainApi.queryUserByDomains(domainId);
				if(urList!=null){
					for (User user : list) {
						for (UserRole role : urList) {
							if(user.getId()!=null && user.getId().longValue()==role.getUserId().longValue()){
								user.setRoleName(role.getRoleName());
							}
						}
					}	
				}
			
				
				pageUser.setDatas(list);
			}else{
				return toSuccess(pageUser);
			}
			
			break;
		case 1://自定义策略,可以选择策略所属域下的用户
			if(null!=domainId && domainId.length>0){
				getUserRolesByDomainId(domainId[0],pageUser);
			}else{
				return toSuccess(pageUser);
			}
			
			break;
		case 2://个性化策略,可以选择所有用户,此时domainId为instanceId
			if(null!=domainId && domainId.length>0){
				try {
					ResourceInstance ri = resourceInstanceService.getResourceInstance(domainId[0]);
					
					if(null!=ri){
						getUserRolesByDomainId(ri.getDomainId(),pageUser);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				return toSuccess(pageUser);
			}
			
			break;
		case 3://其他模块调用,无权限限制,返回所有用户
			alarmProfileQueryApi.getUser(pageUser);
			return toSuccess(pageUser);
			
		case 4://其他模块调用,也根据域过滤告警接收人
			if(null!=domainId && domainId.length>0){
				getUserRolesByDomainIds(domainId,pageUser);
			}else{
				return toSuccess(pageUser);
			}
			
		}
		List<User> listUserADMIN =stm_system_userApi.getUsersByType(UserConstants.USER_TYPE_SYSTEM_ADMIN);
		List<User> listUser = pageUser.getDatas();
		List<User> listUserAdd = new ArrayList<User>();
		if(null==listUser || listUser.size()==0){
			listUserAdd = listUserADMIN;
		}else{
			for(User userADMIN:listUserADMIN){
				boolean flag = true;
				for(User userIn:listUser){
					if(null!=userIn.getId() && userADMIN.getId().longValue()==userIn.getId().longValue()){
						flag = false;
						break;
					}
				}
				if(flag){
					listUserAdd.add(userADMIN);
				}
			}
			for(User user:listUser){
				if(user.getStatus()==1){
					listUserAdd.add(user);
				}
			}
		}
		
		pageUser.setDatas(listUserAdd);
		
		return toSuccess(pageUser);
	}
	
	/**
	 * 根据instanceid获取domainid
	 * 
	 * @return
	 */
	@RequestMapping("/getDomainIdByinstanceId")
	public JSONObject getDomainIdByinstanceId(Long[] instanceIds){
		List<Long> lonList = new ArrayList<Long>();
		for(Long instanceId:instanceIds){
			lonList.add(instanceId);
		}
		
		try {
			List<ResourceInstance> riList = resourceInstanceService.getResourceInstances(lonList);
			Set<Long> riSet = new HashSet<Long>(); 
			for(ResourceInstance ri:riList){
				riSet.add(ri.getDomainId());
			}
			return toSuccess(riSet);
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 策略操作资源时,减少的域ID,根据减少的域ID删除,配置在策略上的告警接收人
	 * 
	 * @return
	 */
	@RequestMapping("/filterUserByResourceDomainIdArr")
	public void filterUserByResourceDomainIdArr(Long[] domainIdArr,Long profileId){
		
		alarmProfileQueryApi.filterUserByResourceDomainIdArr(domainIdArr, profileId);
		
	}
	
	private Page<User, User> getUserRolesByDomainId(Long domainId,Page<User, User> page){
//		List<UserRole> userRoleList = new ArrayList<UserRole>();
		List<User> userList = new ArrayList<User>();
		
		List<UserRole> urList = domainApi.getUserRolesByDomainId(domainId);
		
		Set<Long> urSet = new HashSet<Long>();
		
		for(UserRole ur:urList){
			urSet.add(ur.getUserId());
		}
		for(Long urs:urSet){
			for(UserRole ur:urList){
				if(ur.getUserId().longValue()==urs.longValue()){
//					userRoleList.add(ur);
					
					User user = new User();
					user.setId(ur.getUserId());
					user.setName(ur.getUserName());
					user.setAccount(ur.getUserAccount());
					if(ur.getUserStatus()!=null){
						user.setStatus(Integer.parseInt(ur.getUserStatus()));
					}
					user.setRoleName(ur.getRoleName());
					userList.add(user);
					break;
				}
			}
		}
		
		page.setTotalRecord(userList.size());
		page.setDatas(userList);
//		int startRow = (int)page.getStartRow();
//		int rowCount = (int)page.getRowCount();
		
		//分页
//		if(rowCount>userList.size()){
//			page.setDatas(userList);
//		}else if((startRow+1)*rowCount>userList.size()){
//			List<User> dataList =userList.subList(startRow*rowCount, userList.size());
//			page.setDatas(dataList);
//		}else{
//			List<User> dataList =userList.subList(startRow*rowCount, (startRow+1)*rowCount);
//			page.setDatas(dataList);
//		}
		return page;
	}
	
	private Page<User, User> getUserRolesByDomainIds(Long[] domainIds,Page<User, User> page){
		List<User> userList = domainApi.queryUserByDomains(domainIds);//new ArrayList<User>();
		
		if(null!=userList && userList.size()>0){
			page.setTotalRecord(userList.size());
			page.setDatas(userList);
		}
		
		return page;
	}
	/**
	 * 获取父资源模型
	 * 
	 * @return
	 */
	@RequestMapping("/getParentProfile")
	public JSONObject getParentProfile(){
		
		JSONObject js = toSuccess(alarmProfileQueryApi.getParentCategory());
		
		return js;
	}
	
	/**
	 * 获取子资源模型
	 * 
	 * @return
	 */
	@RequestMapping("/getChildProfile")
	public JSONObject getChildProfile(String profileID){
		
		JSONObject js =toSuccess(alarmProfileQueryApi.getChildCategory(profileID));
		
		return js;
	}
	
	/**
	 * 用户策略查询
	 * 
	 * @return
	 */
	@RequestMapping("/userProfileQuery")
	public JSONObject userProfileQuery(ReceiveAlarmQueryPageVo receiveAlarmQueryPageVo){
		//获取所有告警规则(包含userID,profileID(根据页面选择,过滤profileID)),分离出所有userID
		//根据VO,取得当前页要展示的userID,根据这些ID查出profileID,查出profile信息
		String parentResourceId = null;
		String childResourceId = null;
		if(null!=receiveAlarmQueryPageVo.getParentProfileID()){
			parentResourceId =receiveAlarmQueryPageVo.getParentProfileID();
		}
        if(null!=receiveAlarmQueryPageVo.getChildProfileID()){
        	childResourceId=receiveAlarmQueryPageVo.getChildProfileID();
		}
		
      //获取所有告警规则
  		List<AlarmRule> alarmRuleList = alarmProfileQueryApi.getAllAlarmRules("model_profile");
  		Map<String,List<String>> userProfile = new HashMap<String,List<String>>();
        List<ProfileInfo> pfiAll = alarmProfileQueryApi.getProfileInfoByChoseResource(parentResourceId, childResourceId);
		
        List<ProfileInfo> pfiUsed = new ArrayList<ProfileInfo>();
        for(ProfileInfo pfi:pfiAll){
        	if(ifProfileUseByInstance(pfi.getProfileId())){
        		pfiUsed.add(pfi);
        	}
        }
        //将告警规则转化为以userID为key,profileName为值的map
		for(AlarmRule alarmRule:alarmRuleList){
			if(userProfile.containsKey(alarmRule.getUserId())){
				List<String> profileNameList = userProfile.get(alarmRule.getUserId());
				//根据profileId查询
				for(ProfileInfo pfi:pfiUsed){
					if(pfi.getProfileId() == alarmRule.getProfileId()){
						
						profileNameList.add(pfi.getProfileName());
					}
				}
			}else{
				List<String> profileNameList = new ArrayList<String>();
				//根据profileId查询
				for(ProfileInfo pfi:pfiUsed){
					if(pfi.getProfileId() == alarmRule.getProfileId()){
						profileNameList.add(pfi.getProfileName());
					}
				}
				if(profileNameList.size()>0){
					userProfile.put(alarmRule.getUserId(), profileNameList);
				}
			}
		}
		
		//分页
		long position = 0;
		long endRow = receiveAlarmQueryPageVo.getStartRow()+receiveAlarmQueryPageVo.getRowCount();
		List<ReceiveAlarmQueryVo> receiveAQs = new ArrayList<ReceiveAlarmQueryVo>();
		for(String userPro:userProfile.keySet()){
			if(position > (receiveAlarmQueryPageVo.getStartRow()-1) && position<endRow){
				List<String> profileIdList = userProfile.get(userPro);
				StringBuffer strBuff = new StringBuffer();
				for(int i=0;i<profileIdList.size();i++){
					String proName = profileIdList.get(i);
					if(i==0){
						strBuff.append("[ "+proName+" ]");
					}else if(i==4){
						strBuff.append(",[点击查看更多]");
						break;
					}else{
						strBuff.append(" , [ "+proName+" ]");
					}
					
				}
				
				ReceiveAlarmQueryVo raq = new ReceiveAlarmQueryVo();
				//raq.setProfileID(userProfile.get(userPro).);
				raq.setUserID(Long.parseLong(userPro));
				//根据userID查询
				User user = alarmProfileQueryApi.getUserById(new Long(userPro));
				String userName = "";
				if(null == user){
					userName = userPro;
				}else{
					userName = user.getName();
				}
				
				raq.setProfileNameArray(strBuff.toString());
				raq.setUserName(userName);
				receiveAQs.add(raq);
			}
			position += 1;
		}
		
		ReceiveAlarmQueryPageVo reAlVo = new ReceiveAlarmQueryPageVo();
		reAlVo.setTotalRecord(userProfile.size());
		reAlVo.setReceiveAQs(receiveAQs);
		
		return toSuccess(reAlVo);
	}
	
	private boolean ifProfileUseByInstance(long profileId){
		List<Long> instanceIds = alarmProfileQueryApi.getProfileResourceInstance(profileId);
		if(null!=instanceIds && instanceIds.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 根据用户ID获取策略
	 * 
	 * @return
	 */
	@RequestMapping("/getProfileByUserID")
	public JSONObject getProfileByUserID(ReceiveAlarmQueryPageVo receiveAlarmQueryPageVo){
		
		String parentResourceId = null;
		String childResourceId = null;
		if(null!=receiveAlarmQueryPageVo.getParentProfileID()){
			parentResourceId =receiveAlarmQueryPageVo.getParentProfileID();
		}
        if(null!=receiveAlarmQueryPageVo.getChildProfileID()){
        	childResourceId=receiveAlarmQueryPageVo.getChildProfileID();
		}
        String userId = String.valueOf(receiveAlarmQueryPageVo.getUserID());
      //获取所有告警规则
  		List<AlarmRule> alarmRuleList = alarmProfileQueryApi.getAlarmRulesByUserId(userId,"model_profile"); 
        List<ProfileInfo> pfiAll = alarmProfileQueryApi.getProfileInfoByChoseResource(parentResourceId, childResourceId);
		
        List<ProfileInfo> pfiUser = new ArrayList<ProfileInfo>();
		for(AlarmRule alarmRule:alarmRuleList){
			//获取user的profileInfo
			for(ProfileInfo pfi:pfiAll){
				if(pfi.getProfileId() == alarmRule.getProfileId()){
					pfiUser.add(pfi);
				}
			}
		}
        
		long position = 0;
		long endRow = receiveAlarmQueryPageVo.getStartRow()+receiveAlarmQueryPageVo.getRowCount();
		List<ReceiveAlarmQueryVo> receiveAQs = new ArrayList<ReceiveAlarmQueryVo>();
		for(ProfileInfo userPro:pfiUser){
			if(position > (receiveAlarmQueryPageVo.getStartRow()-1) && position<endRow){
				
				ReceiveAlarmQueryVo raq = new ReceiveAlarmQueryVo();
				raq.setProfileNameArray(userPro.getProfileName());
				raq.setProfileID(userPro.getProfileId());
				
				receiveAQs.add(raq);
			}
			position += 1;
		}
		
		ReceiveAlarmQueryPageVo reAlVo = new ReceiveAlarmQueryPageVo();
		reAlVo.setTotalRecord(pfiUser.size());
		reAlVo.setReceiveAQs(receiveAQs);
		
		return toSuccess(reAlVo);
	}
	
	/**
	 * 告警规则查询
	 * 
	 * @return
	 */
	@RequestMapping("/profileAlarmRules")
	public JSONObject profileAlarmRules(AlarmRulePageVo alarmRulePageVo){
		
		long profileId = alarmRulePageVo.getProfileId();
		String profileType = alarmRulePageVo.getProfileType();
		Long[] domainIdArr = alarmRulePageVo.getDomainIdArr();
		
		
		List<AlarmRule> alarmRuList = alarmProfileQueryApi.getAlarmRulesByProfileId(profileId,profileType);
		List<User> userList;
		List<User> listUserADMIN ;
		//queryType  partQuery区别本模块调用, allQuery其他模块调用
		if("partQuery".equals(alarmRulePageVo.getQueryType()) && alarmRulePageVo.getDomainIdArr().length>0){
			//个性化策略特殊处理
			if(alarmRulePageVo.getProfileNameType()==2){
				long domainID;
				try {
					domainID = resourceInstanceService.getResourceInstance(alarmRulePageVo.getDomainIdArr()[0]).getDomainId();
					domainIdArr = new Long[]{domainID};
				} catch (InstancelibException e) {
					logger.error(e.getMessage(),e);
				}
			}
			
			listUserADMIN =stm_system_userApi.getUsersByType(UserConstants.USER_TYPE_SYSTEM_ADMIN);
			userList = domainApi.queryUserByDomains(domainIdArr);
		}else{
			listUserADMIN = new ArrayList<User>();
			userList = new ArrayList<User>();
		}
		
		List<AlarmRuleVo> alarmRuVoList = new ArrayList<AlarmRuleVo>();
		
		//分页
//		long position = 0;
//		long endRow = alarmRulePageVo.getStartRow()+alarmRulePageVo.getRowCount();
		for(AlarmRule ar:alarmRuList){
//			if(position > (alarmRulePageVo.getStartRow()-1) && position<endRow){
				AlarmRuleVo arv = new AlarmRuleVo();
				arv.setAlarmRulesID(ar.getId());
				arv.setProfileID(ar.getProfileId());
				arv.setUserID(ar.getUserId());
				
				//根据userID查询
				User user = alarmProfileQueryApi.getUserById(new Long(ar.getUserId()));
				String userName = "";
				if(null == user){
					userName = null;
				}else{
					userName = user.getName();
				}
				arv.setUserName(userName);
				
				boolean flag = false;
				if(!"partQuery".equals(alarmRulePageVo.getQueryType())){
					flag = true;
				}else{
					Long userId = new Long(ar.getUserId());
					//策略下有资源的时候系统管理员可编辑,策略下无资源,系统管理员不能编辑
					if(domainIdArr.length==0){
						//策略下无资源
						flag = false;
					}else{
						for(User userADMIN : listUserADMIN){
							if(userId.longValue() == userADMIN.getId().longValue()){
								flag = true;
								break;
							}
						}
						if(!flag){
							for(User userIn : userList){
								if(null!=userIn.getId() && userId.longValue() == userIn.getId().longValue()){
									flag = true;
									break;
								}
							}
						}
					}
				}
				
				if(null != ar.getSendConditions()){
					for(AlarmSendCondition asc:ar.getSendConditions()){
						if(asc.getSendWay() == SendWayEnum.sms){
							arv.setMessEnable(asc.isEnabled());
							arv.setHaveMess(true);
							List<AlarmLevelEnum> aleList = asc.getAlarmLevels();
							if(null==aleList || aleList.size()==0){
								arv.setMessEnableSetContent(true);
							}
						}else if(asc.getSendWay() == SendWayEnum.email){
							arv.setEmailEnable(asc.isEnabled());
							arv.setHaveEmail(true);
							List<AlarmLevelEnum> aleList = asc.getAlarmLevels();
							if(null==aleList || aleList.size()==0){
								arv.setEmailEnableSetContent(true);
							}
						}
						else if(asc.getSendWay() == SendWayEnum.alert){
							arv.setAlertEnable(asc.isEnabled());
							arv.setHaveAlert(true);
							List<AlarmLevelEnum> aleList = asc.getAlarmLevels();
							if(null==aleList || aleList.size()==0){
								arv.setAlertEnableSetContent(true);
							}
						}
					}
					arv.setOperation(flag);
				}else{
					arv.setMessEnable(false);
					arv.setEmailEnable(false);
					arv.setAlertEnable(false);
					
					arv.setHaveMess(false);
					arv.setHaveEmail(false);
					arv.setHaveAlert(false);
					
					arv.setOperation(flag);
				}
				
				//检查告警规则是否含有alarmRuleCondition
//				if(null != alarmProfileQueryApi.getAlarmSendCondition("message", ar.getId())){
//					arv.setHaveMess(true);
//				}else{
//					arv.setHaveMess(false);
//				}
//				if(null != alarmProfileQueryApi.getAlarmSendCondition("email", ar.getId())){
//					arv.setHaveEmail(true);
//				}else{
//					arv.setHaveEmail(false);
//				}
//				if(null != alarmProfileQueryApi.getAlarmSendCondition("alert", ar.getId())){
//					arv.setHaveAlert(true);
//				}else{
//					arv.setHaveAlert(false);
//				}
				if(arv.getUserName()!=null){
					alarmRuVoList.add(arv);
				}
//			}
//			position += 1;
		}
		
		AlarmRulePageVo arp = new AlarmRulePageVo();
		arp.setTotalRecord(alarmRuList.size());
		arp.setAlarmRules(alarmRuVoList);
		
		return toSuccess(arp);
	}
	
	/**
	 * 根据ruleID,sendWay查询alarmSendCondition
	 * 
	 * @return
	 */
	@RequestMapping("/getAlarmSendCondition")
	public JSONObject getAlarmSendCondition(AlarmRuleSetVo alarmRuleSetVo){
		
		SendWayEnum swe = SendWayEnum.sms;
		switch (alarmRuleSetVo.getSendWay()) {
		case "message":
			swe = SendWayEnum.sms;
			break;
		case "email":
			swe = SendWayEnum.email;
			break;
		case "alert":
			swe = SendWayEnum.alert;
			break;
		}
		
		JSONObject js =toSuccess(alarmProfileQueryApi.getAlarmSendCondition(swe, alarmRuleSetVo.getAlarmRulesID()));
		
		return js;
	}
	/**
	 * 添加alarmSendCondition
	 * 
	 * @return
	 */
	@RequestMapping("/addAlarmCondition")
	public JSONObject addAlarmCondition(AlarmRuleSetVo alarmRuleSetVo){
		long ruleId = alarmRuleSetVo.getAlarmRulesID();
		SendWayEnum swe = SendWayEnum.sms;
		switch (alarmRuleSetVo.getSendWay()) {
		case "message":
			swe = SendWayEnum.sms;
			break;
		case "email":
			swe = SendWayEnum.email;
			break;
		case "alert":
			swe = SendWayEnum.alert;
			break;
		}
		
		//判断新增还是更新
		AlarmSendCondition asc = alarmProfileQueryApi.getAlarmSendCondition(swe, alarmRuleSetVo.getAlarmRulesID());
		if(null==asc){
			asc = new AlarmSendCondition();
			//add
			List<AlarmLevelEnum> listAle = new ArrayList<AlarmLevelEnum>();
			for(String alarmLevel:alarmRuleSetVo.getAlarmLevel()){
				switch(alarmLevel)
				{
				case "down":listAle.add(AlarmLevelEnum.down);break;
				case "metric_error":listAle.add(AlarmLevelEnum.metric_error);break;
				case "metric_warn":listAle.add(AlarmLevelEnum.metric_warn);break;
				case "metric_recover":listAle.add(AlarmLevelEnum.metric_recover);break;
				case "metric_unkwon":listAle.add(AlarmLevelEnum.metric_unkwon);break;
				case "perf_metric_recover":listAle.add(AlarmLevelEnum.perf_metric_recover);break;
				}
			}
			if("sendCondition".equals(alarmRuleSetVo.getSendCondition())){
				asc.setContinus(true);
				asc.setContinusCount(alarmRuleSetVo.getContinusCount());
				if("minute".equals(alarmRuleSetVo.getContinusCountUnit())){
					asc.setContinusCountUnit(ContinusUnitEnum.minute);
				}else{
					asc.setContinusCountUnit(ContinusUnitEnum.hour);
				}
			}else{
				asc.setContinus(false);
				if(0 < alarmRuleSetVo.getContinusCount()){
					asc.setContinusCount(alarmRuleSetVo.getContinusCount());
				}
				if(null != alarmRuleSetVo.getContinusCountUnit()){
					if("minute".equals(alarmRuleSetVo.getContinusCountUnit())){
					asc.setContinusCountUnit(ContinusUnitEnum.minute);
					}else{
						asc.setContinusCountUnit(ContinusUnitEnum.hour);
					}
				}
			}
			asc.setSendTimes(alarmRuleSetVo.getSendTimes());
			
			switch (alarmRuleSetVo.getAlarmSendTimeType()) {
			case "allTime":
				asc.setAllTime(true);
				break;
			case "everyDay":
				List<AlarmNotifyPeriodForDay> listDay = new ArrayList<AlarmNotifyPeriodForDay>();
				
				for(String afdd:alarmRuleSetVo.getAlarmForDayData()){
					AlarmNotifyPeriodForDay anpfd = new AlarmNotifyPeriodForDay();
					String[] alarmForDayDataStr = afdd.split("-");
					anpfd.setStart(Integer.valueOf(alarmForDayDataStr[1]));
					anpfd.setEnd(Integer.valueOf(alarmForDayDataStr[2]));
					
					listDay.add(anpfd);
				}
				if(null!=alarmRuleSetVo.getIfSendUnalarmTimeAlarm() ){
					asc.setSendIntime("checked".equals(alarmRuleSetVo.getIfSendUnalarmTimeAlarm()));
				}
				asc.setDayPeriodes(listDay);
				break;
			case "everyWeek":
				List<AlarmNotifyPeriodForWeek> listWeek = new ArrayList<AlarmNotifyPeriodForWeek>();
				
				for(String afwd:alarmRuleSetVo.getAlarmForWeekData()){
					AlarmNotifyPeriodForWeek anpfw = new AlarmNotifyPeriodForWeek();
					String[] alarmForWeekDataStr = afwd.split("-");
					anpfw.setDayOfWeek(Integer.valueOf(alarmForWeekDataStr[0]));
					anpfw.setStart(Integer.valueOf(alarmForWeekDataStr[1]));
					anpfw.setEnd(Integer.valueOf(alarmForWeekDataStr[2]));
					
					listWeek.add(anpfw);
				}
				if(null!=alarmRuleSetVo.getIfSendUnalarmTimeAlarm() ){
					asc.setSendIntime("checked".equals(alarmRuleSetVo.getIfSendUnalarmTimeAlarm()));
				}
				asc.setWeekPeriodes(listWeek);
				break;
			}
			
			asc.setSendWay(swe);
			asc.setAlarmLevels(listAle);
			if(null!=alarmRuleSetVo.getIsAlarm()){
				asc.setEnabled(alarmRuleSetVo.getIsAlarm().equals("checked"));
			}else{
				asc.setEnabled(false);
			}
			
			alarmProfileQueryApi.addAlarmCondition(asc, ruleId);
		}else if(ruleId>0){
			
			List<AlarmLevelEnum> listAle = new ArrayList<AlarmLevelEnum>();
			for(String alarmLevel:alarmRuleSetVo.getAlarmLevel()){
				switch(alarmLevel)
				{
				case "down":listAle.add(AlarmLevelEnum.down);break;
				case "metric_error":listAle.add(AlarmLevelEnum.metric_error);break;
				case "metric_warn":listAle.add(AlarmLevelEnum.metric_warn);break;
				case "metric_recover":listAle.add(AlarmLevelEnum.metric_recover);break;
				case "metric_unkwon":listAle.add(AlarmLevelEnum.metric_unkwon);break;
				case "perf_metric_recover":listAle.add(AlarmLevelEnum.perf_metric_recover);break;
				}
			}
			if("sendCondition".equals(alarmRuleSetVo.getSendCondition())){
				asc.setContinus(true);
				asc.setContinusCount(alarmRuleSetVo.getContinusCount());
				if("minute".equals(alarmRuleSetVo.getContinusCountUnit())){
					asc.setContinusCountUnit(ContinusUnitEnum.minute);
				}else{
					asc.setContinusCountUnit(ContinusUnitEnum.hour);
				}
			}else{
				asc.setContinus(false);
				if(0 < alarmRuleSetVo.getContinusCount()){
					asc.setContinusCount(alarmRuleSetVo.getContinusCount());
				}
				if(null != alarmRuleSetVo.getContinusCountUnit()){
					if("minute".equals(alarmRuleSetVo.getContinusCountUnit())){
					asc.setContinusCountUnit(ContinusUnitEnum.minute);
					}else{
						asc.setContinusCountUnit(ContinusUnitEnum.hour);
					}
				}
			}
			asc.setSendTimes(alarmRuleSetVo.getSendTimes());
			switch (alarmRuleSetVo.getAlarmSendTimeType()) {
			case "allTime":
				asc.setAllTime(true);
				break;
			case "everyDay":
				List<AlarmNotifyPeriodForDay> listDay = new ArrayList<AlarmNotifyPeriodForDay>();
				
				for(String afdd:alarmRuleSetVo.getAlarmForDayData()){
					AlarmNotifyPeriodForDay anpfd = new AlarmNotifyPeriodForDay();
					String[] alarmForDayDataStr = afdd.split("-");
					anpfd.setStart(Integer.valueOf(alarmForDayDataStr[1]));
					anpfd.setEnd(Integer.valueOf(alarmForDayDataStr[2]));
					
					listDay.add(anpfd);
				}
				if(null!=alarmRuleSetVo.getIfSendUnalarmTimeAlarm() ){
					asc.setSendIntime("checked".equals(alarmRuleSetVo.getIfSendUnalarmTimeAlarm()));
				}
				asc.setAllTime(false);
				asc.setWeekPeriodes(null);
				asc.setDayPeriodes(listDay);
				break;
			case "everyWeek":
				List<AlarmNotifyPeriodForWeek> listWeek = new ArrayList<AlarmNotifyPeriodForWeek>();
				
				for(String afwd:alarmRuleSetVo.getAlarmForWeekData()){
					AlarmNotifyPeriodForWeek anpfw = new AlarmNotifyPeriodForWeek();
					String[] alarmForWeekDataStr = afwd.split("-");
					anpfw.setDayOfWeek(Integer.valueOf(alarmForWeekDataStr[0]));
					anpfw.setStart(Integer.valueOf(alarmForWeekDataStr[1]));
					anpfw.setEnd(Integer.valueOf(alarmForWeekDataStr[2]));
					
					listWeek.add(anpfw);
				}
				if(null!=alarmRuleSetVo.getIfSendUnalarmTimeAlarm() ){
					asc.setSendIntime("checked".equals(alarmRuleSetVo.getIfSendUnalarmTimeAlarm()));
				}
				asc.setAllTime(false);
				asc.setDayPeriodes(null);
				asc.setWeekPeriodes(listWeek);
				break;
			}
			
			asc.setSendWay(swe);
			asc.setAlarmLevels(listAle);
			if(null!=alarmRuleSetVo.getIsAlarm()){
				asc.setEnabled(alarmRuleSetVo.getIsAlarm().equals("checked"));
			}else{
				asc.setEnabled(false);
			}
			asc.setTemplateId(alarmRuleSetVo.getTemplateId());
			
			alarmProfileQueryApi.updateAlarmCondition(asc, ruleId);
		}
		
		return toSuccess("success");
	}
	
	/**
	 * 修改alarmSendCondition的sendWay enable
	 * 
	 * @return
	 */
	@RequestMapping("/enableAlarmCondition")
	public JSONObject enableAlarmCondition(AlarmRulePageVo alarmRulePageVo){
		
		List<AlarmConditonEnableInfo> aceiList = new ArrayList<AlarmConditonEnableInfo>();
		for(String messRul:alarmRulePageVo.getMessageRulList()){
			boolean enable = false;
			for(String mess:alarmRulePageVo.getEnableMessageRulList()){
			   if(mess.equals(messRul)){
				   enable = true;
			   }
		    }
			AlarmConditonEnableInfo acei = new AlarmConditonEnableInfo();
			acei.setRuleId(new Long(messRul));
			acei.setEnabled(enable);
			acei.setSendWay(SendWayEnum.sms);
			aceiList.add(acei);
		}
		for(String emailRul:alarmRulePageVo.getEmailRulList()){
			boolean enable = false;
			for(String emai:alarmRulePageVo.getEnableEmailRulList()){
				if(emai.equals(emailRul)){
					enable = true;
				}
			}
			AlarmConditonEnableInfo acei = new AlarmConditonEnableInfo();
			acei.setRuleId(new Long(emailRul));
			acei.setEnabled(enable);
			acei.setSendWay(SendWayEnum.email);
			aceiList.add(acei);
		}
		for(String alertRul:alarmRulePageVo.getAlertRulList()){
			boolean enable = false;
			for(String alai:alarmRulePageVo.getEnableAlertRulList()){
				if(alai.equals(alertRul)){
					enable = true;
				}
			}
			AlarmConditonEnableInfo acei = new AlarmConditonEnableInfo();
			acei.setRuleId(new Long(alertRul));
			acei.setEnabled(enable);
			acei.setSendWay(SendWayEnum.alert);
			aceiList.add(acei);
		}
		alarmProfileQueryApi.changeAlarmConditionEnabled(aceiList);
		
		return toSuccess("success");
	}
	
	/**
	 * 删除告警规则
	 * 
	 * @return
	 */
	@RequestMapping("/deleteAlarmRule")
	public JSONObject deleteAlarmRule(long[] ruleId){
		
		alarmProfileQueryApi.deleteAlarmRule(ruleId);
		
		return toSuccess("success");
	}
	
	/**
	 * 添加告警规则
	 * 
	 * @return
	 */
	@RequestMapping("/addAlarmRule")
	public JSONObject addAlarmRule(AlarmRuleVo alarmRuleVo){
		//AlarmRule ar = toModel(alarmRuleVo);
//		String[] userid = {"55501","199001","200501","201501","206501","206502","206502","201501","206503","208001","225501","287501","1001"};
//		Long[] proid = {new Long(429001),new Long(429002),new Long(430501),new Long(430502),new Long(431501),new Long(431502),new Long(431501),new Long(430501),new Long(429001),new Long(429002),new Long(430501),new Long(430502),new Long(431501)};
//		for(int i=0;i<userid.length;i++){
//			AlarmRule ar = new AlarmRule();
//			ar.setUserId(userid[i]);
//			ar.setProfileType(AlarmRuleProfileEnum.model_profile);
//			ar.setProfileId(proid[i]);
//			
//			alarmRuleService.addAlarmRule(ar);
//		}
		
		for(String userIds:alarmRuleVo.getUserIDs()){
			AlarmRule ar = new AlarmRule();
			ar.setUserId(userIds);
			ar.setProfileType(getAlarmRuleProfileEnum(alarmRuleVo.getProfileType()));
			ar.setProfileId(alarmRuleVo.getProfileID());
			
			alarmProfileQueryApi.addAlarmRule(ar);
		}
		
		return toSuccess("success");
	}
	
	/**
	 * 添加告警规则
	 * 
	 * @return
	 */
	@RequestMapping("/addAlarmRuleContent")
	public JSONObject addAlarmRuleContent(AlarmRuleContentVo alarmRuleContentVo){
		
		//save alarmRule
		List<Long> rulesId = new ArrayList<Long>();
		List<AlarmRule> alarmRuList = alarmProfileQueryApi.getAlarmRulesByProfileId(alarmRuleContentVo.getProfileTypeId(),alarmRuleContentVo.getProfileType());
		
		for(String userIds:alarmRuleContentVo.getReceiveUser()){
			//去除重复添加
			boolean flag = true;
			for(AlarmRule arule:alarmRuList){
				if(arule.getUserId().equals(userIds)){
					flag = false;
					break;
				}
			}
			if(flag){
				AlarmRule ar = new AlarmRule();
				ar.setUserId(userIds);
				ar.setProfileType(getAlarmRuleProfileEnum(alarmRuleContentVo.getProfileType()));
				ar.setProfileId(alarmRuleContentVo.getProfileTypeId());
				alarmProfileQueryApi.addAlarmRule(ar);
				rulesId.add(ar.getId());
			}
		}
		
		boolean isSmsAlarm_SMS = "checked".equals(alarmRuleContentVo.getIsSmsAlarm_SMS());
		boolean isSmsAlarm_EMAIL = "checked".equals(alarmRuleContentVo.getIsSmsAlarm_EMAIL());
		boolean isSmsAlarm_ALERT = "checked".equals(alarmRuleContentVo.getIsSmsAlarm_ALERT());
		boolean sendTimeSet_SMS = "checked".equals(alarmRuleContentVo.getSendTimeSet_SMS());
		boolean sendTimeSet_EMAIL = "checked".equals(alarmRuleContentVo.getSendTimeSet_EMAIL());
		boolean sendTimeSet_ALERT = "checked".equals(alarmRuleContentVo.getSendTimeSet_ALERT());
		
		for(Long id:rulesId){
//			//save 短信alarmCondition
//			saveAlarmCondition("sms",id,isSmsAlarm_SMS,alarmRuleContentVo.getAlarmLevel_SMS(),sendTimeSet_SMS,alarmRuleContentVo.getSendTimeNum_SMS(),alarmRuleContentVo.getSendTimeType_SMS(),alarmRuleContentVo.getAlarmSendTimeTypeSms(),alarmRuleContentVo.getAlarmForDayDataSms(),alarmRuleContentVo.getAlarmForWeekDataSms(),alarmRuleContentVo.getIfSendUnalarmTimeAlarmSms());
//			//save 邮件
//			saveAlarmCondition("email",id,isSmsAlarm_EMAIL,alarmRuleContentVo.getAlarmLevel_EMAIL(),sendTimeSet_EMAIL,alarmRuleContentVo.getSendTimeNum_EMAIL(),alarmRuleContentVo.getSendTimeType_EMAIL(),alarmRuleContentVo.getAlarmSendTimeTypeEmail(),alarmRuleContentVo.getAlarmForDayDataEmail(),alarmRuleContentVo.getAlarmForWeekDataEmail(),alarmRuleContentVo.getIfSendUnalarmTimeAlarmEmail());
//			//save alert
//			saveAlarmCondition("alert",id,isSmsAlarm_ALERT,alarmRuleContentVo.getAlarmLevel_ALERT(),sendTimeSet_ALERT,alarmRuleContentVo.getSendTimeNum_ALERT(),alarmRuleContentVo.getSendTimeType_ALERT(),alarmRuleContentVo.getAlarmSendTimeTypeAlert(),alarmRuleContentVo.getAlarmForDayDataAlert(),alarmRuleContentVo.getAlarmForWeekDataAlert(),alarmRuleContentVo.getIfSendUnalarmTimeAlarmAlert());
			saveAlarmCondition(id,alarmRuleContentVo);
		}
		return toSuccess("success");
	}
	
	private void saveAlarmCondition(Long ruleId,AlarmRuleContentVo alarmRuleContentVo){
		for(SendWayEnum swe:SendWayEnum.values()){
			boolean isAlarm = false;
			String[] alarmLevelList = null;
			boolean sendTimeSet = false;
			int sendTimeNum = 0;
			String sendTimeType = null;
			String alarmSendTimeType = null;
			String[] alarmForDayData = null;
			String[] alarmForWeekData = null;
			String ifSendUnalarmTimeAlarm = null;
			long templateId=0;
			int sendTimes = 0;
			
			boolean flag = false;
			switch(swe.name()){
			case "sms":
				isAlarm = "checked".equals(alarmRuleContentVo.getIsSmsAlarm_SMS());
				alarmLevelList = alarmRuleContentVo.getAlarmLevel_SMS();
				sendTimeSet = "checked".equals(alarmRuleContentVo.getSendTimeSet_SMS());
				sendTimeNum = alarmRuleContentVo.getSendTimeNum_SMS();
				sendTimeType = alarmRuleContentVo.getSendTimeType_SMS();
				alarmSendTimeType = alarmRuleContentVo.getAlarmSendTimeTypeSms();
				alarmForDayData = alarmRuleContentVo.getAlarmForDayDataSms();
				alarmForWeekData = alarmRuleContentVo.getAlarmForWeekDataSms();
				ifSendUnalarmTimeAlarm = alarmRuleContentVo.getIfSendUnalarmTimeAlarmSms();
				templateId=alarmRuleContentVo.getTemplateIdSms();
				sendTimes = alarmRuleContentVo.getSendTimes_SMS();
				flag = true;
				
				break;
			case "email":
				isAlarm = "checked".equals(alarmRuleContentVo.getIsSmsAlarm_EMAIL());
				alarmLevelList = alarmRuleContentVo.getAlarmLevel_EMAIL();
				sendTimeSet = "checked".equals(alarmRuleContentVo.getSendTimeSet_EMAIL());
				sendTimeNum = alarmRuleContentVo.getSendTimeNum_EMAIL();
				sendTimeType = alarmRuleContentVo.getSendTimeType_EMAIL();
				alarmSendTimeType = alarmRuleContentVo.getAlarmSendTimeTypeEmail();
				alarmForDayData = alarmRuleContentVo.getAlarmForDayDataEmail();
				alarmForWeekData = alarmRuleContentVo.getAlarmForWeekDataEmail();
				ifSendUnalarmTimeAlarm = alarmRuleContentVo.getIfSendUnalarmTimeAlarmEmail();
				templateId=alarmRuleContentVo.getTemplateIdEmail();
				sendTimes = alarmRuleContentVo.getSendTimes_EMAIL();
				flag = true;
				
				break;
			case "alert":
				isAlarm = "checked".equals(alarmRuleContentVo.getIsSmsAlarm_ALERT());
				alarmLevelList = alarmRuleContentVo.getAlarmLevel_ALERT();
				sendTimeSet = "checked".equals(alarmRuleContentVo.getSendTimeSet_ALERT());
				sendTimeNum = alarmRuleContentVo.getSendTimeNum_ALERT();
				sendTimeType = alarmRuleContentVo.getSendTimeType_ALERT();
				alarmSendTimeType = alarmRuleContentVo.getAlarmSendTimeTypeAlert();
				alarmForDayData = alarmRuleContentVo.getAlarmForDayDataAlert();
				alarmForWeekData = alarmRuleContentVo.getAlarmForWeekDataAlert();
				ifSendUnalarmTimeAlarm = alarmRuleContentVo.getIfSendUnalarmTimeAlarmAlert();
				templateId=alarmRuleContentVo.getTemplateIdAlert();
				flag = true;
				
				break;
			}
			if(flag){
				AlarmSendCondition asc = new AlarmSendCondition();
				//add
				List<AlarmLevelEnum> listAle = new ArrayList<AlarmLevelEnum>();
				for(String alarmLevel:alarmLevelList){
					switch(alarmLevel)
					{
					case "down":listAle.add(AlarmLevelEnum.down);break;
					case "metric_error":listAle.add(AlarmLevelEnum.metric_error);break;
					case "metric_warn":listAle.add(AlarmLevelEnum.metric_warn);break;
					case "metric_recover":listAle.add(AlarmLevelEnum.metric_recover);break;
					case "metric_unkwon":listAle.add(AlarmLevelEnum.metric_unkwon);break;
					case "perf_metric_recover":listAle.add(AlarmLevelEnum.perf_metric_recover);break;
					}
				}
				asc.setContinus(sendTimeSet);
				if(0 < sendTimeNum){
					asc.setContinusCount(sendTimeNum);
				}
				if("minute".equals(sendTimeType)){
					asc.setContinusCountUnit(ContinusUnitEnum.minute);
				}else{
					asc.setContinusCountUnit(ContinusUnitEnum.hour);
				}
				//设置最多发送次数，只有短信和邮件方式才有此参数 dfw 20170104
				if(sendTimes > 0){
					asc.setSendTimes(sendTimes);
				}
				asc.setSendWay(swe);
				asc.setAlarmLevels(listAle);
				asc.setEnabled(isAlarm);
				asc.setTemplateId(templateId);
				if(null!=ifSendUnalarmTimeAlarm ){
					asc.setSendIntime("checked".equals(ifSendUnalarmTimeAlarm));
				}
				
				
				switch (alarmSendTimeType) {
				case "allTime":
					asc.setAllTime(true);
					break;
				case "everyDay":
					List<AlarmNotifyPeriodForDay> listDay = new ArrayList<AlarmNotifyPeriodForDay>();
					
					for(String afdd:alarmForDayData){
						AlarmNotifyPeriodForDay anpfd = new AlarmNotifyPeriodForDay();
						String[] alarmForDayDataStr = afdd.split("-");
						anpfd.setStart(Integer.valueOf(alarmForDayDataStr[1]));
						anpfd.setEnd(Integer.valueOf(alarmForDayDataStr[2]));
						
						listDay.add(anpfd);
					}
					asc.setDayPeriodes(listDay);
					break;
				case "everyWeek":
					List<AlarmNotifyPeriodForWeek> listWeek = new ArrayList<AlarmNotifyPeriodForWeek>();
					
					for(String afwd:alarmForWeekData){
						AlarmNotifyPeriodForWeek anpfw = new AlarmNotifyPeriodForWeek();
						String[] alarmForWeekDataStr = afwd.split("-");
						anpfw.setDayOfWeek(Integer.valueOf(alarmForWeekDataStr[0]));
						anpfw.setStart(Integer.valueOf(alarmForWeekDataStr[1]));
						anpfw.setEnd(Integer.valueOf(alarmForWeekDataStr[2]));
						
						listWeek.add(anpfw);
					}
					asc.setWeekPeriodes(listWeek);
					break;
				}
				alarmProfileQueryApi.addAlarmCondition(asc, ruleId);
			}
		}
	}
	
//	private AlarmSendCondition saveAlarmCondition(String type,Long ruleId,boolean isSmsAlarm,String[] alarmLevelList,boolean sendTimeSet,
//			int sendTimeNum,String sendTimeType,String alarmSendTimeType,String[] alarmForDayData,String[] alarmForWeekData,String ifSendUnalarmTimeAlarm){
//		
//		
//		SendWayEnum swe = SendWayEnum.sms;
//		switch(type){
//			case "sms":
//				swe = SendWayEnum.sms;
//			break;
//			case "email":
//				swe = SendWayEnum.email;
//			break;
//			case "alert":
//				swe = SendWayEnum.alert;
//			break;
//		}
//		
//		AlarmSendCondition asc = new AlarmSendCondition();
//		//add
//		List<AlarmLevelEnum> listAle = new ArrayList<AlarmLevelEnum>();
//		for(String alarmLevel:alarmLevelList){
//			switch(alarmLevel)
//			{
//			case "down":listAle.add(AlarmLevelEnum.down);break;
//			case "metric_error":listAle.add(AlarmLevelEnum.metric_error);break;
//			case "metric_warn":listAle.add(AlarmLevelEnum.metric_warn);break;
//			case "metric_recover":listAle.add(AlarmLevelEnum.metric_recover);break;
//			case "metric_unkwon":listAle.add(AlarmLevelEnum.metric_unkwon);break;
//			}
//		}
//		asc.setContinus(sendTimeSet);
//		if(0 < sendTimeNum){
//			asc.setContinusCount(sendTimeNum);
//		}
//		if("minute".equals(sendTimeType)){
//			asc.setContinusCountUnit(ContinusUnitEnum.minute);
//		}else{
//			asc.setContinusCountUnit(ContinusUnitEnum.hour);
//		}
//		
//		asc.setSendWay(swe);
//		asc.setAlarmLevels(listAle);
//		asc.setEnabled(isSmsAlarm);
//		if(null!=ifSendUnalarmTimeAlarm ){
//			asc.setSendIntime("checked".equals(ifSendUnalarmTimeAlarm));
//		}
//		
//		
//		switch (alarmSendTimeType) {
//		case "allTime":
//			asc.setAllTime(true);
//			break;
//		case "everyDay":
//			List<AlarmNotifyPeriodForDay> listDay = new ArrayList<AlarmNotifyPeriodForDay>();
//			
//			for(String afdd:alarmForDayData){
//				AlarmNotifyPeriodForDay anpfd = new AlarmNotifyPeriodForDay();
//				String[] alarmForDayDataStr = afdd.split("-");
//				anpfd.setStart(Integer.valueOf(alarmForDayDataStr[1]));
//				anpfd.setEnd(Integer.valueOf(alarmForDayDataStr[2]));
//				
//				listDay.add(anpfd);
//			}
//			asc.setDayPeriodes(listDay);
//			break;
//		case "everyWeek":
//			List<AlarmNotifyPeriodForWeek> listWeek = new ArrayList<AlarmNotifyPeriodForWeek>();
//			
//			for(String afwd:alarmForWeekData){
//				AlarmNotifyPeriodForWeek anpfw = new AlarmNotifyPeriodForWeek();
//				String[] alarmForWeekDataStr = afwd.split("-");
//				anpfw.setDayOfWeek(Integer.valueOf(alarmForWeekDataStr[0]));
//				anpfw.setStart(Integer.valueOf(alarmForWeekDataStr[1]));
//				anpfw.setEnd(Integer.valueOf(alarmForWeekDataStr[2]));
//				
//				listWeek.add(anpfw);
//			}
//			asc.setWeekPeriodes(listWeek);
//			break;
//		}
//		
//		alarmProfileQueryApi.addAlarmCondition(asc, ruleId);
//		return asc;
//	}
	private AlarmRuleProfileEnum getAlarmRuleProfileEnum(String type){
		AlarmRuleProfileEnum arpf = AlarmRuleProfileEnum.model_profile ;
		for(AlarmRuleProfileEnum apf:AlarmRuleProfileEnum.values()){
			if(apf.name().equals(type)){
				arpf = apf;
				return arpf;
			}
		}
		return null;
	}
	
	/******************流量分析接口START*******************/
	private String jsonp(String args, String json){
		return args + "(" + json + ")";
	}
	
	@RequestMapping(value="/netflow/getUser")
	public String netflowGetUser(Page<User, User> pageUser,Long[] domainId,int domainType, String args){
		return jsonp(args,userPage(pageUser, domainId, domainType).toJSONString());
	}
	
	@RequestMapping("/netflow/getDomainIdByinstanceId")
	public String getDomainIdByinstanceId(Long[] instanceIds, String args){
		return jsonp(args,getDomainIdByinstanceId(instanceIds).toJSONString());
	}
	
	@RequestMapping("/netflow/getParentProfile")
	public String netflowGetParentProfile(String args){
		return jsonp(args, getParentProfile().toJSONString());
	}
	
	@RequestMapping("/netflow/getChildProfile")
	public String netflowGetChildProfile(String profileID, String args){
		return args + "(" + getChildProfile(profileID) + ")";
	}
	
	@RequestMapping("/netflow/userProfileQuery")
	public String userProfileQuery(ReceiveAlarmQueryPageVo receiveAlarmQueryPageVo, String args){
		return jsonp(args, userProfileQuery(receiveAlarmQueryPageVo).toJSONString());
	}
	
	@RequestMapping("/netflow/getProfileByUserID")
	public String getProfileByUserID(ReceiveAlarmQueryPageVo receiveAlarmQueryPageVo, String args){
		return jsonp(args, getProfileByUserID(receiveAlarmQueryPageVo).toJSONString());
	}
	
	@RequestMapping(value="/netflow/profileAlarmRules")
	public String netflowProfileAlarmRules(AlarmRulePageVo alarmRulePageVo, String args){
		return jsonp(args, profileAlarmRules(alarmRulePageVo).toJSONString());
	}
	
	@RequestMapping("/netflow/getAlarmSendCondition")
	public String getAlarmSendCondition(AlarmRuleSetVo alarmRuleSetVo, String args){
		return jsonp(args, getAlarmSendCondition(alarmRuleSetVo).toJSONString());
	}
	
	@RequestMapping("/netflow/addAlarmCondition")
	public String addAlarmCondition(AlarmRuleSetVo alarmRuleSetVo, String args){
		return jsonp(args, addAlarmCondition(alarmRuleSetVo).toJSONString());
	}
	
	@RequestMapping("/netflow/enableAlarmCondition")
	public String enableAlarmCondition(AlarmRulePageVo alarmRulePageVo, String args){
		return jsonp(args, enableAlarmCondition(alarmRulePageVo).toJSONString());
	}
	
	@RequestMapping("/netflow/deleteAlarmRule")
	public String deleteAlarmRule(long[] ruleId, String args){
		return jsonp(args, deleteAlarmRule(ruleId).toJSONString());
	}
	
	@RequestMapping("/netflow/addAlarmRule")
	public String addAlarmRule(AlarmRuleVo alarmRuleVo, String args){
		return jsonp(args,addAlarmRule(alarmRuleVo).toJSONString());
	}
	
	@RequestMapping("/netflow/addAlarmRuleContent")
	public String addAlarmRuleContent(AlarmRuleContentVo alarmRuleContentVo, String args){
		return jsonp(args,addAlarmRuleContent(alarmRuleContentVo).toJSONString());
	}
	
	/******************流量分析接口END*******************/
	/**
	 * 查告警规则信息
	 * @param profileId
	 * @param profileType
	 * @return
	 */
	@RequestMapping("/getAlarmRuleByProfileId")
	public JSONObject getAlarmRuleByProfileId(long profileId,String profileType){
		List<AlarmRule> rules= alarmProfileQueryApi.getAlarmRulesByProfileId(profileId, profileType);
		return toSuccess(rules);
		
	}
	@RequestMapping("/delAlarmRule")
	public JSONObject delAlarmRule(long profileId,String profileType){
		List<AlarmRule> rules= alarmProfileQueryApi.getAlarmRulesByProfileId(profileId, profileType);
		if(rules!=null){
			long[]  ids=  new long[rules.size()];
			for (int i=0;i<rules.size();i++) {
				ids[i]=rules.get(i).getId();
			}
			try {
				alarmProfileQueryApi.deleteAlarmRule(ids);
				return toSuccess(0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return toFailForGroupNameExsit(-1);
			}
		}else{
			return toFailForGroupNameExsit(-1);
		}
	
		
		
	}
}
