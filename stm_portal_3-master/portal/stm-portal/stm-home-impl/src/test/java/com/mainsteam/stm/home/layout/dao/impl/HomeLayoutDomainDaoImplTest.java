package com.mainsteam.stm.home.layout.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.dao.IHomeLayoutDomainDao;
import com.mainsteam.stm.home.ssq.SqlSessionTemplateFactory;
import com.mainsteam.stm.home.test.ITest;
import com.mainsteam.stm.system.um.domain.bo.Domain;

public class HomeLayoutDomainDaoImplTest implements ITest {
	
	private SqlSessionTemplateFactory stt = null;
	
	public HomeLayoutDomainDaoImplTest(SqlSessionTemplateFactory stt){
		this.stt = stt;
	}

	@Override
	public void test() {
		// TODO Auto-generated method stub
		try {
			SqlSessionTemplate st = this.stt.getSqlSessionTemplate(IHomeLayoutDomainDao.class);
			HomeLayoutDomainDaoImpl impl = new HomeLayoutDomainDaoImpl(st);
			List<Domain> list = new ArrayList<>();
			list = impl.getDomainByLayoutId(182015, null);
			System.out.println("ID为【182015】布局,已分配给下列域：");
			if (list.size()>0) {
				for (Domain domain : list) {
					System.out.println("id:"+domain.getId()+",name:"+domain.getName());
				}
			}else {
				System.out.println("return null");
			}
			list = impl.getUnDomainByLayoutId(182015, null);
			System.out.println("ID为【182015】布局,未分配给下列域：");
			if (list.size()>0) {
				for (Domain domain : list) {
					System.out.println("id:"+domain.getId()+",name:"+domain.getName());
				}
			}else {
				System.out.println("return null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
