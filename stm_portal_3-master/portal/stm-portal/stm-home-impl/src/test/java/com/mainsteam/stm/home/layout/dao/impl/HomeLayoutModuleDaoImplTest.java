package com.mainsteam.stm.home.layout.dao.impl;

import java.sql.SQLException;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutModuleDao;
import com.mainsteam.stm.home.ssq.SqlSessionTemplateFactory;
import com.mainsteam.stm.home.test.ITest;

public class HomeLayoutModuleDaoImplTest implements ITest{

	private SqlSessionTemplateFactory stt = null;
	
	public HomeLayoutModuleDaoImplTest(SqlSessionTemplateFactory stt){
		this.stt = stt;
	}
	
	@Override
	public void test() {
		SqlSessionTemplate st = null;
		try {
			st = this.stt.getSqlSessionTemplate(IHomeLayoutModuleDao.class);
			HomeLayoutModuleDaoImpl hdi = new HomeLayoutModuleDaoImpl(st);
			
			this.testGet(hdi);
			this.testGetById(hdi);
			this.testInsert(hdi);
			this.testUpdate(hdi);
			this.testDelete(hdi);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}
	private void testGet(HomeLayoutModuleDaoImpl hdi){
		System.out.println("开始测试####get###方法");
		
		HomeLayoutModuleBo homeLayoutModuleBo = hdi.get(1);
		System.err.println("测试结果: " + homeLayoutModuleBo.getId());
		
		System.out.println("结束测试####get###方法");
	}
	
	private void testGetById(HomeLayoutModuleDaoImpl hdi){
		System.out.println("开始测试####getById###方法");
		
		HomeLayoutModuleBo byId = hdi.getById(1);
		System.err.println("测试结果: " + byId.getId());
		
		System.out.println("结束测试####getById###方法");
	}
	private void testInsert(HomeLayoutModuleDaoImpl hdi){
		System.out.println("开始测试####insert###方法");
		
		HomeLayoutModuleBo homeLayoutModuleBo = new HomeLayoutModuleBo();
		homeLayoutModuleBo.setId(2);
		homeLayoutModuleBo.setName("单元测试");
		homeLayoutModuleBo.setModuleCode("001");
		homeLayoutModuleBo.setDefaultWidth(123);
		homeLayoutModuleBo.setDefaultHeight(456);
		homeLayoutModuleBo.setUrl("baidu.com");
		homeLayoutModuleBo.setSortNum(1);
		
		hdi.insert(homeLayoutModuleBo);
		HomeLayoutModuleBo byId = hdi.getById(2);
		System.err.println("测试结果: " + byId.getId());
		
		System.out.println("结束测试####insert###方法");
	}
	private void testUpdate(HomeLayoutModuleDaoImpl hdi){
		System.out.println("开始测试####update###方法");
		HomeLayoutModuleBo homeLayoutModuleBo = new HomeLayoutModuleBo();
		homeLayoutModuleBo.setId(2);
		homeLayoutModuleBo.setName("修改单元测试");
		homeLayoutModuleBo.setModuleCode("001");
		homeLayoutModuleBo.setDefaultWidth(123);
		homeLayoutModuleBo.setDefaultHeight(456);
		homeLayoutModuleBo.setUrl("baidu.com");
		homeLayoutModuleBo.setSortNum(1);
		
		hdi.update(homeLayoutModuleBo);
		
		HomeLayoutModuleBo byId = hdi.getById(2);
		System.err.println("测试结果: " + byId.getName());
		
		System.out.println("结束测试####update###方法");
	}
	private void testDelete(HomeLayoutModuleDaoImpl hdi){
		System.out.println("开始测试####delete###方法");
		
		hdi.delete(2);
		HomeLayoutModuleBo byId = hdi.getById(2);
		System.err.println("测试结果: " + byId);
		
		System.out.println("结束测试####delete###方法");
	}
}
