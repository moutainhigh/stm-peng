package com.mainsteam.stm.portal.business.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.IBizServiceApi;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizWarnViewBo;
import com.mainsteam.stm.portal.business.web.vo.CategoryVo;
import com.mainsteam.stm.portal.business.web.vo.ResourceInstanceVo;
import com.mainsteam.stm.system.um.user.bo.User;

/**
 * <li>????????????: BizServiceAction.java</li>
 * <li>????????????: ????????????????????????????????????</li>
 * <li>????????????: ????????????(C)2019-2020</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * @version  ms.stm
 * @since    2019???8???7???
 * @author   caoyong
 */
@Controller
@RequestMapping("/portal/business/service")
public class BizServiceAction extends BaseAction {
	/**contextPath????????????*/
	private static final String replaceHolder = "%s";
	/**FILE_REQUEST????????????*/
	private static final String FILE_REQUEST = "/platform/file/getFileInputStream.htm";
	@Autowired
	private IBizServiceApi bizServiceApi;
	@Autowired
	private CapacityService capacityService;
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private InstanceStateService instanceStateService;
	@Autowired
	private IFileClientApi fileClient;
	@Autowired
	private ILicenseCalcService licenseCalcService;
	
	@Resource
	private ILicenseCapacityCategory licenseCapacityCategory;
	
	/**
	 * ??????
	 */
	private Logger logger = Logger.getLogger(BizServiceAction.class);
	
	/**
	 * ??????????????????
	 * @param bizServiceVo
	 * @return
	 */
	@RequestMapping("/insert")
	public JSONObject insert(@ModelAttribute BizServiceBo bizServiceBo,HttpSession session){
		try {
			
			if(!licenseCalcService.isLicenseEnough(LicenseModelEnum.stmMonitorBusi)){
				logger.error("portal.business.service.insert bussiness license over flow");
				return toFailForLicenseOverFlow("?????????????????????license????????????,??????????????????!");
			}
			
			if(null==bizServiceBo.getFileId()||bizServiceBo.getFileId()==0){
				bizServiceBo.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_MAIN_IMG.getFileId());
			}
			bizServiceBo.setCreaterId(String.valueOf(getLoginUser(session).getId()));
			Long count = bizServiceApi.insert(bizServiceBo);
			if(count == -1l){
				logger.info("portal.business.service.insert failure for same name");
				return toFailForGroupNameExsit(null);
			}else{
				logger.info("portal.business.service.insert successful");
				return toSuccess(count);
			}
		} catch (Exception e) {
			logger.error("portal.business.service.insert failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"????????????????????????");
		}
	}
	

	/**
	 * ??????????????????
	 * @param id
	 * @return
	 */
	@RequestMapping("/del")
	public JSONObject del(int id){
		try {
			int count = bizServiceApi.del(id);
			logger.info("portal.business.service.del successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.business.service.del failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????????????????");
		}
	}
	
	/**
	 * ??????????????????
	 * @param bizServiceVo
	 * @param request
	 * @return
	 * @throws Exception 
	 * @throws InstancelibException 
	 */
	@RequestMapping("/update")
	public JSONObject update(@ModelAttribute BizServiceBo bizServiceBo,HttpServletRequest request){
		try {
			if(!StringUtils.isEmpty(bizServiceBo.getTopology())){
				if(!StringUtils.isEmpty(request.getContextPath())){
					//??????????????????????????????
					bizServiceBo.setTopology(bizServiceBo.getTopology().replace(request.getContextPath(), replaceHolder));
				}else{
					if(!bizServiceBo.getTopology().contains(replaceHolder)){
						bizServiceBo.setTopology(bizServiceBo.getTopology().replace(FILE_REQUEST, replaceHolder+FILE_REQUEST));
					}
				}
			}
			int count = bizServiceApi.update(bizServiceBo);
			if(count == -1){
				logger.info("portal.business.service.update failure for same name");
				return toFailForGroupNameExsit(null);
			}else{
				logger.info("portal.business.service.update successful");
				return toSuccess(count);
			}
		} catch (Exception e) {
			logger.error("portal.business.service.update failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????????????????");
		}
	}
	
