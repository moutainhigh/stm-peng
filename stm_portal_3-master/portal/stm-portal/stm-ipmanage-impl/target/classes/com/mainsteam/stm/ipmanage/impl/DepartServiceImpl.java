package com.mainsteam.stm.ipmanage.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.ipmanage.api.DepartService;
import com.mainsteam.stm.ipmanage.bo.Depart;
import com.mainsteam.stm.ipmanage.impl.dao.DepartMapper;
import com.mainsteam.stm.ipmanage.impl.dao.IpMainMapper;

public class DepartServiceImpl implements DepartService {
	Logger logger = Logger.getLogger(DepartServiceImpl.class);
	@Resource
	private DepartMapper departMapper;

	@Resource
	private IpMainMapper ipMainMapper;
	
	public List<Depart> getDepartList() {
		// TODO Auto-generated method stub
		List<Depart> i=null;
		try {
			i=departMapper.getDepartList();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			
		}
		return i;
	}

	
	public int insert(Depart depart) {
		// TODO Auto-generated method stub
		try {
			return departMapper.insert(depart);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
		
	}

	
	public int del(Integer id) {
		
		try {
			ipMainMapper.resetDepart(id);
			return departMapper.delete(id);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
		// TODO Auto-generated method stub
		
	}

}
