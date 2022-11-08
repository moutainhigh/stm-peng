package com.mainsteam.stm.ct.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.ct.api.IResourceinfoService;
import com.mainsteam.stm.ct.bo.MsCtResourceinfo;
import com.mainsteam.stm.ct.dao.CtResourceinfoMapper;
public class ResourceinfoServiceImpl implements IResourceinfoService {
	Logger log=Logger.getLogger(ResourceinfoServiceImpl.class);
	
	@Resource
	private CtResourceinfoMapper ctResourceinfoMapper;
	@Override
	public List<MsCtResourceinfo> selectById(String id) {
		// TODO Auto-generated method stub
		return ctResourceinfoMapper.selectById(id);
	}
	@Override
	public void addInfo(MsCtResourceinfo resourceinfo) {
		// TODO Auto-generated method stub
		ctResourceinfoMapper.addInfo(resourceinfo);
	}
}
