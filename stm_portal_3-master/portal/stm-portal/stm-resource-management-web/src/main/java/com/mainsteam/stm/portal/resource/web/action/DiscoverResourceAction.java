package com.mainsteam.stm.portal.resource.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.BoxStyleEnum;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.plugin.SupportValue;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeGroupService;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IAccountApi;
import com.mainsteam.stm.portal.resource.api.IDiscoverResourceApi;
import com.mainsteam.stm.portal.resource.api.IReAccountInstanceApi;
import com.mainsteam.stm.portal.resource.web.vo.DiscCategoryVo;

/**
 * <li>文件名称: AccountAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 发现资源相关的操作</li>
 * <li>其他说明:</li>
 * 
 * @version ms.stm
 * @since 2019年7月22日
 */
@Controller
@RequestMapping("/portal/resource/discoverResource")
public class DiscoverResourceAction extends BaseAction {
	private Logger logger = Logger.getLogger(DiscoverResourceAction.class);
	private static final String DOWLOAD_PATH = "common" + File.separator + "localfile";
	private static final String DOWLOAD_FILENAME = "batchDiscoverTemplate.xls";
	private static final String CATALINA_HOME = "catalina.home";
	@Resource
	private CapacityService capacityService;
	@Resource
	private NodeGroupService nodeGroupService;
	@Resource
	private IReAccountInstanceApi reAccountInstanceApi;
	@Resource
	private IAccountApi accountApi;
	@Resource
	private IDiscoverResourceApi discoverResourceApi;
	/**
	 * 获取category的所有类型在前台显示
	 * 
	 * @return
	 */
	@RequestMapping("/getResPrototype")
	public JSONObject getResPrototype() {
		List<DiscCategoryVo> discCategoryList = new ArrayList<DiscCategoryVo>();
		searchCategory(capacityService.getRootCategory(), discCategoryList);
		return toSuccess(discCategoryList);
	}

