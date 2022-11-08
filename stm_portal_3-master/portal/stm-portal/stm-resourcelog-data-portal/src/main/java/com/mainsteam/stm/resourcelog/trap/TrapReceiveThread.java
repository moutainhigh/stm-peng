package com.mainsteam.stm.resourcelog.trap;

/** *
 * 测试snmp4j 中trap的接收方法.这里只测试了v1和v2的trap.
 * trap接收原理:snmp实例在注册了实现CommandResponder的listener之后,可以通过异步调用的方法
 * 将收到内容输出.
 * listen()启动监听线程,该线程中的操作是监听指定端口,在收到trap告警之后将调用
 * listener.processPdu(CommandResponderEvent event)方法,由processPdu来处理trap信息.
 *
 * @modify  Xiaopf
 * 在snmp v2 trap中，如果传入的不是Generic trap，而是Specific trap,则需要针对不同的企业加载不同的mib
 * 才能找到对应的trap type。
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.SimpleVariableTextFormat;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.util.VariableTextFormat;

import com.mainsteam.stm.resourcelog.vo.SnmptrapVo;
import com.mainsteam.stm.transfer.MetricDataTransferSender;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * 接收trap消息
 * <li>文件名称: TrapReceiveThread</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月12日 下午2:46:54
 * @author   xiaolei
 */
//@Service("trapReceiveThread")
public class TrapReceiveThread implements CommandResponder{
	
	private static final Logger logger = Logger.getLogger(TrapReceiveThread.class);

	private static final VariableTextFormat variableTextFormat = new SimpleVariableTextFormat();

	private MultiThreadedMessageDispatcher dispatcher;
	
	private Snmp snmp = null;
	
	private Address listenAddress;
	
	private ThreadPool threadPool;

	private static final Set<String> enterpriseOID = new HashSet<>();
	
	private MetricDataTransferSender metricDataTransferSender;

	private int port;
	private String listen;

//	
//	@Value("${ip}")
//	private String trapIp;
	
	public TrapReceiveThread() {}
	/**
	 * 
	* @Title: getPort
	* @Description: 返回snmptrap的端口号
	* @return  int
	* @throws
	 */
	private int getPort() {
//		int port=Integer.parseInt(configApi.getSystemConfigById(SystemConfigConstantEnum.SYSTEM_CONFIG_LOG_SNMPTRAP.getCfgId()).getContent());
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	public void setListen(String listen) {
		this.listen = listen;
	}
	@SuppressWarnings("rawtypes")
	private void init() throws IOException {
		if(!"on".equalsIgnoreCase(listen)) {
			if (logger.isInfoEnabled()) {
				logger.info("snmptrap is off.please modify the $server/config/properties/trap.properties ");
			}
			return;
		}
		threadPool = ThreadPool.create("Trap", 2);
		dispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
		int port = getPort();
		Properties prop=new Properties();
		prop.load(TrapReceiveThread.class.getClassLoader().getResourceAsStream("properties/stm.properties"));
		String localIp=prop.getProperty("ip",InetAddress.getLocalHost().getHostAddress());
		listenAddress = GenericAddress.parse(System.getProperty("snmp4j.listenAddress", "udp:"+localIp+"/" + port));
		logger.info("trap receiver udp address :" + localIp + "/" + port);
		TransportMapping transport;

		//对TCP与UDP协议进行处理
		if (listenAddress instanceof UdpAddress) {
			transport = new DefaultUdpTransportMapping((UdpAddress)listenAddress);
		} else {
			transport = new DefaultTcpTransportMapping((TcpAddress)listenAddress);
		}

		snmp = new Snmp(dispatcher, transport);
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
		snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());

		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		snmp.listen();

		enterpriseOID.add("1.3.6.1.4.1.2011.2.290.1.1.1");
		enterpriseOID.add("1.3.6.1.4.1.789.1123.1.500.0.2");
		enterpriseOID.add("1.3.6.1.4.1.2011.2.288.1.1.0");

	}
	
