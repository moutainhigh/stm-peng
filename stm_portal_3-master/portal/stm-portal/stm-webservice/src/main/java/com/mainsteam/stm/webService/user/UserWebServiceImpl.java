package com.mainsteam.stm.webService.user;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.system.cmdb.api.ICmdbApi;
import com.mainsteam.stm.system.itsmuser.api.IItsmUserApi;
import com.mainsteam.stm.system.itsmuser.bo.ItsmSystemBo;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.CipherUtil;
import com.mainsteam.stm.webService.obj.ItsmUserBean;
import com.mainsteam.stm.webService.obj.Result;
import com.mainsteam.stm.webService.obj.ResultCodeEnum;
import com.mainsteam.stm.webService.obj.UserOperatetype;
import com.mainsteam.stm.webService.user.client.UserServiceApi;
import com.mainsteam.stm.webService.user.client.UserServiceImpl;
import com.mainsteam.stm.webService.user.client.WSException;
import com.mainsteam.stm.webService.util.URLAvailability;

public class UserWebServiceImpl implements UserWebService{

	private final static String ADD="add";
	private final static String DELETE="delete";
	private final static String CHANGE="change";
	private final static String CHGSTATUS="chgstatus";
	private final static String RESETPWD="resetpwd";
	
	private static final Logger logger=LoggerFactory.getLogger(UserWebServiceImpl.class);
	
	@Resource
	private IUserApi userApi;
	
	@Resource
	private ICmdbApi cmdbApi;
	
	@Resource
	private IItsmUserApi itsmUserApi;
	