	// 递归查询出所有category
	private void searchCategory(CategoryDef categoryDef, List<DiscCategoryVo> discCategoryList) {
		DiscCategoryVo discCategory = new DiscCategoryVo();
		if(CapacityConst.STORAGE.equals(categoryDef.getId())){
			try {
				if(License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelStor) == 0){
					return;
				}
			} catch (LicenseCheckException e) {
				logger.error("searchCategory stmModelStor:", e);
			}
		}
		discCategory.setId(categoryDef.getId());
		discCategory.setName(categoryDef.getName());
		discCategory.setType("category");
		if (null != categoryDef.getParentCategory()) {
			discCategory.setPid(categoryDef.getParentCategory().getId());
		}
		discCategoryList.add(discCategory);
		// 判断是否还有child category
		if (null == categoryDef.getChildCategorys()) {
			if (null != categoryDef.getResourceDefs()) {
				ResourceDef[] resourceDefs = categoryDef.getResourceDefs();
				for (int i = 0; i < resourceDefs.length; i++) {
					ResourceDef resourceDef = resourceDefs[i];
					DiscCategoryVo discResource = new DiscCategoryVo();
					discResource.setId(resourceDef.getId());
					discResource.setName(resourceDef.getName());
					discResource.setType("resource");
					discResource.setPid(categoryDef.getId());
					discCategoryList.add(discResource);
				}
			}
		} else {
			CategoryDef[] categoryDefs = categoryDef.getChildCategorys();
			for (int i = 0; i < categoryDefs.length; i++) {
				if(categoryDefs[i].isDisplay())
					searchCategory(categoryDefs[i], discCategoryList);
			}
		}
	}
	/**
	 * 根据resourceId和pluginId查询相应的plugin参数
	 * @param resourceId
	 * @param pluginId
	 * @return
	 */
	@RequestMapping("/getPlugParamByCollType")
	public JSONObject getPlugParamByCollType(String resourceId, String pluginId){
		ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
		return toSuccess(resourceDef.getPluginInitParameterMap().get(pluginId));
	}
	/**
	 * 获取资源原型的初始化plugin参数
	 * 
	 * @param resourceId
	 * @return
	 */
	@RequestMapping("/getpluginInitParameter")
	public JSONObject getpluginInitParameter(String resourceId) {
		// 返回结果集
		Map<String, PluginInitParameter[]> result = new HashMap<String, PluginInitParameter[]>();
		// 资源模型
		ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
		// 初始化插件参数
		Map<String, PluginInitParameter[]> pluginInitParameterMap = resourceDef.getPluginInitParameterMap();
		// 必选发现方式
		Set<String> requiredCollType = resourceDef.getRequiredCollPluginIds();
		// 把nodeGroup放在最前
		PluginInitParameter[] nodeGroupPluginParamter = { getNodeGroupParameter() };
		result.put("nodeGroup", nodeGroupPluginParamter);
		// 可选发现方式(1.可选发现方式有可以为空)
		Map<String, String> optionCollType = resourceDef.getOptionCollPluginIds();
		if (!optionCollType.isEmpty()) {
			PluginInitParameter[] optionPluginParamter = { getCollectTypeParameter(optionCollType) };
			result.put("option", optionPluginParamter);
			Iterator<String> iter = optionCollType.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				if(requiredCollType.contains(key))
					requiredCollType.remove(key);
			}
		}
		if (!requiredCollType.isEmpty()){
			Iterator<String> iter = requiredCollType.iterator();
			while(iter.hasNext()){
				String key = iter.next();
				PluginInitParameter[] requiredPluginParamter = pluginInitParameterMap.get(key);
				result.put(key, requiredPluginParamter);
			}
		}
		return toSuccess(result);
	}
	@RequestMapping("/getParameter")
	public JSONObject getParameter(String resourceId){
		ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
		String type = null;
		if(resourceDef!=null){
			CategoryDef def=	resourceDef.getCategory();
			if(def.getParentCategory()!=null){
				type=def.getParentCategory().getName();
			}
		}
		return toSuccess(type);
		
		
	}
	
	
	/**
	 * 新建一个nodegroupParameter节点
	 * 
	 * @return
	 */
	private PluginInitParameter getNodeGroupParameter(){
		// 新建一个NodeGroupID节点参数
		PluginInitParameter NGParamter = new PluginInitParameter();
		NGParamter.setId("nodeGroupId");
		NGParamter.setName("DCS");
		NGParamter.setBoxStyle(BoxStyleEnum.OptionBox);
		NGParamter.setDisplay(true);
		NGParamter.setDisplayOrder("0");
		NGParamter.setMustInput(true);
		List<SupportValue> NGSupportValue = new ArrayList<SupportValue>();
		List<NodeGroup> nodeGroupList = nodeGroupService.getNodeGroups();
		for (int i = 0; nodeGroupList != null && i < nodeGroupList.size(); i++) {
			NodeGroup nodeGroup = nodeGroupList.get(i);
			if (NodeFunc.collector.compareTo(nodeGroup.getFunc()) == 0) {
				SupportValue supportValue = new SupportValue();
				supportValue.setValue(String.valueOf(nodeGroup.getId()));
				supportValue.setName(nodeGroup.getName());
				NGSupportValue.add(supportValue);
			}
		}
		NGParamter.setSupportValues(NGSupportValue
				.toArray(new SupportValue[NGSupportValue.size()]));
		return NGParamter;
	}
	/**
	 * 新建一个CollectTypeParameter节点
	 * @return
	 */
	private PluginInitParameter getCollectTypeParameter(Map<String, String> pluginIds){
		PluginInitParameter CTParamter = new PluginInitParameter();
		CTParamter.setId("collectType");
		CTParamter.setName("发现方式");
		CTParamter.setBoxStyle(BoxStyleEnum.OptionBox);
		CTParamter.setDisplay(true);
		CTParamter.setDisplayOrder("-1");
		CTParamter.setMustInput(true);
		List<SupportValue> CTSupportValue = new ArrayList<SupportValue>();
		Iterator<String> iter = pluginIds.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			SupportValue supportValue = new SupportValue();
			supportValue.setValue(key);
			supportValue.setName(pluginIds.get(key));
			CTSupportValue.add(supportValue);
		}
		CTParamter.setSupportValues(CTSupportValue
				.toArray(new SupportValue[CTSupportValue.size()]));
		return CTParamter;
	}
	
	/**
	 * 发现资源 status : 0 失败 1 成功
	 * 把发现参数转换成Map对象
	 * 
	 * @param resourceId
	 * @return
	 */
	@RequestMapping("/discoverResource")
	public JSONObject discoverResource(String jsonData, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		//将jsonData.u jsonData.p 恢复为jsonData.username jsonData.password
		editDiscoverJsonData(paramter);
		Map<String, Object> result = discoverResourceApi.discoverResource(paramter, user);
		return toSuccess(result);
	}

	/**
	 * 模块文件下载
	 * 
	 * @param response
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadTemplateFile")
	public String downloadTemplateFile(HttpServletResponse response,
			HttpServletRequest request) throws Exception {
		String path = System.getProperty(CATALINA_HOME) + File.separator
				+ DOWLOAD_PATH + File.separator + DOWLOAD_FILENAME;
		File file = new File(path);
		writeFile2Client(file, response);
		return null;
	}

	/**
	 * 下载批量发现的结果excel
	 * 
	 * @param response
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadBatchResultFile")
	public String downloadBatchResultFile(HttpServletResponse response,
			HttpServletRequest request, HttpSession session) throws Exception {
		ILoginUser user = getLoginUser(session);
		File file = discoverResourceApi.getBatchResultFile(user);
		writeFile2Client(file, response);
		return null;
	}
	
	/**
	 * 把excel文件写到前台
	 * @param file
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	private void writeFile2Client(File file, HttpServletResponse response) throws IOException{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			fis = new FileInputStream(file);
			String fileName = URLEncoder.encode(file.getName(), "UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentType("application/octet-stream");
			int contentLength = fis.available();
			response.setContentLength(contentLength);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
	}
	/**
	 * 批量发现功能
	 * @param file
	 * @return
	 */
	@RequestMapping(value="/batchDiscoverResouce", headers="content-type=multipart/*", method=RequestMethod.POST)
	public String batchDiscoverResouce(@RequestParam("domain") String domain, @RequestParam("dcs") String dcs, @RequestParam("file") MultipartFile file, HttpSession session) {
		ILoginUser user = getLoginUser(session);
		Map<String, Object> result = discoverResourceApi.resourceBatchDiscover(file, domain, dcs, user);
		return toSuccess(result).toJSONString();
	}
	
	/**
	 * 获取批量发现当前状态
	 * @param session
	 * @return
	 */
	@RequestMapping("/batchDiscoverStatus")
	public String batchDiscoverStatus(HttpSession session){
		ILoginUser user = getLoginUser(session);
		return toSuccess(user.getCache()).toJSONString();
	}
	/**
	 * 批量发现取消
	 * @param session
	 * @return
	 */
	@RequestMapping("/batchDiscoverCancel")
	public JSONObject batchDiscoverCancel(HttpSession session){
		ILoginUser user = getLoginUser(session);
		user.getCache().put("batchDiscoverCancel", true);
		return toSuccess(user.getCache());
	}
	
	/**
	 * 处理重复资源
	 * @param method
	 * @param session
	 * @return
	 */
	@RequestMapping("/handleRepeatInstance")
	public JSONObject handleRepeatInstance(String method, HttpSession session){
		ILoginUser user = getLoginUser(session);
		Map<String, Object> result = discoverResourceApi.handleRepeatInstance(method, user);
		return toSuccess(result);
	}
	
	/**
	 * 更新资源实例名称
	 * 0 失败 1 成功 2 重复
	 * @param instanceId
	 * @param newInstanceName
	 * @return
	 */
	@RequestMapping("/updateInstanceName")
	public JSONObject updateInstanceName(Long instanceId, String newInstanceName){
		int flag = discoverResourceApi.updateInstanceName(instanceId, newInstanceName);
		return toSuccess(flag);
	}
	
	/**
	 * 获取资源对应的监控类型
	 * 
	 * @param instanceId
	 * @return
	 */
	@RequestMapping("/getProfileType")
	public JSONObject getProfileType(Long instanceId) {
		List<Map<String, String>> result = discoverResourceApi
				.getProfileByInstanceId(instanceId);
		return toSuccess(result);
	}
	
	/**
	 * 把资源实例加入到监控中
	 * 
	 * @param instanceId
	 * @return
	 */
	@RequestMapping("/joinMonitor")
	public JSONObject joinMonitor(Long resourceGroupId, String newInstanceName, Long mainInstanceId, String childInstanceIds) {
		Long[] childInstanceIdLong = new Long[0];
		if(childInstanceIds != null && !"".equals(childInstanceIds)){
			String[] instanceIds = childInstanceIds.split(",");
			childInstanceIdLong = new Long[instanceIds.length];
			for (int i = 0; i < instanceIds.length; i++) {
				childInstanceIdLong[i] = Long.valueOf(instanceIds[i]);
			}
		}
		Map<String, String> result = discoverResourceApi.addMonitor(resourceGroupId, newInstanceName, mainInstanceId, childInstanceIdLong);
		return toSuccess(result);
	}
	/**
	 * 重新发现后，将结果加入监控
	 * @param newInstanceName
	 * @param mainInstanceId
	 * @param childInstanceIds
	 * @return
	 */
	@RequestMapping("/refreshjoinMonitor")
	public JSONObject refreshjoinMonitor( String newInstanceName, Long mainInstanceId, String childInstanceIds,String delChildInstanceIds,String cancleInstanceIds) {
		Long[] childInstanceIdLong = new Long[0];
		Long[] delchildInstanceIdLong = new Long[0];
		Long[] cancleInstanceIdsIdLong = new Long[0];
		if(childInstanceIds != null && !"".equals(childInstanceIds)){
			String[] instanceIds = childInstanceIds.split(",");
			childInstanceIdLong = new Long[instanceIds.length];
			for (int i = 0; i < instanceIds.length; i++) {
				childInstanceIdLong[i] = Long.valueOf(instanceIds[i]);
			}
		}
		if(delChildInstanceIds != null && !"".equals(delChildInstanceIds)){
			String[] delInstanceIds = delChildInstanceIds.split(",");
			delchildInstanceIdLong = new Long[delInstanceIds.length];
			for (int i = 0; i < delInstanceIds.length; i++) {
				delchildInstanceIdLong[i] = Long.valueOf(delInstanceIds[i]);
			}
		}
		if(cancleInstanceIds != null && !"".equals(cancleInstanceIds)){
			String[] cancleIds = cancleInstanceIds.split(",");
			cancleInstanceIdsIdLong = new Long[cancleIds.length];
			for (int i = 0; i < cancleIds.length; i++) {
				cancleInstanceIdsIdLong[i] = Long.valueOf(cancleIds[i]);
			}
		}
		Map<String, String> result = discoverResourceApi.refAddMonitor(newInstanceName, mainInstanceId, childInstanceIdLong,delchildInstanceIdLong,cancleInstanceIdsIdLong);
		return toSuccess(result);
	}
	/**
	 * 发现参数 status : 0 失败 1 成功
	 * 把发现参数转换成Map对象
	 * 
	 * @param resourceId
	 * @return
	 */
	@RequestMapping("/updateDiscoverParamter")
	public JSONObject updateDiscoverParamter(String jsonData, long instanceId) {
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		//将jsonData.u jsonData.p 恢复为jsonData.username jsonData.password
		editDiscoverJsonData(paramter);
		int result = discoverResourceApi.updateDiscoverParamter(paramter, instanceId);
		return toSuccess(result);
	}
	/**
	 * 发现参数 status : 0 失败 1 成功
	 * 把发现参数转换成Map对象
	 * 
	 * @param resourceId
	 * @return
	 */
	@RequestMapping("/reDiscover")
	public JSONObject reDiscover(String jsonData, long instanceId) {
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		//将jsonData.u jsonData.p 恢复为jsonData.username jsonData.password
		editDiscoverJsonData(paramter);
		int result = discoverResourceApi.reDiscover(paramter, instanceId);
		return toSuccess(result);
	}
	/**
	 * 重新发现
	 * @param jsonData
	 * @param instanceId
	 * @param session
	 * @return
	 */
	@RequestMapping("/refreshDiscover")
	public JSONObject refreshDiscover(String jsonData, long instanceId,HttpSession session) {
		ILoginUser user = this.getLoginUser(session);
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		Map<String, Object> result = new HashMap<String, Object>();
		editDiscoverJsonData(paramter);
		result=discoverResourceApi.refreshDiscover(paramter, instanceId,user);
		return toSuccess(result);
	}
	/**
	 * 测试连接 status : 0 失败 1 成功
	 * 把发现参数转换成Map对象
	 * 
	 * @param resourceId
	 * @return
	 */
	@RequestMapping("/testDiscover")
	public JSONObject testDiscover(String jsonData, long instanceId) {
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		//将jsonData.u jsonData.p 恢复为jsonData.username jsonData.password
		editDiscoverJsonData(paramter);
		int result = discoverResourceApi.testDiscover(paramter, instanceId);
		return toSuccess(result);
	}
	/**
	 * 删除子资源（逻辑删除）
	 * @param instanceId
	 * @return
	 */
	@RequestMapping("/delResounceInstance")
	public JSONObject delResounceInstance(long instanceId) {
	int result=	discoverResourceApi.delResourceInstance(instanceId);
		return toSuccess(result);
	}
	
	private void editDiscoverJsonData(Map paramter){
		editItem(paramter,"u","username");
		editItem(paramter,"p","password");
	}
	private void editItem(Map paramter,String beforeDateName,String afterDataName){
		if(paramter.containsKey(beforeDateName)){
			paramter.put(afterDataName, null==paramter.get(beforeDateName)?"":paramter.get(beforeDateName));
			paramter.remove(beforeDateName);
		}
	}
}
