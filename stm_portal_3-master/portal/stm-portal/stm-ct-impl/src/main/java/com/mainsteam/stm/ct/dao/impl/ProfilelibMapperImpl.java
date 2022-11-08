package com.mainsteam.stm.ct.dao.impl;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsProfilelibMain;
import com.mainsteam.stm.ct.dao.ProfilelibMapper;
import com.mainsteam.stm.ct.impl.ProfilelibServiceImpl;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class ProfilelibMapperImpl extends BaseDao<MsProfilelibMain> implements ProfilelibMapper {
	Logger log=Logger.getLogger(ProfilelibMapperImpl.class);
	public ProfilelibMapperImpl(SqlSessionTemplate session) {
		super(session, ProfilelibMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getProfilelibList(Page<MsProfilelibMain, MsProfilelibMain> page) {
		// TODO Auto-generated method stub
		try {
			super.select("getProfilelibList", page);
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage()+"\t"+e.getStackTrace());
		}
		
		
		
	}
	@Override
	public int insert(MsProfilelibMain msProfilelibParams){
		return super.insert( msProfilelibParams);
	}
	
	@Override
	public int updateById(MsProfilelibMain msProfilelibParams) {
		// TODO Auto-generated method stub
		return super.update(msProfilelibParams);
	}

	@Override
	public int removeById(String id) {
		// TODO Auto-generated method stub
		return super.del("del", id);
	}

	@Override
	public int removeByIds(Long[] temps) {
		// TODO Auto-generated method stub
		return super.batchDel(temps);
	}

	@Override
	public MsProfilelibMain getById(String id) {
		// TODO Auto-generated method stub
		return super.get("get", id);
	}

}
