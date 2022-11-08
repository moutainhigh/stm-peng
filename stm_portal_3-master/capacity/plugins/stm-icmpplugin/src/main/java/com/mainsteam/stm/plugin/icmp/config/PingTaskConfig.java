package com.mainsteam.stm.plugin.icmp.config;

import com.mainsteam.stm.plugin.icmp.core.AbstractPingTask;

/**
 * Created by Lich on 2017/5/25.
 */
public class PingTaskConfig {
    public final Class<? extends AbstractPingTask> clazz;
    public final int threads;
    public final int maxTargets;

    public PingTaskConfig(Class<? extends AbstractPingTask> clazz, int threads, int maxTargets) {
        this.clazz = clazz;
        this.threads = threads;
        this.maxTargets = maxTargets;
    }

    @Override
    public String toString() {
        return "PingTaskConfig{" +
                "clazz=" + clazz +
                ", threads=" + threads +
                ", maxTargets=" + maxTargets +
                '}';
    }
}
