package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.SnapshotProfileApi;
import com.mainsteam.stm.portal.resource.bo.ResourceCategoryTreeBo;
import com.mainsteam.stm.portal.resource.bo.SnapshotProfilePageBo;
import com.mainsteam.stm.portal.resource.bo.SnapshotResourceInstance;
import com.mainsteam.stm.portal.resource.bo.SnapshotResourceMetric;
import com.mainsteam.stm.profilelib.fault.obj.Profilefault;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;

@Controller
@RequestMapping("/portal/resource/snapshotProfile")
public class SnapshotProfileAction extends BaseAction {
	
	@Resource
	private SnapshotProfileApi snapshotProfileApi;
	
	/**
	 * 获取资源类型
	 */
	@RequestMapping("/getResourceCategoryList")
	public JSONObject getResourceCategoryList() {
		List<ResourceCategoryTreeBo> categorys = snapshotProfileApi.getAllResourceCategory();
		return toSuccess(categorys);
	}
	/**
	 * 获取子资源类型根据类别或者主资源
	 */
	@RequestMapping("/getChildResourceListByMainResourceOrCategory")
	public JSONObject getChildResourceListByMainResourceOrCategory(ResourceCategoryTreeBo category) {
		List<ResourceCategoryTreeBo> defs = new ArrayList<ResourceCategoryTreeBo>();
		if(category.getType() == 1){
			//类别获取子资源
			defs = snapshotProfileApi.getChildResourceByResourceCategory(category.getId());
		}else if(category.getType() == 2){
			//主资源获取子资源
			defs = snapshotProfileApi.getChildResourceByMainResource(category.getId());
		}
		return toSuccess(defs);
	}
	/**
	 * 获取资源实例
	 */
	@RequestMapping("/getResourceInstanceList")
	public JSONObject getResourceInstanceList(String queryId,int type,Long domainId,HttpSession session) {
		List<SnapshotResourceInstance> defs = new ArrayList<SnapshotResourceInstance>();
		if(type == 1){
			//通过类别获取资源实例
			defs = snapshotProfileApi.getResourceInstanceByCategoryId(queryId,domainId,getLoginUser(session));
		}else if(type == 2){
			//通过资源获取资源实例
			defs = snapshotProfileApi.getInstanceByResource(queryId,domainId,getLoginUser(session));
		}
		return toSuccess(defs);
	}
	/**
	 * 获取指标
	 */
	@RequestMapping("/getMetricListByResourceId")
	public JSONObject getMetricListByResourceId(String[] resourceIdArr,Long[] instanceIdArr) {
		List<SnapshotResourceMetric> defs = new ArrayList<SnapshotResourceMetric>();
		Set<String> idSet = new HashSet<String>();
		
		for(String id : resourceIdArr){
			idSet.add(id);
		}
		defs = snapshotProfileApi.getMetricListByResource(idSet, instanceIdArr);
		return toSuccess(defs);
		
	}
	/**
	 * 获取快照脚本
	 * @return
	 */
	@RequestMapping("/getSnapshotScript")
	public JSONObject getSnapshotScript(){
		Map result = new HashMap();
		List<ScriptManage> scriptManageList = snapshotProfileApi.getSnapshotScript();
		result.put("total", scriptManageList.size());
		result.put("rows", scriptManageList);
		return toSuccess(result);
	}
	/**
	 * 获取恢复脚本
	 * @return
	 */
	@RequestMapping("/getResumScript")
	public JSONObject getResumScript(){
		Map result = new HashMap();
		List<ScriptManage> scriptManageList = snapshotProfileApi.getResumScript();
		result.put("total", scriptManageList.size());
		result.put("rows", scriptManageList);
		return toSuccess(result);
	}
	
	/**
	 * 新增策略
	 * @param pff
	 * @param instanceIds
	 * @param metricIds
	 * @param session
	 * @return
	 */
	@RequestMapping("/addSnapshotProfile")
	public JSONObject addSnapshotProfile(Profilefault pff, String instanceIds, String metricIds, HttpSession session){
		ILoginUser user = getLoginUser(session);
		return toSuccess(snapshotProfileApi.addSnapshotProfile(pff, instanceIds, metricIds, user));
	}
	/**
	 * 修改策略
	 * @param pff
	 * @param instanceIds
	 * @param metricIds
	 * @param session
	 * @return
	 */
	@RequestMapping("/editSnapshotProfile")
	public JSONObject editSnapshotProfile(Profilefault pff, String instanceIds, String metricIds, HttpSession session){
		ILoginUser user = getLoginUser(session);
		return toSuccess(snapshotProfileApi.editSnapshotProfile(pff, instanceIds, metricIds, user));
	}
	/**
	 * 获取所有策略
	 * @param session
	 * @param spPBo
	 * @return
	 */
	@RequestMapping("/getAllSnapshotProfile")
	public JSONObject getAllSnapshotProfile(HttpSession session, SnapshotProfilePageBo spPBo){
		return toSuccess(snapshotProfileApi.getAllSnapshotProfile(spPBo,getLoginUser(session)));
	}
	/**
	 * 删除策略
	 * @param profileIds
	 * @return
	 */
	@RequestMapping("/delSnapshotProfile")
	public JSONObject delSnapshotProfile(String profileIds){
		return toSuccess(snapshotProfileApi.delSnapshotProfile(profileIds));
	}
	/**
	 * 更新启用状态
	 * @param profileId
	 * @return
	 */
	@RequestMapping("/updateIsUse")
	public JSONObject updateIsUse(String profileId){
		return toSuccess(snapshotProfileApi.updateIsUse(profileId));
	}
	/**
	 * 查询策略、资源、指标关联关系
	 * @param profileId
	 * @return
	 */
	@RequestMapping("/getSnapshotProfileRelation")
	public JSONObject getSnapshotProfileRelation(String profileId){
		return toSuccess(snapshotProfileApi.getSnapshotProfileRelation(profileId));
	}
}