	@Override
	public RequestInfo UpdateAppAcctSoap(RequestInfo requestInfo){
		UserInfo userInfo = requestInfo.getUerInfo();
		String OPERATORID = requestInfo.getOperatorId();
		String OPERATORPWD = requestInfo.getOperatorPwd();
//		String OPERATORIP = requestInfo.getOperatorIp();
		if(userInfo==null){
			requestInfo.setErrCode("");
			requestInfo.setErrDesc("??????????????????!");
			
			return requestInfo;
		}else if(OPERATORID==null||"".equals(OPERATORID)||OPERATORPWD==null||"".equals(OPERATORPWD)){
			requestInfo.setKey(userInfo.getLoginNo());
			requestInfo.setErrCode("");
			requestInfo.setErrDesc("????????????????????????!");
			
			return requestInfo;
		}
		
		//???operator??????????????????    operator??????????????????,operator?????????????????????,????????????
		String validateStr = validateOperator(OPERATORID,OPERATORPWD);
		if("pwdError".equals(validateStr)){
			requestInfo.setKey(userInfo.getLoginNo());
			requestInfo.setErrCode("");
			requestInfo.setErrDesc("???????????????????????????!");
			
			return requestInfo;
		}else if("noPermission".equals(validateStr)){
			requestInfo.setKey(userInfo.getLoginNo());
			requestInfo.setErrCode("");
			requestInfo.setErrDesc("operator?????????!");
			
			return requestInfo;
		}
		
		String MODIFYMODE = requestInfo.getModifyMode();
		switch (MODIFYMODE) {
		case ADD:
			User user = convertUserInfo(userInfo);
			Map<String,Object> userMap = validateUser(user);
			boolean flag = (boolean)userMap.get("flag");
			if(!flag){
				requestInfo.setKey(userInfo.getLoginNo());
				requestInfo.setErrCode("");
				requestInfo.setErrDesc((String)userMap.get("msg"));
				
				return requestInfo;
			}
			User operator = userApi.getByAccount(OPERATORID);
			user.setCreatorId(operator.getId());
			//??????????????????
			user.setUserType(1);
			if(add(user)){
				requestInfo.setUserId(String.valueOf(user.getId()));
				requestInfo.setLoginNo(user.getAccount());
				requestInfo.setRsp("success");
			}else{
				requestInfo.setKey(user.getAccount());
				requestInfo.setErrCode("");
				requestInfo.setErrDesc("?????????,????????????!!!");
			}
			
			break;
		case DELETE:
			String userDe = userInfo.getUserId();
			if(null==userDe||"".equals(userDe)){
				logger.debug("?????????????????? : userId is null");
				requestInfo.setErrCode("");
				requestInfo.setErrDesc("?????????????????? : userId is null");
			}
			Long userDeIn = new Long(userDe);
			User userDel = userApi.get(userDeIn);
			if(null!=userDel){
				if(userDel.getUserType()>2){
					requestInfo.setKey(userDel.getAccount());
					requestInfo.setErrCode("");
					requestInfo.setErrDesc("???????????????????????????,????????????!");
				}else{
					Long[] userIds = new Long[1];
					userIds[0] = userDeIn;
					if(delete(userIds)){
						requestInfo.setUserId(String.valueOf(userDel.getId()));
						requestInfo.setLoginNo(userDel.getAccount());
						requestInfo.setRsp("success");
					}else{
						requestInfo.setKey(userDe);
						requestInfo.setErrCode("");
						requestInfo.setErrDesc("?????????,????????????!!!");
					}
				}
				
			}else{
				requestInfo.setKey(userDe);
				requestInfo.setErrCode("");
				requestInfo.setErrDesc("???????????????!!!");
			}
			
			break;
		case CHGSTATUS:
		case CHANGE:
			String userCh = userInfo.getUserId();
			if(null==userCh||"".equals(userCh)){
				logger.debug("?????????????????? : userId is null");
				requestInfo.setErrCode("");
				requestInfo.setErrDesc("?????????????????? : userId is null");
			}
			
			Long userChIn = new Long(userCh);
			User userChange = userApi.get(userChIn);
			if(null!=userChange){
				if(userChange.getUserType()>2){
					requestInfo.setKey(userChange.getAccount());
					requestInfo.setErrCode("");
					requestInfo.setErrDesc("???????????????????????????,????????????!");
				}else{
					convertUserInfo(userChange,userInfo);
					if(update(userChange)){
						requestInfo.setUserId(String.valueOf(userChange.getId()));
						requestInfo.setLoginNo(userChange.getAccount());
						requestInfo.setRsp("success");
					}else{
						requestInfo.setKey(userChange.getAccount());
						requestInfo.setErrCode("");
						requestInfo.setErrDesc("?????????,????????????!!!");
					}
				}
				
			}else{
				requestInfo.setKey(userCh);
				requestInfo.setErrCode("");
				requestInfo.setErrDesc("???????????????!!!");
			}
			
			
			break;
		
		case RESETPWD:
			String userPwd = userInfo.getUserId();
			if(null==userPwd||"".equals(userPwd)){
				logger.debug("?????????????????? : userId is null");
				requestInfo.setErrCode("");
				requestInfo.setErrDesc("?????????????????? : userId is null");
			}
			
			Long userIdPwd = new Long(userPwd);
			User userPwdObj = userApi.get(userIdPwd);
			if(null!=userPwdObj){
				if(userPwdObj.getUserType()>2){
					requestInfo.setKey(userPwdObj.getAccount());
					requestInfo.setErrCode("");
					requestInfo.setErrDesc("???????????????????????????,????????????!");
				}else{
					convertUserInfo(userPwdObj,userInfo);
					String lodPwd = userInfo.getOldPassword();
					if(null!=lodPwd&&!"".equals(lodPwd)){
						userPwdObj.setPassword(lodPwd);
					}
					if(update(userPwdObj)){
						requestInfo.setUserId(String.valueOf(userPwdObj.getId()));
						requestInfo.setLoginNo(userPwdObj.getAccount());
						requestInfo.setRsp("success");
					}else{
						requestInfo.setKey(userPwdObj.getAccount());
						requestInfo.setErrCode("");
						requestInfo.setErrDesc("????????????,?????????,????????????!!!");
					}
				}
				
			}else{
				requestInfo.setKey(userPwd);
				requestInfo.setErrCode("");
				requestInfo.setErrDesc("???????????????!!!");
			}
			
			break;
		}
		return requestInfo;
	}
	
