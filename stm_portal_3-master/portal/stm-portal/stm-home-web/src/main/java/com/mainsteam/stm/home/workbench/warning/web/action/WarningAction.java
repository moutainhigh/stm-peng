package com.mainsteam.stm.home.workbench.warning.web.action;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.home.workbench.main.api.IUserWorkBenchApi;
import com.mainsteam.stm.home.workbench.resource.api.IResourceApi;
import com.mainsteam.stm.home.workbench.warning.api.IWarningApi;
import com.mainsteam.stm.home.workbench.warning.bo.Warning;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.util.DateUtil;
@Controller
@RequestMapping("/home/warning")
public class WarningAction extends BaseAction{
	@Autowired
	IWarningApi warningApi;
	
	@Autowired
	@Qualifier("stm_home_workbench_resourceApi")
	private IResourceApi homeResourceApi;

	@Autowired
	private IUserWorkBenchApi userWorkBenchApi;
	
	/**
	 * 告警查询
	 * @param groupId 资源组Id
	 * @param start 起始时间
	 * @param end 结束时间
	 * @param domainId 域ID
	 * @return
	 */
	@RequestMapping("/getAll")
	public JSONObject getAll(long groupId,Long start,Long end,Long... domainId){
		ILoginUser user = getLoginUser();
		List<Long> instanceIds = new ArrayList<Long>();
		if(groupId != 0){
			instanceIds = userWorkBenchApi.getInstanceIdByGroupId(groupId, user);
		}else{
			instanceIds = homeResourceApi.queryGroupResourceByDomain("", user, groupId, domainId);
		}
		List<String> myInstanceIds = new ArrayList<String>();
		for (Long instanceId : instanceIds) {
			myInstanceIds.add(String.valueOf(instanceId));
		}
		Page<Warning, AlarmEventQuery> page=new Page<Warning, AlarmEventQuery>();
		AlarmEventQuery condition = new AlarmEventQuery();
		condition.setSourceIDes(myInstanceIds); 
		
		if(start == null || end == null){//默认查询时间为一天
			Date now = new Date();
			Date yesterday = DateUtil.subDay(now, 1);
			condition.setStart(yesterday);
			condition.setEnd(now);
		}else{
			condition.setStart(new Date(start));
			condition.setEnd(new Date(end));
		}
		
		page.setCondition(condition);
		warningApi.getAllWarning(page);
		page.setTotalRecord(page.getDatas()==null?0:page.getDatas().size());
		return toSuccess(page);
	}

	/**
	 * 告警一览
	 * @param groupId
	 * @param alarmLevel
	 * @param begin
	 * @param end
	 * @param domainId
	 * @return
	 */
	@RequestMapping("/get")
	public JSONObject get(long groupId,String alarmLevel,long start,long end,String count,Long ... domainId){
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
		List<Long> instanceIds = new ArrayList<Long>();
		if(groupId != 0){
			instanceIds = userWorkBenchApi.getInstanceIdByGroupId(groupId, user);
		}else{
			instanceIds = homeResourceApi.queryGroupResourceByDomain("", user, groupId, domainId);
		}
		List<String> myInstanceIds = new ArrayList<String>();
		for (Long instanceId : instanceIds) {
			myInstanceIds.add(String.valueOf(instanceId));
		}
		Page<Warning, AlarmEventQuery> page=new Page<Warning, AlarmEventQuery>();
		AlarmEventQuery condition = new AlarmEventQuery();
		condition.setEnd(new Date(end));
		condition.setStart(new Date(start));
		condition.setRecovered(false);
		
		if(alarmLevel != null){
			List<MetricStateEnum> states = new ArrayList<>();
			String[] als = alarmLevel.split(",");
			for (String al : als) {
				try{
					states.add(MetricStateEnum.valueOf(al));
				}catch(Exception e){}
			}
			condition.setStates(states );
		}
		condition.setSourceIDes(myInstanceIds);
		//域信息
		condition.setExt9(StringUtils.join(domainId,","));
		page.setCondition(condition);
		page.setStartRow(0);
		page.setRowCount(Integer.parseInt(count));
		warningApi.getWarning(page,user);
		
		page.setTotalRecord(page.getDatas()==null?0:page.getDatas().size());
		if(page.getDatas() != null && page.getDatas().size() > Integer.parseInt(count)){
			List<Warning> wList = page.getDatas();
			page.setDatas(wList);
		}
		return toSuccess(page);
	}

}
