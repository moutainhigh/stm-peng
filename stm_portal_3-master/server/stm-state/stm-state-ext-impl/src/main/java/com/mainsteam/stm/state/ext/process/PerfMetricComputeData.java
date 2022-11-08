package com.mainsteam.stm.state.ext.process;

import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.state.obj.MetricStateData;

/**
 * Created by Xiaopf on 2017/7/12.
 */
public class PerfMetricComputeData {

    private boolean alarmNotifiable;
    private MetricStateData metricStateData;
    private ProfileThreshold threshold;
    private int flapping;

    public PerfMetricComputeData(){
    }

    public PerfMetricComputeData(boolean alarmNotifiable){
        this.alarmNotifiable = alarmNotifiable;
    }

    public int getFlapping() {
        return flapping;
    }
    public void setFlapping(int flapping) {
        this.flapping = flapping;
    }
    public MetricStateData getMetricStateData() {
        return metricStateData;
    }
    public void setMetricStateData(MetricStateData metricStateData) {
        this.metricStateData = metricStateData;
    }
    public boolean isAlarmNotifiable() {
        return alarmNotifiable;
    }
    public void setAlarmNotifiable(boolean alarmNotifiable) {
        this.alarmNotifiable = alarmNotifiable;
    }

    public ProfileThreshold getThreshold() {
        return threshold;
    }

    public void setThreshold(ProfileThreshold threshold) {
        this.threshold = threshold;
    }
}
