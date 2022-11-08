package com.mainsteam.stm.knowledge.service.api;

import java.util.List;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;

/**
 * <li>文件名称: IKnowledgeServiceApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public interface IKnowledgeServiceApi {

	/**
	 * <pre>
	 * 分析故障，返回分析知识结果列表
	 * </pre>
	 * @param re
	 * @return
	 */
	List<KnowledgeBo> analyzeFault(FaultBo fb);

	
	/**
	 * <pre>
	 * 根据选择的知识解决故障，返回知识解决方案
	 * </pre>
	 * @param re
	 * @return
	 */
	List<KnowledgeResolveBo> resolveFault(long knowledgeId);

	/**
	 * <pre>
	 * 根据知识id获取知识详情
	 * </pre>
	 * @param knowledgeId
	 * @return
	 */
	KnowledgeBo getKnowledge(long knowledgeId);
	
	
	
	/**
	* @Title: getKnowledgeResolve
	* @Description:通过解决方案ID获取解决方案详细信息
	* @param resolveId
	* @return  KnowledgeResolveBo
	* @throws
	*/
	KnowledgeResolveBo getKnowledgeResolve(long resolveId);
	
	/**
	* @Title: saveResolveEvaluation
	* @Description: 保存解决方案评价信息
	* @param evaluation
	* @return  boolean
	* @throws
	*/
	boolean saveResolveEvaluation(ResolveEvaluationBo evaluation);
}


