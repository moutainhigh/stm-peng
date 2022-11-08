package com.mainsteam.stm.knowledge.local.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.knowledge.local.dao.IKnowledgeAttachmentDao;
import com.mainsteam.stm.platform.dao.BaseDao;

@Repository("knowledgeAttachmentDao")
public class KnowledgeAttachmentDaoImpl extends BaseDao<KnowledgeAttachmentBo> implements IKnowledgeAttachmentDao{

	@Autowired
	public KnowledgeAttachmentDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IKnowledgeAttachmentDao.class.getName());
	}
	
	@Override
	public int delete(KnowledgeAttachmentBo attachment) {
		return getSession().delete(getNamespace()+"delete", attachment);
	}

	@Override
	public List<KnowledgeAttachmentBo> select(long resolveId) {
		return getSession().selectList(getNamespace()+"select",resolveId);
	}

}
