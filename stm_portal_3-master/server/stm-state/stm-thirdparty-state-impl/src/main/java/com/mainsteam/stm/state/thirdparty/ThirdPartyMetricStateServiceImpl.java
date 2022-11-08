package com.mainsteam.stm.state.thirdparty;


import com.mainsteam.stm.state.thirdparty.dao.ThirdPartyMetricStateDAO;
import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;

import java.util.List;

/**
 * Created by Xiaopf on 6/15/2016.
 */
public class ThirdPartyMetricStateServiceImpl implements ThirdPartyMetricStateService{

    private ThirdPartyMetricStateDAO thirdPartyMetricStateDAO;

    public void setThirdPartyMetricStateDAO(ThirdPartyMetricStateDAO thirdPartyMetricStateDAO) {
        this.thirdPartyMetricStateDAO = thirdPartyMetricStateDAO;
    }

    @Override
    public void saveThirdPartyMetricState(ThirdPartyMetricStateData thirdPartyMetricStateData) {
        thirdPartyMetricStateDAO.save(thirdPartyMetricStateData);
    }

    @Override
    public ThirdPartyMetricStateData findThirdPartyMetricState(long instanceId, String metricId) {
        ThirdPartyMetricStateData thirdPartyMetricStateData = new ThirdPartyMetricStateData();
        thirdPartyMetricStateData.setInstanceID(instanceId);
        thirdPartyMetricStateData.setMetricID(metricId);
        return thirdPartyMetricStateDAO.findOne(thirdPartyMetricStateData);
    }

    @Override
    public List<ThirdPartyMetricStateData> findThirdPartyMetricState(long instanceId) {
        ThirdPartyMetricStateData thirdPartyMetricStateData = new ThirdPartyMetricStateData();
        thirdPartyMetricStateData.setInstanceID(instanceId);
        return thirdPartyMetricStateDAO.findList(thirdPartyMetricStateData);
    }

    @Override
    public void deleteThirdPartyMetricState(ThirdPartyMetricStateData thirdPartyMetricStateData) {
        thirdPartyMetricStateDAO.delete(thirdPartyMetricStateData);
    }

    @Override
    public void updateIfExistsOrAddState(ThirdPartyMetricStateData thirdPartyMetricStateData) {
        thirdPartyMetricStateDAO.updateIfExistsOrAdd(thirdPartyMetricStateData);
    }
}
