package com.mainsteam.stm.topo.dao;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class INodeDaoTest extends AbstractDaoest{
	@Autowired
	private INodeDao idao;
	@Test
	@Transactional
	@Rollback(true)
	public void testInsert(){
		NodeBo nb = new NodeBo();
		nb.setDeviceId("kjdkfjdkjfkdjf");
		nb.setIp("192.168.1.1");
		nb.setId(11111L);
		int i = idao.insert(nb);
		Assert.assertTrue(i>0);
	}
}
