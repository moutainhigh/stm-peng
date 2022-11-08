package com.mainsteam.stm.home.workbench.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import com.mainsteam.stm.system.resource.bo.CategoryBo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.home.layout.api.HomeLayoutModuleApi;
import com.mainsteam.stm.home.workbench.resource.api.IResourceApi;
import com.mainsteam.stm.home.workbench.resource.bo.PageResource;
import com.mainsteam.stm.home.workbench.resource.bo.WorkbenchResourceInstance;
import com.mainsteam.stm.home.workbench.resource.web.vo.CustomGroupVo;
import com.mainsteam.stm.home.workbench.resource.web.vo.zTreeVo;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.bo.ResourceViewBo;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.login.bo.LoginUser;

/**
 * <li>文件名称: ResourceAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年9月18日
 * @author ziwenwen
 */
@Controller
@RequestMapping("/home/workbench/resource")
public class ResourceAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(ResourceAction.class);
	@Autowired
	IResourceApi resourceApi;
	
	@Autowired
	@Qualifier("customResourceGroupApi")
	private ICustomResourceGroupApi resourceGroupApi;
	
	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;
	
	@Resource
	private HomeLayoutModuleApi homeLayoutModuleApi;
	
	@Resource
	private ResourceCategoryApi resourceCategoryApi;
	
	@Resource
	private CapacityService capacityService;
	/**
	 * 获取所有资源种类
	 * 
	 * @return
	 */
	@RequestMapping("/getAllCategory")
	public JSONObject getAllCategory() {
		return toSuccess(resourceApi.getAllCategory());
	}

	/**
	 * 根据资源种类id获取资源列表
	 * 
	 * @param catogaryId
	 * @return
	 */
	@RequestMapping("/getResources")
	public JSONObject getResources(String categoryId) {
		return toSuccess(resourceApi.getResources(categoryId));
	}

	@RequestMapping("/getAll")
	public JSONObject getTotal(String typeId, Long ... domainId) {
		PageResource list = resourceApi.getResourceCount(typeId,getLoginUser(), domainId);
		return toSuccess(list);
	}
	
	@RequestMapping("/getByTypeIds")
	public JSONObject getTotal(String[] typeIds, Long ... domainId) {
		ILoginUser user = getLoginUser();
		if(domainId != null){
			//domainId[0] ==-1 表示是获取当前用户所有的域
			if(domainId[0] == -1){
				Set<IDomain> dms = user.getDomains();
				Long dmIds[] = new Long[dms.size()];
				int i=0;
				for (IDomain dm : dms) {
					dmIds[i++] = dm.getId();
				}
				domainId = dmIds;
			}
		}
		JSONObject output = new JSONObject();
		for (int i = 0; i < typeIds.length; i++) {
			if(typeIds[i].equals(""))
				continue;
			PageResource list = resourceApi.getResourceCount(typeIds[i],getLoginUser(), domainId);
			output.put(typeIds[i], list);
		}
		
		return toSuccess(output);
	}
	
	/**
	* @Title: getResourceGroup
	* @Description: 获取当前合登录用户下的自定义资源组
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getResourceGroupByUser")
	public JSONObject getResourceGroupByUser(HttpSession session){
		LoginUser user = (LoginUser) session.getAttribute(LoginUser.SESSION_LOGIN_USER);//获取当前登录用户
		List<CustomGroupBo> customGroupBos = resourceGroupApi.getList(user.getId());
		return toSuccess(customGroupBos);
	}
	
	/**
	* @Title: getResourceGroup
	* @Description: 获取当前合登录用户下的自定义资源组
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getResourceGroupByUser2Tree")
	public JSONObject getResourceGroupByUser2Tree(HttpSession session){
		LoginUser user = (LoginUser) session.getAttribute(LoginUser.SESSION_LOGIN_USER);//获取当前登录用户
		
		List<CustomGroupBo> list=resourceGroupApi.getList(user.getId());
		
		List<CustomGroupVo> voList = new ArrayList<CustomGroupVo>();
		
		for(CustomGroupBo bo : list){
			CustomGroupVo vo = this.toVo(bo);
			voList.add(vo);
		}
		
		List<CustomGroupVo> newVoList = new ArrayList<CustomGroupVo>();
		for(CustomGroupVo vo : voList){
			if(vo.getPid() == null){
				voList2Tree(vo, voList);
				newVoList.add(vo);
			}
		}
		return toSuccess(newVoList);
	}
	
	/**
	 * 获取资源实例
	 */
	@RequestMapping("/getResourceInstanceList")
	public JSONObject getResourceInstanceList(String queryId,int type,Long[] domainId,int startNum,int pageSize,String content,HttpSession session) {
		List<WorkbenchResourceInstance> defs = new ArrayList<WorkbenchResourceInstance>();
		if(domainId.length > 0 && Integer.parseInt(domainId[0].toString()) == -1){
			ILoginUser user = getLoginUser();
			Set<IDomain> dms = user.getDomains();
			Long dmIds[] = new Long[dms.size()];
			int i=0;
			for (IDomain dm : dms) {
				dmIds[i++] = dm.getId();
			}
			domainId = dmIds;
		}
		if(type == 1){
			//通过类别获取资源实例
			defs = resourceApi.getResourceInstanceByCategoryId(queryId,domainId,startNum,pageSize,content,getLoginUser(session));
		}else if(type == 2){
			//通过资源获取资源实例
			defs = resourceApi.getInstanceByResource(queryId,domainId,startNum,pageSize,content,getLoginUser(session));
		}
		return toSuccess(defs);
		
	}
	
	/**
	 * 获取指标
	 * @param instanceId
	 * @param metricType 多个指标类型
	 * @return
	 */
	@RequestMapping(value= "/getMetric")
	public JSONObject getMetric(Long instanceId, String... metricTypes){
		List<Map<String, Object>> metricData = new ArrayList<>();
		if(metricTypes != null)
		for (int i = 0; i < metricTypes.length; i++) {
			String mt = metricTypes[i];
			if(mt.equals(""))
				continue;
			metricData.addAll(resourceDetailInfoApi.getMetricByType(instanceId, mt,false));
		}
		
		return toSuccess(metricData);
	}
	
	/**
	 * 获取多个资源指标
	 * @param instanceIds
	 * @param metricType 多个指标类型
	 * @return
	 */
	@RequestMapping(value= "/getMetrics")
	public JSONObject getMetrics(Long[] instanceIds, String... metricTypes){
		HashMap<Long,Object> output = new HashMap<>();
		for(Long instanceId :instanceIds){
			List<Map<String, Object>> metricData = new ArrayList<>();
			if(metricTypes != null)
			for (int i = 0; i < metricTypes.length; i++) {
				String mt = metricTypes[i];
				if(mt.equals(""))
					continue;
				metricData.addAll(resourceDetailInfoApi.getMetricByType(instanceId, mt,false));
			}
			output.put(instanceId, metricData);
		}
		
		return toSuccess(output);
	}
	
	/**
	 * 获取多个资源指标
	 * @param instanceIds
	 * @param metricType 单个指标类型
	 * @return
	 */
	@RequestMapping(value= "/getNewMetrics")
	public JSONObject getNewMetrics(String[] ids, String metricTypes){
		Map<String, Object> output = new HashMap<String, Object>();
		Map<String, Object> insInfo = null;
		if(ids.length <= 0){
			return toSuccess(output);
		}else{
			Map<String, Object> resourceInfo = null;
			for(String id : ids){
				insInfo = new HashMap<String, Object>();
				resourceInfo = resourceDetailInfoApi.getResourceInfo(Long.parseLong(id));
				if((boolean) resourceInfo.get("isExist") == false){
					insInfo.put("isExist", false);
				}else{
					if("NOT_MONITORED".equals(resourceInfo.get("lifeState").toString())){
						insInfo.put("isExist", false);
					}else{
						insInfo.put("isExist", true);
					}
					InstanceStateData state  = (InstanceStateData) resourceInfo.get("state");
					
					if(InstanceStateEnum.CRITICAL != state.getState()){
						insInfo.put("statusCollection", true);
					}else{
						insInfo.put("statusCollection", false);
					}
				}
				output.put(id, insInfo);
			}
		}
//		output = resourceDetailInfoApi.getResourceConnectivity(ids, metricTypes);
		return toSuccess(output);
	}
	
	/**
	 * 获取除去指定id集合的资源列表(带搜索关键字)
	 * @return
	 */
	@RequestMapping("/getNewLeftResourceDefBySearchContent")
	public JSONObject getNewLeftResourceDefBySearchContent(String ids,String searchContent,int startNum,int pageSize,HttpSession session) {
		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getNewExceptResourceInstanceListByIdsAndSearchContent(ids,getLoginUser(session),searchContent, startNum, pageSize);
		
		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);
		
		List<ResourceViewBo>  resourceViewList = this.filterResourceType(treeList,startNum,pageSize);
		
		return toSuccess(JSONObject.toJSON(resourceViewList));

	}
	
	@RequestMapping("/getResourceDefBySearchContent")
	public JSONObject getResourceDefBySearchContent(String ids,String searchContent,HttpSession session) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getExceptResourceInstanceListByIdsAndSearchContent(ids,getLoginUser(session),searchContent);

		List<zTreeVo> treeList = this.getTreeListByAllResourceInstanceList(resourceInstanceList, true);

		//组装虚拟化资源
        treeList = this.treeListHome(treeList);

        //屏蔽视频监控
        if(treeList != null && treeList.size() > 0){
            for(zTreeVo zv : treeList){
                if("VideoSurveillance".equals(zv.getId())){
                    treeList.remove(zv);
                    break;
                }
            }
        }


        return toSuccess(JSONObject.toJSON(treeList));

	}

	private List<zTreeVo> treeListHome(List<zTreeVo> treeList){
        //获取虚拟化分类
        CategoryBo allCategory = resourceApi.getAllCategory();
        CategoryBo vmCategoryBo = null;
        for(CategoryBo categoryBo : allCategory.getChildren()){
            if("VM".equals(categoryBo.getId())){
                vmCategoryBo = categoryBo;
                break;
            }
        }
        //创建虚拟化
        zTreeVo vmZt = new zTreeVo();
        vmZt.setId("VM");
        vmZt.setIsParent(true);
        vmZt.setName("虚拟化资源");
        vmZt.setNocheck(false);
        vmZt.setOpen(false);
        vmZt.setPId("0");
        List<zTreeVo> list = new ArrayList<zTreeVo>(treeList.size());
        for(zTreeVo zv : treeList){
            list.add(zv);
            for(CategoryBo categoryBo : vmCategoryBo.getChildren()){
                if(zv.getId().equals(categoryBo.getId())){
                    List<zTreeVo> children = vmZt.getChildren();
                    if(children == null){
                        children = new ArrayList<zTreeVo>(vmCategoryBo.getChildren().length);
                    }
                    zv.setPId("VM");
                    children.add(zv);
                    vmZt.setChildren(children);
                    list.remove(zv);
                }
            }
        }
        if(vmZt.getChildren() != null && vmZt.getChildren().size() > 0){
            list.add(vmZt);
        }

        treeList = list;

        return treeList;
    }
	
	
	/**
	 * voList转化成树
	 * @param parentVo
	 * @param voList
	 */
	private void voList2Tree(CustomGroupVo parentVo, List<CustomGroupVo> voList){
		List<CustomGroupVo> childCustomGroupVo = new ArrayList<CustomGroupVo>();
		for(int i = 0; i < voList.size(); i++){
			CustomGroupVo vo = voList.get(i);
			if(vo.getPid() != null && parentVo.getId().longValue() == vo.getPid().longValue()){
				childCustomGroupVo.add(vo);
				voList2Tree(vo, voList);
			}
		}
		parentVo.setChildCustomGroupVo(childCustomGroupVo);
	}

	/**
	 * 对象转化
	 * @param bo
	 * @return
	 */
	public CustomGroupVo toVo(CustomGroupBo bo){
		CustomGroupVo vo=new CustomGroupVo();
		vo.setId(bo.getId());
		vo.setName(bo.getName());
		vo.setResourceInstanceIds(bo.getResourceInstanceIds());
		vo.setEntryId(bo.getEntryId());
		vo.setEntryDatetime(bo.getEntryDatetime());
		vo.setGroupType(bo.getGroupType().toString());
		vo.setPid(bo.getPid());
		return vo;
	}
	
	// 通过resourceInstance列表构建树结构(两级类别，一级资源)
	// 
	private List<zTreeVo> getTreeListByResourceInstanceList(List<ResourceInstanceBo> instanceList, boolean isFilterDisplay) {
		List<zTreeVo> treeOneList = new ArrayList<zTreeVo>();
		List<zTreeVo> treeTwoList = new ArrayList<zTreeVo>();
		
		List<ResourceInstanceBo> filterList = new ArrayList<ResourceInstanceBo>();
		
		Map<String, List<zTreeVo>> secondTreeAndInstanceMap = new HashMap<String, List<zTreeVo>>();

		Map<String, List<zTreeVo>> firstTreeAndSecondTreeMap = new HashMap<String, List<zTreeVo>>();

		if (instanceList == null || instanceList.size() <= 0) {
			return treeOneList;
		}
		for(ResourceInstanceBo re : instanceList){
			if(InstanceLifeStateEnum.MONITORED.equals(re.getLifeState())){
				filterList.add(re);
			}
		}
		if(filterList.size() <= 0){
			return treeOneList;
		}
		
		for (int i = 0; i < filterList.size(); i++) {

			ResourceInstanceBo resourceInstance = filterList.get(i);
			if (resourceInstance.getCategoryId() == null) {
				if(logger.isErrorEnabled()){
					logger.error("resourceInstance getCategoryId is null ,resourceId : " + resourceInstance.getId());
				}
			}

			// 根据资源实例ID获取二级类别
			CategoryDef secondCategory = capacityService
					.getCategoryById(resourceInstance.getCategoryId());
			// 根据一级类别获取二级类别
			if(secondCategory == null){
				if(logger.isErrorEnabled()){
					logger.error("capacityService.getCategoryById() error,id = " + resourceInstance.getCategoryId()); 
				}
				continue;
			}
			CategoryDef firstCategory = secondCategory.getParentCategory();
			
			//过滤虚拟化资源
			if(!DefIsdisplay(firstCategory) && isFilterDisplay) {
				continue;
			}
			zTreeVo instanceTree = this.defTozTreeVo(resourceInstance, false,
					secondCategory.getId());

			zTreeVo secondTree = this.defTozTreeVo(secondCategory, true,
					firstCategory.getId());

			zTreeVo firstTree = this.defTozTreeVo(firstCategory, true, "0");

			// 判断secondTreeAndInstanceMap中是否存在二级类别ID的数据
			if (!secondTreeAndInstanceMap.containsKey(secondTree.getId())) {

				secondTreeAndInstanceMap.put(secondTree.getId(),
						new ArrayList<zTreeVo>());

			}

			secondTreeAndInstanceMap.get(secondTree.getId()).add(instanceTree);

			if (!treeTwoList.contains(secondTree)) {

				treeTwoList.add(secondTree);

			}

			if (!treeOneList.contains(firstTree)) {
				treeOneList.add(firstTree);
			}

		}

		// 构建二级树
		for (zTreeVo secondTree : treeTwoList) {

			secondTree.setChildren(secondTreeAndInstanceMap.get(secondTree
					.getId()));

			if (!firstTreeAndSecondTreeMap.containsKey(secondTree.getPId())) {

				firstTreeAndSecondTreeMap.put(secondTree.getPId(),
						new ArrayList<zTreeVo>());

			}

			firstTreeAndSecondTreeMap.get(secondTree.getPId()).add(secondTree);

		}

		// 构建一级树
		for (zTreeVo firstTree : treeOneList) {

			firstTree.setChildren(firstTreeAndSecondTreeMap.get(firstTree
					.getId()));

		}

		return treeOneList;

	}
	//不过滤未监控
	private List<zTreeVo> getTreeListByAllResourceInstanceList(List<ResourceInstanceBo> instanceList, boolean isFilterDisplay) {
		List<zTreeVo> treeOneList = new ArrayList<zTreeVo>();
		List<zTreeVo> treeTwoList = new ArrayList<zTreeVo>();
		
		List<ResourceInstanceBo> filterList = new ArrayList<ResourceInstanceBo>();
		
		Map<String, List<zTreeVo>> secondTreeAndInstanceMap = new HashMap<String, List<zTreeVo>>();

		Map<String, List<zTreeVo>> firstTreeAndSecondTreeMap = new HashMap<String, List<zTreeVo>>();

		if (instanceList == null || instanceList.size() <= 0) {
			return treeOneList;
		}
//		for(ResourceInstanceBo re : instanceList){
//			if(InstanceLifeStateEnum.MONITORED.equals(re.getLifeState())){
//				filterList.add(re);
//			}
//		}
		filterList = instanceList;
		if(filterList.size() <= 0){
			return treeOneList;
		}
		
		for (int i = 0; i < filterList.size(); i++) {

			ResourceInstanceBo resourceInstance = filterList.get(i);
			if (resourceInstance.getCategoryId() == null) {
				if(logger.isErrorEnabled()){
					logger.error("resourceInstance getCategoryId is null ,resourceId : " + resourceInstance.getId());
				}
			}

			// 根据资源实例ID获取二级类别
			CategoryDef secondCategory = capacityService
					.getCategoryById(resourceInstance.getCategoryId());
			// 根据一级类别获取二级类别
			if(secondCategory == null){
				if(logger.isErrorEnabled()){
					logger.error("capacityService.getCategoryById() error,id = " + resourceInstance.getCategoryId()); 
				}
				continue;
			}
			CategoryDef firstCategory = secondCategory.getParentCategory();
			
			//过滤虚拟化资源
			/*if(!DefIsdisplay(firstCategory) && isFilterDisplay) {
				continue;
			}
			//*/
			
			zTreeVo instanceTree = this.defTozTreeVo(resourceInstance, false,
					secondCategory.getId());

			zTreeVo secondTree = this.defTozTreeVo(secondCategory, true,
					firstCategory.getId());

			zTreeVo firstTree = this.defTozTreeVo(firstCategory, true, "0");

			// 判断secondTreeAndInstanceMap中是否存在二级类别ID的数据
			if (!secondTreeAndInstanceMap.containsKey(secondTree.getId())) {

				secondTreeAndInstanceMap.put(secondTree.getId(),
						new ArrayList<zTreeVo>());

			}

			secondTreeAndInstanceMap.get(secondTree.getId()).add(instanceTree);

			if (!treeTwoList.contains(secondTree)) {

				treeTwoList.add(secondTree);

			}

			if (!treeOneList.contains(firstTree)) {
				treeOneList.add(firstTree);
			}

		}

		// 构建二级树
		for (zTreeVo secondTree : treeTwoList) {

			secondTree.setChildren(secondTreeAndInstanceMap.get(secondTree
					.getId()));

			if (!firstTreeAndSecondTreeMap.containsKey(secondTree.getPId())) {

				firstTreeAndSecondTreeMap.put(secondTree.getPId(),
						new ArrayList<zTreeVo>());

			}

			firstTreeAndSecondTreeMap.get(secondTree.getPId()).add(secondTree);

		}

		// 构建一级树
		for (zTreeVo firstTree : treeOneList) {

			firstTree.setChildren(firstTreeAndSecondTreeMap.get(firstTree
					.getId()));

		}

		return treeOneList;

	}
	
	
	private List<ResourceViewBo> filterResourceType(List<zTreeVo> treeList,int startNum,int pageSize){
		List<zTreeVo> zlist = new ArrayList<zTreeVo>();

		for(zTreeVo zTreeVo : treeList){
			if("Host".equals(zTreeVo.getId()) || "NetworkDevice".equals(zTreeVo.getId())){
				zlist.add(zTreeVo);
			}
		}
		
		List<ResourceViewBo> list = new ArrayList<ResourceViewBo>();
		ResourceViewBo re = null;
		for(int i=0;i<zlist.size();++i){
			zTreeVo zTreeVo =  zlist.get(i);
			if(zTreeVo.getIsParent()){
				List<zTreeVo> childs = zTreeVo.getChildren();
				for(int j =0;j < childs.size(); ++j){
					zTreeVo tree = childs.get(j);
    				tree.setResourceType(zTreeVo.getName());
		    	}
				List<ResourceViewBo> tp = regroupResource(childs);
				list.addAll(tp);
			}else{
				re = new ResourceViewBo();
				String name = zTreeVo.getName();
				int idx = name.indexOf(")");
				String ip = name.substring(1,idx);
				String realname = name.substring(idx+1);
				re.setId(zTreeVo.getId());
				re.setIsParent(zTreeVo.getIsParent());
				re.setName(realname);
				re.setNocheck(zTreeVo.getNocheck());
				re.setOpen(zTreeVo.getOpen());
				re.setpId(zTreeVo.getPId());
				re.setIp(ip);
				re.setResourceType(zTreeVo.getResourceType());
				
				list.add(re);
			}

		}
		
		if((startNum + pageSize) > list.size()){
			list = list.subList(startNum, list.size());
		}else{
			list = list.subList(startNum, (startNum + pageSize));
		}
		
		
		return list;
	}
	private zTreeVo defTozTreeVo(Object def, boolean isParent, String pid) {

		zTreeVo tree = new zTreeVo();

		if (def instanceof ResourceInstanceBo) {

			ResourceInstanceBo instance = (ResourceInstanceBo) def;
			tree.setId(instance.getId() + "");
			tree.setIsParent(isParent);
			String showName = "";
			if(instance.getShowName() != null){
				showName = instance.getShowName();
			}
			String ip = "";
			if(instance.getDiscoverIP() != null){
				ip = instance.getDiscoverIP();
			}
			tree.setName("("+ip + ")" + showName );
			tree.setNocheck(false);
			tree.setOpen(false);
			tree.setPId(pid);

		} else if (def instanceof CategoryDef) {

			CategoryDef instance = (CategoryDef) def;
			tree.setId(instance.getId());
			tree.setIsParent(isParent);
			tree.setName(instance.getName());
			tree.setNocheck(false);
			tree.setOpen(false);
			tree.setPId(pid);

		}

		return tree;

	}
	private List<ResourceViewBo> regroupResource(List<zTreeVo> treeList){
		List<ResourceViewBo> list = new ArrayList<ResourceViewBo>();
		ResourceViewBo re = null;
		for(int i=0;i<treeList.size();++i){
			zTreeVo zTreeVo =  treeList.get(i);
			if(zTreeVo.getIsParent()){
				List<zTreeVo> childs = zTreeVo.getChildren();
				for(int j =0;j < childs.size(); ++j){
					zTreeVo tree = childs.get(j);
    				tree.setResourceType(zTreeVo.getName());
		    	}
				List<ResourceViewBo> tp = regroupResource(childs);
				list.addAll(tp);
			}else{
				re = new ResourceViewBo();
				String name = zTreeVo.getName();
				int idx = name.indexOf(")");
				String ip = name.substring(1,idx);
				String realname = name.substring(idx+1);
				re.setId(zTreeVo.getId());
				re.setIsParent(zTreeVo.getIsParent());
				re.setName(realname);
				re.setNocheck(zTreeVo.getNocheck());
				re.setOpen(zTreeVo.getOpen());
				re.setpId(zTreeVo.getPId());
				re.setIp(ip);
				re.setResourceType(zTreeVo.getResourceType());
				
				list.add(re);
			}

		}
		return list;
	}
	private boolean DefIsdisplay(CategoryDef def){
		if(!def.isDisplay()){
			return false;
		}else{
			if(def.getParentCategory() != null){
				return DefIsdisplay(def.getParentCategory());
			}else{
				return true;
			}
		}
	}
}
