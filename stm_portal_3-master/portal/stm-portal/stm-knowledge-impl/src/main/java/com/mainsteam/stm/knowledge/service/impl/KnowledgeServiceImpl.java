package com.mainsteam.stm.knowledge.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;
import com.mainsteam.stm.knowledge.local.api.IKnowledgeResolveApi;
import com.mainsteam.stm.knowledge.local.api.ILocalKnowledgeApi;
import com.mainsteam.stm.knowledge.service.api.IKnowledgeServiceApi;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;

/**
 * <li>文件名称: KnowledgeServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月20日
 * @author   ziwenwen
 */
@Service("knowledgeServiceApi")
public class KnowledgeServiceImpl implements IKnowledgeServiceApi {

	@Autowired
	@Qualifier("localKnowledgeApi")
	private ILocalKnowledgeApi localKnowledgeApi;
	
	@Autowired
	@Qualifier("knowledgeResolveApi")
	private IKnowledgeResolveApi knowledgeResolveApi;
	
	
	@Override
	public List<KnowledgeBo> analyzeFault(FaultBo fb) {
		// TODO Auto-generated method stub
		return localKnowledgeApi.queryKnowledgeByType(fb);
	}

	@Override
	public List<KnowledgeResolveBo> resolveFault(long knowledgeId) {
		return knowledgeResolveApi.queryKnowledgeResolves(knowledgeId);
	}

	@Override
	public KnowledgeBo getKnowledge(long knowledgeId) {
		return localKnowledgeApi.queryKnowledge(knowledgeId);
	}

	@Override
	public KnowledgeResolveBo getKnowledgeResolve(long resolveId) {
		return knowledgeResolveApi.getResolve(resolveId);
	}

	@Override
	public boolean saveResolveEvaluation(ResolveEvaluationBo evaluation) {
		return knowledgeResolveApi.saveResolveEvaluation(evaluation);
	}

}


