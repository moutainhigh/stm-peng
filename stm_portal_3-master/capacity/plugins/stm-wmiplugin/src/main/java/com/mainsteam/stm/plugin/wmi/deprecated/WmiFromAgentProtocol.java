package com.mainsteam.stm.plugin.wmi.deprecated;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.util.PropertiesFileUtil;

/**
 * 从远处的wmiagent查询wmi数据
 */
public class WmiFromAgentProtocol {

	private static final int DEFAULT_WMI_AGENT_PORT = 12345;
	private static final int DEFAULT_WMI_AGENT_TIMEOUT = 20000;
	private static final String DEFAULT_WMI_AGENT_IP = "127.0.0.1";
	private static final Log logger = LogFactory
			.getLog(WmiFromAgentProtocol.class);
	/**
	 * 用于缓存WmiFromAgentProtocol对象，通一个IP和PORT系统唯一
	 */
	private static final Map<WmiAgent, WmiFromAgentProtocol> instances = new HashMap<WmiAgent, WmiFromAgentProtocol>();
	// 保证instances同步的读写锁
	private static ReentrantReadWriteLock instanceLock = new ReentrantReadWriteLock(false);

	/**
	 * WmiAgent服务器的IP地址
	 */
	private String ipaddress;

	/**
	 * wmiagent服务器的端口
	 */
	private int port;

	/**
	 * 连接wmiagent查询等待时间
	 */
	private long timeout;

	/**
	 * 当数据获取到后，采集线程会将返回的数据放入该缓存，查询线程从该缓存获取数据
	 */
	private java.util.Map<String, ResponseMessage> queryEntry = new HashMap<String, ResponseMessage>();

	/**
	 * 每个线程获取到数据的Condition集合，当每次查询时，查询接口会在该缓存中记录该condition
	 */
	private java.util.Map<String, Condition> entyCondition = new HashMap<String, Condition>();

	/**
	 * 用于同步queryEntry，entyCondition缓存的锁
	 */
	private ReentrantLock lock = new ReentrantLock(false);

	/**
	 * 通信者
	 */
	private TransportContactor contactor;

	/**
	 * WMI代理常量IP
	 */
	private static final String WMI_AGENT_IP = "wmiagent_ipaddress";

	/**
	 * WMI代理常量Port
	 */
	private static final String WMI_AGENT_PORT = "wmiagent_port";
	/**
	 * WMI代理常量查询等待时间
	 */
	private static final String WMI_AGENT_WAITTIME = "wmitimeout";

	/**
	 * WMI属性文件地址
	 */
	private static final String WMI_AGENT_FILE = "WMIAgent.properties";

	/**
	 * WMI连接测试超时时间
	 */
	private static final long WMI_CONNECTION_TIMEOUT = 5000L;

	/**
	 * @param ipaddress
	 *            - wmiagent的ip地址
	 * @param port
	 *            - wmiagent的登录端口
	 * @roseuid 53508C1E0277
	 */
	private WmiFromAgentProtocol(String ipaddress, int port, long timeout) {
		this.ipaddress = ipaddress;
		this.port = port;
		this.timeout = timeout;
	}

	/**
	 * 获取系统默认配置的WMI AGENT
	 * 
	 * @return
	 * @throws IOException
	 */
	public static WmiFromAgentProtocol getDefaultInstance() {

		Properties properties = PropertiesFileUtil
				.getProperties(WMI_AGENT_FILE);
		if (null == properties) {
			InputStream in = ClassLoader
					.getSystemResourceAsStream(WMI_AGENT_FILE);
			properties = new Properties();
			try {
				properties.load(in);
			} catch (IOException e1) {
				logger.error(e1.getMessage(), e1);
			}
		}
		String ipaddress = DEFAULT_WMI_AGENT_IP;

		String port_str = String.valueOf(DEFAULT_WMI_AGENT_PORT);
		String timeout_str = String.valueOf(DEFAULT_WMI_AGENT_TIMEOUT);
		if (null != properties) {
			ipaddress = properties.getProperty(WMI_AGENT_IP);
			port_str = properties.getProperty(WMI_AGENT_PORT);
			timeout_str = properties.getProperty(WMI_AGENT_WAITTIME);
		}

		int port = DEFAULT_WMI_AGENT_PORT;
		try {
			port = Integer.parseInt(port_str);
		} catch (Exception e) {
			;
		}
		if (port <= 0) {
			port = DEFAULT_WMI_AGENT_PORT;
		}

		long timeout_temp = DEFAULT_WMI_AGENT_TIMEOUT;
		try {
			timeout_temp = Long.parseLong(timeout_str);
		} catch (NumberFormatException e) {
			;
		}
		if (timeout_temp <= 0) {
			timeout_temp = DEFAULT_WMI_AGENT_TIMEOUT;
		}
		logger.info("WMI Agent:" + ipaddress + ",PORT:" + port);
		return getInstance(ipaddress, port, timeout_temp);
	}

