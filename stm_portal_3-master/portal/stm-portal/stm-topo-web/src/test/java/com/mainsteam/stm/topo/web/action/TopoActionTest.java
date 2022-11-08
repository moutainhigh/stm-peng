package com.mainsteam.stm.topo.web.action;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;

import test.AbstractContextControllerTests;

@RunWith(SpringJUnit4ClassRunner.class)
public class TopoActionTest extends AbstractContextControllerTests{
	
	@Test
	public void testTopoFind() throws Exception {	//推荐测试方式
		ResultActions result = this.mockMvc.perform(
				get("/topo/find"))
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
//				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("拓扑发现结果："+result.andReturn().getResponse().getContentAsString());
	}
}


