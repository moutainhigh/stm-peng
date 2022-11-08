package com.mainsteam.stm.network.snmp;

import java.io.IOException;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
/**
 * 
 * <li>文件名称: SnmpTerminal.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   liupeng
 */
public class SnmpTerminal {
	/**[SnmpConstants.version1,SnmpConstants.version2c,SnmpConstants.version3]*/
	private int version = SnmpConstants.version1;
	private static final int DEFAULT_SNMP_PORT = 161;
	private Snmp snmp = null;
	private Address targetAddress = null;
	/**
	 * init snmp use default snmp port
	 * @param ip
	 * @throws IOException
	 */
	public SnmpTerminal(String ip) throws IOException {
		this(ip, DEFAULT_SNMP_PORT);
	}
	/**
	 * 
	 * @param ip
	 * @param port
	 * @throws IOException
	 */
	public SnmpTerminal(String ip, int port) throws IOException {
		targetAddress = GenericAddress.parse("udp:" + ip + "/" + port);
		TransportMapping<?> transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		transport.listen();
	}
	
	/**
	 * 
	 * @param community
	 * @param oid
	 * @return
	 * @throws Exception
	 */
	public String sendPDU(String community,int[] oid) throws Exception {
		StringBuffer buffer = new StringBuffer();
		//分区
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(targetAddress);
		// 通信不成功时的重试次数
		target.setRetries(3);
		// 超时时间
		target.setTimeout(3000);
		target.setVersion(version);
		// 创建 PDU
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		// MIB的访问方式
		pdu.setType(PDU.GET);
		// 向Agent发送PDU，并接收Response
		ResponseEvent respEvnt = snmp.send(pdu, target);
		// 解析Response
		if (respEvnt != null && respEvnt.getResponse() != null) {
			@SuppressWarnings("unchecked")
			Vector<VariableBinding> recVBs = (Vector<VariableBinding>) respEvnt.getResponse().getVariableBindings();
			for (int i = 0; i < recVBs.size(); i++) {
				VariableBinding recVB = recVBs.elementAt(i);
				buffer.append(recVB.getVariable());
			}
		}
		return buffer.toString();
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) throws Exception {
		if(version != SnmpConstants.version1 && version != SnmpConstants.version2c && version != SnmpConstants.version3)
			throw new Exception("Unsupported snmp protocol version");
		this.version = version;
	}
}
