/**
 * 
 */
package com.mainsteam.stm.knowledge.local.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.bo.ResolveEvaluationBo;
import com.mainsteam.stm.knowledge.local.api.IKnowledgeAttachmentApi;
import com.mainsteam.stm.knowledge.local.api.IKnowledgeResolveApi;
import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.knowledge.local.dao.IKnowledgeResolveDao;
import com.mainsteam.stm.lucene.api.IIndexManageApi;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;

@Service("knowledgeResolveApi")
public class KnowledgeResolveImpl implements IKnowledgeResolveApi {

	@Autowired
	@Qualifier("knowledgeResolveDao")
	private IKnowledgeResolveDao knowledgeResolveDao;
	
	@Autowired
	@Qualifier("knowledgeAttachmentApi")
	private IKnowledgeAttachmentApi knowledgeAttachmentApi;
	
	@Autowired
	@Qualifier("IIndexManageApi")
	private IIndexManageApi indexManagerApi;
	
	@Autowired
	private IFileClientApi fileClient;
	
//	private IIndexApi indexApi;
	
	@PostConstruct
	public void init(){
//		try {
//			indexApi =indexManagerApi.getIndexApi(IIndexKeyConstant.KEY_KNOWLEDGE_RESOLVE);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private ISequence sequence;
	@Autowired
	public KnowledgeResolveImpl(SequenceFactory sequenceFactory){
		sequence = sequenceFactory.getSeq(SEQ.SEQNAME_STM_KNOWLEDGE_SOURCE_RESOLVE_REL);
	}
	
	
	@Override
	public List<KnowledgeResolveBo> queryKnowledgeResolves(long knowledgeId) {
		List<KnowledgeResolveBo> resolves = knowledgeResolveDao.queryKnowledgeResolve(knowledgeId);
		for (KnowledgeResolveBo resolve : resolves) {
			resolve.setResolveAttachments(knowledgeAttachmentApi.selectResolveAttachment(resolve.getId()));
		}
		return resolves;
	}

	@Override
	public KnowledgeResolveBo getResolve(long resolveId) {
		KnowledgeResolveBo resolve = knowledgeResolveDao.getResolve(resolveId);
		if(null!=resolve){
			List<KnowledgeAttachmentBo> attachments = knowledgeAttachmentApi.selectResolveAttachment(resolveId);//查询解决方案附件
			resolve.setResolveAttachments(attachments);//将附件添加到解决方案中
		}
		return resolve;
	}

	@Override
	public boolean saveResolveEvaluation(ResolveEvaluationBo evaluation) {
		evaluation.setEvaluateTime(Calendar.getInstance().getTime());
		int result = knowledgeResolveDao.saveResolveEvaluation(evaluation);
		return result>0?true:false;
	}

	@Override
	public boolean insertKnowledgeResolve(KnowledgeResolveBo resolve) {
		if(null!=resolve){
			resolve.setId(sequence.next());
			int result = knowledgeResolveDao.insertKnowledgeResolve(resolve);
			List<InputStream> ins = new ArrayList<InputStream>();
			if(result>0 && resolve.getResolveAttachments()!=null){
				for (KnowledgeAttachmentBo  attachment : resolve.getResolveAttachments()) {
					attachment.setResolveId(resolve.getId());
					try {
						ins.add(fileClient.getFileInputStreamByID(attachment.getFileId()));
					} catch (Exception e) {
						e.printStackTrace();
					}
					knowledgeAttachmentApi.insertKnowledgeAttachent(attachment);
				}
			}
//			indexApi.createIndex(resolve,ins.toArray(new InputStream[]{}));
			return result>0?true:false;
		}
		return false;
	}
	
	public boolean updateKnowledgeResolve(KnowledgeResolveBo resolve){
		int result = knowledgeResolveDao.updateKnowledgeResolve(resolve);
		List<InputStream> ins = new ArrayList<InputStream>();
		if(result>0 && resolve.getResolveAttachments()!=null){
			KnowledgeAttachmentBo delAttachment = new KnowledgeAttachmentBo();
			delAttachment.setResolveId(resolve.getId());
			knowledgeAttachmentApi.deleteKnowledgeAttachment(delAttachment);
			for (KnowledgeAttachmentBo  attachment : resolve.getResolveAttachments()) {
				attachment.setResolveId(resolve.getId());
				try {
					ins.add(fileClient.getFileInputStreamByID(attachment.getFileId()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				knowledgeAttachmentApi.insertKnowledgeAttachent(attachment);
			}
//			indexApi.updateIndex(resolve, ins.toArray(new InputStream[]{}));
			return true;
		}
		return false;
	}


	@Override
	public boolean deleteKnowledgeResolve(long resolveId) {
		KnowledgeResolveBo resolveBo = this.getResolve(resolveId);
		if(resolveBo!=null){
			if(resolveBo.getResolveAttachments()!=null){
				for (KnowledgeAttachmentBo attachment : resolveBo.getResolveAttachments()) {
					knowledgeAttachmentApi.deleteKnowledgeAttachment(attachment);
				}
			}
//			indexApi.deleteIndex(resolveId);
			return knowledgeResolveDao.deleteKnowledgeResolve(resolveBo.getId())>0?true:false;
		}
		return false;
	}


	@Override
	public boolean deleteResolveByKnowledge(long knowledgeId) {
		boolean result = true;
		List<KnowledgeResolveBo> resolveBos = this.queryKnowledgeResolves(knowledgeId);
		if(resolveBos!=null){
			for (KnowledgeResolveBo knowledgeResolveBo : resolveBos) {
				if(!this.deleteKnowledgeResolve(knowledgeResolveBo.getId())){
					result = false;
				}
			}
		}
		return result;
	}

}
