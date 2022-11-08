/**
 * 
 */
package com.mainsteam.stm.home.layout.dao.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDao;
import com.mainsteam.stm.home.layout.dao.impl.HomeLayoutDaoImpl;
import com.mainsteam.stm.home.layout.vo.HomeLayoutVo;
import com.mainsteam.stm.home.ssq.SqlSessionTemplateFactory;
import com.mainsteam.stm.home.test.ITest;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: HomeLayoutDaoImplTest.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年5月10日
 * @author  tandl
 */
public class HomeLayoutDaoImplTest implements ITest{
	
	private SqlSessionTemplateFactory stt = null;
	
	public HomeLayoutDaoImplTest(SqlSessionTemplateFactory stt){
		this.stt = stt;
	}
	
	@Override
	public void test() {
		SqlSessionTemplate st = null;
		try {
			st = this.stt.getSqlSessionTemplate(IHomeLayoutDao.class);
			HomeLayoutDaoImpl hdi = new HomeLayoutDaoImpl(st);
			
			this.testGetOne(hdi);
			this.testGetById(hdi);
			this.testGetByUserId(hdi);
			this.testGetTemplateByUserId(hdi);
			this.testInsert(hdi);
			this.testUpdateLayout(hdi);
			this.testUpdateBaseInfo(hdi);
			this.testUpdateLayoutInfo(hdi);
			this.testDelete(hdi);
			this.testGetTemplates(hdi);
			this.testSelectByPage(hdi);
			this.testSelectByPage(hdi);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	private void testGetById(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####getById###方法");
		
		HomeLayoutBo b = hdi.getById(1);
		System.err.println("测试结果: " + b.getName());
		
		System.out.println("结束测试####getById###方法");
	}
	
	private void testGetOne(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####getOne###方法");
		
		HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
		homeLayoutBo.setId(1);
		homeLayoutBo = hdi.getOne(homeLayoutBo);
		System.err.println("测试结果: " + homeLayoutBo.getId());
		
		System.out.println("结束测试####getOne###方法");
	}
	
	private void testGetByUserId(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####getByUserId###方法");
		
		List<HomeLayoutBo> list = hdi.getByUserId(1);
		System.err.println("测试结果: " + list.size());
		
		System.out.println("结束测试####getByUserId###方法");
	}
	
	private void testGetTemplateByUserId(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####getTemplateByUserId###方法");
		
		List<HomeLayoutBo> list = hdi.getTemplateByUserId(1);
		System.err.println("测试结果: " + list.size());
		
		System.out.println("结束测试####getTemplateByUserId###方法");
	}
	
	private void testInsert(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####insert###方法");
		
		HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
		homeLayoutBo.setId(999999);
		homeLayoutBo.setName("单元测试模板");
		homeLayoutBo.setRefreshTime(60);
		homeLayoutBo.setUserId(1);
		homeLayoutBo.setLayoutType((byte)2);
		homeLayoutBo.setLayout("{\"w\":1366,\"h\":677,\"widgets\":[]}");
		homeLayoutBo.setCreateTime(new Timestamp(System.currentTimeMillis()));
		homeLayoutBo.setTempId("0");
		homeLayoutBo.setCopyUserId(0);
		
		hdi.insert(homeLayoutBo);
		HomeLayoutBo byId = hdi.getById(homeLayoutBo.getId());
		
		System.err.println("测试结果: " + byId.getId());
		
		System.out.println("结束测试####insert###方法");
	}
	
	private void testUpdateLayout(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####updateLayout###方法");
		
		HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
		homeLayoutBo.setId(999999);
		homeLayoutBo.setUserId(1);
		homeLayoutBo.setLayout("{\"w\":366,\"h\":677,\"widgets\":[]}");
		
		hdi.updateLayout(homeLayoutBo);
		
		homeLayoutBo = hdi.getById(homeLayoutBo.getId());
		System.err.println("测试结果: " + homeLayoutBo.getLayout());
		
		System.out.println("结束测试####updateLayout###方法");
	}
	
	private void testUpdateBaseInfo(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####updateBaseInfo###方法");
		
		HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
		homeLayoutBo.setId(999999);
		homeLayoutBo.setName("单元测试模板修改");
		homeLayoutBo.setRefreshTime(60);
		homeLayoutBo.setUserId(1);
		homeLayoutBo.setTempId("0");
		homeLayoutBo.setLayout("{\"w\":366,\"h\":677,\"widgets\":[]}");
		
		hdi.updateBaseInfo(homeLayoutBo);
		
		homeLayoutBo = hdi.getById(homeLayoutBo.getId());
		System.err.println("测试结果: " + homeLayoutBo.getName());
		
		System.out.println("结束测试####updateBaseInfo###方法");
	}
	
	private void testDelete(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####delete###方法");
		
		hdi.delete(999999,1);
		HomeLayoutBo homeLayoutBo = hdi.getById(999999);
		System.err.println("测试结果: " + homeLayoutBo);
		
		System.out.println("结束测试####delete###方法");
	}
	
	private void testGetTemplates(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####getTemplates###方法");
		
		List<HomeLayoutBo> templates = hdi.getTemplates();
		System.err.println("测试结果: " + templates.size());
		
		System.out.println("结束测试####getTemplates###方法");
	}
	
	private void testUpdateLayoutInfo(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####updateLayoutInfo###方法");
		
		HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
		homeLayoutBo.setId(999999);
		homeLayoutBo.setLayout("{\"w\":366,\"h\":677,\"widgets\":[]}");
		
		hdi.updateLayoutInfo(homeLayoutBo);
		
		homeLayoutBo = hdi.getById(homeLayoutBo.getId());
		System.err.println("测试结果: " + homeLayoutBo.getLayout());
		
		System.out.println("结束测试####updateLayoutInfo###方法");
	}
	
	
	private void testSelectByPage(HomeLayoutDaoImpl hdi){
		System.out.println("开始测试####selectByPage###方法");
		Page<HomeLayoutBo, HomeLayoutVo> page = new Page<HomeLayoutBo, HomeLayoutVo>();
		hdi.selectByPage(page);
		
		System.out.println("结束测试####selectByPage###方法");
	}

}
