package com.mainsteam.stm.topo.test;


import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({
	"classpath:META-INF/services/public-mybatis-beans.xml",
	"classpath:META-INF/services/public-jpa-beans.xml",
	"classpath:META-INF/services/portal-platform-beans.xml",
	"classpath*:META-INF/services/public-portal-topo-beans.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AbstractDaoest extends AbstractJUnit4SpringContextTests{
	//TODO:修改备份
	//@ContextConfiguration({"classpath:META-INF/services/portal-platform-beans.xml","classpath*:META-INF/services/public-*-beans.xml"})
	/*@ContextConfiguration({
		"classpath:META-INF/services/public-mybatis-beans.xml",
		"classpath:META-INF/services/public-jpa-beans.xml",
		"classpath:META-INF/services/portal-platform-beans.xml",
		"classpath*:META-INF/services/public-portal-topo-beans.xml"})*/
}