package com.mainsteam.stm.topo.realPanelXml;

import javax.annotation.Resource;

import org.junit.Test;

import com.mainsteam.stm.topo.dao.IBackbordBaseDao;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class ImportXmlTest extends AbstractDaoest{
	@Resource(name="stm_topo_backbord_baseDao")
	IBackbordBaseDao dao;
	
	@Test
	public void testExport(){
		ImportXml xml = new ImportXml();
		try {
			xml.importXml(dao);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}


