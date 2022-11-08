package com.mainsteam.stm.state.thirdparty.dao;

import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;

import java.util.List;

/**
 * Created by Xiaopf on 6/15/2016.
 */
public interface ThirdPartyMetricStateDAO {

    public void save(ThirdPartyMetricStateData thirdPartyMetricStateData);

    public void updateIfExistsOrAdd(ThirdPartyMetricStateData thirdPartyMetricStateData);

    public void delete(ThirdPartyMetricStateData thirdPartyMetricStateData);

    public ThirdPartyMetricStateData findOne(ThirdPartyMetricStateData thirdPartyMetricStateData);

    public List<ThirdPartyMetricStateData> findList(ThirdPartyMetricStateData thirdPartyMetricStateData);

}
