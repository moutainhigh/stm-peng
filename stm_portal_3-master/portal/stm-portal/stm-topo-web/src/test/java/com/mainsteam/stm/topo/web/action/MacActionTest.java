package com.mainsteam.stm.topo.web.action;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import test.AbstractContextControllerTests;

@RunWith(SpringJUnit4ClassRunner.class)
public class MacActionTest extends AbstractContextControllerTests{
	
	@Test
	public void testExportBaseMacs() throws Exception{
		ResultActions result = this.mockMvc.perform(
				post("/topo/mac/export")
				.param("ids", "2257001")
				.param("exportType", "all"))
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		System.out.println("导出基准表excel结果："+result.andReturn().getResponse().getContentAsString());
	}
	
	@Test
	public void testMacruntimeTobase() throws Exception {
		//TODO:待解决测试：如何传递数组到后台接口
//		Long[] ids = {1l,2l,3l,4l,5l,6l}; 
		List<Long> list = new ArrayList<Long>();
		list.add(1l);
		list.add(2l);
		list.add(3l);
		System.out.println(">>>"+list.toString());
		ResultActions result = this.mockMvc.perform(
				post("/topo/mac/runtime/addbse")
				.param("ids", "[1,2,3]"))
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("实时表加入基准表结果："+result.andReturn().getResponse().getContentAsString());
	}
	
	@Test
	public void testGetSubHistoryPageList() throws Exception {
		String mac = "bc-30-5b-a7-e2-cb";
		String id = "1";
		ResultActions result = this.mockMvc.perform(
				post("/topo/mac/subHistory/list")
				.param("mac", mac)
				.param("id", id))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				;
		
		System.out.println("查询结果："+result.andReturn().getResponse().getContentAsString());
	}
	
	@Test
	public void testGetHistoryPageList() throws Exception {
		String mac = "cc";
		String startTime = "2014-09-01 00:00:00";
		String endTime = "2014-09-03 00:00:00";
		ResultActions result = this.mockMvc.perform(
				post("/topo/mac/history/list")
				.param("mac", mac)
				.param("startTime", startTime)
				.param("endTime", endTime))
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("查询结果："+result.andReturn().getResponse().getContentAsString());
	}
	
	@Test
	public void testGetRuntimePageList() throws Exception {	//推荐测试方式
		ResultActions result = this.mockMvc.perform(
				post("/topo/mac/runtime/list")
//				.param("startRow","0" )
//				.param("rowCount", "10")
//				.param("searchType", "1")
//				.param("searchVal", "118")
				)
				.andExpect(status().isOk())				//判断访问是否正确(status=200)
				.andDo(MockMvcResultHandlers.print())	//查看访问信息
				;
		
		System.out.println("实时表查询结果："+result.andReturn().getResponse().getContentAsString());
	}
	
}

	



