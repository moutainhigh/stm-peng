package com.mainsteam.stm.plugin.icmp.task;

import com.mainsteam.stm.plugin.icmp.config.PingConfig;
import com.mainsteam.stm.plugin.icmp.core.AbstractPingTask;
import com.mainsteam.stm.plugin.icmp.core.Target;
import com.mainsteam.ms.util.protocol.RemotePing.CResponse;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.List;

/**
 * Created by Lich on 2017/5/25.
 */
public class CPingTask extends AbstractPingTask {

    private static final Log LOGGER = LogFactory.getLog(CPingTask.class);

    public CPingTask(int maxTargets, int threads, int index) {
        super(1, threads, index);
    }

    @Override
    public void init() {
        PingConfig pingConfig = PingConfig.getDefaultConfig();
        File directory;
        try {
            directory = new File(pingConfig.getProperty(PingConfig.FPING_PATH));
        } catch (NullPointerException e) {
            directory = new File(pingConfig.getProperty(PingConfig.CAPLIBS_PATH), "tools");
        }
        String fileName = "cping";
        String archSuffix = "_x32";
        if (SystemUtils.OS_ARCH.contains("64")) {
            archSuffix = "_x64";
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            fileName += archSuffix + ".dll";
        } else {
            fileName += archSuffix + ".so";
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("cping directory = " + directory + ", fileName = " + fileName);
        }
        System.load(new File(directory, fileName).getAbsolutePath());
    }

    @Override
    public void ping(List<Target> targets) {
        for (Target target : targets) {
            Thread.currentThread().setName("cping-" + target.getIp() + "-" + target.getTimeout() + "-" + target.getRetry());
            int[] result = CResponse.GetTime(target.getIp(), target.getRetry(), target.getTimeout());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("CPing result: ip = " + target.getIp() + ", latency = " + result[0] + ", avail = " + result[1] + ", packetLoss = " + result[2] + ", jitter = " + result[4]);
            }
            target.setLastExecuteTime(System.currentTimeMillis());
            if (result[1] > 0) {
                target.restoreHealth();
                target.setLatency(result[0] > 0 ? result[0] : 0);
                target.setPacketLoss(result[2] > 0 ? result[2] : 0);
                target.setJitter(result[4] > 0 ? result[4] : 0);
            } else {
                target.decreaseHealth();
            }
        }
    }

    @Override
    public boolean validate(Target target) {
        if (target == null || target.getIp() == null ||
                !target.getIp().matches("([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])\\.([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])\\.([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])\\.([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])") ||
                target.getTimeout() <= 0 || target.getRetry() <= 0) {
            return false;
        }
        return true;
    }

}
