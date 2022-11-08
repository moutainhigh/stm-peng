package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IResourceMonitorApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryBo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceCategoryVo;
import com.mainsteam.stm.portal.resource.web.vo.zTreeVo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.resources.api.IUserResourcesApi;
import com.mainsteam.stm.system.um.user.api.IUserApi;

/**
 * <li>文件名称: ResourceCategoryAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>
 * 版权所有: 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明:
 * ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月24日
 * @author pengl
 */
@Controller
@RequestMapping("/resourceManagement/resourceCategory")
public class ResourceCategoryAction extends BaseAction {

	private static final Log logger = LogFactory.getLog(ResourceCategoryAction.class);

	@Resource
	private CapacityService capacityService;

	@Resource
	private ResourceCategoryApi resourceCategoryApi;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private IUserResourcesApi userResourcesApi;
	
	@Resource
	private IUserApi userApi;
	
	@Resource
	private IResourceMonitorApi resourceMonitorApi;
	/**
	 * 获取所有资源类别
	 */
	@RequestMapping("/getAllCategorys")
	public JSONObject getAllCategorys() {

		if(logger.isInfoEnabled()){
			logger.info("Excute getAllCategorys!");
		}

		List<ResourceCategoryVo> categoryVoList = new ArrayList<ResourceCategoryVo>();

		List<ResourceCategoryBo> categoryList = resourceCategoryApi
				.getFirstStageResourceCategoryList();

		for (ResourceCategoryBo categoryBo : categoryList) {

			ResourceCategoryVo categoryVo = this
					.resourceCategoryBoToResourceCategoryVo(categoryBo);
			categoryVoList.add(categoryVo);

		}

		JSONArray array = new JSONArray();
		array.addAll(categoryList);

		// 向客户端写返回数据
		return toSuccess(array);

	}

	private ResourceCategoryVo resourceBoToVo(
			ResourceCategoryBo resourceCategoryBo) {

		ResourceCategoryVo categoryVo = new ResourceCategoryVo();
		categoryVo.setId(resourceCategoryBo.getId());
		categoryVo.setName(resourceCategoryBo.getName());
		categoryVo.setResourceNumber(resourceCategoryBo.getResourceNumber());
		return categoryVo;

	}
	
	/**
	 * 获取有资源的资源类别
	 */
	@RequestMapping("/getResourceCategorys")
	public JSONObject getResourceCategorys(HttpSession session) {
		JSONArray array = new JSONArray();
		List<ResourceCategoryBo> categoryVoList = new ArrayList<ResourceCategoryBo>();
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
	try {
		List<ResourceCategoryBo> categoryList = resourceCategoryApi
					.getResourceCategoryList(user);
		for (ResourceCategoryBo categoryBo : categoryList) {
			if("VM".equals(categoryBo.getId())){
				continue;
			}
			//无效转换代码，既没有隐藏什么关键信息，又没有在最终输出结果中展示
			//ResourceCategoryVo categoryVo = this.resourceBoToVo(categoryBo);
			categoryVoList.add(categoryBo);
		}
		array.addAll(categoryVoList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 向客户端写返回数据
		return toSuccess(array);
		
	}
	
	
	/**
	 * 获取所有资源类别
	 */
	@RequestMapping("/getAllResourceDef")
	public JSONObject getAllResourceDef(HttpSession session) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getAllResourceInstanceList(getLoginUser(session));

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	/**
	 * 获取所有资源类别, 二开使用
	 */
	@RequestMapping("/getAllResourceDefIsDisplayFalse")
	public JSONObject getAllResourceDefIsDisplayFalse(HttpSession session) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getAllResourceInstanceList(getLoginUser(session));

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, false);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	/**
	 * 获取所有资源类别(不过滤域)(系统管理使用)
	 */
	@RequestMapping("/getAllResourceInstance")
	public JSONObject getAllResourceInstance(Long domainId,HttpSession session) {
		
		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getAllResourceInstanceList(domainId,getLoginUser(session));

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);

		return toSuccess(JSONObject.toJSON(treeList));

	}

