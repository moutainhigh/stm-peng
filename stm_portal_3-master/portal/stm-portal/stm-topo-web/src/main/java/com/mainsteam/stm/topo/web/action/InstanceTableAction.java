package com.mainsteam.stm.topo.web.action;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.topo.api.IInstanceTableApi;
import com.mainsteam.stm.topo.api.TopoAlarmExApi;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.NodeBo;

/**
 * <li>设备一览表管理</li>
 * <li>文件名称: ScheduleAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月31日
 * @author zwx
 */
@Controller
@RequestMapping(value="/topo/instanceTable")
public class InstanceTableAction extends BaseAction{
	
	private final Logger logger = LoggerFactory.getLogger(InstanceTableAction.class);
	@Autowired
	private IInstanceTableApi instanceTableApi;
	@Autowired
	private TopoAlarmExApi alarmExApi;
	
	/**
	 * 修改链路告警规则
	 * @param alarmRules
	 * @return
	 */
	@RequestMapping(value="/link/warn/setting/edit",method=RequestMethod.POST)
	public JSONObject editAlarmRules(String alarmSetting){
		try {
			if(StringUtils.isNotBlank(alarmSetting)){
				instanceTableApi.updateAlarmRules(alarmSetting);
				return super.toSuccess("编辑成功");
			}else{
				return super.toSuccess("未提交编辑数据");
			}
		} catch (Exception e) {
			logger.error("编辑链路告警规则失败!",e);
			return toJsonObject(700, "编辑失败");
		}
	}
	
	/**
	 * 改变链路告规则发送条件是否启用
	 */
	@RequestMapping(value="/link/warn/setting/condition/enable",method=RequestMethod.POST)
	public JSONObject changeLinkAlarmConditionEnabled(String enables){
		try {
			if(StringUtils.isNotBlank(enables)){
				instanceTableApi.changeLinkAlarmConditionEnabled(enables);
				return super.toSuccess("应用成功");
			}else{
				return super.toSuccess("未提交应用数据");
			}
		} catch (Exception e) {
			logger.error("应用链路告警发送条件失败!",e);
			return toJsonObject(700, "应用失败");
		}
	}
	
	/**
	 * 删除链路告警设置信息
	 * @param alarmSetting
	 */
	@RequestMapping(value="/link/warn/setting/delete",method=RequestMethod.POST)
	public JSONObject deleteLinkAlarmSetting(long[] ids){
		try {
			alarmExApi.deleteAlarmSetting(ids);
			return super.toSuccess("删除成功");
		} catch (Exception e) {
			logger.error("删除链路告警设置数据失败!",e);
			return toJsonObject(700, "删除失败");
		}
	}
	
	/**
	 * 保存链路告警设置
	 * @param alarmSetting
	 */
	@RequestMapping(value="/link/warn/setting/save",method=RequestMethod.POST)
	public JSONObject saveLinkAlarmSetting(String alarmSetting){
		try {
			if(StringUtils.isNotBlank(alarmSetting)){
				instanceTableApi.saveLinkAlarmSetting(alarmSetting);
				return super.toSuccess("保存成功");
			}else {
				return toJsonObject(700, "保存失败,缺少保存数据");
			}
		} catch (Exception e) {
			logger.error("保存链路告警设置数据失败!",e);
			return toJsonObject(700, "保存失败");
		}
	}
	
	/**
	 * 获取链路告警设置信息
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/link/warn/setting/get")
	public JSONObject getLinkWarnSetting(){
		try {
			Page page = new Page();
			JSONArray alarms = instanceTableApi.getLinkAlarmSetting();
			page.setDatas(alarms);
			page.setTotalRecord(alarms.size());
			
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询链路告警设置列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");
		}
	}
	
	/**
	 * 分页查询资源链路列表
	 * @param page
	 * @param conditions
	 * @return JSONObject
	 */
	@RequestMapping("/link/list/page")
	public JSONObject getLinkPageList(Page<LinkBo, LinkBo> page, LinkBo params) {
		try {
			//封装查询条件
			params.setDomainSet(this.getLoginUserDomains());
			page.setCondition(params);
			instanceTableApi.selectLinkByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询资源链路列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 根据资源实例ids获取链路数据
	 * @param resourceIds
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ResponseBody
	@RequestMapping(value="/link/list")
	public JSONObject getLinksByInstanceIds(long[] resourceIds,String type){
		try {
			Page page = new Page();
			List<LinkBo> linkBos = instanceTableApi.getLinksByInstanceIds(resourceIds,type);
			page.setDatas(linkBos);
			page.setTotalRecord(linkBos.size());
			return (JSONObject)JSON.toJSON(page);
		} catch (Exception e) {
			logger.error("根据资源实例ids查询资源链路列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");
		}
	}
	
	/**
	 * 分页查询设备资源实例列表数据
	 * @param page
	 * @param conditions
	 * @return
	 */
	@RequestMapping("/device/list")
	public JSONObject getDevicePageList(Page<NodeBo, NodeBo> page, NodeBo params) {
		try {
			//封装查询条件
			params.setDomainSet(this.getLoginUserDomains());
			page.setCondition(params);
			instanceTableApi.selectDeviceByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询设备资源实例列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 * 获取登录用户所属域集合
	 * @return
	 */
	private Set<Long> getLoginUserDomains(){
		ILoginUser user = getLoginUser();
		Set<IDomain> domains = user.getDomains();
		
		Set<Long> domainSet = new HashSet<Long>();
		for(IDomain domain:domains){
			domainSet.add(domain.getId());
		}
		return domainSet;
	}
}
