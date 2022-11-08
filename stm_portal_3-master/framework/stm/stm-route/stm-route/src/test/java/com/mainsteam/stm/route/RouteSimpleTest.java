package com.mainsteam.stm.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.physical.PhysicalServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/portal-*-beans.xml",
		"classpath*:META-INF/services/public-*-beans.xml" })
public class RouteSimpleTest {

	@Resource
	private PhysicalServer physicalServer;
	
	@Resource
	private LogicServer logicServer;
	
	@Resource
	private LogicClient logicClient;

	private String requestMsg;

	private String responseMsg;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		/**
		 * 创建一个客户端，一个server端。进行通讯，然后发送，接收，响应，接收到的信息相同，则成功。
		 */
		TestServer server = new TestServer();
		new Thread(server).start();
		synchronized (this) {
			try {
				this.wait(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		TestClient client = new TestClient("127.0.0.1", server.getPort());
		requestMsg = "hello test.";
		client.sendMsg(requestMsg);
		requestMsg = "new hello test.";
		client.sendMsg(requestMsg);
		server.close();
	}

	class TestClient {
		private LogicConnection conn;

		public TestClient(String serverIp, int serverPort) {
			try {
				conn = logicClient.createConection(serverIp, serverPort, LogicAppEnum.TRANSFER_TCP);
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}

		public void sendMsg(String msg) {
			System.out.println("to request msg=" + msg);
			try {
				conn.getOutputStream().write(msg.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
			byte[] content = new byte[1024];
			try {
				int length = conn.getInputStream().read(content);
				assertTrue(length > 0);
				String response = new String(content, 0, length);
				System.out.println("response msg=" + response);
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}
	}

	class TestServer implements Runnable {
		private int port = 9999;

		public TestServer() {
			try {
				physicalServer.setConfig(InetAddress.getLocalHost().getHostAddress(), port);
				physicalServer.startServer();
				logicServer.startServer(LogicAppEnum.TRANSFER_TCP);
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}

		/**
		 * @return the port
		 */
		public final int getPort() {
			return port;
		}

		@Override
		public void run() {
			try {
				while (true) {
					System.out.println("ready to read connection.");
					ResponseHandler h = new ResponseHandler();
					h.acceptSocket();
					new Thread(h).start();
				}
			} finally {
				try {
					close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public void close() throws IOException {
			logicServer.stopServer(LogicAppEnum.TRANSFER_TCP);
			physicalServer.stopServer();
		}
	}

	private class ResponseHandler implements Runnable {
		private LogicConnection conn;

		@Override
		public void run() {
			try {
				InputStream in = conn.getInputStream();
				byte[] content = new byte[1024];
				while (true) {
					int length = in.read(content);
					assertTrue(length > 0);
					String msg = new String(content, 0, length);
					assertEquals(requestMsg, msg);
					responseMsg = requestMsg + "'s return.";
					conn.getOutputStream().write(responseMsg.getBytes());
				}
			} catch (IOException e) {
				e.printStackTrace();
				fail();
			}
		}

		private void acceptSocket() {
			conn = logicServer.accept(LogicAppEnum.TRANSFER_TCP);
		}
	}

}
