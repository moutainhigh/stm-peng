/**
 * 
 */
package com.mainsteam.stm.portal.resourcelog.web.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.resourcelog.strategy.bo.AlarmListBo;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.api.ISyslogStrategyApi;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;
import com.mainsteam.stm.system.resource.api.Filter;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;
import com.mainsteam.stm.util.Util;

/**
 * <li>文件名称: SyslogAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 接口流量统计</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年11月12日
 * @author lil
 */
@Controller
@RequestMapping("/portal/syslog")
public class SyslogAction extends BaseAction {

	@Resource
	private ISyslogStrategyApi syslogStrategyApi;
	
	@Autowired
	private IResourceApi resourceApi;
	
	/**
	 * 
	* @Title: saveSingleStrateRule
	* @Description: 添加规则
	* @param rule
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveSingleStrateRule")
	public JSONObject saveSingleStrateRule(SysLogRuleBo rule) {
		//判断是否存在
		boolean isRuleNameExist = this.syslogStrategyApi.isRuleNameExist(rule);
		if (isRuleNameExist) {//如果存在返回提示信息
			return toFailForGroupNameExsit("保存失败，该策略下已存在相同的规则名!");
		} else {
			Long strategyId = rule.getStrategyId();
			int ret = this.syslogStrategyApi.saveSingleStrateRule(strategyId, rule);
			return toSuccess(ret);
		}
	}
	
	/**
	 * 
	* @Title: getSyslogRules
	* @Description: 根据策略ID获取syslog规则
	* @param strategyId
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getSyslogRules")
	public JSONObject getSyslogRules(Long strategyId) {
		Page<SysLogRuleBo, SysLogRuleBo> page = new Page<SysLogRuleBo, SysLogRuleBo>();
		page.setDatas(this.syslogStrategyApi.getRules(strategyId));
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: updateRuleStatus
	* @Description: 更新规则状态
	* @param id
	* @param isOpen
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateRuleStatus")
	public JSONObject updateRuleStatus(Long id, int isOpen, int isAlarm, String type,Long strategyId) {
		SysLogRuleBo sysLogRuleBo = new SysLogRuleBo();
		sysLogRuleBo.setId(id);
		sysLogRuleBo.setStrategyId(strategyId);
		sysLogRuleBo.setIsOpen(isOpen);
		sysLogRuleBo.setIsAlarm(isAlarm);
		return toSuccess(this.syslogStrategyApi.updateRules(sysLogRuleBo));
	}
	
	/**
	 * 
	* @Title: removeRules
	* @Description: 批量删除规则
	* @param ids
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("removeRules")
	public JSONObject removeRules(Long[] ids) {
		return toSuccess(this.syslogStrategyApi.delRules(ids));
	}
	
	/**
	 * 
	* @Title: getSysLogRule
	* @Description: 根据ID得到规则信息
	* @param sysLogRuleId
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getSysLogRuleById")
	public JSONObject getSysLogRuleById(Long id) {
		return toSuccess(this.syslogStrategyApi.getRuleById(id));
	}
	
	/**
	 * 
	* @Title: updateStrateRule
	* @Description: 更新策略表单信息
	* @param sysLogRuleBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateStrateRule")
	public JSONObject updateStrateRule(SysLogRuleBo sysLogRuleBo) {
		//判断是否存在
		boolean isRuleNameExist = this.syslogStrategyApi.isRuleNameExist(sysLogRuleBo);
		if (isRuleNameExist) {//如果存在返回提示信息
			return toFailForGroupNameExsit("修改失败，该策略下已存在相同的规则名!");
		} else {
			return toSuccess(this.syslogStrategyApi.updateStrateRule(sysLogRuleBo));
		}
	}
	
	/**
	 * 
	* @Title: getAlarmList
	* @Description: 根据条件查询告警列表
	* @param page
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getAlarmList")
	public JSONObject getAlarmList(Page<AlarmListBo, AlarmListBo> page,AlarmListBo alarmListBo) {
		if ("-1".equals(alarmListBo.getLevel())) {
			alarmListBo.setLevel("");
		}
		alarmListBo.setOccurredTime(new Date());
		alarmListBo.setStartTime("");
		alarmListBo.setEndTime("");
		page.setCondition(alarmListBo);
		List<AlarmListBo> listAlarm = this.syslogStrategyApi.getAlarmList(page);
		page.setDatas(listAlarm);
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: getTotalAlarm
	* @Description: 查询所有的告警列表
	* @param page
	* @param alarmListBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getTotalAlarm")
	public JSONObject getTotalAlarm(Page<AlarmListBo, AlarmListBo> page,AlarmListBo alarmListBo) {
		if ("-1".equals(alarmListBo.getLevel())) {
			alarmListBo.setLevel("");
		}
		page.setCondition(alarmListBo);
		List<AlarmListBo> listAlarm = this.syslogStrategyApi.getAlarmList(page);
		page.setDatas(listAlarm);
		return toSuccess(page);
	}
	/**
	 * 
	* @Title: saveRuleList
	* @Description: 保存规则集合
	* @param strategyId
	* @param gridData
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveRuleList")
	public JSONObject saveRuleList(Long strategyId,String gridData){
		List<SysLogRuleBo> listRules = JSONObject.parseArray(gridData, SysLogRuleBo.class);
		return toSuccess(this.syslogStrategyApi.saveStrategyRule(strategyId, listRules));
	}
	
	/**
	 * 
	* @Title: removeStrategy
	* @Description: 批量删除策略信息
	* @param delIds
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("removeStrategy")
	public JSONObject removeStrategy(Long[] delIds) {
		int returnDelId = this.syslogStrategyApi.delStrategy(delIds);
		
		return toSuccess(returnDelId > 0 ? true : false);
	}
	
	@RequestMapping("findStrategyResource")
	public JSONObject findStrategyResource(SyslogResourceBo syslogBo) {
		String[] categoryIdArr = syslogBo.getCategoryId().split(",");
		List<String> categoryIds = Arrays.asList(categoryIdArr);
		
		final String searchKey = syslogBo.getName();
		
		final String selectedResourceIds = syslogBo.getSelecedResourceIds();
		
		//根据条件查询所有资源信息
		ResourceQueryBo queryBo = new ResourceQueryBo(getLoginUser());
		queryBo.setDomainIds(syslogBo.getDomainIdsList());
		queryBo.setCategoryIds(categoryIds);
		queryBo.setFilter(new Filter() {
			@Override
			public boolean filter(ResourceInstanceBo riBo) {
				if (!"".equals(searchKey) && searchKey != null) {
					if (!"".equals(selectedResourceIds) && selectedResourceIds != null) {
						if ((riBo.getDiscoverIP().contains(searchKey) || riBo.getShowName().contains(searchKey)) 
								&& !selectedResourceIds.contains(String.valueOf(riBo.getId()))) {
							return true;
						}else {
							return false;
						}
					}else {
						if ((riBo.getDiscoverIP() != null && riBo.getDiscoverIP().contains(searchKey)) || riBo.getShowName().contains(searchKey)) {
							return true;
						}else {
							return false;
						}
					}
				}
				return true;
			}
		});
		
		List<ResourceInstanceBo> listAll = this.resourceApi.getResources(queryBo);
		//得到资源类型的映射
		Map<String, String> categoryMap = this.resourceApi.getCategoryMapper();
		//判断资源是否已挂在策略下，如果是，则移除该资源
		List<SyslogResourceBo> listSyslogRes = new ArrayList<SyslogResourceBo>();
		int countRes = 0;
		SyslogResourceBo syslogRes;
		for (ResourceInstanceBo resourceInstanceBo : listAll) {
			syslogRes = new SyslogResourceBo();
			syslogRes.setResourceId(resourceInstanceBo.getId());
			syslogRes.setName(resourceInstanceBo.getShowName());
			syslogRes.setResourceType(categoryMap.get(resourceInstanceBo.getCategoryId()));
			syslogRes.setResourceIp(resourceInstanceBo.getDiscoverIP());
			syslogRes.setStrategyType(syslogBo.getStrategyType());
			countRes = this.syslogStrategyApi.countRes(syslogRes);
			//资源没有挂在策略上才添加
			if (countRes <= 0) {
				listSyslogRes.add(syslogRes);
			}
		}
		return toSuccess(listSyslogRes);
	}
	
	/**
	 * 
	* @Title: getResourceByStrategy
	* @Description: 根据策略ID获取资源实例
	* @param strategyId
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getResourceByStrategy")
	public JSONObject getResourceByStrategy(Long strategyId) {
		List<SyslogResourceBo> list = this.syslogStrategyApi.getResource(strategyId);
		//得到资源类型的映射
		Map<String, String> categoryMap = this.resourceApi.getCategoryMapper();
		
		for (SyslogResourceBo syslogResourceBo : list) {
			if (0 != syslogResourceBo.getResourceId()) {
				ResourceInstanceBo res =  resourceApi.getResource(syslogResourceBo.getResourceId());
				if (res.getDiscoverIP() != null && res.getName() != null) {
					syslogResourceBo.setName(res.getName());
					syslogResourceBo.setResourceIp(res.getDiscoverIP());
					syslogResourceBo.setResourceType(categoryMap.get(res.getCategoryId()));
				}
			}
		}
		return toSuccess(list);
	}
	
	/**
	 * 
	* @Title: getBasicStrategy
	* @Description: 根据ID获取基本策略信息
	* @param strategyId
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getBasicStrategy")
	public JSONObject getBasicStrategy(Long id) {
		StrategyBo strategyBo = this.syslogStrategyApi.getStrategyBasic(id);
		return toSuccess(strategyBo);
	}
	
	/**
	 * 
	* @Title: saveSyslogStrategyBasic
	* @Description: 保存策略基本信息
	* @param strategy
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveSyslogStrategyBasic")
	public JSONObject saveSyslogStrategyBasic(StrategyBo strategy) {
		boolean isNameExist = this.syslogStrategyApi.isStrategyNameExist(strategy);
		if (!isNameExist) {
			int ret = this.syslogStrategyApi.saveStrategyBasic(strategy);
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("successRow", ret);
			retMap.put("strategyId", strategy.getId());
			return toSuccess(retMap);
		} else {
			return toFailForGroupNameExsit("保存失败!策略名在所选域中已存在!");
		}
		
	}
	
	/**
	 * 
	* @Title: updateSyslogStrategyBasic
	* @Description: 更新基本策略信息
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateSyslogStrategyBasic")
	public JSONObject updateSyslogStrategyBasic(StrategyBo strategy) {
		boolean isNameExist = this.syslogStrategyApi.isStrategyNameExist(strategy);
		if (!isNameExist) {
			int ret = this.syslogStrategyApi.updateStrategyBasic(strategy);
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("successRow", ret);
			retMap.put("strategyId", strategy.getId());
			return toSuccess(retMap);
		} else {
			return toFailForGroupNameExsit("修改失败!策略名在所选域中已存在!");
		}
	}
	
	/**
	 * 
	* @Title: saveStrategyResource
	* @Description: 保存策略资源
	* @param strategyId
	* @param rightDatas
	* @param strategyType
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveStrategyResource")
	public JSONObject saveStrategyResource(Long strategyId, String rightDatas,int strategyType) {
		List<SyslogResourceBo> listRes = JSONObject.parseArray(rightDatas, SyslogResourceBo.class);
		int ret = this.syslogStrategyApi.saveStrategyResource(strategyId, listRes,strategyType);
		return toSuccess(ret);
	}
	

	/**
	 * 
	* @Title: updateMonitorState
	* @Description: 更新监听状态
	* @param syslogResourceBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateMonitorState")
	public JSONObject updateMonitorState(Long[] id, int isMonitor) {
		return toSuccess(this.syslogStrategyApi.updateMonitorState(id,isMonitor));
	}
	
	/**
	 * 
	* @Title: querySyslogList
	* @Description: 查询策略日志
	* @param page
	* @param strategyBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("querySyslogList")
	public JSONObject querySyslogList(Page<SyslogResourceBo, StrategyBo> page,StrategyBo strategyBo) {
		List<Long> domainList = new ArrayList<Long>();
		if (null == strategyBo.getDomainId() || 0 == strategyBo.getDomainId()) {
			ILoginUser loginUser = getLoginUser();
			Set<IDomain> domains = loginUser.getDomains();
			Iterator<IDomain> iter = domains.iterator(); 
			while (iter.hasNext()) {
				domainList.add(iter.next().getId());
			}
		} else {
			domainList.add(strategyBo.getDomainId());
		}
		strategyBo.setDomainIds(domainList);
		page.setCondition(strategyBo);
		List<SyslogResourceBo> list = this.syslogStrategyApi.getResourceBos(page);
		
		//得到资源类型的映射
		Map<String, String> categoryMap = this.resourceApi.getCategoryMapper();
		for (SyslogResourceBo syslogResourceBo : list) {
			if (0 != syslogResourceBo.getResourceId()) {
				ResourceInstanceBo res =  resourceApi.getResource(syslogResourceBo.getResourceId());
				syslogResourceBo.setName(res.getShowName());
				syslogResourceBo.setResourceIp(res.getDiscoverIP());
				syslogResourceBo.setResourceType(categoryMap.get(res.getCategoryId()));
			}else {
				syslogResourceBo.setName("");
			}
		}
		
		Iterator<SyslogResourceBo> it = list.iterator();
		while(it.hasNext()){
			SyslogResourceBo syslogResourceBos = it.next();
			if(Util.isEmpty(syslogResourceBos.getName())){
				it.remove();
			}
		}
		
		page.setDatas(list);
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: findSyslogStrategy
	* @Description: 根据创建者查找syslog和snmpTrap策略
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("queryStrategy")
	public JSONObject queryStrategy(Page<StrategyBo, StrategyBo> page,StrategyBo sBo) {
		ILoginUser loginUser = getLoginUser();
		Set<IDomain> domains = loginUser.getDomains();
		List<Long> domainList = new ArrayList<Long>();
		
		Iterator<IDomain> iter = domains.iterator(); 
		while (iter.hasNext()) {
			domainList.add(iter.next().getId());
		}
		sBo.setDomainIds(domainList);
		page.setCondition(sBo);
		//根据创建者ID条件查询策略
		this.syslogStrategyApi.queryStrategy(page);
		return toSuccess(page);
	}
	
	/**
	 * 
	* @Title: querySetStrategy
	* @Description: 查询要可以更改的策略
	* @param page
	* @param sBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("querySetStrategy")
	public JSONObject querySetStrategy(Page<StrategyBo, StrategyBo> page,StrategyBo sBo) {
		page.setCondition(sBo);
		//根据创建者ID条件查询策略
		this.syslogStrategyApi.queryStrategy(page);
		return toSuccess(page);
	}
	/**
	 * 
	* @Title: updateResourceStrategy
	* @Description: 更新资源下的策略信息
	* @param syslogResourceBo
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateResourceStrategy")
	public JSONObject updateResourceStrategy(SyslogResourceBo syslogResourceBo) {
		return toSuccess(this.syslogStrategyApi.updateResourceStrategy(syslogResourceBo));
	}
	
	/**
	 * 
	* @Title: updateRuleLevel
	* @Description: 更新规则的告警级别
	* @param alarmLevel
	* @param ruleId
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("updateRuleLevel")
	public JSONObject updateRuleLevel(String alarmLevel, Long ruleId) {
		return toSuccess(this.syslogStrategyApi.updateRuleLevel(alarmLevel, ruleId));
	}
	
}