	public void start() {
		logger.info("----> Trap Receiver run ... <----");
		try {
			init();
			snmp.addCommandResponder(this);
			//loadMib();
			logger.info("Trap thread has been started, waiting for income message...");
		} catch (Exception e) {
			logger.error("Trap thread listening occurs error," + e.getMessage(), e);
		}
	}
	/**
	 * 用于处理传入的请求、PDU等信息，
	 * 当接收到trap时，会自动进入这个方法
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void processPdu(CommandResponderEvent respEvent) {
		//创建一个trapVo对象
		SnmptrapVo trapVo = new SnmpTrapTransferVO();
		// 取设备的IP地址
		String peerAddress = respEvent.getPeerAddress().toString();
		String ip[] = peerAddress.split("/");
		trapVo.setAddressIP(ip[0]);
		try {
			logger.info("Community name----------------" + new String(respEvent.getSecurityName(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("processPdu", e);
		}
		if (respEvent == null || respEvent.getPDU() == null) {
			logger.info("[Warn] ResponderEvent or PDU is null");
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String receivedTime = dateFormat.format(new Date(System
				.currentTimeMillis()));
		
		StringBuilder message = new StringBuilder();
		
		PDU command;
		Vector<VariableBinding> recVBs = null;
		if (respEvent.getPDU().getType() == PDU.V1TRAP) { // SNMP V1
			command = respEvent.getPDU();
			if (command != null) {
				trapVo.setReceiveTime(receivedTime);
				int genericTrapID = ((PDUv1) command).getGenericTrap();
				trapVo.setGenericType(genericTrapID);
				if(genericTrapID == PDUv1.ENTERPRISE_SPECIFIC){
					trapVo.setEnterprise(((PDUv1) command).getEnterprise().toString());
				}
				if (genericTrapID == -1) {
					logger.warn("found generic trap id is -1, source ip is " + trapVo.getAddressIP());
					return;
				}
				recVBs = (Vector<VariableBinding>) respEvent.getPDU().getVariableBindings();
				logger.info("recVBs------>" + recVBs);
			}
		} else { //snmp v2c
			command = respEvent.getPDU();
			if (command != null) {
				trapVo.setReceiveTime(receivedTime);
				recVBs = (Vector<VariableBinding>) respEvent.getPDU().getVariableBindings();
				logger.info("recVBs------>" + recVBs);
				Iterator<VariableBinding> iterator = recVBs.iterator();
				while (iterator.hasNext()) {
					VariableBinding varBin = iterator.next();
					if (SnmpConstants.snmpTrapOID.toString().equals(varBin.getOid().toString())) {//说明是trap类型的OID
						int commonType = SnmpConstants.getGenericTrapID(new OID(varBin.getVariable().toString()));
						trapVo.setGenericType(commonType);
						if (commonType == -1) {
							trapVo.setEnterprise(varBin.toValueString());
							trapVo.setGenericType(PDUv1.ENTERPRISE_SPECIFIC);
						}
						iterator.remove();
					}
				}
			}
		}
		dealVariables(recVBs, trapVo);

	}

	private void dealVariables(Vector<VariableBinding> recVBs, SnmptrapVo trapVo) {

		StringBuilder message = new StringBuilder();
		boolean isDealed = false;
		if (trapVo.getGenericType() == PDUv1.ENTERPRISE_SPECIFIC) {
			if(StringUtils.equals("1.3.6.1.4.1.2011.2.288.1.1.0", trapVo.getEnterprise())) {
				for (VariableBinding varBin : recVBs) {
					String s = format(varBin.getOid(), varBin.getVariable(), false);
					String key = varBin.getOid().format();
					switch (key) {
						case "1.3.6.1.4.1.2011.2.288.1.1.62": //操作类型 新增，更新，删除
							trapVo.setOperationType(s);
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.4":
							message.append("; 告警级别:");
							switch (s) {
								case "3":
									message.append("紧急");
									break;
								case "2":
									message.append("重要");
									break;
								case "1":
									message.append("次要");
									break;
								case "0":
									message.append("提示");
							}
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.6":
						case "1.3.6.1.4.1.2011.2.288.1.1.5":
							if (key.equals("1.3.6.1.4.1.2011.2.288.1.1.6"))
								message.append("; 最后发生时间:");
							else if (key.equals("1.3.6.1.4.1.2011.2.288.1.1.5"))
								message.append("; 首次发生时间:");
							if (StringUtils.isNotBlank(s) && !StringUtils.equals(s, "null")) {
								try{
									long createTime = Long.parseLong(s);
									Date date = new Date(createTime);
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									message.append(sdf.format(date));
								}catch (Exception e){
									message.append("-");
								}
							}else{
								message.append("-");
							}
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.2":
							message.append("; 告警ID:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.1":
							message.append(";告警流水号:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.3":
							message.append("; 告警名称:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.7":
							message.append(";告警级别变更次数:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.8":
							message.append(";告警所属对象的编码:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.63":
							message.append(";原始告警ID:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.60":
							if(StringUtils.isNotBlank(s)) {
								message.append(";告警可能原因:");
								message.append(s);
							}
							break;
						case "1.3.6.1.4.1.2011.2.288.1.1.61":
							if(StringUtils.isNotBlank(s)) {
								message.append(";告警定位信息:");
								message.append(s);
							}
							break;
					}
					message.deleteCharAt(0);
				}
				isDealed = true;
			}else if(StringUtils.equals("1.3.6.1.4.1.789.1123.1.500.0.2", trapVo.getEnterprise())){
				isDealed = true;
				message.append("浪潮AS500H储存电源故障");
			}else if(StringUtils.equals("1.3.6.1.4.1.2011.2.290.1.1.1", trapVo.getEnterprise())) {
				for (VariableBinding varBin : recVBs) {
					String s = format(varBin.getOid(), varBin.getVariable(), false);
					String key = varBin.getOid().format();
					switch (key) {
						case "1.3.6.1.4.1.2011.2.290.1.1.1.8":
							trapVo.setOperationType(s);
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.9":
							message.append("; 告警级别:");
							switch (s) {
								case "1":
									message.append("紧急");
									break;
								case "2":
									message.append("重要");
									break;
								case "3":
									message.append("次要");
									break;
								case "4":
									message.append("提示");
							}
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.10":
						case "1.3.6.1.4.1.2011.2.290.1.1.1.11":
						case "1.3.6.1.4.1.2011.2.290.1.1.1.21":
							if (key.equals("1.3.6.1.4.1.2011.2.290.1.1.1.10"))
								message.append("; 产生时间:");
							else if (key.equals("1.3.6.1.4.1.2011.2.290.1.1.1.11"))
								message.append("; 清除时间:");
							else
								message.append("; 更新时间:");
							if (StringUtils.isNotBlank(s) && !StringUtils.equals(s, "null")) {
								try{
									long createTime = Long.parseLong(s);
									Date date = new Date(createTime);
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									message.append(sdf.format(date));
								}catch (Exception e){
									message.append("-");
								}
							}else{
								message.append("-");
							}
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.2":
							message.append("; 告警ID:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.3":
							message.append("; 告警名称:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.5":
							message.append("; 告警对象:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.6":
							message.append("; 对象类型:");
							String temp = s;
							switch (s) {
								case "vms":
									temp = "虚拟机";
									break;
								case "clusters":
									temp = "集群";
									break;
								case "DATASTORE":
									temp = "数据存储";
									break;
								case "hosts":
									temp = "主机";
									break;
								case "sites":
									temp = "站点";
									break;
								case "vrms":
									temp = "VRM节点";
									break;
							}
							message.append(temp);
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.12":
							message.append("; 清除类型:");
							if (StringUtils.isNotBlank(s)) {
								switch (s) {
									case "-1":
										message.append("未清除");
										break;
									case "0":
										message.append("正常清除");
										break;
									case "1":
										message.append("手动清除");
										break;
									case "2":
										message.append("相关性清除");
										break;
								}
							} else {
								message.append("-");
							}
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.13":
							message.append("; 是否自动清除:");
							switch (s) {
								case "0":
									message.append("是");
									break;
								case "1":
									message.append("否");
							}
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.7":
							message.append("; 流水号:");
							message.append(s);
							break;
						case "1.3.6.1.4.1.2011.2.290.1.1.1.15":
							message.append("; 附件信息:");
							if(StringUtils.isNotBlank(s))
								message.append(s);
							else
								message.append("-");
							break;

					}
				}
			}
		}

		if(!isDealed) {
			for (VariableBinding varBin: recVBs) {
				String value;
				Variable variable = varBin.getVariable();
				if(variable instanceof OctetString) {
					try {
						value = new String(((OctetString) variable).getValue(), "GBK");
					} catch (UnsupportedEncodingException e) {
						logger.warn(e);
						value = variableTextFormat.format(varBin.getOid(), variable, false);
					}
				}else {
					value = variableTextFormat.format(varBin.getOid(), variable, false);
				}
				if (StringUtils.isNotBlank(value)) {
					message.append(value + ",");
				}
			}
			message.deleteCharAt(message.length() - 1);
		}
		//设置trap消息
		trapVo.setMessage(message.toString());
		sendData(trapVo);
	}


	public static String format(OID oid, Variable variable, boolean withOID) {
		if(variable instanceof OctetString) {
			if(((OctetString) variable).isPrintable()) //如果是中文，则不会格式化数据
				return variableTextFormat.format(oid, variable, false);
			else {
				try {
					return new String(((OctetString) variable).getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					return new String(((OctetString) variable).getValue());
				} catch (Throwable throwable) {
					return variableTextFormat.format(oid, variable, false);
				}
			}
		}else {
			return variableTextFormat.format(oid, variable, false);
		}
	}
	
	private void sendData(SnmptrapVo trapVo) {
		TransferData data = new TransferData(TransferDataType.SnmpTrap, trapVo);
		metricDataTransferSender.sendData(data);
	}
	public void setMetricDataTransferSender(MetricDataTransferSender metricDataTransferSender) {
		this.metricDataTransferSender = metricDataTransferSender;
	}
	
//	public static void main(String[] args) {
//		TrapReceiveThread receiveThread = new TrapReceiveThread();
//		receiveThread.run();
//	}

}
