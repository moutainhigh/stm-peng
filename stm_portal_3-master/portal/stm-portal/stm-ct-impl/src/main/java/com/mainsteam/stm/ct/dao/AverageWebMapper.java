package com.mainsteam.stm.ct.dao;

import java.util.List;

import com.mainsteam.stm.ct.bo.MsAverageWeb;

public interface AverageWebMapper{

	
    List<MsAverageWeb> getList(MsAverageWeb msAverageWeb);

	int insert(MsAverageWeb msAverageWeb);
	
}
 