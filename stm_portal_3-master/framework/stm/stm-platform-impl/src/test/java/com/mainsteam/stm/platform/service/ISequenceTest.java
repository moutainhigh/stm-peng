package com.mainsteam.stm.platform.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;

/**
 * <li>文件名称: ISequenceTest.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/services/public-*-beans.xml"})
public class ISequenceTest {
	
	@Autowired
	private ISystemConfigApi configApi;
	
	@Value("${stm.license.path}")
	private String path;
	
	@Test
	public void test(){
		configApi.insertTransaction(600);
	}
}


