package com.mainsteam.stm.system.um.user.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.home.workbench.main.api.IUserWorkBenchApi;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.relation.api.IUmRelationApi;
import com.mainsteam.stm.system.um.resourcegroup.api.IResourceGroupApi;
import com.mainsteam.stm.system.um.resourcegroup.bo.DomainResourceGroupRel;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
import com.mainsteam.stm.system.um.user.vo.UserConditionVo;
import com.mainsteam.stm.system.um.user.web.vo.ExportUser;
import com.mainsteam.stm.system.um.user.web.vo.ParameterVo;
import com.mainsteam.stm.system.um.user.web.vo.Resource;
import com.mainsteam.stm.util.ClassPathUtil;

/**
 * <li>????????????: UserAction.java</li>
 * <li>????????????: ????????????</li>
 * <li>????????????: ????????????(C)2019-2020</li>
 * <li>???    ???: ????????????????????????????????????</li>
 * <li>????????????: ???</li>
 * <li>????????????:???</li>
 * <li>???????????????@date</li>
 * <li>????????????: ???</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019???8???21???
 */
@SuppressWarnings("unchecked")
@Controller
@RequestMapping(value="/system/user")
public class UserAction extends BaseAction{
	@Autowired
	private IUserApi userApi;
	
	@Autowired
	private IUmRelationApi iUmRelationApi;
	
	@Autowired
	private IDomainApi domainApi;
	
	@Autowired
	private IUserWorkBenchApi userWorkbenchApi;
	
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	
	@Autowired
	private ResourceCategoryApi resourceCategoryApi;
	
	@Autowired
	private IResourceGroupApi resourceGroupApi;
	
	@Autowired
	private IResourceApi resourceApi;
	
	@Autowired
	private CapacityService capacityService;
	
	@Autowired
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	@Autowired
	private AlarmRuleService alarmRuleService;
	
	@Autowired
	private BizMainApi bizMainApi;
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * ????????????
	 * @param page
	 * @return
	 * @author  ziwen
	 * @date	2019???8???21???
	 */
	@RequestMapping(value="/userPage")
	public JSONObject userPage(Page<User, UserConditionVo> page, UserConditionVo conditions, HttpSession session){
		ILoginUser user = this.getLoginUser(session);
		conditions.setUserType(user.getUserType());
		if(conditions.getKeyword() != null&&conditions.getKeyword().equals("?????????/??????/????????????/??????")){
			conditions.setKeyword("");
		}
		page.setCondition(conditions);
		userApi.selectByPage(page);
		return toSuccess(page);
	}
	@RequestMapping(value="/getLoginUserDomains")
	public JSONObject getLoginUserDomains(HttpSession session){
		ILoginUser user = this.getLoginUser(session);
		return toSuccess(userApi.getDomainUsersDomains(user.getId()));
	}
	/**
	 * ??????????????????
	 * @param user
	 * @return
	 * @author  ziwen
	 * @date	2019???8???21???
	 */
	@RequestMapping("/insert")
	public JSONObject insert(User user, HttpSession session){
		if(user.getPhone()!=null&&!"".equals(user.getPhone())){
			user.setMobile(user.getPhone());
		}
		ILoginUser iLoginUser = this.getLoginUser(session);
		user.setCreatorId(iLoginUser.getId());
		userApi.add(user);
//		userWorkbenchApi.insertUserAllWorkbenchs(user.getId());
		return toSuccess(user);
	}
	/**
	 * ????????????????????????
	 * @param parametersVO
	 * @return
	 * @author  ziwen
	 * @date	2019???8???21???
	 */