	/**
	 * ????????????????????????
	 * @param bizServiceBo
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateStatusType")
	public JSONObject updateStatusType(@ModelAttribute BizServiceBo bizServiceBo,HttpServletRequest request){
		try {
			BizServiceBo bo = bizServiceApi.get(bizServiceBo.getId());
			bizServiceBo.setStatus(bo.getStatus());
			bizServiceBo.setTopology(bo.getTopology());
			bizServiceBo.setOldName(bo.getName());
			bizServiceBo.setName(bo.getName());
			int count = bizServiceApi.update(bizServiceBo);
			logger.info("portal.business.service.updateStatusType successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.business.service.updateStatusType failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "??????????????????????????????");
		}
	}
	/**
	 * ??????ID????????????????????????
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping("/get")
	public JSONObject get(int id,HttpServletRequest request){
		try {
			BizServiceBo bizServiceBo = bizServiceApi.get(id);
			if(bizServiceBo!=null && !StringUtils.isEmpty(bizServiceBo.getTopology())){
				bizServiceBo.setTopology(bizServiceBo.getTopology().replace(replaceHolder, request.getContextPath()));
			}
			logger.info("portal.business.service.get successful");
			//???????????????????????????(?????????...)
			bizServiceBo.setOldName(bizServiceBo.getName());
			bizServiceApi.update(bizServiceBo);
			return toSuccess(bizServiceBo);
		} catch (Exception e) {
			logger.error("portal.business.service.get failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????????????????");
		}
	}
	
	/**
	 * ??????????????????????????????
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/getList")
	public JSONObject getList(HttpServletRequest request,HttpSession session){
		try {
			ILoginUser user = getLoginUser(session);
			List<BizServiceBo> bizServiceBoList = bizServiceApi.getList();
			List<BizServiceBo> list = new ArrayList<BizServiceBo>();
			for(BizServiceBo bo : bizServiceBoList){
				if(!StringUtils.isEmpty(bo.getTopology())){
					bo.setTopology(bo.getTopology().replace(replaceHolder, request.getContextPath()));
				}
				//???????????????????????????(?????????...)
				bo.setOldName(bo.getName());
				bizServiceApi.update(bo);
				if(isShowByPrivilege(user, bo)) list.add(bo);
			}
			Map result = new HashMap();
			result.put("total", list.size());
			result.put("rows", list);
			logger.info("portal.business.service.getList successful!");
			return toSuccess(result);
		} catch (Exception e) {
			logger.error("portal.business.service.getList failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "??????????????????list??????");
		}
	}
	
	/**
	 * ????????????????????????????????????
	 * @param request
	 * @return
	 */
	@RequestMapping("/getListPage")
	public JSONObject getListPage(Page<BizServiceBo, Object> page, HttpSession session,HttpServletRequest request){
		try {
			logger.error(" malachi ????????????getListPage");
			ILoginUser user = getLoginUser(session);
			bizServiceApi.getListPage(page);
			List<BizServiceBo> list = new ArrayList<BizServiceBo>();
			for(BizServiceBo bo : page.getRows()){
				if(!StringUtils.isEmpty(bo.getTopology())){
					bo.setTopology(bo.getTopology().replace(replaceHolder, request.getContextPath()));
				}
				//???????????????????????????(?????????...)
				bo.setOldName(bo.getName());
				bizServiceApi.update(bo);
				if(isShowByPrivilege(user, bo)) list.add(bo);
			}
			page.setDatas(list);
			return toSuccess(page);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return toJsonObject(299, "??????????????????list??????");
		}
	}
	