	private boolean userInterfaceCheckOper(UserAgent userAgent,Result result){
		boolean flag = true;
		String operId = userAgent.getOperatorId();
		String OperPwd = userAgent.getOperatorPwd();
		
		if(operId==null||"".equals(operId)||OperPwd==null||"".equals(OperPwd)){
			logger.debug("?????????????????? : ????????????????????????! user_account"+userAgent.getAccount());
			result.setErrorMsg("????????????????????????! user_account="+userAgent.getAccount());
			flag = false;
			return flag;
		}
		//???operator??????????????????    operator??????????????????,operator?????????????????????,????????????
		String validateStr = validateOperator(operId,OperPwd);
		if("pwdError".equals(validateStr)){
			logger.debug("?????????????????? : ???????????????????????????! user_account"+userAgent.getAccount());
			result.setErrorMsg("???????????????????????????!user_account="+userAgent.getAccount());
			flag = false;
			return flag;
		}else if("noPermission".equals(validateStr)){
			logger.debug("?????????????????? : operator?????????! user_account"+userAgent.getAccount());
			result.setErrorMsg("operator?????????!user_account="+userAgent.getAccount());
			flag = false;
			return flag;
		}
		return flag;
	}
	
	//??????????????????
	private boolean userInterfaceCheckUser(User user,Result result){
		boolean flag = true;
		if(user.getId()>0){
			if(user.getUserType()>2){
				logger.debug("?????????????????? : ???????????????????????????,????????????!  user_account="+user.getAccount());
				result.setErrorMsg("?????????????????? : ???????????????????????????,????????????!  user_account="+user.getAccount());
				flag = false;
				return flag;
			}
		}
		Map<String,Object> userMap = validateUser(user);
		boolean flagIn = (boolean)userMap.get("flag");
		if(!flagIn){
			logger.debug("?????????????????? : "+(String)userMap.get("msg")+" user_account="+user.getAccount());
			result.setErrorMsg("?????????????????? : "+(String)userMap.get("msg")+" user_account="+user.getAccount());
			flag = false;
			return flag;
		}
		return flag;
	}
	
	@Override
	public String addUser(UserAgent userAgent){
		//????????????
		Result result = new Result();
		if(!userInterfaceCheckOper(userAgent,result)){
			return JSONObject.toJSONString(result);
		}
		
		User user = convertUserAgent(userAgent);
		User operator = userApi.getByAccount(userAgent.getOperatorId());
		user.setCreatorId(operator.getId());
		
		Map<String,Object> userMap = validateUser(user);
		boolean flagIn = (boolean)userMap.get("flag");
		if(!flagIn){
			logger.debug("?????????????????? : "+(String)userMap.get("msg")+" user_account="+user.getAccount());
			result.setErrorMsg("?????????????????? : "+(String)userMap.get("msg")+" user_account="+user.getAccount());
			return JSONObject.toJSONString(result);
		}
		
		//??????????????????
		user.setUserType(1);
		if(!add(user)){
			logger.debug("?????????????????? : user_account"+user.getAccount());
			result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
			return JSONObject.toJSONString(result);
		}
			
		result.setResultcodeEnum(ResultCodeEnum.RESULT_SUCCESS_CODE);
		return JSONObject.toJSONString(result);
	}
	@Override
	public String deleteUser(UserAgent userAgent){
		//????????????
		Result result = new Result();
		String userId = userAgent.getUserId();
		if(null==userId||"".equals(userId)){
			logger.debug("?????????????????? : ??????id??????!");
			result.setErrorMsg("?????????????????? : ??????id??????!");
			return JSONObject.toJSONString(result);
		}
		
		if(!userInterfaceCheckOper(userAgent,result)){
			return JSONObject.toJSONString(result);
		}
		
		User userSys = userApi.get(new Long(userId));
		if(null!=userSys){
			if(userSys.getUserType()>2){
				logger.debug("?????????????????? : ???????????????????????????,????????????!  user_account="+userSys.getAccount());
				result.setErrorMsg("?????????????????? : ???????????????????????????,????????????!  user_account="+userSys.getAccount());
				
				return JSONObject.toJSONString(result);
			}
			
			Long[] userIds = new Long[1];
			userIds[0] = userSys.getId();
			if(!delete(userIds)){
				logger.debug("?????????????????? : user_id"+userId);
				result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
				return JSONObject.toJSONString(result);
			}
		}else{
			result.setErrorMsg("???????????????,????????????!  userId="+userId);
			return JSONObject.toJSONString(result);
		}
		
		result.setResultcodeEnum(ResultCodeEnum.RESULT_SUCCESS_CODE);
		return JSONObject.toJSONString(result);
	}
	@Override
	public String updateUser(UserAgent userAgent){
		//????????????
		Result result = new Result();
		String userId = userAgent.getUserId();
		if(null==userId||"".equals(userId)){
			logger.debug("?????????????????? : ??????id??????!");
			result.setErrorMsg("?????????????????? : ??????id??????!");
			return JSONObject.toJSONString(result);
		}
		
		if(!userInterfaceCheckOper(userAgent,result)){
			return JSONObject.toJSONString(result);
		}
		
		User userSys = userApi.get(new Long(userId));
		if(null!=userSys){
			if(userSys.getUserType()>2){
				logger.debug("?????????????????? : ???????????????????????????,????????????!  user_account="+userSys.getAccount());
				result.setErrorMsg("?????????????????? : ???????????????????????????,????????????!  user_account="+userSys.getAccount());
				
				return JSONObject.toJSONString(result);
			}
			//?????????,user account????????????
			convertUserAgent(userSys,userAgent);
			if(!update(userSys)){
				logger.debug("?????????????????? : user_id"+userId);
				result.setResultcodeEnum(ResultCodeEnum.RESULT_ERROR_CODE);
				return JSONObject.toJSONString(result);
			}
		}else{
			result.setErrorMsg("???????????????,????????????!  userId="+userId);
			return JSONObject.toJSONString(result);
		}
		
		result.setResultcodeEnum(ResultCodeEnum.RESULT_SUCCESS_CODE);
		return JSONObject.toJSONString(result);
	}
	
