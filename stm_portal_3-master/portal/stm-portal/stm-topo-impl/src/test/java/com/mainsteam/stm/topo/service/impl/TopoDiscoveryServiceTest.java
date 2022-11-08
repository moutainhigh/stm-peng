package com.mainsteam.stm.topo.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;
import com.mainsteam.stm.topo.test.AbstractDaoest;

public class TopoDiscoveryServiceTest extends AbstractDaoest{
	//资源实例服务
	@Autowired
	private ResourceInstanceDiscoveryService risvc;
	//能力哭服务
	@Autowired
	private CapacityService csvc;
	@Test
	public void testFindResult(){
		//装载发现参数
		ResourceInstanceDiscoveryParameter param = new ResourceInstanceDiscoveryParameter();
		Map<String,String> info = new HashMap<String,String>();
		info.put("IP","172.16.10.1");
		info.put("port","161");
		info.put("community", "public");
		param.setDiscoveryWay("snmp");
		param.setResourceId(csvc.getResourceId("1.3.6.1.4.1.2636.1.1.1.2.31"));
		param.setDiscoveryInfos(info);
		param.setNodeGroupId(183001);//TODO 183001具体填写什么？
		//处理资源发现业务
		try {
			DiscoverResourceIntanceResult  result = risvc.discoveryResourceInstance(param);
			Assert.assertTrue(result.isSuccess());
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.toString());
			}
		}
	
	}
}
