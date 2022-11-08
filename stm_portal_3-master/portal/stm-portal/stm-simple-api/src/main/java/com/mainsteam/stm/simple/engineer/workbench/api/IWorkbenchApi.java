package com.mainsteam.stm.simple.engineer.workbench.api;

import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;
import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.simple.engineer.workbench.bo.FaultProcessFlowBo;
import com.mainsteam.stm.simple.engineer.workbench.bo.RemoteAcessBo;
import com.mainsteam.stm.simple.engineer.workbench.bo.StoreBo;

/**
 * <li>文件名称: IWorkbenchApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public interface IWorkbenchApi {
	/**
	 * <pre>
	 * 根据传入的分页对象，将未解决的故障信息查询后置入分页对象中
	 * </pre>
	 * @param page
	 */
	Page<AlarmEvent, AlarmEventQuery> setResolveFault(Page<AlarmEvent, AlarmEventQuery> page,ILoginUser user);
	
	/**
	 * <pre>
	 * 根据传入的分页对象，将已解决的故障信息查询后置入分页对象中
	 * </pre>
	 * @param fpb
	 */
	Page<AlarmEvent, AlarmEventQuery> setResolvedFault(Page<AlarmEvent, AlarmEventQuery> page,ILoginUser user);
	
	/**
	 * <pre>
	 * 分析故障，返回分析知识结果列表
	 * </pre>
	 * @param re
	 * @return
	 */
	List<KnowledgeBo> analyzeFault(AlarmEvent re);

	
	/**
	 * <pre>
	 * 根据选择的知识解决故障，返回知识解决方案列表
	 * </pre>
	 * @param re
	 * @return
	 */
	List<KnowledgeResolveBo> resolveFault(long knowledgeId);
	
	

	/**
	* @Title: getKnowledgeResolve
	* @Description: 通过解决方案ID获取解决方案详细信息
	* @param resolveId
	* @return  KnowledgeResolveBo
	*/
	KnowledgeResolveBo getKnowledgeResolve(long resolveId);
	
	/**
	 * <pre>
	 * 根据知识id获取知识详情
	 * </pre>
	 * @param knowledgeId
	 * @return
	 */
	KnowledgeBo getKnowledge(long knowledgeId);
	
	/**
	 * <pre>
	 * 保存故障处理流程快照
	 * </pre>
	 * @param faultProcessFlowBo
	 * @return
	 */
	int saveFaultProcessFlow(FaultProcessFlowBo faultProcessFlowBo);
	
	/**
	 * <pre>
	 * 根据故障id获取故障处理快照信息
	 * </pre>
	 * @param faultId
	 * @return
	 */
	FaultProcessFlowBo getFaultProcessFlow(Long faultId);
	
	/**
	 * <pre>
	 * 根据远程访问协议对象和解决方案ID自动修复故障
	 * 脚本执行成功返回1 失败返回0
	 * </pre>
	 * @param rab 远程访问协议对象
	 * @param batFileId 脚本文件id
	 * @return
	 */
	int updateRepairFaultByResole(RemoteAcessBo rab);
	
	/**
	 * <pre>
	 * 获取所有店铺信息
	 * </pre>
	 * @return
	 */
	List<StoreBo> getStores();
	
	/**
	* @Title: saveResolveEvaluation
	* @Description: 保存解决方案评价信息
	* @param evaluation
	* @return  boolean
	* @throws
	*/
	boolean saveResolveEvaluation(ResolveEvaluationBo evaluation);
	
	/**
	* @Title: getAlarmEventById
	* @Description: 通过告警ID获取告警详细信息
	* @param eventId
	* @return  AlarmEvent
	* @throws
	*/
	AlarmEvent getAlarmEventById(long eventId);
	
	/**
	* @Title: getResourceInstance
	* @Description: 通过资源实例ID获取资源详细
	* @param resourceId
	* @return  ResourceInstance
	* @throws
	*/
	ResourceInstance getResourceInstance(long resourceId);
	
	/**
	* @Title: getResolveAttachments
	* @Description: 通过解决方案ID获取解决方案附件，并通过上传时间排序
	* @param resolveId
	* @return  List<KnowledgeAttachmentBo>
	* @throws
	*/
	List<KnowledgeAttachmentBo> getResolveAttachments(long resolveId);
	
	/**
	 * 检查已解决的告警是否恢复，将超过一定时间尚未恢复的告警解决状态改为未解决
	 * */
	void updateCheckedAlarmEvemtIsRecovered();
}


