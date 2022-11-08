package com.mainsteam.stm.portal.resource.web.action;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ProcessMetricDataApi;
import com.mainsteam.stm.portal.resource.bo.AddInstanceResult;
import com.mainsteam.stm.portal.resource.bo.ProcessDeadLockDataPageBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataBo;
import com.mainsteam.stm.portal.resource.bo.ProcessMetricDataPageBo;
import com.mainsteam.stm.portal.resource.web.vo.ProcessParameterVo;


/**
 * 
 * <li>文件名称: ProcessMetricDataAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   pengl
 */

@Controller
@RequestMapping("/portal/resourceManager/processMetricData")
public class ProcessMetricDataAction extends BaseAction {
	
	private static final Log logger = LogFactory.getLog(ProcessMetricDataAction.class);
	
	@Autowired
	private ProcessMetricDataApi processMetricDataApi;
	
	/**
	 * 获取指标实时数据
	 */
	@RequestMapping("/getProcessData")
	public JSONObject getProcessData(Long mainInstanceId) {
		
		ProcessMetricDataPageBo pageBo = processMetricDataApi.queryRealTimeMetricDatas(mainInstanceId);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 获取进程实时数据
	 */
	@RequestMapping("/scanProcessData")
	public JSONObject scanProcessData(long mainInstanceId) {
		
		ProcessMetricDataPageBo pageBo = processMetricDataApi.scanCurrentProcessData(mainInstanceId);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 加入监控
	 */
	@RequestMapping("/addProcessToMonitor")
	public JSONObject addProcessToMonitor(ProcessParameterVo parameter,HttpSession session) {
		//当前用户
		ILoginUser user = getLoginUser(session);
		
		List<ProcessMetricDataBo> processMetricDataBoList = JSONObject.parseArray(parameter.getProcessList(), ProcessMetricDataBo.class);
		
		AddInstanceResult result = processMetricDataApi.addProcessMonitor(processMetricDataBoList,parameter.getMainInstanceId(),parameter.getType(),user);
		
		return toSuccess(result);
		
	}
	
	/**
	 * 删除进程子资源
	 */
	@RequestMapping("/deleteProcessInstance")
	public JSONObject deleteProcessInstance(String instanceIds) {
		
		boolean result = processMetricDataApi.deleteProcessInstance(instanceIds);
		
		return toSuccess(result);
		
	}
	
	/**
	 * 更新进程备注
	 */
	@RequestMapping("/updateProcessInstanceRemark")
	public JSONObject updateProcessInstanceRemark(long mainInstanceId,String newRemark) {
		
		boolean isSuccess = processMetricDataApi.updateProcessInstanceRemark(mainInstanceId, newRemark);
		
		return toSuccess(isSuccess);
		
	}
	
	/**
	 * 获取实时僵死进程
	 */
	@RequestMapping("/diedProcessData")
	public JSONObject diedProcessData(long mainInstanceId) {
		
		ProcessMetricDataPageBo pageBo = processMetricDataApi.diedProcessData(mainInstanceId);
		
		return toSuccess(pageBo);
		
	}
	
	/**
	 * 获取实时死锁进程
	 */
	@RequestMapping("/deadLockInfoData")
	public JSONObject deadLockInfoData(long mainInstanceId) {
		
		ProcessDeadLockDataPageBo pageBo = processMetricDataApi.deadLockInfoData(mainInstanceId);
		
		return toSuccess(pageBo);
		
	}
}