	/**
	 * 连接成功后返回，如果连接失败则抛出异常
	 * 
	 * @param ipaddress
	 *            WMI AGENT服务器IP地址
	 * @param port
	 *            WMI AGENT服务器登录端口
	 * @return
	 */
	public static WmiFromAgentProtocol getInstance(String ipaddress, int port,
			long timeout) {
		// 查看缓存中是否有该连接
		if (ipaddress == null || port < 1) {
			throw new RuntimeException("Wmi Agent login information error");
		}
		WmiAgent agent = new WmiAgent(ipaddress, port);
		WmiFromAgentProtocol wmi = null;
		instanceLock.writeLock().lock();
		try {
			wmi = instances.get(agent);
			if (wmi != null) {
				if (wmi.testConnection()) {
					return wmi;
				} else {// 重新连接
					try {
						wmi.disConnection();
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						if (wmi.connect(WMI_CONNECTION_TIMEOUT)) {
							return wmi;
						} else {
							instances.remove(agent);
							wmi.disConnection();
							throw new RuntimeException(
									"Unable to connect Wmi Agent");
						}
					} catch (Exception e) {
						wmi.disConnection();
						throw new RuntimeException(
								"Unable to connect Wmi Agent", e);
					}

				}
			}

			final WmiFromAgentProtocol instance = new WmiFromAgentProtocol(
					ipaddress, port, timeout);
			// 使用匿名内部类，简化代码，因为TransportHandle只需要使用一次
			instance.contactor = new TransportContactor(instance.ipaddress,
					instance.port, new TransportHandle() {
						public void operation(String message) {
							if (message != null) {
								// 首先转换成JSON对象
								ResponseMessage response = JSON.parseObject(
										message, ResponseMessage.class);
//								 System.out.println(message);
								if (response != null) {
									// 这个判断是为了确保当前返回值就是当前请求的结果，因为有可能因为与wmi代理端超时导致重复请求的情况发生
									if (response.getWmi_query_req() != null
											&& instance.entyCondition
													.containsKey(response
															.getWmi_query_req()
															.getWmi_query_uuid())) {
										instance.lock.lock();
										try {
											// 放入结果值
											instance.queryEntry.put(response
													.getWmi_query_req()
													.getWmi_query_uuid(),
													response);
											// 唤醒等待线程
											Condition condition = instance.entyCondition
													.get(response
															.getWmi_query_req()
															.getWmi_query_uuid());
											condition.signalAll();
										} finally {
											instance.lock.unlock();
										}
									}
								}
							}
						}
					});

			try {
				if (instance.connect(WMI_CONNECTION_TIMEOUT)) {
					instances.put(agent, instance);
					return instance;
				} else {
					throw new RuntimeException("Unable to connect Wmi Agent");
				}
			} catch (Exception e) {
				throw new RuntimeException("Unable to connect Wmi Agent", e);
			}
		} finally {
			instanceLock.writeLock().unlock();
		}
	}

	/**
	 * 连接服务器
	 * 
	 * @param timeout
	 *            连接超时时间（毫秒）
	 * @return true连接成功，false连接事变
	 * @throws Exception
	 */
	private boolean connect(long timeout) throws Exception {
		return this.contactor.connect(timeout);
	}

	/**
	 * 测试连接是否是通的
	 * 
	 * @return
	 */
	public boolean testConnection() {
		RequestMessage message = new RequestMessage();
		message.setWmi_domain_user("0.0.0.0");
		message.setWmi_namespace("0.0.0.0");
		message.setWmi_password("0.0.0.0");
		message.setWmi_query_cmd("0.0.0.0");
		ResponseMessage response = this.query(message, WMI_CONNECTION_TIMEOUT);
		if (response != null) {
			return true;
		}
		return false;
	}

	/**
	 * 断开连接
	 */
	private void disConnection() {
		this.contactor.disConnection();
	}

