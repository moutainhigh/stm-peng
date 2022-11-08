package com.mainsteam.stm.simple.engineer.workbench.web.action;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.simple.engineer.workbench.api.IWorkbenchApi;
import com.mainsteam.stm.simple.engineer.workbench.bo.FaultProcessFlowBo;
import com.mainsteam.stm.simple.engineer.workbench.bo.RemoteAcessBo;
import com.mainsteam.stm.simple.engineer.workbench.web.vo.AlarmEventVo;
import com.mainsteam.stm.util.SecureUtil;

/**
 * <li>文件名称: WorkbenchAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
@Controller("engineerWorkbenchAction")
@RequestMapping("/simple/engineer/workbench")
public class WorkbenchAction extends BaseAction {
	
	@Autowired
	@Qualifier("com.mainsteam.stm.simple.engineer.workbench.api.IWorkbenchApi")
	private IWorkbenchApi workbenchApi;
	
	@Resource
	private CapacityService capacityService;
	
	
	/**
	 * <pre>
	 * 根据传入的分页对象，将未解决的故障信息查询后置入分页对象中
	 * </pre>
	 * @param fpb
	 */
	@RequestMapping("/getResolveFault")
	JSONObject getResolveFault(Page<AlarmEvent, AlarmEventQuery> page,String likeSourceIP){
		Page<AlarmEventVo, AlarmEventQuery> resultPage = new Page<>();
		ILoginUser user = getLoginUser();
		AlarmEventQuery aeq = page.getCondition()==null?new AlarmEventQuery():page.getCondition();
		aeq.setLikeSourceIP(likeSourceIP);
		page.setCondition(aeq);
		page = workbenchApi.setResolveFault(page,user);
		if(page!=null && page.getDatas()!=null){
			List<AlarmEvent> eventRows = page.getDatas();
			List<AlarmEventVo> eventVos = new ArrayList<AlarmEventVo>();
			for (AlarmEvent alarmEvent : eventRows) {
				eventVos.add(toAlarmEventVo(alarmEvent));
			}
			resultPage.setCondition(page.getCondition());
			resultPage.setDatas(eventVos);
			resultPage.setOrder(page.getOrder());
			resultPage.setRowCount(page.getRowCount());
			resultPage.setSort(page.getSort());
			resultPage.setStartRow(page.getStartRow());
			resultPage.setTotalRecord(page.getTotalRecord());
			return toSuccess(resultPage);
		}
		return toSuccess(page);
	}
	
	/**
	 * <pre>
	 * 根据传入的分页对象，将已解决的故障信息查询后置入分页对象中
	 * </pre>
	 * @param fpb
	 */
	@RequestMapping("/getResolvedFault")
	JSONObject getResolvedFault(Page<AlarmEvent, AlarmEventQuery> page,String likeSourceIP){
		Page<AlarmEventVo, AlarmEventQuery> resultPage = new Page<>();
		ILoginUser user = getLoginUser();
		AlarmEventQuery aeq = page.getCondition()==null?new AlarmEventQuery():page.getCondition();
		aeq.setLikeSourceIP(likeSourceIP);
		page.setCondition(aeq);
		page = workbenchApi.setResolvedFault(page,user);
		if(page!=null && page.getDatas()!=null){
			List<AlarmEvent> eventRows = page.getDatas();
			List<AlarmEventVo> eventVos = new ArrayList<AlarmEventVo>();
			for (AlarmEvent alarmEvent : eventRows) {
				eventVos.add(toAlarmEventVo(alarmEvent));
			}
			resultPage.setCondition(page.getCondition());
			resultPage.setDatas(eventVos);
			resultPage.setOrder(page.getOrder());
			resultPage.setRowCount(page.getRowCount());
			resultPage.setSort(page.getSort());
			resultPage.setStartRow(page.getStartRow());
			resultPage.setTotalRecord(page.getTotalRecord());
			return toSuccess(resultPage);
		}
		return toSuccess(page);
	}
	
	/**
	 * <pre>
	 * 分析故障，返回分析知识结果列表
	 * </pre>
	 * @param re
	 * @return
	 */
	@RequestMapping("/analyzeFault")
	JSONObject analyzeFault(AlarmEvent re){
		return toSuccess(workbenchApi.analyzeFault(re));
	}

	
	/**
	 * 通过解决方案ID获取解决方案详细信息
	 * @param solutionId 解决方案ID
	 * @return List<KnowledgeSolutionBo> 解决方案列表
	 * */
	@RequestMapping("/getResolveDetail")
	JSONObject getResolveDetail(long resolveId){
		KnowledgeResolveBo resolveBo = workbenchApi.getKnowledgeResolve(resolveId);
		return toSuccess(resolveBo);
	}
	
	
	/**
	 * <pre>
	 * 根据选择的知识解决故障，返回知识解决结果列表
	 * </pre>
	 * @param re
	 * @return
	 */
	@RequestMapping("/resolveFault")
	JSONObject resolveFault(long knowledgeId){
		return toSuccess(workbenchApi.resolveFault(knowledgeId));
	}
	
	/**
	 * <pre>
	 * 根据知识id获取知识详情
	 * </pre>
	 * @param knowledgeId
	 * @return
	 */
	@RequestMapping("/getKnowledge")
	JSONObject getKnowledge(long knowledgeId){
		return toSuccess(workbenchApi.getKnowledge(knowledgeId));
	}
	
	/**
	 * <pre>
	 * 保存故障处理流程快照
	 * </pre>
	 * @param faultProcessFlowBo
	 * @return
	 */
	@RequestMapping("/saveFaultProcessFlow")
	JSONObject saveFaultProcessFlow(FaultProcessFlowBo faultProcessFlowBo){
		return toSuccess(workbenchApi.saveFaultProcessFlow(faultProcessFlowBo));
	}
	
	/**
	 * <pre>
	 * 根据故障id获取故障处理快照信息
	 * </pre>
	 * @param faultId
	 * @return
	 */
	@RequestMapping("/getFaultProcessFlow")
	JSONObject getFaultProcessFlow(Long faultId){
		return toSuccess(workbenchApi.getFaultProcessFlow(faultId));
	}
	
	
	/**
	* @Title: getResourceDetailInfo
	* @Description: 通过告警ID获取资源主机信息
	* @param eventId
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getResourceInfo")
	JSONObject getResourceDetailInfo(long eventId){
		AlarmEvent event = workbenchApi.getAlarmEventById(eventId);
		ResourceInstance resourceInstance = workbenchApi.getResourceInstance(Long.valueOf(event.getSourceID()));
		RemoteAcessBo repair = new RemoteAcessBo();
		repair.setRepairType(2);
		repair.setIp(event.getSourceIP());
		if(resourceInstance!=null){
			String[] ports = resourceInstance.getDiscoverPropBykey("port");
			String[] usernames = resourceInstance.getDiscoverPropBykey("username");
			String[] passwords = resourceInstance.getDiscoverPropBykey("password");
			if(ports!=null && ports.length>0){
				repair.setPort(ports[0]);
			}else{
				repair.setPort("22");
			}
			if(usernames!=null && usernames.length>0){
				repair.setAccount(usernames[0]);
			}
			if(passwords!=null && passwords.length>0){
				String pwd=SecureUtil.pwdDecrypt(passwords[0]);
				repair.setPassword(pwd);
			}
		}
		return toSuccess(repair);
	}
	
	/**
	* @Title: executeScriptAutoRepair
	* @Description: 通过脚本执行自动修复功能
	* @param repair
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("/executeScriptAutoRepair")
	JSONObject executeScriptAutoRepair(RemoteAcessBo repair){
		try {
			if(null!=repair){
				return toSuccess(workbenchApi.updateRepairFaultByResole(repair)>0?true:false);
			}
			return toSuccess(false);
		} catch (Exception e) {
			e.printStackTrace();
			return toSuccess(e.getMessage());
		}
		
	}
	
	
	/**
	* @Title: saveResolveEvaluation
	* @Description: 保存解决方案评价信息
	* @param evaluation
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("saveResolveEvaluation")
	JSONObject saveResolveEvaluation(ResolveEvaluationBo evaluation){
		return toSuccess(workbenchApi.saveResolveEvaluation(evaluation));
	}
	
	
	/**
	* @Title: toRepairSuccess
	* @Description: 跳转到故障修复成功页面
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("toRepairSuccess")
	JSONObject toRepairSuccess(long eventId){
		return toSuccess(workbenchApi.getAlarmEventById(eventId));
	}
	
	/**
	 * 用于手动验证故障是否修复完成，警告是否恢复
	 * */
	@RequestMapping("/checkAlarmEvent")
	void checkAlarmEvent(){
		workbenchApi.updateCheckedAlarmEvemtIsRecovered();
	}
	
	public AlarmEventVo toAlarmEventVo(AlarmEvent event){
		AlarmEventVo eventVo = new AlarmEventVo();
		BeanUtils.copyProperties(event, eventVo);
		eventVo.setAlarmTypeName(null == capacityService.getResourceDefById(event.getExt0())?"":capacityService.getResourceDefById(event.getExt0()).getName());
		return eventVo;
	}
}


