package com.mainsteam.stm.topo.web.action;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;

import test.AbstractContextControllerTests;

@RunWith(SpringJUnit4ClassRunner.class)
public class TopoImageActionTest extends AbstractContextControllerTests{
	
	@Test
	public void testDeleteImagesByIds() throws Exception {
		ResultActions result = this.mockMvc.perform(
				post("/topo/image/del")
				.param("ids", "25001")
				)
//				.andExpect(status().isOk())				//判断访问是否正确(status=200)
//				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("删除图片结果："+result.andReturn().getResponse().getContentAsString());
	}
	
	//删除图片本地测试需要指定图片磁盘路径，服务器读的是tomcat图片路径，测试读取的不是tomcat环境路径
	@Test
	public void testGetImagesByType() throws Exception {
		ResultActions result = this.mockMvc.perform(
				post("/topo/image/getByType")
				.param("type", "cmd")
				)
//				.andExpect(status().isOk())				//判断访问是否正确(status=200)
//				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("获取图片结果："+result.andReturn().getResponse().getContentAsString());
	}
}

	



