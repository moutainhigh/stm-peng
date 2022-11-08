package com.mainsteam.stm.ct.impl;


import org.springframework.beans.factory.annotation.Autowired;
import com.mainsteam.stm.ct.api.IAverageWebService;
import com.mainsteam.stm.ct.bo.MsAverageWeb;
import com.mainsteam.stm.ct.dao.AverageWebMapper;

import java.util.List;


public class AverageWebServiceImpl implements IAverageWebService {

    @Autowired
    private AverageWebMapper averageWebMapper;

    @Override
    public List<MsAverageWeb> getList(MsAverageWeb msAverageWeb) {
        return averageWebMapper.getList(msAverageWeb);
    }

	@Override
	public int save(MsAverageWeb msAverageWeb) {
		// TODO Auto-generated method stub
		return averageWebMapper.insert(msAverageWeb);
	}
}
