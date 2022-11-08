package com.mainsteam.stm.ct.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.mainsteam.stm.ct.api.IProfilelibService;
import com.mainsteam.stm.ct.bo.MsProfilelibMain;
import com.mainsteam.stm.ct.dao.ProfilelibMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

import java.util.Arrays;

public class ProfilelibServiceImpl implements IProfilelibService{
	Logger log=Logger.getLogger(ProfilelibServiceImpl.class);
    @Autowired
    private ProfilelibMapper profilelibMapper;

    

    @Override
    public int addProfilelib(MsProfilelibMain msProfilelibParams) {
       return profilelibMapper.insert(msProfilelibParams);
    }

    @Override
    public int editProfilelib(MsProfilelibMain msProfilelibParams) {
       return profilelibMapper.updateById(msProfilelibParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProfilelib(String id) {
        return profilelibMapper.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBatchProfilelib(String ids) {
    	String[] split = ids.split(",");
		Long [] temps=new Long[split.length];
		for (int i = 0; i < split.length; i++) {
			temps[i]=Long.parseLong(split[i]);
		}
        return profilelibMapper.removeByIds(temps);
    }

	@Override
	public void getProfilelibList(Page<MsProfilelibMain, MsProfilelibMain> page) {
		// TODO Auto-generated method stub
		try {
			profilelibMapper.getProfilelibList(page);
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
		}
		
	}

	@Override
	public MsProfilelibMain getById(String string) {
		// TODO Auto-generated method stub
		return profilelibMapper.getById(string);
	}
}
