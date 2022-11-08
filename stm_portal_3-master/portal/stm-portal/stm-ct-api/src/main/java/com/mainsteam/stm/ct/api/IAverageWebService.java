package com.mainsteam.stm.ct.api;

import java.util.List;

import com.mainsteam.stm.ct.bo.MsAverageWeb;

public interface IAverageWebService{

    List<MsAverageWeb> getList(MsAverageWeb msAverageWeb);
    int save(MsAverageWeb msAverageWeb);
}