	/**
	 * ?????????????????????????????????(????????????)
	 * @param user ??????????????????
	 * @param bo ????????????
	 * @return
	 */
	private boolean isShowByPrivilege(ILoginUser user,BizServiceBo bo){
		boolean flag = false;
		if(user.isSystemUser() || user.isDomainUser() || user.isManagerUser()){
			//????????????????????????????????????????????????????????????
			flag = true;
		}
		return flag;
	}
	/**
	 * ????????????????????????
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/getCategory")
	public JSONObject getCategory(String type,String level,String categoryId){
		try {
			List<CategoryVo> categoryVos = new ArrayList<CategoryVo>();
			searchCategory(capacityService.getRootCategory(), categoryVos,type,level,null);
			//??????????????????
			if(!"".equals(categoryId) && null != categoryId){
				List<CategoryVo> vos = new ArrayList<CategoryVo>();
				for(CategoryVo vo: categoryVos){
					if(vo.getPid().equals(categoryId))
						vos.add(vo);
				}
				return toSuccess(vos);
			}
			logger.info("portal.business.service.getCategory successful!");
			return toSuccess(categoryVos);
		} catch (Exception e) {
			logger.error("portal.business.service.getCategory failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "??????????????????????????????");
		}
	}
	/**
	 * ????????????
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/getResources")
	public JSONObject getResources(String type,String level,
			String categoryId,String resourceId,String ipAddress){
		try {
			List<CategoryVo> categoryVos = new ArrayList<CategoryVo>();
			searchCategory(capacityService.getRootCategory(), categoryVos,type,level,categoryId);
			List<ResourceInstanceVo> resourceInstanceVos = new ArrayList<ResourceInstanceVo>();
			List<ResourceInstance> instances = null;
			instances = resourceInstanceService.getParentResourceInstanceByLifeState(InstanceLifeStateEnum.MONITORED);
			ResourceInstanceVo rvo = null;
			for(CategoryVo cvo: categoryVos){
				rvo = new ResourceInstanceVo();
				rvo.setId(cvo.getId());
				rvo.setText(cvo.getName());
				rvo.setState("closed");
				rvo.setChecked(false);
				Map<String, String> attributes = new HashMap<String, String>();
				if(null!=resourceId && !"".equals(resourceId)){
					if(categoryId.equals(CapacityConst.VM)){
						String vmCategory = capacityService.getCategoryById(cvo.getPid()).getParentCategory().getId();
						if(!vmCategory.equals(resourceId)){
							continue;
						}
					}else{
						if(!resourceId.equals(cvo.getPid())){
							continue;
						}
					}
				}
				attributes.put("parentCategoryId", capacityService.getCategoryById(cvo.getPid()).getParentCategory().getId());
				attributes.put("pid", cvo.getPid());
				attributes.put("ipAddress", "");
				rvo.setAttributes(attributes);
				List<ResourceInstanceVo> children = new ArrayList<ResourceInstanceVo>();
				for(ResourceInstance instance: instances){
					if(!instance.getResourceId().equals(cvo.getId()))
						continue;
					ResourceInstanceVo instanceVo = new ResourceInstanceVo();
					instanceVo.setId(String.valueOf(instance.getId()));
					instanceVo.setText(instance.getShowName());
					instanceVo.setState("open");
					instanceVo.setChecked(false);
					attributes = new HashMap<String, String>();
					attributes.put("pid", instance.getResourceId());
					
					if(null!=ipAddress && !"".equals(ipAddress)){
						if(instance.getShowIP()!=null && !instance.getShowIP().toLowerCase().contains(ipAddress.trim().toLowerCase()))
						continue;
					}
					attributes.put("ipAddress", instance.getShowIP());
					attributes.put("resourceId", instance.getResourceId());
					if(null == instanceStateService.getState(instance.getId())){
						attributes.put("status", InstanceStateEnum.NORMAL.name());
					}else{
						attributes.put("status", instanceStateService.getState(instance.getId()).getState().name());
					}
					instanceVo.setAttributes(attributes);
					children.add(instanceVo);
				}
				if(children.size()==0) continue;
				rvo.setChildren(children);
				resourceInstanceVos.add(rvo);
			}
			logger.info("portal.business.service.getResources successful!");
			return toSuccess(resourceInstanceVos);
		} catch (Exception e) {
			logger.error("portal.business.service.getResources failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????????????????");
		}
	}
	/**
	 * ?????????????????????category
	 * @param categoryDef
	 * @param categoryVos
	 * @param type
	 * @param level
	 * @param categoryId
	 */
	private void searchCategory(CategoryDef categoryDef,
			List<CategoryVo> categoryVos,String type,String level,String categoryId) throws Exception{
		if(!licenseCapacityCategory.isAllowCategory(categoryDef.getId())){
			return;
		}
		CategoryVo categoryVo = new CategoryVo();
		if (null != categoryDef.getParentCategory() && "category".equals(type)) {
			categoryVo.setId(categoryDef.getId());
			categoryVo.setName(categoryDef.getName());
			categoryVo.setType(type);
			categoryVo.setPid(categoryDef.getParentCategory().getId());
			categoryVo.setLevel(level);
			if(("Resource".equals(categoryDef.getParentCategory().getId()) && "1".equals(level))
					|| (!"Resource".equals(categoryDef.getParentCategory().getId()) && "2".equals(level)))
				categoryVos.add(categoryVo);
		}
		// ??????????????????child category
		if (null == categoryDef.getChildCategorys() && "resource".equals(type)) {
			if (null != categoryDef.getResourceDefs()) {
				
				boolean compareResult = false;
				
				if(categoryId.equals(CapacityConst.VM)){
					compareResult = categoryId.equals(categoryDef.getParentCategory().getParentCategory().getId());
				}else{
					compareResult = categoryId.equals(categoryDef.getParentCategory().getId());
				}
				
				if((categoryId!=null && compareResult)
						|| "".equals(categoryId)){
					ResourceDef[] resourceDefs = categoryDef.getResourceDefs();
					for (int i = 0; i < resourceDefs.length; i++) {
						ResourceDef resourceDef = resourceDefs[i];
						categoryVo = new CategoryVo();
						categoryVo.setId(resourceDef.getId());
						categoryVo.setName(resourceDef.getName());
						categoryVo.setType(type);
						categoryVo.setLevel("3");
						categoryVo.setPid(categoryDef.getId());
						if(categoryVo.getLevel().equals(level)) 
							categoryVos.add(categoryVo);
					}
				}
			}
		} else {
			CategoryDef[] categoryDefs = categoryDef.getChildCategorys();
			if (null != categoryDefs) {
				for (int i = 0; i < categoryDefs.length; i++) {
					searchCategory(categoryDefs[i], categoryVos,type,level,categoryId);
				}
			}
		}
	}
	/**
	 * ??????????????????????????????????????????
	 * @param data
	 * @param request
	 * @return
	 * @throws InstancelibException
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/updateTopologyAndSnapshoot")
	public JSONObject updateTopologyAndSnapshoot(String data,HttpServletRequest request){
		BizServiceBo bizServiceBo = null;
		try {
			Map<String, Object> map = JSONObject.parseObject(data,Map.class);
			bizServiceBo = bizServiceApi.get(Long.parseLong(map.get("id").toString()));
			bizServiceBo.setTopology(map.get("topology").toString());
			if(!StringUtils.isEmpty(bizServiceBo.getTopology())){
				//??????????????????????????????
				if(!StringUtils.isEmpty(request.getContextPath())){
					bizServiceBo.setTopology(bizServiceBo.getTopology().replace(request.getContextPath(), replaceHolder));
				}else{
					if(!bizServiceBo.getTopology().contains(replaceHolder)){
						bizServiceBo.setTopology(bizServiceBo.getTopology().replace(FILE_REQUEST, replaceHolder+FILE_REQUEST));
					}
				}
			}
			bizServiceBo.setOldName(bizServiceBo.getName());
			bizServiceApi.update(bizServiceBo);
			logger.info("portal.business.service.updateTopologyAndSnapshoot successful!");
			return toSuccess(bizServiceBo);
		} catch (Exception e) {
			logger.error("portal.business.service.updateTopologyAndSnapshoot failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????????????????????????????????????????");
		}
	}
	/**
	 * ???????????????????????????svg(??????????????????)
	 * @return
	 */
	@RequestMapping("/getSnapshootList")
	public JSONObject getSnapshootList(HttpServletRequest request,HttpSession session){
		try {
			ILoginUser user = getLoginUser(session);
			List<BizServiceBo> bizServiceBos = bizServiceApi.getList();
			List<BizServiceBo> result = new ArrayList<BizServiceBo>();
			for(BizServiceBo bo:bizServiceBos){
//				if(!StringUtils.isEmpty(bo.getSvg())){
//					bo.setSvg(bo.getSvg().replace(replaceHolder,request.getContextPath() ));
//				}
				if(!StringUtils.isEmpty(bo.getTopology())){
					bo.setTopology(bo.getTopology().replace(replaceHolder,request.getContextPath() ));
				}
				if(isShowByPrivilege(user, bo)) result.add(bo);
			}
			logger.info("portal.business.service.getSnapshootList successful!");
			return toSuccess(result);
		} catch (Exception e) {
			logger.error("portal.business.service.getSnapshootList failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????????????????list??????");
		}
	}
	/**
	 * ?????????????????????????????????????????????
	 * @param page ????????????
	 * @param bo ????????????
	 * @param status ??????
	 * @return
	 */
	@RequestMapping("/selectWarnViewPage")
	public JSONObject selectWarnViewPage(Page<BizWarnViewBo, BizWarnViewBo> page,
			BizServiceBo bo,String status){
		try {
			bizServiceApi.selectWarnViewPage(page,bo,status);
			logger.info("selectWarnViewPage by "+bo.getName()+"("+bo.getId()+")"+" successful!");
			return toSuccess(page);
		} catch (Exception e) {
			logger.error("portal.business.service.selectWarnViewPage failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????????????????");
		}
	}
	/**
	 * ????????????????????????????????????(????????????????????????)
	 * @param userPage
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/getSystemUsers")
	public JSONObject getSystemUsers(){
		try {
			List<User> datas = bizServiceApi.getSystemUsers();
			List<User> result = new ArrayList<User>();
			if(null!=datas && datas.size()>0){
				for(User u : datas){
					if(u.getId()!=1l)
						result.add(u);
				}
			}
			Map map = new HashMap();
			map.put("total", result.size());
			map.put("rows", result);
			logger.info("portal.business.service.getSystemUsers successful!");
			return toSuccess(map);
		} catch (Exception e) {
			logger.error("portal.business.service.getSystemUsers failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "??????????????????");
		}
	}
	/**
	 * ??????????????????(???????????????????????????????????????????????????)
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getNewlyState")
	public JSONObject getNewlyState(Long id){
		try {
			List result = bizServiceApi.getNewlyState(id);
			logger.info("portal.business.service.getNewlyState successful!");
			return toSuccess(result);
		} catch (Exception e) {
			logger.error("portal.business.service.getNewlyState failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "?????????????????????????????????????????????");
		}
	}
	/**
	 * ??????????????????????????????????????????????????????????????????????????????????????????
	 * @param ids
	 * @param resourceIds
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getNewlyStateByIds")
	public JSONObject getNewlyStateByIds(Long[] ids,Long[] resourceIds){
		try {
			List result = bizServiceApi.getNewlyStateByIds(ids,resourceIds);
			logger.info("portal.business.service.getNewlyStateByIds successful!");
			return toSuccess(result);
		} catch (Exception e) {
			logger.error("portal.business.service.getNewlyStateByIds failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "????????????(????????????)?????????????????????????????????????????????");
		}
	}
}
