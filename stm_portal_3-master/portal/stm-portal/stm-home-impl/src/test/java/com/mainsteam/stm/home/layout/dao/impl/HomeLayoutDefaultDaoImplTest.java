package com.mainsteam.stm.home.layout.dao.impl;

import java.sql.SQLException;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutDefaultBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDefaultDao;
import com.mainsteam.stm.home.ssq.SqlSessionTemplateFactory;
import com.mainsteam.stm.home.test.ITest;

public class HomeLayoutDefaultDaoImplTest implements ITest{
	private SqlSessionTemplateFactory stt = null;
	
	public HomeLayoutDefaultDaoImplTest(SqlSessionTemplateFactory stt){
		this.stt = stt;
	}

	@Override
	public void test() {
		SqlSessionTemplate st = null;
		try {
			st = this.stt.getSqlSessionTemplate(IHomeLayoutDefaultDao.class);
			HomeLayoutDefaultDaoImpl hdi = new HomeLayoutDefaultDaoImpl(st);
			
			this.testGetByUserId(hdi);
			this.testInsert(hdi);
			this.testUpdate(hdi);
			this.testDelete(hdi);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	private void testGetByUserId(HomeLayoutDefaultDaoImpl hdi){
		System.out.println("开始测试####getByUserId###方法");
		
		HomeLayoutDefaultBo byUserId = hdi.getByUserId(1);
		System.err.println("测试结果: " + byUserId.getUserId());
		
		System.out.println("结束测试####getByUserId###方法");
	}
	private void testInsert(HomeLayoutDefaultDaoImpl hdi){
		System.out.println("开始测试####insert###方法");
		HomeLayoutDefaultBo homeLayoutDefaultBo = new HomeLayoutDefaultBo();
		homeLayoutDefaultBo.setUserId(2);
		homeLayoutDefaultBo.setDefaultLayoutId(0);
		hdi.insert(homeLayoutDefaultBo);
		HomeLayoutDefaultBo byUserId = hdi.getByUserId(2);
		System.err.println("测试结果: " + byUserId.getUserId());
		
		System.out.println("结束测试####insert###方法");
	}
	private void testUpdate(HomeLayoutDefaultDaoImpl hdi){
		System.out.println("开始测试####update###方法");
		HomeLayoutDefaultBo homeLayoutDefaultBo = new HomeLayoutDefaultBo();
		homeLayoutDefaultBo.setUserId(2);
		homeLayoutDefaultBo.setDefaultLayoutId(2);
		hdi.update(homeLayoutDefaultBo);
		HomeLayoutDefaultBo byUserId = hdi.getByUserId(2);
		System.err.println("测试结果: " + byUserId.getUserId());
		
		System.out.println("结束测试####update###方法");
	}
	private void testDelete(HomeLayoutDefaultDaoImpl hdi){
		System.out.println("开始测试####delete###方法");
		
		hdi.delete(2);
		HomeLayoutDefaultBo byUserId = hdi.getByUserId(2);
		System.err.println("测试结果: " + byUserId);
		
		System.out.println("结束测试####delete###方法");
	}
}
