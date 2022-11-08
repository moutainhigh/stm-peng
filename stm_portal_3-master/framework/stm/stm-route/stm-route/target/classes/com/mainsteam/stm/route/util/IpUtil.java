/**
 * 
 */
package com.mainsteam.stm.route.util;

/**
 * @author ziw
 * 
 */
public class IpUtil {
	public static byte[] makeBytes(String srcIp, int srcPort, String distIp,
			int distPort) {
		byte[] srcIpBytes = srcIp.getBytes();
		byte[] distIpBytes = distIp.getBytes();
		byte[] address = new byte[1 + srcIpBytes.length + 2 + 1
				+ distIpBytes.length + 2];
		int offset = 0;
		address[offset++] = (byte) srcIpBytes.length;
		// offset += setIpBytes(srcIp, address, offset);
		System.arraycopy(srcIpBytes, 0, address, offset, srcIpBytes.length);
		offset += srcIpBytes.length;
		offset += setPortBytes(srcPort, address, offset);
		// offset += setIpBytes(distIp, address, offset);
		address[offset++] = (byte) distIpBytes.length;
		System.arraycopy(distIpBytes, 0, address, offset, distIpBytes.length);
		offset += distIpBytes.length;
		setPortBytes(distPort, address, offset);
		return address;
	}

	public static Object[] getAddress(byte[] address) {
		Object[] objs = new Object[4];
		int offset = 0;
		int length = address[offset++];
		objs[0] = new String(address, offset, length);
		offset += length;
		objs[1] = getPort(address, offset);
		offset += 2;
		length = address[offset++];
		objs[2] = new String(address, offset, length);
		offset += length;
		objs[3] = getPort(address, offset);
		return objs;
	}

	//
	// private static String getIp(byte[] address, int offset) {
	// int ipLength = 6;
	// int startIndex = 0;
	// for (int i = offset + startIndex; i < offset + ipLength; i++) {
	// if (address[i] == 0) {
	// continue;
	// }
	// startIndex = i;
	// break;
	// }
	// StringBuilder b = new StringBuilder();
	// b.append(address[startIndex] & 0x00FF);
	// for (int i = startIndex + 1; i < offset + ipLength; i++) {
	// b.append('.').append(address[i] & 0x00FF);
	// }
	// return b.toString();
	// }

	private static int getPort(byte[] address, int offset) {
		return ((address[offset] & 0x00ff) << 8)
				| ((address[offset + 1] & 0x00ff));
	}

	//
	// private static int setIpBytes(String ipValue, byte[] address, int offset)
	// {
	// int ipLength = 6;
	// String[] ipSplit = ipValue.split("\\.");
	// int ipOffset = ipLength - ipSplit.length;
	// for (int i = offset + ipOffset, splitIndex = 0; i < offset + ipLength;
	// i++, splitIndex++) {
	// address[i] = (byte) Integer.valueOf(ipSplit[splitIndex]).intValue();
	// }
	// return ipLength;
	// }

	private static int setPortBytes(int port, byte[] address, int offset) {
		address[offset] = (byte) ((port & 0x00ff00) >> 8);
		address[offset + 1] = (byte) (port & 0x00ff);
		return 2;
	}

	private static byte int3(int x) {
		return (byte) (x >> 24);
	}

	private static byte int2(int x) {
		return (byte) (x >> 16);
	}

	private static byte int1(int x) {
		return (byte) (x >> 8);
	}

	private static byte int0(int x) {
		return (byte) (x);
	}

	static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
	}
	
	public static int makeInt(byte[] c,int offset){
		return makeInt(c[offset], c[offset+1], c[offset+2], c[offset+3]);
	}

	public static byte[] toBytes(int ll) {
		byte[] toWriteCotent = new byte[4];
		toWriteCotent[0] = int3(ll);
		toWriteCotent[1] = int2(ll);
		toWriteCotent[2] = int1(ll);
		toWriteCotent[3] = int0(ll);
		return toWriteCotent;
	}

	public static void main(String[] args) {
		String srcIp = "172.16.7.152";
		int srcPort = 9003;

		String distIp = "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee.f";
		int distPort = 8790;
		byte[] content = makeBytes(srcIp, srcPort, distIp, distPort);
		Object[] read = getAddress(content);
		for (Object object : read) {
			System.out.println(object);
		}
		
		int a = 10000;
		byte[] b = toBytes(a);
		System.out.println(makeInt(b, 0));
	}
}
