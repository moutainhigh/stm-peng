package com.mainsteam.stm.topo.web.action;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import test.AbstractContextControllerTests;

@RunWith(SpringJUnit4ClassRunner.class)
public class BackbordActionTest extends AbstractContextControllerTests{
	
	@Test
	public void testExportBackbordInfo() throws Exception {
		ResultActions result = this.mockMvc.perform(
				post("/topo/backboard/export")
				.param("id", "1"))
//				.andExpect(status().isOk())				//判断访问是否正确(status=200)
//				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("导出背板信息结果：\n"+result.andReturn().getResponse().getContentAsString());
	}
	
	@Test
	public void testGetBackboard() throws Exception {
		ResultActions result = this.mockMvc.perform(
				post("/topo/backboard/get")
				.param("id", "11"))
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("查询设备背板信息结果："+result.andReturn().getResponse().getContentAsString());
	}
}

	



