/**
 * 
 */
package com.mainsteam.stm.knowledge.local.api;

import java.util.List;

import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;

/**
 * <li>文件名称: IKnowledgeResolveApi</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 知识故障解决方案管理api</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月5日 下午3:54:05
 * @author   俊峰
 */
public interface IKnowledgeResolveApi {


	
	/**
	* @Title: queryKnowledgeResolves
	* @Description: 解决故障，通过知ID返回故障解决方案列表
	* @param knowledgeId
	* @return  List<KnowledgeResolveBo>
	* @throws
	*/
	List<KnowledgeResolveBo> queryKnowledgeResolves(long knowledgeId);
	
	
	/**
	* @Title: getResolve
	* @Description: 通过解决方案ID获取解决方案详细信息
	* @param resolveId
	* @return  KnowledgeResolveBo
	* @throws
	*/
	KnowledgeResolveBo getResolve(long resolveId);
	
	/**
	* @Title: saveResolveEvaluation
	* @Description: 保存解决方案评价信息
	* @param evaluation
	* @return  boolean
	* @throws
	*/
	boolean saveResolveEvaluation(ResolveEvaluationBo evaluation);
	
	/**
	* @Title: insertKnowledgeResolve
	* @Description: 添加知识解决方案
	* @param resolve
	* @return  int
	* @throws
	*/
	boolean insertKnowledgeResolve(KnowledgeResolveBo resolve);
	
	/**
	* @Title: updateKnowledgeResolve
	* @Description:更新知识解决方案
	* @param resolve
	* @return  boolean
	* @throws
	*/
	boolean updateKnowledgeResolve(KnowledgeResolveBo resolve);
	
	/**
	* @Title: deleteKnowledgeResolve
	* @Description: 通过ID删除一条解决方案
	* @param resolveId
	* @return  boolean
	* @throws
	*/
	boolean deleteKnowledgeResolve(long resolveId);
	
	/**
	* @Title: deleteResolveByKnowledge
	* @Description: 通过知识ID删除知识下所有解决方案
	* @param knowledgeId
	* @return  boolean
	* @throws
	*/
	boolean deleteResolveByKnowledge(long knowledgeId);
}
