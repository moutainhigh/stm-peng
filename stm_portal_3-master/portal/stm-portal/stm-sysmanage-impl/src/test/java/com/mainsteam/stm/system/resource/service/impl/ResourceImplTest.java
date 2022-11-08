package com.mainsteam.stm.system.resource.service.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.system.resource.api.IResourceApi;

/**
 * <li>文件名称: ResourceImplTest.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月24日
 * @author   ziwenwen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml"
	})
public class ResourceImplTest {

	@Test
	public void testGetResourcesLong() {
	}

}


