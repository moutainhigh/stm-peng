package com.mainsteam.stm.portal.statist.web.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.statist.api.IStatistQueryDetailApi;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMainBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceCategoryBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceInstanceBo;
import com.mainsteam.stm.portal.statist.bo.StatistResourceMetricBo;

/**
 * 统计查询报表新增、修改、删除 相关操作
 * @author lankui
 *
 */
@Controller
@RequestMapping(value="/portal/statistQuery/detail")
public class StatistQueryDetailAction extends BaseAction {
	@Resource
	private IStatistQueryDetailApi statistQueryDetail;
	
	/**
	 * 获取资源类型
	 */
	@RequestMapping("/getResourceCategoryList")
	public JSONObject getResourceCategoryList() {
		List<StatistResourceCategoryBo> categorys = statistQueryDetail.getAllResourceCategory();
		return toSuccess(categorys);
	}
	
	/**
	 * 获取子资源类型根据类别或者主资源
	 */
	@RequestMapping("/getChildResourceListByMainResourceOrCategory")
	public JSONObject getChildResourceListByMainResourceOrCategory(StatistResourceCategoryBo category) {
		List<StatistResourceCategoryBo> defs = new ArrayList<StatistResourceCategoryBo>();
		if(category.getType() == 1){
			//类别获取子资源
			defs = statistQueryDetail.getChildResourceByResourceCategory(category.getId());
		}else if(category.getType() == 2){
			//主资源获取子资源
			defs = statistQueryDetail.getChildResourceByMainResource(category.getId());
		}
		return toSuccess(defs);
		
	}
	
	/**
	 * 获取资源实例
	 */
	@RequestMapping("/getResourceInstanceList")
	public JSONObject getResourceInstanceList(String queryId, int type, Long domainId,HttpSession session) {
		List<StatistResourceInstanceBo> defs = new ArrayList<StatistResourceInstanceBo>();
		if(type == 1){
			//通过类别获取资源实例
			defs = statistQueryDetail.getResourceInstanceByCategoryId(queryId,domainId,getLoginUser(session));
		}else if(type == 2){
			//通过资源获取资源实例
			defs = statistQueryDetail.getInstanceByResource(queryId,domainId,getLoginUser(session));
		}
		return toSuccess(defs);
		
	}
	/**
	 * 获取指标
	 */
	@RequestMapping("/getMetricListByResourceId")
	public JSONObject getMetricListByResourceId(String resourceIdList,Long instanceId, String statQType) {
		List<StatistResourceMetricBo> defs = new ArrayList<StatistResourceMetricBo>();
		Set<String> idSet = new HashSet<String>();
		if(resourceIdList.contains(",")){
			for(String id : resourceIdList.split(",")){
				idSet.add(id);
			}
		}else{
			idSet.add(resourceIdList);
		}
		List<String> ids = new ArrayList<String>(idSet);
		defs = statistQueryDetail.getMetricListByResource(ids, instanceId, statQType);
		return toSuccess(defs);
	}
	
	/**
	 * 新增统计报表查询
	 * @param statQMainBo
	 * @return
	 */
	@RequestMapping("/addStatQueryDetail")
	public JSONObject addStatQueryDetail(String statQMain, HttpSession session){
		ILoginUser user = getLoginUser(session);
		StatistQueryMainBo statQMainBo = JSONObject.parseObject(statQMain, StatistQueryMainBo.class);
		if(statistQueryDetail.isExistsStatQMainName(statQMainBo)){
			return toJsonObject(202, "报表名称已存在！");
		}
		statistQueryDetail.insertStatQMain(statQMainBo, user.getId());
		return toSuccess(statQMainBo);
	}
	
	/**
	 * 获取所有统计报表
	 * @param session
	 * @return
	 */
	@RequestMapping("/getAllSQMain")
	public JSONObject getAllSQMain(HttpSession session){
		ILoginUser user = getLoginUser(session);
		return toSuccess(statistQueryDetail.getAllSQMain(user));
	}
	
	/**
	 * 根据statQMainId查询数据
	 * @param statQMainId
	 * @return
	 */
	@RequestMapping("/getSQMainByStatQId")
	public JSONObject getSQMainByStatQId(Long statQMainId){
		return toSuccess(statistQueryDetail.getSQMainByStatQId(statQMainId));
	}
	
	/**
	 * 根据statQMainId删除数据
	 * @param statQMainId
	 * @return
	 */
	@RequestMapping("/delSQMainByStatQId")
	public JSONObject delSQMainByStatQId(Long statQMainId){
		return toSuccess(statistQueryDetail.delSQMainByStatQId(statQMainId));
	}
	/**
	 * 新增统计报表查询
	 * @param statQMainBo
	 * @return
	 */
	@RequestMapping("/updateStatQueryDetail")
	public JSONObject updateStatQueryDetail(String statQMain, HttpSession session){
		ILoginUser user = getLoginUser(session);
		StatistQueryMainBo statQMainBo = JSONObject.parseObject(statQMain, StatistQueryMainBo.class);
		if(statistQueryDetail.isExistsStatQMainName(statQMainBo)){
			return toJsonObject(202, "报表名称已存在");
		}
		statistQueryDetail.updateStatQMain(statQMainBo, user.getId());
		return toSuccess(statQMainBo);
	}
}
