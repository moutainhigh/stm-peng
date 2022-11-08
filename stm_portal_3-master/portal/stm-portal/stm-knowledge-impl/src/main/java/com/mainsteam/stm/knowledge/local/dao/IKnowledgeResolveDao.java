/**
 * 
 */
package com.mainsteam.stm.knowledge.local.dao;

import java.util.List;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;

/**
 * <li>文件名称: IKnowledgeResolveDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 知识解决方案管理接口</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月5日 下午3:47:14
 * @author   俊峰
 */
public interface IKnowledgeResolveDao {

	
	/**
	* @Title: queryKnowledgeResolve
	* @Description:  解决故障，通过知ID返回故障解决方案列表
	* @param knowledgeId
	* @return  List<KnowledgeResolveBo>
	* @throws
	*/
	List<KnowledgeResolveBo> queryKnowledgeResolve(long knowledgeId);
	
	
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
	int saveResolveEvaluation(ResolveEvaluationBo evaluation);
	
	/**
	* @Title: insertKnowledgeResolve
	* @Description: 添加知识解决方案
	* @param resolve
	* @return  int
	* @throws
	*/
	int insertKnowledgeResolve(KnowledgeResolveBo resolve);
	
	/**
	* @Title: updateKnowledgeResolve
	* @Description: 更新知识解决方案
	* @param resolve
	* @return  int
	* @throws
	*/
	int updateKnowledgeResolve(KnowledgeResolveBo resolve);
	
	/**
	* @Title: deleteKnowledgeResolve
	* @Description: 通过解决方案ID删除解决方案
	* @param resolveId
	* @return  int
	* @throws
	*/
	int deleteKnowledgeResolve(long resolveId);
	
	/**
	* @Title: deleteResolveByKnowledge
	* @Description: 通过知识ID删除知识下所有解决方案
	* @param knowledgeId
	* @return  int
	* @throws
	*/
	int deleteResolveByKnowledge(long knowledgeId);
}
