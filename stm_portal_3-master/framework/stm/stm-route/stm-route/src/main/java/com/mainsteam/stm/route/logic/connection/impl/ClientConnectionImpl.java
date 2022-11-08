/**
 * 
 */
package com.mainsteam.stm.route.logic.connection.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.mainsteam.stm.route.connection.ConnectHeader;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.util.IpUtil;

/**
 * @author ziw
 * 
 */
public class ClientConnectionImpl extends LogicConnectionImpl {

	private static final Log logger = LogFactory
			.getLog(ClientConnectionImpl.class);

	private String targetIp;

	private int targetPort;

	public ClientConnectionImpl(String id,
			INonBlockingPipeline nonBlockingPipeline, LogicAppEnum appEnum)
			throws IOException {
		super(id, nonBlockingPipeline, appEnum);
	}

	/**
	 * @return the targetIp
	 */
	public final String getTargetIp() {
		return targetIp;
	}

	/**
	 * @param targetIp
	 *            the targetIp to set
	 */
	public final void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}

	/**
	 * @return the targetPort
	 */
	public final int getTargetPort() {
		return targetPort;
	}

	/**
	 * @param targetPort
	 *            the targetPort to set
	 */
	public final void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public void connect() throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("connect start");
		}
		ConnectHeader header = new ConnectHeader();
		header.setApp(super.getConnectionApp().getCode());
		header.setOpertion(ConnectHeader.OPERATION_CREATE_CONNECTION);
		header.setVersion((byte)2);
		byte[] ipBytes = IpUtil.makeBytes(getSrcIp(), getSrcPort(),
				getDistIp(), getDistPort());
		header.setDataLength(ipBytes.length);
		byte[] headerBytes = header.toBytes();
		byte[] address = new byte[headerBytes.length + ipBytes.length];
		System.arraycopy(headerBytes, 0, address, 0, headerBytes.length);
		System.arraycopy(ipBytes, 0, address, headerBytes.length,
				ipBytes.length);
		OutputStream out = getOutputStream();
		out.write(address);
		//modify by ziw at 2017年7月27日 上午10:05:54 -- 适应隔离墙机制
//		InputStream in = getInputStream();
//		byte[] returnHeader = new byte[ConnectHeader.getHeadSize()];
//		in.read(returnHeader);
//		header = ConnectHeader.readHeader(returnHeader);
//		if (header.getOpertion() == ConnectHeader.OPERATION_CREATE_CONNECTION_RESPONSE) {
//			byte[] ips = new byte[header.getDataLength()];
//			in.read(ips);
//			Object[] ipAddress = IpUtil.getAddress(ips);
//			String distIp = (String) ipAddress[2];
//			int distPort = (Integer) ipAddress[3];
//			if (logger.isInfoEnabled()) {
//				logger.info("connect logic connection ok.distIp="
//						+ distIp + " distPort=" + distPort);
//			}
//		} else {
//			this.close();
//			throw new IOException("connect logic connection fail.");
//		}
		if (logger.isTraceEnabled()) {
			logger.trace("connect end");
		}
	}

	private static String getIp(byte[] address, int offset) {
		int ipLength = 6;
		int startIndex = 0;
		for (int i = offset + startIndex; i < offset + ipLength; i++) {
			if (address[i] == 0) {
				continue;
			}
			startIndex = i;
			break;
		}
		StringBuilder b = new StringBuilder();
		b.append(address[startIndex] & 0x00FF);
		for (int i = startIndex + 1; i < offset + ipLength; i++) {
			b.append('.').append(address[i] & 0x00FF);
		}
		return b.toString();
	}

	private static int getPort(byte[] address, int offset) {
		return ((address[offset] & 0x00ff) << 8)
				| ((address[offset + 1] & 0x00ff));
	}

	private static int setIpBytes(String ipValue, byte[] address, int offset) {
		int ipLength = 6;
		String[] ipSplit = ipValue.split("\\.");
		int ipOffset = ipLength - ipSplit.length;
		for (int i = offset + ipOffset, splitIndex = 0; i < offset + ipLength; i++, splitIndex++) {
			address[i] = (byte) Integer.valueOf(ipSplit[splitIndex]).intValue();
		}
		return ipLength;
	}

	private static int setPortBytes(int port, byte[] address, int offset) {
		address[offset] = (byte) ((port & 0x00ff00) >> 8);
		address[offset + 1] = (byte) (port & 0x00ff);
		return 2;
	}

	public static void main(String[] args) {

		int p = 172;
		byte b = (byte) p;
		System.out.println(b);
		System.out.println(b & 0x00FF);

		String srcIp = "192.168.7.5";
		int srcPort = 1002;

		String distIp = "172.1.3.2";
		int distPort = 8790;
		byte[] address = new byte[16];

		int offset = 0;
		offset += setIpBytes(distIp, address, offset);
		System.out.println(offset);
		offset += setPortBytes(distPort, address, offset);
		System.out.println(offset);
		offset += setIpBytes(srcIp, address, offset);
		System.out.println(offset);
		setPortBytes(srcPort, address, offset);

		offset = 0;
		System.out.println(getIp(address, offset));
		offset = 6;
		System.out.println(getPort(address, offset));
		offset = 8;
		System.out.println(getIp(address, offset));
		offset = 14;
		System.out.println(getPort(address, offset));
	}
}
