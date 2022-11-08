package com.mainsteam.stm.state.ext.process.bean;

import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;

import java.util.List;
import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/17.
 * 告警事件
 */
public class AlarmEventBean {

    private List<AlarmSenderParamter> alarmEventList; //告警事件列表
    /*
    包含以下信息：1.资源状态历史信息，用于回滚;2.指标历史数据，该集合主要用于资源删除时，需要清理的指标状态集合；
    3.当前指标状态;4.上一次指标状态，用于回滚；5.可用性指标值上一次的数据，用于回滚;6.故障状态队列，用于异常时回退
     */
    private Map<String, Object> additions;

    public Map<String, Object> getAdditions() {
        return additions;
    }

    public void setAdditions(Map<String, Object> additions) {
        this.additions = additions;
    }

    public List<AlarmSenderParamter> getAlarmEventList() {
        return alarmEventList;
    }

    public void setAlarmEventList(List<AlarmSenderParamter> alarmEventList) {
        this.alarmEventList = alarmEventList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(1000);
        stringBuilder.append("AlarmEventBean{");
        stringBuilder.append("alarmEvent=(");
        if(null != alarmEventList) {
            for (AlarmSenderParamter alarmEvent :
                    alarmEventList) {
                stringBuilder.append("[");
                stringBuilder.append("sourceId:");
                stringBuilder.append(alarmEvent.getSourceID());
                stringBuilder.append(";metricId:");
                stringBuilder.append(alarmEvent.getExt3());
                stringBuilder.append(";collectTime:");
                stringBuilder.append(alarmEvent.getGenerateTime());
                stringBuilder.append(";content:");
                stringBuilder.append(alarmEvent.getDefaultMsg());
                stringBuilder.append("],");
            }
        }else {
            stringBuilder.append("null");
        }
        stringBuilder.append(");\r\nadditions=(curMetricState:");
        stringBuilder.append(additions.get("persistenceMetricState"));
        stringBuilder.append(";");
        Object persistenceInstStates = additions.get("persistenceInstStates");
        if(null != persistenceInstStates) {
            stringBuilder.append("\r\npersistenceInstStates:");
            stringBuilder.append(persistenceInstStates);
        }else {
            stringBuilder.append("persistenceInstStates:null");
        }
        stringBuilder.append(")");
        stringBuilder.append("}");
        return stringBuilder.toString();

    }
}
