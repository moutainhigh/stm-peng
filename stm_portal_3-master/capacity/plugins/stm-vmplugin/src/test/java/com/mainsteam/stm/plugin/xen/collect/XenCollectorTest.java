package com.mainsteam.stm.plugin.xen.collect;

import org.junit.Test;

import com.mainsteam.stm.plugin.xen.bo.ConnectionInfo;

public class XenCollectorTest {

	@Test
	public void testGetResourceTree() {
		try {
			XenCollector collector = new XenCollector(new ConnectionInfo("192.168.1.201", "root", "12345678"));
			System.out.println(collector.getResourceTree());
			System.out.println(collector.getHostCpuRate("46c36375-3ee9-4201-817d-3c0cded3806b"));
			System.out.println(collector.getHostMemRate("46c36375-3ee9-4201-817d-3c0cded3806b"));
			System.out.println(collector.getHostThroughput("46c36375-3ee9-4201-817d-3c0cded3806b"));
			System.out.println(collector.getHostSysUpTime("46c36375-3ee9-4201-817d-3c0cded3806b"));
//			System.out.println(collector.getVMCpuRate("d65c0918-588c-536e-8462-a9df7712bfb9"));
//			System.out.println(collector.getVMMemRate("d65c0918-588c-536e-8462-a9df7712bfb9"));
//			System.out.println(collector.getVMThroughput("d65c0918-588c-536e-8462-a9df7712bfb9"));
//			System.out.println(collector.getVMSysUpTime("d65c0918-588c-536e-8462-a9df7712bfb9"));
//			System.out.println(collector.getSRPhysicalSize("476345a0-3d62-3502-606a-e018989e6d16"));
//			System.out.println(collector.getSRPhysicalUtilisation("476345a0-3d62-3502-606a-e018989e6d16"));
//			System.out.println(collector.getSRPhysicalRate("476345a0-3d62-3502-606a-e018989e6d16"));
			System.out.println(collector.getSRAddress("476345a0-3d62-3502-606a-e018989e6d16"));
			System.out.println(collector.getSRAddress("a90b5384-e52e-d530-30af-26422173c470"));
			System.out.println(collector.getSRAddress("dd011815-d0b9-57fb-40cd-fc07c178d6bd"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
