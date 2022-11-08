package com.mainsteam.stm.topo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.VlanBo;
import com.mainsteam.stm.topo.dao.VlanDao;
@Repository
public class VlanDaoImpl extends BaseDao<VlanBo> implements VlanDao {
	@Autowired
	@Qualifier(value="stm_topo_vlan")
	private ISequence seq;
	@Autowired
	public VlanDaoImpl(@Qualifier(value=BaseDao.SESSION_DEFAULT) SqlSessionTemplate session) {
		super(session, VlanDao.class.getName());
	}
	@Override
	public void saveVlan(VlanBo vb) {
		vb.setId(seq.next());
		try {
			getSession().insert(getNamespace()+"saveVlan",vb);
		} catch (Exception e) {
			
		}
	}
	@Override
	public List<VlanBo> getVlanForNodeBo(Long queryId) {
		Assert.notNull(queryId);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("nodeId",queryId);
		return getSession().selectList(getNamespace()+"getVlanForNodeBo",param);
	}
}
