/**
 * 
 */
package com.mainsteam.stm.plugin.snmptest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * @author fevergreen
 * 
 */
public class SnmpAgent {

	private static final Log logger = LogFactory.getLog(SnmpAgent.class);

	private static SnmpAgent _self = new SnmpAgent();

	private TransportMapping<UdpAddress> transportMapping;
	private Snmp snmp = null;

	/**
	 * 
	 */
	private SnmpAgent() {
		try {
			transportMapping = new DefaultUdpTransportMapping();
			transportMapping.listen();
			snmp = new Snmp(transportMapping);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static SnmpAgent getInstance() {
		return _self;
	}

	public Map<String, List<String>> getSnmpResult(SnmpParameter p)
			throws IOException {
		CommunityTarget target = new CommunityTarget();
		Address address = GenericAddress.parse("udp:" + p.getIp() + "/"
				+ p.getPort());
		System.out.println("ip=" + p.getIp() + "address=" + address);
		target.setCommunity(new OctetString(p.getPassword()));
		target.setAddress(address);
		// 通信不成功时的重试次数
		target.setRetries(2);
		// 超时时间
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);

		// set PDU
		PDU pdu = new PDU();
		String[] oids = p.getOids();
		for (int i = 0; i < oids.length; i++) {
			pdu.add(new VariableBinding(new OID(oids[i])));// pcName
		}
		pdu.setType(PDU.GET);

		// 向Agent发送PDU，并返回Response
		ResponseEvent e = snmp.send(pdu, target);
		Vector<? extends VariableBinding> bindings = e.getResponse()
				.getVariableBindings();

		Map<String, List<String>> oidResultValueMaps = new HashMap<String, List<String>>();
		List<String> varValues = new ArrayList<String>();
		String oid = null;
		for (Iterator<? extends VariableBinding> iterator = bindings.iterator(); iterator
				.hasNext();) {
			VariableBinding variableBinding = iterator.next();
			if (oid == null) {
				oid = variableBinding.getOid().toString();
				oidResultValueMaps.put(oid, varValues);
			}
			if (oid.equals(variableBinding.getOid().toString())) {
				varValues.add(getValue(variableBinding.getVariable()));
			} else {
				varValues = new ArrayList<String>();
				oid = variableBinding.getOid().toString();

				varValues.add(getValue(variableBinding.getVariable()));
				oidResultValueMaps.put(oid, varValues);
			}
			System.out.println(variableBinding.getOid() + ":"
					+ getValue(variableBinding.getVariable()));
			if (logger.isDebugEnabled()) {
				logger.debug("getSnmpResult " + variableBinding.getOid() + ":"
						+ getValue(variableBinding.getVariable()));
			}
		}
		System.out.println("map=" + oidResultValueMaps);
		if (logger.isDebugEnabled()) {
			logger.debug("getSnmpResult map=" + oidResultValueMaps);
		}
		return oidResultValueMaps;
	}

	public String getValue(Variable v) {
		int syntax = v.getSyntax();
		String value = null;
		switch (syntax) {
		case SnmpSyntax.SNMP_SYNTAX_INT:
		case SnmpSyntax.SNMP_SYNTAX_UINT32:
			value = String.valueOf(v.toInt());
			break;
		case SnmpSyntax.SNMP_SYNTAX_TIMETICKS:
			value = String.valueOf(v.toLong());
			break;
		default:
			value = v.toString();
			break;
		}
		return value;
	}

	public OID getOid(String s) {
		String[] oids = StringUtils.split(s,'.');
		int[] oidValues = new int[oids.length];
		for (int i = 0; i < oids.length; i++) {
			oidValues[i] = Integer.parseInt(oids[i]);
		}
		return new OID(oidValues);
	}

	public void close() throws IOException {
		snmp.close();
	}

	public static void main(String[] args) throws IOException {
		SnmpParameter p = new SnmpParameter();
		p.setPassword("public");
		p.setOids(new String[] { "1.3.6.1.2.1.1.3.0" });
		SnmpAgent.getInstance().getSnmpResult(p);
		SnmpAgent.getInstance().close();
	}
}
