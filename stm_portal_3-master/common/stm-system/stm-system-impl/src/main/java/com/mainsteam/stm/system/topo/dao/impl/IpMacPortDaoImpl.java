package com.mainsteam.stm.system.topo.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.system.topo.dao.IIpMacPortDao;
import com.mainsteam.stm.system.um.domain.bo.Domain;

@Repository("stm_system_ipmacportDao")
public class IpMacPortDaoImpl extends BaseDao<Domain> implements IIpMacPortDao {
	@Autowired
	public IpMacPortDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IIpMacPortDao.class.getName());
	}

	@Override
	public List<Long> getMacBaseIds() {
		return this.getSession().selectList(getNamespace()+"selectIds");
	}

}
