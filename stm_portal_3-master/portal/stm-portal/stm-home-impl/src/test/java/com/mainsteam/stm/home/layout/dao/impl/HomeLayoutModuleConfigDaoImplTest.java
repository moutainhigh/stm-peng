package com.mainsteam.stm.home.layout.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutModuleConfigDao;
import com.mainsteam.stm.home.ssq.SqlSessionTemplateFactory;
import com.mainsteam.stm.home.test.ITest;

public class HomeLayoutModuleConfigDaoImplTest implements ITest{
	
	
	private SqlSessionTemplateFactory stt = null;
	
	public HomeLayoutModuleConfigDaoImplTest(SqlSessionTemplateFactory stt){
		this.stt = stt;
	}

	@Override
	public void test() {
		SqlSessionTemplate st = null;
		try {
			st = this.stt.getSqlSessionTemplate(IHomeLayoutModuleConfigDao.class);
			HomeLayoutModuleConfigDaoImpl hdi = new HomeLayoutModuleConfigDaoImpl(st);
			
			this.testGetById(hdi);
			this.testGetByLayoutId(hdi);
			this.testInsert(hdi);
			this.testUpdate(hdi);
			this.testDelete(hdi);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}
	private void testGetById(HomeLayoutModuleConfigDaoImpl hdi){
		System.out.println("开始测试####getById###方法");
		
		HomeLayoutModuleConfigBo byId = hdi.getById(11);
		System.err.println("测试结果: " + byId.getId());
		
		System.out.println("结束测试####getById###方法");
	}
	
	private void testGetByLayoutId(HomeLayoutModuleConfigDaoImpl hdi){
		System.out.println("开始测试####getByLayoutId###方法");
		
		List<HomeLayoutModuleConfigBo> byLayoutId = hdi.getByLayoutId(11);
		System.err.println("测试结果: " + byLayoutId.size());
		
		System.out.println("结束测试####getByLayoutId###方法");
	}
	private void testInsert(HomeLayoutModuleConfigDaoImpl hdi){
		System.out.println("开始测试####insert###方法");
		HomeLayoutModuleConfigBo homeLayoutModuleConfigBo = new HomeLayoutModuleConfigBo();
		homeLayoutModuleConfigBo.setId(27);
		homeLayoutModuleConfigBo.setModuleId(11);
		homeLayoutModuleConfigBo.setModuleCode("topN");
		homeLayoutModuleConfigBo.setUserId(1);
		homeLayoutModuleConfigBo.setLayoutId(3);
		homeLayoutModuleConfigBo.setProps("{}");
		
		hdi.insert(homeLayoutModuleConfigBo);
		HomeLayoutModuleConfigBo byId = hdi.getById(27);
		System.err.println("测试结果: " + byId.getId());
		
		System.out.println("结束测试####insert###方法");
	}
	private void testUpdate(HomeLayoutModuleConfigDaoImpl hdi){
		System.out.println("开始测试####updateProps###方法");
		HomeLayoutModuleConfigBo homeLayoutModuleConfigBo = new HomeLayoutModuleConfigBo();
		homeLayoutModuleConfigBo.setId(27);
		homeLayoutModuleConfigBo.setUserId(1);
		homeLayoutModuleConfigBo.setLayoutId(3);
		homeLayoutModuleConfigBo.setProps("{\"title\":\"曲线\"}");
		
		hdi.updateProps(homeLayoutModuleConfigBo);
		
		HomeLayoutModuleConfigBo byId = hdi.getById(27);
		System.err.println("测试结果: " + byId.getProps());
		
		System.out.println("结束测试####updateProps###方法");
	}
	private void testDelete(HomeLayoutModuleConfigDaoImpl hdi){
		System.out.println("开始测试####delete###方法");
		
		HomeLayoutModuleConfigBo homeLayoutModuleConfigBo = new HomeLayoutModuleConfigBo();
		homeLayoutModuleConfigBo.setId(27);
		homeLayoutModuleConfigBo.setUserId(1);
		homeLayoutModuleConfigBo.setLayoutId(3);
		hdi.delete(homeLayoutModuleConfigBo);
		HomeLayoutModuleConfigBo byId = hdi.getById(27);
		System.err.println("测试结果: " + byId);
		
		System.out.println("结束测试####delete###方法");
	}
}