	@RequestMapping(value="/del")
	public JSONObject del(Long[] ids){
		//??????????????????????????????????????????????????????????????????????????????
		List<Long> resultIds = new ArrayList<Long>();
		for(long id : ids){
			if(bizMainApi.getBizCountByManagerId(id) <= 0){
				resultIds.add(id);
			}
		}
		
		if(resultIds.size() <= 0){
			return toSuccess(0);
		}
		
		try {
			//??????????????????
			for(Long userId : resultIds){
				List<AlarmRule> rules = alarmRuleService.getAlarmRulesByUserId(String.valueOf(userId), null);
				if(rules != null && rules.size() > 0){
					long[] ruleIds = new long[rules.size()];
					for(int i = 0 ; i < rules.size() ; i++){
						AlarmRule rule = rules.get(i);
						ruleIds[i] = rule.getId();
					}
					alarmRuleService.deleteAlarmRuleById(ruleIds);
				}
			}
		} catch (Exception e) {
			logger.error("Delete alarm rule fail..");
			logger.error(e.getMessage(),e);
		}
		
		Long[] delIds = new Long[resultIds.size()];
		
		userWorkbenchApi.delUserAllWorkbenchs(resultIds.toArray(delIds));
		return toSuccess(userApi.batchDel(resultIds.toArray(delIds)));
	}
	/**
	 * ??????id??????????????????
	 * @param id
	 * @return
	 * @author  ziwen
	 * @date	2019???8???21???
	 */
	@RequestMapping(value="/get")
	public JSONObject get(Long id){
		User user=userApi.get(id);
		return toSuccess(user);
	}
	/**
	 * ??????????????????
	 * @param user
	 * @return
	 * @author  ziwen
	 * @date	2019???8???21???
	 */
	@RequestMapping(value="/update")
	public JSONObject update(User user){
		if(user.getPhone()!=null&&!"".equals(user.getPhone())){
			user.setMobile(user.getPhone());
		}
		return toSuccess(userApi.update(user));
	}
	/**
	 * ???????????????????????????????????????
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019???9???10???
	 */
	@RequestMapping(value="/getDomainRole")
	public JSONObject getDomainRole(Long id, HttpSession session){
		return toSuccess(userApi.getDomainRole());
	}
	
	/**
	 * ???????????????????????????????????????
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019???9???10???
	 */
	@RequestMapping(value="/getDomainRoles")
	public JSONObject getDomainRoles(Long id, HttpSession session){
		return toSuccess(userApi.getDomainRoleByUserId(id));
	}
	
