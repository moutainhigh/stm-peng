package com.mainsteam.stm.knowledge.type.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.knowledge.type.bo.KnowledgeTypeBo;
import com.mainsteam.stm.knowledge.type.dao.IKnowledgeTypeDao;
import com.mainsteam.stm.platform.dao.BaseDao;

@Repository("knowledgeTypeDao")
public class KnowledgeTypeDaoImpl extends BaseDao<KnowledgeTypeBo> implements
		IKnowledgeTypeDao {

	@Autowired
	public KnowledgeTypeDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IKnowledgeTypeDao.class.getName());
	}

//	@Override
//	public List<KnowledgeTypeBo> select() {
//		return getSession().selectList(getNamespace()+"select");
//	}

}
