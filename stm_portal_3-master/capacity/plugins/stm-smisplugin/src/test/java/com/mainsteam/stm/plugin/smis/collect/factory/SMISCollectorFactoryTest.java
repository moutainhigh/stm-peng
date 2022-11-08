package com.mainsteam.stm.plugin.smis.collect.factory;

import com.mainsteam.stm.plugin.smis.collect.SMISCollector;
import com.mainsteam.stm.plugin.smis.collect.SMISProvider;
import com.mainsteam.stm.plugin.smis.exception.SMISException;
import com.mainsteam.stm.plugin.smis.object.SMISDevice;
import org.junit.Test;

import javax.wbem.WBEMException;
import java.util.List;

public class SMISCollectorFactoryTest {


    @Test
    public void test() {
        SMISProvider provider = new SMISProvider();
        provider.setIp("172.16.8.73");
        provider.setPort("5988");
        provider.setProtocol("http");
        provider.setNameSpace("root/lsissi11");
        provider.setUsername("Administrator");
        provider.setPassword("WORKQZSM");
        provider.setVendor("IBM");
        String name = "AC100849030303030303030303030303";

        try {
            SMISCollector collector = SMISCollectorFactory.createDiskArrayCollector(provider, name);
            List<SMISDevice> devices = collector.getDevice("StorageSystem");
            for (SMISDevice device : devices) {
                System.out.println(device);
                System.out.println(collector.getStatisticalData(device));
                System.out.println(collector.getStatisticsService(device));
                System.out.println(collector.getStatisticsCapabilities(device));
            }
        } catch (WBEMException | SMISException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
