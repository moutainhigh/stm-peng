/*package com.mainsteam.stm.topo.web.action;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;

import test.AbstractContextControllerTests;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.web.vo.SettingVo;

@RunWith(SpringJUnit4ClassRunner.class)
public class SettingActionTest extends AbstractContextControllerTests{
	@Autowired
	private SettingAction settingAction;
	
	@Test
	public void testGetCfgInfo() throws Exception {	//推荐测试方式
		ResultActions result = this.mockMvc.perform(
				post("/topo/setting/get")
				.param("key", "warnSetting"))
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
//				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("获取告警设置结果："+result.andReturn().getResponse().getContentAsString());
	}
	
	@Test
	public void testSaveSetting() throws Exception {	//推荐测试方式
		String value = getFindSettingVo();
		ResultActions result = this.mockMvc.perform(
				post("/topo/setting/save")
				.param("key", "findSetting")
				.param("value", value))
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
//				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("拓扑发现配置保存结果："+result.andReturn().getResponse().getContentAsString());
	}
	
	@Ignore
	@Test
	public void testSaveSetting1() {
		SettingVo settingVo = new SettingVo();
		settingVo = new SettingVo();
		settingVo.setKey("findSetting");
		settingVo.setValue(getFindSettingVo());
		
//		settingAction.saveSetting(settingVo);
//		Assert.assertNotNull(rst);
//		System.out.println("拓扑发现配置保存结果："+rst.toJSONString());
	}
	
	*//**
	 * 说明：封装发现配置信息测试数据
	 * @return
	 *//*
	private String getFindSettingVo(){
		//核心ip
		String []coreIps = {"192.168.1.1","192.158.11.15"};
		//子网
		JSONObject subnet1 = new JSONObject();
		subnet1.put("ip","192.168.12.45");
		subnet1.put("mask","255.255.144.0");
		JSONObject subnet2 = new JSONObject();
		subnet2.put("ip","192.168.12.36");
		subnet2.put("mask","255.255.144.0");
		JSONObject []subnets = {subnet1,subnet2};
		//网段
		JSONObject segment1 = new JSONObject();
		segment1.put("ip","192.168.12.45");
		segment1.put("mask","255.255.144.0");
		JSONObject segment2 = new JSONObject();
		segment2.put("ip","192.168.12.36");
		segment2.put("mask","255.255.144.0");
		JSONObject []segments = {segment1,segment2};
		//发现算法
		String alg = "MPLS";
		//其他
		String other = "other……";
		
		JSONObject vo = new JSONObject();
		vo.put("coreIps", coreIps);
		vo.put("subnets", subnets);
		vo.put("segments", segments);
		vo.put("alg", alg);
		vo.put("other", other);
		return vo.toJSONString();
	}
}


*/