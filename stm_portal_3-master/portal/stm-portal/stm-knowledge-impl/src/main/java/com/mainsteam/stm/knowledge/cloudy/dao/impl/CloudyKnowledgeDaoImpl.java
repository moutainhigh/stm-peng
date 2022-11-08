package com.mainsteam.stm.knowledge.cloudy.dao.impl;

import java.util.Date;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.knowledge.cloudy.bo.CKnowledgeStaBo;
import com.mainsteam.stm.knowledge.cloudy.dao.ICloudyKnowledgeDao;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

@Repository("cloudyKnowledgeDao")
public class CloudyKnowledgeDaoImpl extends BaseDao<CKnowledgeStaBo> implements ICloudyKnowledgeDao {

	@Autowired
	public CloudyKnowledgeDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, ICloudyKnowledgeDao.class.getName());
	}

	@Override
	public List<CKnowledgeStaBo> pageSelect(Page<CKnowledgeStaBo, CKnowledgeStaBo> page) {
		return getSession().selectList(getNamespace()+"pageSelect", page);
	}

	@Override
	public int countCloudyKnowledgeTotal() {
		return getSession().selectOne(getNamespace()+"countCloudyKnowledgeTotal",String.class);
	}

	@Override
	public Date cloudyKnowledgeUpdateTime() {
		return getSession().selectOne(getNamespace()+"cloudyKnowledgeUpdateTime",Date.class);
	}

}
