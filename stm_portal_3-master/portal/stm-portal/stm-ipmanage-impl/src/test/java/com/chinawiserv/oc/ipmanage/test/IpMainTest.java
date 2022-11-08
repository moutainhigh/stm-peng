package com.mainsteam.stm.ipmanage.test;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.ipmanage.api.IpMainService;
import com.mainsteam.stm.ipmanage.bo.IpMain;
import com.mainsteam.stm.ipmanage.bo.TreeNode;
import com.mainsteam.stm.ipmanage.impl.IpMainServiceImpl;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class IpMainTest {
	@Test
	public void test() {
		IpMainService ipMainService=new IpMainServiceImpl();
		Map<TreeNode, List<IpMain>> ipAutoDiscover = ipMainService.IpAutoDiscover();
		JSONObject jsonObject=new JSONObject();
		String jsonString = JSONObject.toJSONString(ipAutoDiscover);
		System.out.println(jsonString);
	}
}