	/**
	 * 获取指定id集合的资源列表
	 * 
	 * @return
	 */
	@RequestMapping("/getRightResourceDef")
	public JSONObject getRightResourceDef(String ids) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getResourceInstanceListByIds(ids);

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	/**
	 * 获取指定id集合的资源列表, 二开使用
	 * 
	 * @return
	 */
	@RequestMapping("/getRightResourceDefIsDisplayFalse")
	public JSONObject getRightResourceDefIsDisplayFalse(String ids) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getResourceInstanceListByIds(ids);

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, false);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	/**
	 * 获取除去指定id集合的资源列表
	 * 
	 * @return
	 */
	@RequestMapping("/getLeftResourceDef")
	public JSONObject getLeftResourceDef(String ids,HttpSession session) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getExceptResourceInstanceListByIds(ids,getLoginUser(session));

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	/**
	 * 获取除去指定id集合的资源列表, 二开使用
	 * 
	 * @return
	 */
	@RequestMapping("/getLeftResourceDefIsDisplayFalse")
	public JSONObject getLeftResourceDefIsDisplayFalse(String ids,HttpSession session) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getExceptResourceInstanceListByIds(ids,getLoginUser(session));

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, false);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	/**
	 * 获取除去指定id集合的资源列表(不过滤域)
	 * 
	 * @return
	 */
	@RequestMapping("/getLeftResourceInstance")
	public JSONObject getLeftResourceInstance(String ids,Long domainId,HttpSession session) {
		
		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getExceptResourceInstanceListByIds(ids,domainId,getLoginUser(session));

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	
	/**
	 * 获取除去指定id集合的资源列表(带搜索关键字)
	 * 
	 * @return
	 */
	@RequestMapping("/getLeftResourceDefBySearchContent")
	public JSONObject getLeftResourceDefBySearchContent(String ids,String searchContent,HttpSession session) {

		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getExceptResourceInstanceListByIdsAndSearchContent(ids,getLoginUser(session),searchContent);

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);

		return toSuccess(JSONObject.toJSON(treeList));

	}
	
	/**
	 * 获取除去指定id集合的资源列表(不过滤域)(系统管理)(带搜索关键字)
	 * 
	 * @return
	 */
	@RequestMapping("/getLeftResourceInstanceBySearchContent")
	public JSONObject getLeftResourceInstanceBySearchContent(String ids,Long domainId,String searchContent,HttpSession session) {
		
		List<ResourceInstanceBo> resourceInstanceList = resourceCategoryApi
				.getExceptResourceInstanceListByIdsAndSearchContent(ids,domainId,getLoginUser(session),searchContent);

		List<zTreeVo> treeList = this.getTreeListByResourceInstanceList(resourceInstanceList, true);
		
		return toSuccess(JSONObject.toJSON(treeList));

	}

	private ResourceCategoryVo resourceCategoryBoToResourceCategoryVo(
			ResourceCategoryBo resourceCategoryBo) {

		ResourceCategoryVo categoryVo = new ResourceCategoryVo();
		categoryVo.setId(resourceCategoryBo.getId());
		categoryVo.setName(resourceCategoryBo.getName());

		return categoryVo;

	}

	// 通过resourceInstance列表构建树结构(两级类别，一级资源)
	private List<zTreeVo> getTreeListByResourceInstanceList(List<ResourceInstanceBo> instanceList, boolean isFilterDisplay) {

		List<zTreeVo> treeOneList = new ArrayList<zTreeVo>();
		List<zTreeVo> treeTwoList = new ArrayList<zTreeVo>();

		Map<String, List<zTreeVo>> secondTreeAndInstanceMap = new HashMap<String, List<zTreeVo>>();

		Map<String, List<zTreeVo>> firstTreeAndSecondTreeMap = new HashMap<String, List<zTreeVo>>();

		if (instanceList == null || instanceList.size() <= 0) {
			return treeOneList;
		}
		for (int i = 0; i < instanceList.size(); i++) {

			ResourceInstanceBo resourceInstance = instanceList.get(i);
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