	private String validateOperator(String operatorId,String operatorPwd){
//		'1???????????????2???????????????3??????????????????4???????????????'
		String result = "pwdError";
		
//		User operator = userApi.get(new Long(operatorId));
		User operator = userApi.getByAccount(operatorId);
		if(null!=operator&&(operator.getUserType()==3||operator.getUserType()==4)){
			
			String pwdEncode = CipherUtil.MD5Encode(operatorPwd);
			if(pwdEncode.equals(operator.getPassword())){
				result = "success";
			}
		}else if(null!=operator){
			result = "noPermission";
		}
		return result;
		
	}
	
	//??????????????????
	private Map<String,Object> validateUser(User user){
		Map<String,Object> resutl = new HashMap<String,Object>();
		boolean flag = true;
		String msg = "success";
		
		if(userApi.checkByAccount(user)>0){
			flag = false;
			msg = "??????????????????,?????????!";
		}
		//email  phoneNumber
		
		resutl.put("flag", flag);
		resutl.put("msg", msg);
		return resutl;
	}
	
	private User convertUserInfo(UserInfo userInfo){
		User user = new User();
		if(null!=userInfo.getLoginNo() && !"".equals(userInfo.getLoginNo())){
			user.setAccount(userInfo.getLoginNo());
		}
		if(null!=userInfo.getUserName() && !"".equals(userInfo.getUserName())){
			user.setName(userInfo.getUserName());
		}
		if(null!=userInfo.getStatus() && !"".equals(userInfo.getStatus())){
			user.setStatus(new Integer(userInfo.getStatus()));
		}
		if(null!=userInfo.getEmail() && !"".equals(userInfo.getEmail())){
			user.setEmail(userInfo.getEmail());
		}
//		userInfo.getOrgid()
		if(null!=userInfo.getMobile() && !"".equals(userInfo.getMobile())){
			user.setMobile(userInfo.getMobile());
		}
		if(null!=userInfo.getPassword() && !"".equals(userInfo.getPassword())){
			user.setPassword(userInfo.getPassword());
		}
		return user;
	}
	
	//????????????,?????????account
	private User convertUserInfo(User user,UserInfo userInfo){
//		if(null!=userInfo.getLoginNo() && !"".equals(userInfo.getLoginNo())){
//			user.setAccount(userInfo.getLoginNo());
//		}
		if(null!=userInfo.getUserName() && !"".equals(userInfo.getUserName())){
			user.setName(userInfo.getUserName());
		}
		if(null!=userInfo.getStatus() && !"".equals(userInfo.getStatus())){
			user.setStatus(new Integer(userInfo.getStatus()));
		}
		if(null!=userInfo.getEmail() && !"".equals(userInfo.getEmail())){
			user.setEmail(userInfo.getEmail());
		}
//		userInfo.getOrgid()
		if(null!=userInfo.getMobile() && !"".equals(userInfo.getMobile())){
			user.setMobile(userInfo.getMobile());
		}
		if(null!=userInfo.getPassword() && !"".equals(userInfo.getPassword())){
			user.setPassword(userInfo.getPassword());
		}
		return user;
	}
	
