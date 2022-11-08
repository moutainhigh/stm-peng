package com.mainsteam.stm.resourcelog.trap;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * 向管理进程发送trap消息
 * <li>文件名称: SnmpUtilSendTrap</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月9日 下午7:29:11
 * @author   Administrator
 */
public class SnmpUtilSendTrap {
	
	public static final int DEFAULT_VERSION = SnmpConstants.version2c;
	
    public static final long DEFAULT_TIMEOUT = 3 * 1000L;
    
    public static final int DEFAULT_RETRY = 3;

	private Snmp snmp = null;
	
	private Address targetAddress = null;
	
	public static PDU createPdu(int pduType) {
		
		PDU request;
		if (pduType == PDU.V1TRAP) {
			request = new PDUv1();
		} else {
			request = new PDU();
		}
		request.setType(pduType);
		return request;
	}
	@SuppressWarnings("rawtypes")
	public void initComm() throws IOException {
		System.out.println("----> 初始 Trap 的IP和端口 <----");
		//设置进程的ip和端口
//		targetAddress = GenericAddress.parse("udp:172.16.7.157/162");
//		targetAddress = GenericAddress.parse("udp:172.16.10.12/162");
		targetAddress = GenericAddress.parse("udp:172.16.7.180/162");
		TransportMapping transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		snmp.listen();
	}
	
	public void sendV3PDU() throws IOException {
		UserTarget userTarget = new UserTarget();
		userTarget.setAddress(targetAddress);
		//通信不成功时的重试次数
		userTarget.setRetries(SnmpUtilSendTrap.DEFAULT_RETRY);
		//超时时间
		userTarget.setTimeout(SnmpUtilSendTrap.DEFAULT_TIMEOUT);
		userTarget.setVersion(SnmpConstants.version3);
		ScopedPDU sPdu = new ScopedPDU();
		sPdu.setType(PDU.TRAP);
		//1.添加 uptime 
		TimeTicks sysUpTime = new TimeTicks((long)System.currentTimeMillis()/1000);
		sPdu.add(new VariableBinding(SnmpConstants.sysUpTime,sysUpTime));
		
		//2.添加snmp trap OID
		sPdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.coldStart));
		
		sPdu.add(new VariableBinding(new OID(".1.3.6.1.2.3377.10.1.1.1.1"),new OctetString("SnmpTrapV3")));
		sPdu.add(new VariableBinding(new OID(".1.3.6.1.2.3377.10.1.1.1.2"), new OctetString("发送trapV3消息测试")));
		
		USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
		SecurityModels.getInstance().addSecurityModel(usm);
		
		snmp.send(sPdu, userTarget);
		System.out.println("----> Trap V3 Send END <----");
	}
	public void sendV1PDU() throws IOException {
		CommunityTarget target = new CommunityTarget();
		target.setAddress(targetAddress);
		//通信不成功时的重试次数
		target.setRetries(SnmpUtilSendTrap.DEFAULT_RETRY);
		//超时时间
		target.setTimeout(SnmpUtilSendTrap.DEFAULT_TIMEOUT);
		target.setVersion(SnmpConstants.version1);
		
		//创建PDUV1
		PDUv1 pdu = (PDUv1) createPdu(PDU.V1TRAP);
		//1.添加 uptime 
		TimeTicks sysUpTime = new TimeTicks((long)System.currentTimeMillis()/1000);
		pdu.setGenericTrap(3);
		pdu.add(new VariableBinding(SnmpConstants.sysUpTime,sysUpTime));
		
		//2.添加snmp trap OID
		pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, SnmpConstants.linkDown));
		
		pdu.add(new VariableBinding(new OID(".1.3.6.1.2.3377.10.1.1.1.1"),new OctetString("SnmpTrapV1")));
		pdu.add(new VariableBinding(new OID(".1.3.6.1.2.3377.10.1.1.1.2"), new OctetString("发送trapV1消息测试")));
		snmp.send(pdu, target);
		System.out.println("----> Trap V1 Send END <----");
	}
	public void sendPDU() throws IOException {
		//设置taget
		CommunityTarget target = new CommunityTarget();
		target.setAddress(targetAddress);
		
		//通信不成功时的重试次数
		target.setRetries(SnmpUtilSendTrap.DEFAULT_RETRY);
		//超时时间
		target.setTimeout(SnmpUtilSendTrap.DEFAULT_TIMEOUT);
		//snmp版本
		target.setVersion(SnmpUtilSendTrap.DEFAULT_VERSION);
		//创建PDU
		PDU pdu = createPdu(PDU.TRAP);
		//1.添加 uptime 
		TimeTicks sysUpTime = new TimeTicks((long)System.currentTimeMillis()/1000);
		pdu.add(new VariableBinding(SnmpConstants.sysUpTime,sysUpTime));
		
		//2.添加snmp trap OID
		pdu.add(new VariableBinding(SnmpConstants.authenticationFailure, SnmpConstants.authenticationFailure));
		
		//3.添加custom oid
		pdu.add(new VariableBinding(new OID(".1.3.6.1.2.3377.10.1.1.1.1"),new OctetString("SnmpTrap")));
		pdu.add(new VariableBinding(new OID(".1.3.6.1.2.3377.10.1.1.1.2"), new OctetString("JavaEE")));
		
		snmp.send(pdu, target);
		System.out.println("----> Trap Send END <----");
	}
	
	public static void main(String[] args) {
		SnmpUtilSendTrap snmpUtilSendTrap = new SnmpUtilSendTrap();
		try {
			snmpUtilSendTrap.initComm();
			snmpUtilSendTrap.sendPDU();
//			snmpUtilSendTrap.sendV3PDU();
//			snmpUtilSendTrap.sendV1PDU();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
