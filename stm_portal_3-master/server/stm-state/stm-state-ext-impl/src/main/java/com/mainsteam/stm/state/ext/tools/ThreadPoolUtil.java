package com.mainsteam.stm.state.ext.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Xiaopf on 2017/7/20.
 */
@Component("stateThreadPoolUtil")
public class ThreadPoolUtil {

    private static final int STATE_COMPUTE_THREAD = Runtime.getRuntime().availableProcessors();

    private static final int ALARM_EVENT_THREAD = Runtime.getRuntime().availableProcessors()*2;

    @Value("${state_compute_workers:8}")
    private int stateComputeThread;

    @Value("${alarm_event_workers:24}")
    private int alarmEventThread;

    @Value("${state_compute_timeout:60}")
    private int stateComputeTimeout;

    @Value("${alarm_event_timeout:60}")
    private int alarmEventTimeout;

    public int getStateComputeThread() {
        return stateComputeThread;
    }

    public void setStateComputeThread(int stateComputeThread) {
        if(stateComputeThread < STATE_COMPUTE_THREAD)
            this.stateComputeThread = STATE_COMPUTE_THREAD;
        else
            this.stateComputeThread = stateComputeThread;
    }

    public int getAlarmEventThread() {
        return alarmEventThread;
    }

    public void setAlarmEventThread(int alarmEventThread) {
        if(alarmEventThread < ALARM_EVENT_THREAD)
            this.alarmEventThread = ALARM_EVENT_THREAD;
        else
            this.alarmEventThread = alarmEventThread;
    }

    public int getStateComputeTimeout() {
        return stateComputeTimeout;
    }

    public void setStateComputeTimeout(int stateComputeTimeout) {
        this.stateComputeTimeout = stateComputeTimeout;
    }

    public int getAlarmEventTimeout() {
        return alarmEventTimeout;
    }

    public void setAlarmEventTimeout(int alarmEventTimeout) {
        this.alarmEventTimeout = alarmEventTimeout;
    }
}