	private User convertUserAgent(UserAgent userAgent){
		User user = new User();
		if(null!=userAgent.getUserId() && !"".equals(userAgent.getUserId())){
			user.setId(new Long(userAgent.getUserId()));
		}
		if(null!=userAgent.getAccount() && !"".equals(userAgent.getAccount())){
			user.setAccount(userAgent.getAccount());
		}
		if(null!=userAgent.getUserName() && !"".equals(userAgent.getUserName())){
			user.setName(userAgent.getUserName());
		}
		if(null!=userAgent.getStatus() && !"".equals(userAgent.getStatus())){
			user.setStatus(new Integer(userAgent.getStatus()));
		}
		if(null!=userAgent.getEmail() && !"".equals(userAgent.getEmail())){
			user.setEmail(userAgent.getEmail());
		}
		if(null!=userAgent.getMobile() && !"".equals(userAgent.getMobile())){
			user.setMobile(userAgent.getMobile());
		}
		if(null!=userAgent.getPassword() && !"".equals(userAgent.getPassword())){
			user.setPassword(userAgent.getPassword());
		}
		
		return user;
	}
	
	//????????????,?????????account
	private User convertUserAgent(User user,UserAgent userAgent){
		if(null!=userAgent.getUserId() && !"".equals(userAgent.getUserId())){
			user.setId(new Long(userAgent.getUserId()));
		}
		if(null!=userAgent.getUserName() && !"".equals(userAgent.getUserName())){
			user.setName(userAgent.getUserName());
		}
		if(null!=userAgent.getStatus() && !"".equals(userAgent.getStatus())){
			user.setStatus(new Integer(userAgent.getStatus()));
		}
		if(null!=userAgent.getEmail() && !"".equals(userAgent.getEmail())){
			user.setEmail(userAgent.getEmail());
		}
		if(null!=userAgent.getMobile() && !"".equals(userAgent.getMobile())){
			user.setMobile(userAgent.getMobile());
		}
		if(null!=userAgent.getPassword() && !"".equals(userAgent.getPassword())){
			user.setPassword(userAgent.getPassword());
		}
		
		return user;
	}
	
	private boolean add(User user){
		boolean flag = true;
		if(userApi.add(user)<1){
			return false;
		}
		
		//????????????,???????????????(id=1),???????????????(id=1)
		Long userId = user.getId();
		List<UmRelation> umRelations = new ArrayList<UmRelation>();
		UmRelation ur = new UmRelation();
		Long id = new Long(1);
		ur.setDomainId(id);
		ur.setRoleId(id);
		ur.setUserId(userId);
		umRelations.add(ur);
		if(userApi.updateDomainRole(umRelations,userId)<1){
			flag = false;
			return flag;
		}
		
//		List<ItsmUserBean> listBean = new ArrayList<ItsmUserBean>();
//		ItsmUserBean userBean = new ItsmUserBean(user);
//		userBean.setId(String.valueOf(user.getId()));
//		
//		userBean.setOperatetype(UserOperatetype.INSERT.toString());
//		listBean.add(userBean);
//		
//		String markStr = "/itsm/schemas/baseinfo?wsdl";
//		List<ItsmSystemBo>  list = itsmUserApi.queryAllItsmSystem();
//		for(ItsmSystemBo isb:list){
//			String wsdlUrl = isb.getWsdlURL();
//			if(isb.getIsOpen()==1&&null!=wsdlUrl&&wsdlUrl.indexOf(markStr)>0){
//				
//				UserServiceImpl userServiceImpl = null;
//				try {
//					/* ??????URL?????????????????? */
//					URL wsdlURL = new URL(wsdlUrl);
//					boolean availaby = URLAvailability.isConnect(wsdlURL);
//					
//					if (availaby) {
//						userServiceImpl = new UserServiceImpl(wsdlURL, SERVICE_NAME);
//					} else {
//						logger.error("INSERT URL??????:" + wsdlURL + "??????");
//					}
//				} catch (Exception e) {
//					logger.error("INSERT URL??????:" + wsdlUrl + "??????",e);
//				}
//				
//				UserServiceApi userServiceApi = userServiceImpl.getUserServiceImplPort();
//				try {
//					/* ???????????? */
//					userServiceApi.setUser(JSONObject.toJSONString(listBean));
//				} catch (WSException e) {
//					logger.error("INSERT URL??????:"+e.getMessage(), e);
//				}
//				
//			}
//		}
		
		return flag;
	}
	
	
	private static final QName SERVICE_NAME = new QName("http://impl.service.webservice.stsm.ms.mainsteam.com/", "BaseInforServiceBusImplService");

