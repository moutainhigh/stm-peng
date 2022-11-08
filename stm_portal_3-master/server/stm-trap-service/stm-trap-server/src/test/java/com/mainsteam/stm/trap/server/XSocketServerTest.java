package com.mainsteam.stm.trap.server;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/trap-*-beans.xml"})
public class XSocketServerTest {

	private static final Log Logger =LogFactory.getLog(XSocketServerTest.class);
	@Autowired TrapServer server;
	@Test
	public void testStartSMmp() throws Exception{
		
		ArrayList<TrapDataHandler> list=new ArrayList<TrapDataHandler>();
		
		
		list.add(new TrapDataHandler() {
			@Override public void handleData(String remote,byte[] data) {
				Logger.error("testStartSMmp:"+new String(data, Charset.defaultCharset()));
			}
			
			@Override public int getPort() {
				return 162;
			}

		});
		
		server.startServer(162,list);

		list=new ArrayList<TrapDataHandler>();
		list.add(new TrapDataHandler() {
			@Override public void handleData(String remote,byte[] data) {
				Logger.error("testStartSMmp:"+new String(data, Charset.defaultCharset()));
			}
			
			@Override public int getPort() {
				return 514;
			}

		});
		
		server.startServer(514,list);
		Thread.sleep(500000);
	}
}
