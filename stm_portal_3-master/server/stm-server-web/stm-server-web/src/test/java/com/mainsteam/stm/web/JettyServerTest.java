package com.mainsteam.stm.web;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.eclipse.jetty.servlet.FilterHolder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/server-commons-web-beans.xml"})
public class JettyServerTest {

	@Autowired
	@Resource(name="jettyServer")
	private JettyServer server;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty(JettyServer.WEBAPP_DIR, "./webRoot");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			server.stopServer();
		}
	}

	@Test
	public void test() {
		try {
			URL url = new URL("http://localhost:" + server.getPort()
					+ "/hello.jsp");
			URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();
			byte[] content = new byte[connection.getContentLength()];
			int offset = 0;
			do {
				offset += in.read(content);
			} while (offset < content.length);
			in.close();
			System.out.println();
			String helloContent = new String(content,"GBK");
			System.out.println(helloContent);
			assertTrue(helloContent.contains("hello jetty!"));
			
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		final List<Boolean> result = new ArrayList<>();
		server.addFilter(new FilterHolder(new Filter() {
			@Override
			public void init(FilterConfig arg0) throws ServletException {
				System.out.println("filter is inited.");
			}
			
			@Override
			public void doFilter(ServletRequest arg0, ServletResponse arg1,
					FilterChain arg2) throws IOException, ServletException {
				result.add(Boolean.TRUE);
			}
			
			@Override
			public void destroy() {
				System.out.println("destory the filter.");
			}
		}), "/*",EnumSet.of(DispatcherType.REQUEST));
		
		try {
			URL url = new URL("http://localhost:" + server.getPort()
					+ "/hello.jsp");
			URLConnection connection = url.openConnection();
			InputStream in = connection.getInputStream();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(result.size()>0);
		assertTrue(result.get(0).booleanValue());
	}
}
