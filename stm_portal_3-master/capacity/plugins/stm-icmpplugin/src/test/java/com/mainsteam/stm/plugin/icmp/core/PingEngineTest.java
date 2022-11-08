package com.mainsteam.stm.plugin.icmp.core;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lich on 2017/6/7.
 */
public class PingEngineTest {

    public static final int NUM = 1000;
    public static final int TIMEOUT = 3000;
    public static final int RETRY = 10;
    public static final int START_ADDRESS = (180 << 24) | (97 << 16) | (33 << 8) | 107;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        System.setProperty("caplibs.path", "F:\\OC4\\trunk\\Capacity\\cap_libs");
    }

    @Test
    public void testPingIp() throws IOException, InterruptedException {
        System.out.println(PingEngine.init());
        int address = START_ADDRESS;
        final ArrayList<String> ipList = new ArrayList<>(NUM);
        for (int i = 0; i < NUM; ++i, ++address) {
            int b1 = (address >> 24) & 0xff;
            int b2 = (address >> 16) & 0xff;
            int b3 = (address >> 8) & 0xff;
            int b4 = (address) & 0xff;
            if (b4 == 0 || b4 == 255) {
                --i;
                continue;
            }
            Target target = new Target(b1 + "." + b2 + "." + b3 + "." + b4, TIMEOUT, RETRY);
            ipList.add(target.getIp());
            if (!PingEngine.addTarget(target))
                System.out.println(target);
        }
        ScheduledExecutorService checkExecutor = Executors.newSingleThreadScheduledExecutor();
        checkExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (String ip : ipList) {
                    if (PingEngine.getResult(ip) != null) {
                        System.out.println(PingEngine.getResult(ip).toDetailString());
                    } else {
                        System.out.println(ip + " is out of date, add to engine again.");
                        PingEngine.addTarget(new Target(ip, TIMEOUT, RETRY));
                    }
                }
            }
        }, 1, 2, TimeUnit.MINUTES);
        Thread.sleep(60 * 60 * 1000);
        checkExecutor.shutdownNow();
        PingEngine.stop();
    }
}
