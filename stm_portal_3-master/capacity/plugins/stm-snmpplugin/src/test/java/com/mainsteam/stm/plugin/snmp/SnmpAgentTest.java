package com.mainsteam.stm.plugin.snmp;


import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class SnmpAgentTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testGetSnmpResult() throws IOException {
        SnmpParameter p = new SnmpParameter();
        p.setIp("192.168.1.1");
        p.setPort(161);
        p.setCommunity("public");
        //p.setOids(new String[] { "1.3.6.1.2.1.2.2.1.6" });
        // "1.3.6.1.2.1.1.3.0"
        // 1.3.6.1.2.1.2.2.1.6
        //SnmpAgent.getInstance().getSnmpResult(p, PDU.GETNEXT);
        p.setOids(new String[]{"1.3.6.1.2.1.2.2.1.3"});
        // "1.3.6.1.2.1.1.3.0"

    }

}
