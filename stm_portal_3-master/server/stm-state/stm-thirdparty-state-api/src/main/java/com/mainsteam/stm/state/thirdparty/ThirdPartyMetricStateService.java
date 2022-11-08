package com.mainsteam.stm.state.thirdparty;

import com.mainsteam.stm.state.thirdparty.obj.ThirdPartyMetricStateData;

import java.util.List;

/**
 * Created by Xiaopf on 6/15/2016.
 */
public interface ThirdPartyMetricStateService {

    public void saveThirdPartyMetricState(ThirdPartyMetricStateData thirdPartyMetricStateData);

    public ThirdPartyMetricStateData findThirdPartyMetricState(long instanceId, String metricId);

    public List<ThirdPartyMetricStateData> findThirdPartyMetricState(long instanceId);

    public void deleteThirdPartyMetricState(ThirdPartyMetricStateData thirdPartyMetricStateData);

    public void updateIfExistsOrAddState(ThirdPartyMetricStateData thirdPartyMetricStateData);

}
