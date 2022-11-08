/**
 * 
 */
package com.mainsteam.stm.knowledge.local.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;
import com.mainsteam.stm.knowledge.local.dao.IKnowledgeResolveDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: KnowledgeResolveDaoImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月5日 下午3:50:06
 * @author   俊峰
 */
@Repository("knowledgeResolveDao")
public class KnowledgeResolveDaoImpl extends BaseDao<KnowledgeResolveBo> implements IKnowledgeResolveDao {
	
	@Autowired
	public KnowledgeResolveDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IKnowledgeResolveDao.class.getName());
	}

	@Override
	public List<KnowledgeResolveBo> queryKnowledgeResolve(long knowledgeId) {
		return getSession().selectList(getNamespace()+"queryKnowledgeResolve",knowledgeId);
	}

	@Override
	public KnowledgeResolveBo getResolve(long resolveId) {
		return getSession().selectOne(getNamespace()+"getResolve", resolveId);
	}

	@Override
	public int saveResolveEvaluation(ResolveEvaluationBo evaluation) {
		return getSession().insert(getNamespace()+"saveResolveEvaluation", evaluation);
	}

	@Override
	public int insertKnowledgeResolve(KnowledgeResolveBo resolve) {
		return getSession().insert(getNamespace()+"insertKnowledgeResolve", resolve);
	}

	@Override
	public int deleteKnowledgeResolve(long resolveId) {
		
		return getSession().delete(getNamespace()+"deleteKnowledgeResolve", resolveId);
	}

	@Override
	public int deleteResolveByKnowledge(long knowledgeId) {
		
		return getSession().delete(getNamespace()+"deleteResolveByKnowledge",knowledgeId);
	}

	@Override
	public int updateKnowledgeResolve(KnowledgeResolveBo resolve) {
		return getSession().update(getNamespace()+"updateKnowledgeResolve",resolve);
	}

}
