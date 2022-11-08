package com.mainsteam.stm.state.ext.tools;

import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.ext.process.bean.CompareInstanceState;
import org.apache.commons.lang.StringUtils;

import java.util.PriorityQueue;

/**
 * 序列化工具类
 */
@Deprecated
public class SerializeAndDeserializeUtil {

    public static final String NON_STRING = "null";
    public static final String FIELD_SEPARATOR = ",";

    /**
     * 序列化告警队列
     * @param priorityQueue
     * @return
     */
    public static String serializeAlarmStateQueue(PriorityQueue<CompareInstanceState> priorityQueue) {
        if(priorityQueue.isEmpty()) {
            return NON_STRING;
        }else {
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                CompareInstanceState poll = priorityQueue.poll();
                if(null != poll) {
                    stringBuilder.append(poll.getId());
                    stringBuilder.append(FIELD_SEPARATOR);
                    stringBuilder.append(poll.getAlarmState().getStateVal());
                    stringBuilder.append(FIELD_SEPARATOR);
                }else{
                    break;
                }
            }
            return stringBuilder.toString();
        }
    }

    /**
     * 反序列化告警队列
     * @param priorityQueue
     * @return
     */
    public static PriorityQueue<CompareInstanceState> deserializeAlarmStateQueue(String priorityQueue) {
        if(StringUtils.isNotBlank(priorityQueue)) {
            PriorityQueue<CompareInstanceState> alarmStateQueue = new PriorityQueue<>(10);
            if(StringUtils.equals(priorityQueue, NON_STRING))
                return alarmStateQueue;
            int beginIndex = 0;
            int endIndex = 0;
            int fromIndex = 0;
            int count = 0;
            CompareInstanceState compareInstanceState = null;
            while ((endIndex+1) < priorityQueue.length()) {
                endIndex = priorityQueue.indexOf(FIELD_SEPARATOR, fromIndex);
                String str = priorityQueue.substring(beginIndex, endIndex);
                count++;
                if(StringUtils.isNotBlank(str)) {
                    switch (count){
                        case 1:
                            compareInstanceState = new CompareInstanceState();
                            compareInstanceState.setId(str);
                            break;
                        case 2:
                            compareInstanceState.setAlarmState(MetricStateEnum.valueIt(Integer.parseInt(str)));
                            break;
                    }
                }
                beginIndex = endIndex+1;
                fromIndex = endIndex+1;
                if(count == 2) {
                    alarmStateQueue.offer(compareInstanceState);
                    count = 0;
                }
            }

            return alarmStateQueue;
        }
        return null;
    }

}
