package com.mainsteam.stm.knowledge.capacity.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.knowledge.capacity.bo.CapacityKnowledgeBo;
import com.mainsteam.stm.knowledge.capacity.dao.ICapacityKnowledgeDao;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

@Repository("capacityKnowledgeDao")
public class CapacityKnowledgeDaoImpl extends BaseDao<CapacityKnowledgeBo> implements ICapacityKnowledgeDao {

	@Autowired
	public CapacityKnowledgeDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, ICapacityKnowledgeDao.class.getName());
	}

	@Override
	public List<CapacityKnowledgeBo> pageSelect(Page<CapacityKnowledgeBo, CapacityKnowledgeBo> page) {
		return getSession().selectList(getNamespace()+"pageSelect", page);
	}
}
