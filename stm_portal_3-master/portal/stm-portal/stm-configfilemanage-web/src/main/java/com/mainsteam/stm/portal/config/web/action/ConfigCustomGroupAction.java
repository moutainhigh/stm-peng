package com.mainsteam.stm.portal.config.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.config.api.IConfigCustomGroupApi;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupBo;
import com.mainsteam.stm.portal.config.web.vo.ZTreeVo;
/**
 * <li>文件名称: ConfigCustomGroupAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   caoyong
 */
@Controller
@RequestMapping("/portal/config/file")
public class ConfigCustomGroupAction extends BaseAction {
	private Logger logger = Logger.getLogger(ConfigCustomGroupAction.class);
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private IConfigCustomGroupApi configCustomGroupApi;
	
	/**
	 * 移入配置组分页数据
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/getConfigGroupPage", method=RequestMethod.POST)
	public JSONObject getConfigGroupPage(HttpSession session,Page<ConfigCustomGroupBo, ConfigCustomGroupBo> page){
		try {
			ILoginUser user = getLoginUser(session);
			if(!user.isSystemUser()){
				ConfigCustomGroupBo condition = new ConfigCustomGroupBo();
				condition.setEntryId(user.getId());
				page.setCondition(condition);
			}
			configCustomGroupApi.selectByPage(page);
			logger.info("portal.config.file.getConfigGroupPage successful");
		} catch (Exception e) {
			logger.error("portal.config.file.getConfigGroupPage failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(page);
	}
	/**
	 * 资源移入配置组
	 * @param groupIds 组ids
	 * @param resourceInstanceIds 资源ids
	 * @return
	 */
	@RequestMapping(value="/moveIntoGroup")
	public JSONObject moveIntoGroup(long[] groupIds,long[] resourceInstanceIds){
		try {
			int count = configCustomGroupApi.moveIntoGroup(groupIds, resourceInstanceIds);
			logger.info("portal.config.file.moveIntoGroup successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.file.moveIntoGroup failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "移入配置组失败");
		}
	}
	/**
	 * 添加 自定义配置组
	 * @param categoryVo
	 * @return
	 */
	@RequestMapping("/addCustomGroup")
	public JSONObject addCustomGroup(HttpSession session,ConfigCustomGroupBo ConfigCustomGroupBo) {
		try {
			ILoginUser user = getLoginUser(session);
			ConfigCustomGroupBo.setEntryId(user.getId());
			int newGroupId = configCustomGroupApi.insert(ConfigCustomGroupBo);
			logger.info("portal.config.file.addCustomGroup successful");
			if(newGroupId == -1){
				return toFailForGroupNameExsit(null);
			}else{
				return toSuccess(newGroupId);
			}
		} catch (Exception e) {
			logger.error("portal.config.file.addCustomGroup failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "添加自定义组失败");
		}
	}
	
	/**
	 * 编辑 自定义配置组
	 * @param categoryVo
	 * @return
	 */
	@RequestMapping("/editCustomGroup")
	public JSONObject editCustomGroup(ConfigCustomGroupBo bo) {
		try {
			int count = configCustomGroupApi.update(bo);
			logger.info("portal.config.file.editCustomGroup successful");
			if(count == -1){
				return toFailForGroupNameExsit(null);
			}else{
				return toSuccess(count);
			}
		} catch (Exception e) {
			logger.error("portal.config.file.editCustomGroup failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "编辑自定义组失败");
		}
	}
	/**
	 * 获取所有 自定义配置组
	 * @return
	 */
	@RequestMapping("/getAllCustomGroup")
	public JSONObject getAllCustomGroup(HttpSession session){
		try {
			ILoginUser user = getLoginUser(session);
			List<ConfigCustomGroupBo> list= configCustomGroupApi.getList(user.isSystemUser()?0:user.getId());
			logger.info("portal.config.file.getAllCustomGroup successful");
			return toSuccess(list);
		} catch (Exception e) {
			logger.error("portal.config.file.getAllCustomGroup failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "获取自定义组失败");
		}
	}
	/**
	 * 删除  自定义配置组
	 * @param id
	 * @return
	 */
	@RequestMapping("/delCustomGroup")
	public JSONObject delCustomGroup(ConfigCustomGroupBo bo) {
		try {
			int count = configCustomGroupApi.del(bo.getId());
			logger.info("portal.config.file.delCustomGroup successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.file.delCustomGroup failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "删除自定义组失败");
		}
	}
	
	/**
	 * 从自定义配置组删除资源
	 * @param id
	 * @return
	 */
	@RequestMapping("/delResourceFromCustomGroup")
	public JSONObject delResourceFromCustomGroup(ConfigCustomGroupBo bo) {
		try {
			int count = configCustomGroupApi.deleteResourceFromCustomGroup(bo);
			logger.info("portal.config.file.delResourceFromCustomGroup successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.config.file.delResourceFromCustomGroup failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "删除自定义组资源失败");
		}
	}
	/**
	 * 从自定义配置组id查询
	 * @param id
	 * @return
	 */
	@RequestMapping("/getCustomGroup")
	public JSONObject getCustomGroup(long id){
		try {
			ConfigCustomGroupBo customGroupBo = configCustomGroupApi.getCustomGroup(id);
			logger.info("portal.config.file.getCustomGroup successful");
			return toSuccess(customGroupBo);
		} catch (Exception e) {
			logger.error("portal.config.file.delResourceFromCustomGroup failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "查询自定义组失败");
		}
	}
	
	/**
	 * 获取所有资源类别
	 */
	@RequestMapping("/getAllResourceDef")
	public JSONObject getAllResourceDef(){
		List<ZTreeVo> treeList = new ArrayList<ZTreeVo>();
		try {
			List<ResourceInstance> resourceInstanceList = configCustomGroupApi.getAllResourceInstanceList();
			treeList = this.getTreeListByResourceInstanceList(resourceInstanceList);
			logger.info("portal.config.file.getAllResourceDef successful");
		} catch (Exception e) {
			logger.error("portal.config.file.getAllResourceDef failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(JSONObject.toJSON(treeList));
	}
	
	/**
	 * 获取指定id集合的资源列表
	 * @return
	 */
	@RequestMapping("/getRightResourceDef")
	public JSONObject getRightResourceDef(String ids){
		List<ZTreeVo> treeList = new ArrayList<ZTreeVo>();
		try {
			List<ResourceInstance> resourceInstanceList = configCustomGroupApi.getResourceInstanceListByIds(ids);
			treeList = this.getTreeListByResourceInstanceList(resourceInstanceList);
			logger.info("portal.config.file.getRightResourceDef successful");
		} catch (Exception e) {
			logger.error("portal.config.file.getRightResourceDef failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(JSONObject.toJSON(treeList));
	}
	
	/**
	 * 获取除去指定id集合的资源列表
	 * @return
	 */
	@RequestMapping("/getLeftResourceDef")
	public JSONObject getLeftResourceDef(String ids){
		List<ZTreeVo> treeList = new ArrayList<ZTreeVo>();
		try {
			List<ResourceInstance> resourceInstanceList = configCustomGroupApi.getExceptResourceInstanceListByIds(ids);
			treeList = this.getTreeListByResourceInstanceList(resourceInstanceList);
			logger.info("portal.config.file.getLeftResourceDef successful");
		} catch (Exception e) {
			logger.error("portal.config.file.getLeftResourceDef failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(JSONObject.toJSON(treeList));
	}
	
	//通过resourceInstance列表构建树结构(两级类别，一级资源)
	private List<ZTreeVo> getTreeListByResourceInstanceList(List<ResourceInstance> instanceList){
		List<ZTreeVo> treeOneList = new ArrayList<ZTreeVo>();
		List<ZTreeVo> treeTwoList = new ArrayList<ZTreeVo>();
		
		Map<String, List<ZTreeVo>> secondTreeAndInstanceMap = new HashMap<String, List<ZTreeVo>>();
		Map<String, List<ZTreeVo>> firstTreeAndSecondTreeMap = new HashMap<String, List<ZTreeVo>>();
		if(instanceList == null || instanceList.size() <= 0){
			return treeOneList;
		}
		for(int i = 0 ; i < instanceList.size() ; i ++){
			ResourceInstance resourceInstance = instanceList.get(i);
			if(resourceInstance.getCategoryId() == null){
				System.out.println(resourceInstance.getId());
			}
			//根据资源实例ID获取二级类别
			CategoryDef secondCategory = capacityService.getCategoryById(resourceInstance.getCategoryId());
			//根据一级类别获取二级类别
			CategoryDef firstCategory = secondCategory.getParentCategory();
			ZTreeVo instanceTree = this.defTozTreeVo(resourceInstance, false, secondCategory.getId());
			ZTreeVo secondTree = this.defTozTreeVo(secondCategory, true, firstCategory.getId());
			ZTreeVo firstTree = this.defTozTreeVo(firstCategory, true, "0");
			//判断secondTreeAndInstanceMap中是否存在二级类别ID的数据
			if(!secondTreeAndInstanceMap.containsKey(secondTree.getId())){
				secondTreeAndInstanceMap.put(secondTree.getId(), new ArrayList<ZTreeVo>());
			}
			secondTreeAndInstanceMap.get(secondTree.getId()).add(instanceTree);
			if(!treeTwoList.contains(secondTree)){
				treeTwoList.add(secondTree);
			}
			if(!treeOneList.contains(firstTree)){
				treeOneList.add(firstTree);
			}
		}
		
		//构建二级树
		for(ZTreeVo secondTree : treeTwoList){
			secondTree.setChildren(secondTreeAndInstanceMap.get(secondTree.getId()));
			if(!firstTreeAndSecondTreeMap.containsKey(secondTree.getPId())){
				firstTreeAndSecondTreeMap.put(secondTree.getPId(), new ArrayList<ZTreeVo>());
			}
			firstTreeAndSecondTreeMap.get(secondTree.getPId()).add(secondTree);
		}
		
		//构建一级树
		for(ZTreeVo firstTree : treeOneList){
			firstTree.setChildren(firstTreeAndSecondTreeMap.get(firstTree.getId()));
		}
		return treeOneList;
	}
	
	
	private ZTreeVo defTozTreeVo(Object def,boolean isParent,String pid){
		ZTreeVo tree = new ZTreeVo();
		if(def instanceof ResourceInstance){
			ResourceInstance instance = (ResourceInstance)def;
			tree.setId(instance.getId() + "");
			tree.setIsParent(isParent);
			tree.setName(instance.getShowName());
			tree.setNocheck(false);
			tree.setOpen(false);
			tree.setPId(pid);
		}else if(def instanceof CategoryDef){
			CategoryDef instance = (CategoryDef)def;
			tree.setId(instance.getId());
			tree.setIsParent(isParent);
			tree.setName(instance.getName());
			tree.setNocheck(false);
			tree.setOpen(false);
			tree.setPId(pid);
		}
		return tree;
	}
}