	/**
	 * wmi查询 对于一个WmiFromAgentProtocol实例来说，每个时刻仅允许一个查询线程工作，其余须等待查询结果返回后被唤醒
	 * 
	 * @param message
	 *            - 查询信息数据
	 * @param timout
	 *            - 超时时间(毫秒)
	 * @return com.mainsteam.stm.plugin.wmi.ResponseMessage
	 */
	public ResponseMessage query(RequestMessage message, long timeout) {
		ResponseMessage result = null;
		if (message != null) {
			// 生成uuid，唯一标识该次请求。uuid的作用主要是为了防止，当一次与wmi代理服务器的连接超时（代理服务器与目标服务器没有超时），
			// 再次发起相同请求时获取（这次没有超时）到的返回值与上一次返回值重复
			// 注：即使连接超时也能获取到返回值，因为是通过一个轮询线程取socketChannel的数据，只有手动断开连接才能使轮询线程结束。
			// 简而言之，使用uuid就是为了保证一次请求的返回结果与这次请求匹配。
			String uuid = UUID.randomUUID().toString();
			message.setWmi_query_uuid(uuid);
			try {
				lock.lock();
				Condition condition = lock.newCondition();// 数据返回的condition
				this.entyCondition.put(uuid, condition);
				String sendMessage = JSON.toJSONString(message);
				this.contactor.sendMessage(sendMessage);
				// 线程进入等待时间，此时(应该说获取一个wmiFromAgentProtocol实例时就已经启动)有一个轮询线程（TransportContactor实例）不断地从SocketChannel中读取采集返回值，
				// 然后组装成ResponseMessage对象返回（通过TransportHandle.operation()方法。注：这个类是由匿名内部类实现的，因为它仅需要使用一次），
				// 直到我们调用wmiFromAgentPrtocol实例的disConnetion方法，轮询才会结束。
				condition.await(timeout, TimeUnit.MILLISECONDS);
				// 在等待时间内，轮询线程已经将采集结果取回
				result = this.queryEntry.get(uuid);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				lock.unlock();
				// 清除缓存
				// 清除Condition，在轮询线程调用TransportHandle的operation方法之后才会执行，因为在等待线程被唤醒后才会执行这里
				this.entyCondition.remove(uuid);
				// 清除结果
				this.queryEntry.remove(uuid);
			}
		}
		return result;
	}

	/**
	 * 已字符串形式返回查询结果，行使用\n分割，列使用\t分割，第一行为title
	 * 
	 * @param message
	 *            查询参数
	 * @param timout
	 *            超时时间（毫秒）
	 * @return 返回的表格
	 */
	public ArrayList<ArrayList<ResponseCell>> queryFormat(RequestMessage message) {
		ResponseMessage response = query(message, this.timeout);
		if (response != null) {
			if (response.getWmi_query_err() != 0) {
				throw new RuntimeException("Query fails!error code:"
						+ response.getWmi_query_err() + "\terror msg:" + response.getWmi_query_msg());
			} else {
				ArrayList<ArrayList<ResponseCell>> wmi_query_data = response
						.getWmi_query_data();
				return wmi_query_data;
			}
		} else {
			throw new RuntimeException("Query timeout!");
		}
	}

	public static void main(String[] args) {
		WmiFromAgentProtocol wmi = WmiFromAgentProtocol.getDefaultInstance();
		// .getInstance(DEFAULT_WMI_AGENT_IP, 12345, 10000);

		RequestMessage message = new RequestMessage();
//		message.setWmi_domain_user("192.168.1.36\\administrator");
//		message.setWmi_namespace("\\\\192.168.1.36\\root\\cimv2");
		message.setWmi_domain_user("administrator");
		message.setWmi_namespace("192.168.1.36");
		message.setWmi_password("root3306");
		String cmd = "root\\cimv2::select Name from Win32_ComputerSystem";

		message.setWmi_query_cmd(cmd);

		ArrayList<ArrayList<ResponseCell>> kk = wmi.queryFormat(message);
		for (ArrayList<ResponseCell> list : kk) {
			for (ResponseCell cell : list) {
				for (String str : cell.getWmi_res_value()) {
					System.out.println(str);
				}
			}
		}
		//
		// //wmi.disConnection1();
		//
		// System.out.print(kk);
	}
}

class WmiAgent {

	/**
	 * WMI的IP地址
	 */
	private String ipaddress;

	/**
	 * WMI的登录端口
	 */
	private int port;

	public WmiAgent(String ipaddress, int port) {
		this.ipaddress = ipaddress;
		this.port = port;
	}

	public int hashCode() {

		if (this.ipaddress == null || "".equals(this.ipaddress)) {
			return 0;
		} else {
			byte[] bb = this.ipaddress.getBytes();
			int code = 0;
			for (byte b : bb) {
				code = code + b;
			}
			return code;
		}
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else {
			if (obj instanceof WmiAgent) {
				WmiAgent agent = (WmiAgent) obj;
				if (this.port == agent.port
						&& this.ipaddress.equals(agent.ipaddress)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

}