	private boolean delete(Long[] userId){
		boolean flag = true;
		//??????user??????itsm   cmdb??????
		if(userApi.batchDel(userId)==0){
			flag = false;
			return flag;
		}
		
		List<ItsmUserBean> listBean = new ArrayList<ItsmUserBean>();
		for (Long id : userId) {
			ItsmUserBean userBean = new ItsmUserBean();
			userBean.setId(String.valueOf(id));
			// ??????????????????
			userBean.setOperatetype(UserOperatetype.DELETE.toString());
			listBean.add(userBean);
		}
		
		String markStr = "/itsm/schemas/baseinfo?wsdl";
		List<ItsmSystemBo>  list = itsmUserApi.queryAllItsmSystem();
		for(ItsmSystemBo isb:list){
			String wsdlUrl = isb.getWsdlURL();
			if(isb.getIsOpen()==1&&null!=wsdlUrl&&wsdlUrl.indexOf(markStr)>0){
				
				UserServiceImpl userServiceImpl = null;
				try {
					/* ??????URL?????????????????? */
					URL wsdlURL = new URL(wsdlUrl);
					boolean availaby = URLAvailability.isConnect(wsdlURL);
					
					if (availaby) {
						userServiceImpl = new UserServiceImpl(wsdlURL, SERVICE_NAME);
					} else {
						logger.error("deleteUser URL??????:" + wsdlURL + "??????");
					}
				} catch (Exception e) {
					logger.error("deleteUser URL??????:" + wsdlUrl + "??????",e);
				}
				
				UserServiceApi userServiceApi = userServiceImpl.getUserServiceImplPort();
				try {
					/* ???????????? */
					userServiceApi.setUser(JSONObject.toJSONString(listBean));
				} catch (WSException e) {
					logger.error("deleteUser URL??????:"+e.getMessage(), e);
				}
				
			}
		}
		
		return flag;
	}
	
	private boolean update(User user){
		if(userApi.update(user)>0){
			
//			List<ItsmUserBean> listBean = new ArrayList<ItsmUserBean>();
//			ItsmUserBean userBean = new ItsmUserBean(user);
//			userBean.setId(String.valueOf(user.getId()));
//			
//			userBean.setOperatetype(UserOperatetype.UPDATE.toString());
//			listBean.add(userBean);
//			
//			String markStr = "/itsm/schemas/baseinfo?wsdl";
//			List<ItsmSystemBo>  list = itsmUserApi.queryAllItsmSystem();
//			for(ItsmSystemBo isb:list){
//				String wsdlUrl = isb.getWsdlURL();
//				if(isb.getIsOpen()==1&&null!=wsdlUrl&&wsdlUrl.indexOf(markStr)>0){
//					
//					UserServiceImpl userServiceImpl = null;
//					try {
//						/* ??????URL?????????????????? */
//						URL wsdlURL = new URL(wsdlUrl);
//						boolean availaby = URLAvailability.isConnect(wsdlURL);
//						
//						if (availaby) {
//							userServiceImpl = new UserServiceImpl(wsdlURL, SERVICE_NAME);
//						} else {
//							logger.error("INSERT URL??????:" + wsdlURL + "??????");
//						}
//					} catch (Exception e) {
//						logger.error("INSERT URL??????:" + wsdlUrl + "??????",e);
//					}
//					
//					UserServiceApi userServiceApi = userServiceImpl.getUserServiceImplPort();
//					try {
//						/* ???????????? */
//						userServiceApi.setUser(JSONObject.toJSONString(listBean));
//					} catch (WSException e) {
//						logger.error("INSERT URL??????:"+e.getMessage(), e);
//					}
//					
//				}
//			}
			
			return true;
		}else{
			return false;
		}
	}
	
	

	
}
