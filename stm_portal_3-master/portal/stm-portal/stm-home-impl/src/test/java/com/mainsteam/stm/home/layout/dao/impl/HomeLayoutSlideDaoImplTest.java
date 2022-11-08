package com.mainsteam.stm.home.layout.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutSlideDao;
import com.mainsteam.stm.home.ssq.SqlSessionTemplateFactory;
import com.mainsteam.stm.home.test.ITest;

public class HomeLayoutSlideDaoImplTest implements ITest{

	
	private SqlSessionTemplateFactory stt = null;
	
	public HomeLayoutSlideDaoImplTest(SqlSessionTemplateFactory stt){
		this.stt = stt;
	}
	
	@Override
	public void test() {
		SqlSessionTemplate st = null;
		try {
			st = this.stt.getSqlSessionTemplate(IHomeLayoutSlideDao.class);
			HomeLayoutSlideDaoImpl hdi = new HomeLayoutSlideDaoImpl(st);
			
			this.testGetByUserId(hdi);
			this.testInsert(hdi);
			this.testDeleteByUserId(hdi);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	private void testGetByUserId(HomeLayoutSlideDaoImpl hdi){
		System.out.println("开始测试####getByUserId###方法");
		
		List<HomeLayoutSlideBo> byUserId = hdi.getByUserId(1);
		System.err.println("测试结果: " + byUserId.size());
		
		System.out.println("结束测试####getByUserId###方法");
	}
	private void testInsert(HomeLayoutSlideDaoImpl hdi){
		System.out.println("开始测试####insert###方法");
		
		HomeLayoutSlideBo homeLayoutSlideBo = new HomeLayoutSlideBo();
		homeLayoutSlideBo.setId(980005);
		homeLayoutSlideBo.setUserId(2);
		homeLayoutSlideBo.setLayoutId(106500);
		homeLayoutSlideBo.setSortNum(6);
		homeLayoutSlideBo.setSlideTime(30);
		homeLayoutSlideBo.setAnimation("nochoose");
		
		hdi.insert(homeLayoutSlideBo);
		List<HomeLayoutSlideBo> byUserId = hdi.getByUserId(2);
		System.err.println("测试结果: " + byUserId.size());
		
		System.out.println("结束测试####insert###方法");
	}
	private void testDeleteByUserId(HomeLayoutSlideDaoImpl hdi){
		System.out.println("开始测试####deleteByUserId###方法");
		
		hdi.deleteByUserId(2);
		List<HomeLayoutSlideBo> byUserId = hdi.getByUserId(2);
		System.err.println("测试结果: " + byUserId.size());
		
		System.out.println("结束测试####deleteByUserId###方法");
	}
}