	@RequestMapping(value="/getDomainRoleRel")
	public JSONObject getDomainRoleRel(Long id){
		return toSuccess(userApi.getDomainRoleRel(id));
	}
	/**
	 * ??????????????????????????????
	 * @param vo
	 * @return
	 * @author	ziwen
	 * @date	2019???9???10???
	 */
	@RequestMapping(value="/domainRoleUpdate")
	public JSONObject domainRoleUpdate(ParameterVo vo){
		return toSuccess(userApi.updateDomainRole(vo.getUmRelations()==null ? Collections.EMPTY_LIST : vo.getUmRelations(), vo.getId()));
	}
	/**
	 * ???????????????????????????
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019???9???10???
	 */
	@RequestMapping(value="/getDomains")
	public JSONObject getDomains(Long id, HttpSession session){
		return getDomainRole(id, session);
	}
	/**
	 * ???????????????????????????
	 * @param user
	 * @return
	 * @author	ziwen
	 * @date	2019???9???10???
	 */
	@RequestMapping(value="/checkAccount")
	public JSONObject checkAccount(User user){
		return toSuccess(userApi.checkByAccount(user));
	}
	/**
	 * ??????????????????
	 * @param vo
	 * @return
	 * @author	ziwen
	 * @date	2019???9???10???
	 */
	@RequestMapping(value="/updateStatus")
	public JSONObject updateStatus(Long [] ids, Integer type){
		ArrayList<User> users = new ArrayList<User>();
		for(Long id : ids){
			User user = new User(id, type==1 ? UserConstants.USER_STATUS_ENABLE : UserConstants.USER_STATUS_DISABLE);
			if(type == 1){
				user.setPassErrorCnt(0);
			}else{
				user.setLockType(UserConstants.USER_LOCK_TYPE_MANUAL);
				user.setLockTime(new Date());
			}
			users.add(user);
		}
		return toSuccess(userApi.batchUpdate(users));
	}
	/**
	 * ????????????
	 * @return
	 * @author	ziwen
	 * @date	2019???10???22???
	 */
	@RequestMapping(value="/getResourceType")
	public JSONObject getResourceType(){
		
		// ??????????????????????????????????????????
		CategoryDef categoryDef = capacityService.getRootCategory();

		// ????????????????????????
		CategoryDef[] categoryDefs = categoryDef.getChildCategorys();

		List<ResourceCategoryBo> categoryBoOneLevelList = new ArrayList<ResourceCategoryBo>();

		for (CategoryDef singleCategory : categoryDefs) {

			if(!singleCategory.isDisplay() && !(singleCategory.getId().equals("VM"))){
				//???????????????VM??????
				continue;
			}
			
			if(!licenseCapacityCategory.isAllowCategory(singleCategory.getId())){
				continue;
			}
			
			ResourceCategoryBo categoryBo = new ResourceCategoryBo();
			categoryBo.setId(singleCategory.getId());
			categoryBo.setName(singleCategory.getName());

			// ????????????????????????
			CategoryDef[] categoryDefs_secondLevel = singleCategory
					.getChildCategorys();

			if (categoryDefs_secondLevel == null
					|| categoryDefs_secondLevel.length <= 0) {
				categoryBo.setChildCategorys(null);
			} else {

				List<ResourceCategoryBo> categoryBoTwoLevelList = new ArrayList<ResourceCategoryBo>();

				for (CategoryDef lastCategory : categoryDefs_secondLevel) {

					ResourceCategoryBo categoryVo_2 = new ResourceCategoryBo();
					categoryVo_2.setId(lastCategory.getId());
					categoryVo_2.setName(lastCategory.getName());
					
					// ????????????????????????
					CategoryDef[] categoryDefs_thirdLevel = lastCategory
							.getChildCategorys();
					
					if (categoryDefs_thirdLevel == null
							|| categoryDefs_thirdLevel.length <= 0) {
						categoryVo_2.setChildCategorys(null);
					} else {

						List<ResourceCategoryBo> categoryBoThirdLevelList = new ArrayList<ResourceCategoryBo>();

						for (CategoryDef thirdCategory : categoryDefs_thirdLevel) {
							
							if(!thirdCategory.isDisplay()){
								continue;
							}
							
							ResourceCategoryBo categoryVo_3 = new ResourceCategoryBo();
							categoryVo_3.setId(thirdCategory.getId());
							categoryVo_3.setName(thirdCategory.getName());

							categoryBoThirdLevelList.add(categoryVo_3);

						}

						categoryVo_2.setChildCategorys(categoryBoThirdLevelList);

					}

					categoryBoTwoLevelList.add(categoryVo_2);

				}

				categoryBo.setChildCategorys(categoryBoTwoLevelList);

			}

			categoryBoOneLevelList.add(categoryBo);

		}
		
		return toSuccess(categoryBoOneLevelList);
	}
	/**
	 * ?????????id??????????????????
	 * @param domainIds
	 * @return
	 * @throws Exception
	 * @author	ziwen
	 * @date	2019???10???22???
	 */
	@RequestMapping(value="/getResources")
	public JSONObject getResources(Long [] domainIds, Long userId)throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		List<ResourceInstance> instances = resourceInstanceService.getParentResourceInstanceByDomainIds(new HashSet<Long>(Arrays.asList(domainIds)));
		instances = instances!=null ? instances : Collections.EMPTY_LIST;
		Map<Long, List<Resource>> resourcesMap = new HashMap<Long, List<Resource>>();
		for(ResourceInstance instance : instances){
			Resource resource = new Resource();
			resource.setId(instance.getId());
			resource.setResourceName(instance.getName());
			resource.setType(instance.getCategoryId());
			resource.setIp(instance.getShowIP());
			List<Resource> resources = resourcesMap.get(instance.getDomainId());
			if(resources!=null){
				resources.add(resource);
			}else{
				resources = new ArrayList<Resource>();
				resources.add(resource);
				resourcesMap.put(instance.getDomainId(), resources);
			}
		}
		result.put("resource", resourcesMap);
		Map<Long, List<DomainResourceGroupRel>> resourceGroup = new HashMap<Long, List<DomainResourceGroupRel>>();
		for(Long domainId : domainIds){
			resourceGroup.put(domainId, resourceGroupApi.queryAllGroupByDomain(domainId));
		}
		result.put("resourceGroup", resourceGroup);
		result.put("userResources", userApi.getUserResources(userId));
		return toSuccess(result);
	}
	/**
	 * ???????????????????????????
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author	ziwen
	 * @date	2019???10???22???
	 */
	@RequestMapping(value="/getUserResources")
	public JSONObject getUserResources(Long userId){
		return toSuccess(userApi.getUserResources(userId));
	}
	
	/**
	 * ??????????????????????????????
	 * @param vo
	 * @return
	 * @throws Exception
	 * @author	ziwen
	 * @date	2019???10???22???
	 */
	@RequestMapping(value="/userResourcesSave")
	public JSONObject userResourcesSave(String parameterVo)throws Exception{
		ParameterVo vo = JSONObject.parseObject(parameterVo, ParameterVo.class);
		return toSuccess(userApi.saveUserResources(vo.getUserResources(), vo.getId()));
	}
	
	/**
	* @Title: queryAllUsers
	* @Description: ????????????????????????
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryAllUsers")
	public JSONObject queryAllUsers(){
		return toSuccess(userApi.queryAllUserNoPage(null));
	}
	
	@RequestMapping(value="/getCommonUserDomains")
	@ResponseBody
	public List<Domain> getUserCommonDomains(Long id){
		return userApi.getUserCommonDomains(id);
	}
	
	private static final String TEMPLATE_EXCEL = "template.xls";
	@RequestMapping(value="/downloadUsers")
	public void downloadUsers(HttpServletResponse response, HttpServletRequest request,
			HttpSession session, UserConditionVo conditions) throws IOException{
		conditions.setUserType(this.getLoginUser(session).getUserType());
		List<User> users = userApi.exportUsers(conditions);
		List<ExportUser> exportUsers = new ArrayList<ExportUser>();
		for(User user : users){
			ExportUser exportUser = new ExportUser();
			exportUser.setAccount(user.getAccount());
			exportUser.setName(user.getName());
			exportUser.setSex(user.getSex()==0 ? "???" : "???");
			exportUser.setUserType(user.getUserType()==1 ? "????????????" : "???????????????");
			exportUser.setStatus(user.getStatus()==1 ? "??????" : "??????");
			exportUser.setMobile(user.getUserType() != 4 ? user.getMobile() : "");
			exportUser.setEmail(user.getUserType() != 4 ?user.getEmail() : "");
			exportUsers.add(exportUser);
		}
		/***********????????????end*************/
		String [] columns = new String[]{"userType", "account", "name", "sex", "mobile", "email", "status"};
		
		URL url = new URL(getClass().getResource("")+TEMPLATE_EXCEL);
		POIFSFileSystem ps = new POIFSFileSystem(url.openStream());
		HSSFWorkbook wb=new HSSFWorkbook(ps);
		HSSFSheet sheet=wb.getSheetAt(0);
		HSSFRow row = null;
		for(ExportUser user : exportUsers){
			row = sheet.createRow((short)(sheet.getLastRowNum()+1));
			@SuppressWarnings("rawtypes")
			Class clazz = user.getClass();
			for(int i=0,len=columns.length; i<len; i++){
				String value = "";
				try {
					Field feild = clazz.getDeclaredField(columns[i]);
					feild.setAccessible(true);
					value = feild.get(user).toString();
				} catch (Exception e) {
					logger.error(user.getAccount() + ":" + columns[i] + "??????????????????");
					e.printStackTrace();
				}
				row.createCell(i).setCellValue(value);
			}
		}
		
		String encoding="UTF-8";
		String filename = "????????????.xls";
		String agent = request.getHeader("USER-AGENT");
		
		if(null==agent){
			filename = URLEncoder.encode(filename,encoding); 
		}else if((agent=agent.toLowerCase()).indexOf("firefox")>-1){
			filename = new String(filename.getBytes(encoding),"iso-8859-1");
		}else{
			filename = URLEncoder.encode(filename,encoding);
			if(agent.indexOf("msie")>-1&&filename.length()>150)//??????IE 6.0 bug
				filename=new String(filename.getBytes(encoding),"ISO-8859-1");
		}
		response.setHeader( "Content-Disposition", "attachment;filename="+filename);
		response.setContentType("application/octet-stream");
		response.addHeader("Content-Type", "text/html; charset="+encoding);
		OutputStream os = response.getOutputStream();
		
		os.flush();
		wb.write(os);
		os.close();
	}
	
	
	@RequestMapping(value="/importUsers", headers="content-type=multipart/*", method=RequestMethod.POST)
	@ResponseBody
	public String importUsers(@RequestParam("file") MultipartFile file, HttpSession session)throws Exception{
		return userApi.saveUsers(file.getInputStream(), this.getLoginUser(session));
	}
	
	private static final String EXCEL_NAME = "userTemplate.xls";
	private static final String FILE_PATH = ClassPathUtil.getCommonClasses()
			+"config"+File.separatorChar;
	@RequestMapping(value="/downloadUserTemplate")
	public void downloadUserTemplate(HttpServletResponse response) throws IOException{
		String filePath = FILE_PATH + EXCEL_NAME;
		File file = new File(filePath);
		BufferedInputStream in = null;	//??????????????????????????????????????????
		BufferedOutputStream os = null;
		FileInputStream result = null;
		response.setHeader("Content-Disposition", "attachment; filename=" + EXCEL_NAME);
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		try{
			result = new FileInputStream(file);
			in = new BufferedInputStream(result);
			os = new BufferedOutputStream(response.getOutputStream());
			int size = 1024;
			byte[] buf = new byte[size];
			while(in.read(buf)!=-1){
				os.write(buf);
			}
			os.flush();
		}catch(Exception e){
			logger.error("exprotZip:"+e.getMessage());
		}finally{
			if(result!=null){
				result.close();
			}
			if(in!=null){
				in.close();
			}
			if(os!=null){
				os.close();
			}
		}
		
	}
	
	public static void main(String[] args){
	}
}
