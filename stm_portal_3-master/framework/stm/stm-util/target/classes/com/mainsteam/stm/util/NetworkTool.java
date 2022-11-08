package com.mainsteam.stm.util;

import java.nio.ByteBuffer;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

final public class NetworkTool {

	private static final String _01_00_5E2 = "01:00:5e";
	private static final String _01_00_5E = "01 00 5e";
	public final static byte[] asBytes(final int addr) {

		byte[] ipByts = new byte[4];
		ipByts[3] = (byte) (addr & 0xFF);
		ipByts[2] = (byte) ((addr >> 8) & 0xFF);
		ipByts[1] = (byte) ((addr >> 16) & 0xFF);
		ipByts[0] = (byte) ((addr >> 24) & 0xFF);
		return ipByts;
	}

	public final static byte[] asBytes(final String addr) {
		return asBytes(parseNumericAddress(addr));
	}

	public final static String asString(byte[] ipaddr) {

		if (ipaddr == null || ipaddr.length != 4)
			return null;

		StringBuffer str = new StringBuffer();
		str.append((int) (ipaddr[0] & 0xFF));
		str.append(STR_DOT);
		str.append((int) (ipaddr[1] & 0xFF));
		str.append(STR_DOT);
		str.append((int) (ipaddr[2] & 0xFF));
		str.append(STR_DOT);
		str.append((int) (ipaddr[3] & 0xFF));
		return str.toString();
	}

	public static boolean isVrrpMac(String mac) {
		if (mac == null) {
			return false;
		}
		boolean b = mac.toLowerCase().startsWith("00 00 5e 00 01");
		boolean c = mac.toLowerCase().startsWith("00:00:5e:00:01");
		return b || c;
	}

	public static boolean isLoopback(String ip) {
		if (ip == null || ip.length() == 0) {
			return false;
		}
		return (ip.startsWith("127"));
	}

	public static boolean isHsrpMac(String mac) {
		if (mac == null) {
			return false;
		}
		boolean b = mac.startsWith("00 00 0c 07 ac");
		boolean c = mac.startsWith("00:00:0c:07:ac");
		boolean d = mac.startsWith("00:00:0c:9f:f");
		boolean e = mac.startsWith("00 00 0c 9f f");
		return b || c || d|| e;
	}

	public static boolean isVirutalMac(String mac) {
		if (mac == null) {
			return false;
		}
		if (isHsrpMac(mac)) {
			return true;
		} else if (isVrrpMac(mac)) {
			return true;
			// } else if (isVmwareMac(mac)) {
			// return true;
		} else if (isHeartbeatMac(mac)) {
			return true;
		} else if (isMultiCastMac(mac)) {
			return true;
		} else if (isBroadCastMAC(mac)) {
			return true;
		}
		return false;
	}

	public final static String asString(int ipaddr) {

		byte[] ipbyts = new byte[4];
		ipbyts[0] = (byte) ((ipaddr >> 24) & 0xFF);
		ipbyts[1] = (byte) ((ipaddr >> 16) & 0xFF);
		ipbyts[2] = (byte) ((ipaddr >> 8) & 0xFF);
		ipbyts[3] = (byte) (ipaddr & 0xFF);

		return asString(ipbyts);
	}

	public static final int byteArrayToInt(byte[] b) {
		if (b == null || b.length != 4) {
			return 0;
		}
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

	public static String[] getAllIps(String ipaddress, String netmask) {

		if (DEFAULT_NET_MASK.equals(netmask)) {
			return new String[] { ipaddress };
		}

		try {
			byte[] t_ipBytes = asBytes(ipaddress);
			byte[] t_maskBytes = asBytes(netmask);
			byte[] t_network = getNetwork(t_ipBytes, t_maskBytes);
			int t_networkInt = byteArrayToInt(t_network);
			int t_total = totalInSubnet(t_maskBytes);

			if (t_total < 1) {
				return new String[] {};
			}
			String[] t_ips = new String[t_total - 2];
			for (int i = 1; i < t_total - 1; i++) {
				int nodeIp = t_networkInt | i;
				String ip = asString(nodeIp);
				t_ips[i - 1] = ip;
			}
			return t_ips;
		} catch (Exception e) {
			;
		}
		return new String[] {};
	}

	public static int getMaskCount(int x) {
		x = x - ((x >>> 1) & 0x55555555);
		x = (x & 0x33333333) + ((x >>> 2) & 0x33333333);
		x = (x + (x >>> 4)) & 0x0F0F0F0F;
		x = x + (x >>> 8);
		x = x + (x >>> 16);
		return x & 0x0000003F;
	}

	private static byte[] getNetwork(final byte[] ip, final byte[] mask) {

		if (ip == null || ip.length != 4) {
			return new byte[] { 0, 0, 0, 0 };
		}

		if (mask == null || mask.length != 4) {
			return new byte[] { 0, 0, 0, 0 };
		}

		return new byte[] { (byte) (mask[0] & ip[0]), (byte) (mask[1] & ip[1]),
				(byte) (mask[2] & ip[2]), (byte) (mask[3] & ip[3]) };

	}

	public static String[] getFromAndToIps(String ipaddress, String netmask) {

		if (DEFAULT_NET_MASK.equals(netmask)) {
			return new String[] { ipaddress, ipaddress };
		}
		byte[] t_ipBytes = asBytes(ipaddress);
		byte[] t_maskBytes = asBytes(netmask);
		// get the network address
		byte[] t_network = getNetwork(t_ipBytes, t_maskBytes);

		// convert network address into integer
		int t_networkInt = byteArrayToInt(t_network);

		// how many valid IP available for given netmask?
		int t_total = totalInSubnet(t_maskBytes);

		if (t_total < 4) {
			return null;
		}
		// load all the valid IP address into validIPList
		String[] t_ips = new String[2];
		t_ips[0] = asString(t_networkInt | 1);
		t_ips[1] = asString(t_networkInt | t_total - 2);
		return t_ips;
	}

	public static String hexToIp(String value, String splitCh) {
		if (value == null) {
			return null;
		}
		String t_array[] = null;
		if (value.indexOf(splitCh) >= 0) {
			t_array = value.split(splitCh);
		} else {
			t_array = new String[1];
			t_array[0] = value;
		}

		StringBuffer t_builder = new StringBuffer();
		for (int i = 0; i < t_array.length; i++) {
			t_builder.append(Integer.parseInt(t_array[i], 16));
			if (i < t_array.length - 1) {
				t_builder.append(STR_DOT);
			}
		}
		return t_builder.toString();
	}

	public static boolean isMultiCastMac(String mac) {
		if (mac != null) {
			if (mac.toLowerCase().startsWith(_01_00_5E)
					|| mac.toLowerCase().startsWith(_01_00_5E2)) {
				return true;
			}
		}
		return false;
	}

	public static byte[] getMacBytes(ByteBuffer buffer, int pos) {
		byte[] mac = new byte[6];
		for (int j = 0; j < 6; j++) {
			mac[j] = buffer.get(pos + j);
		}
		return mac;
	}

	public static String macToHexString(byte[] data) {
		StringBuilder sBuilder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int tmp = Byte.valueOf(data[i]).intValue();
			if (tmp < 0) {
				tmp = (1 << 8) + tmp;
			}
			int pos1 = tmp / 16;
			int pos2 = tmp % 16;

			sBuilder.append(FROM0TOF[pos1]).append(FROM0TOF[pos2]);
		}
		return sBuilder.toString();
	}

	public final static boolean isNumericAddress(String ipaddr) {

		if (ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15)
			return false;

		StringTokenizer token = new StringTokenizer(ipaddr, STR_DOT);
		if (token.countTokens() != 4)
			return false;

		while (token.hasMoreTokens()) {

			String ipNum = token.nextToken();

			try {
				int ipVal = Integer.valueOf(ipNum).intValue();
				if (ipVal < 0 || ipVal > 255)
					return false;
			} catch (NumberFormatException ex) {
				return false;
			}
		}

		return true;
	}

	public final static int parseNumericAddress(final String ipaddr) {

		if (ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15)
			return 0;

		StringTokenizer token = new StringTokenizer(ipaddr, STR_DOT);
		if (token.countTokens() != 4)
			return 0;

		int ipInt = 0;

		while (token.hasMoreTokens()) {

			String ipNum = token.nextToken();

			try {

				int ipVal = Integer.valueOf(ipNum).intValue();
				if (ipVal < 0 || ipVal > 255)
					return 0;

				ipInt = (ipInt << 8) + ipVal;
			} catch (NumberFormatException ex) {
				return 0;
			}
		}

		return ipInt;
	}

	public static int totalInSubnet(final byte[] mask) {

		byte[] bytes = new byte[] { (byte) ~(mask[0] & 0xFF),
				(byte) ~(mask[1] & 0xFF), (byte) ~(mask[2] & 0xFF),
				(byte) ~(mask[3] & 0xFF) };

		return byteArrayToInt(bytes) + 1;

	}

	public static String pkey2mac(String keyStr) {

		String[] splits = keyStr.split("\\.");
		if (splits.length >= 6) {
			StringBuffer macs = new StringBuffer("");
			for (int i = 0; i < 6; i++) {
				int tmp = Integer.parseInt(splits[i]);
				if (tmp < 16) {
					macs.append("0" + Integer.toHexString(tmp));
				} else {
					macs.append(Integer.toHexString(tmp));
				}
				if (i < 5) {
					macs.append(":");
				}
			}
			return macs.toString();
		}
		return keyStr;
	}

	/**
	 * 转化字符串为十六进制编码 
	 * @param s
	 * @return
	 */
	public static String toHexString(String s) 
	{ 
		String str = ""; 
		for (int i=0;i<s.length();i++) 
		{ 
			int ch = (int)s.charAt(i); 
			String s4 = Integer.toHexString(ch); 
			if (s4.length() == 1) {
				s4 = "0" + s4;
			}
			str = str + " " + s4; 
		} 
		return str.substring(1); 
	} 
	
	/**
	 * 转化十六进制编码为字符串 
	 * @param s
	 * @return
	 */
	public static String toStringHex(String s) 
	{ 
		s = s.replaceAll(" ", "");
		s = s.replaceAll(":", "");
		byte[] baKeyword = new byte[s.length()/2]; 
		for(int i = 0; i < baKeyword.length; i++) 
		{ 
			try 
			{ 
				baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16)); 
			} 
			catch(Exception e) 
			{ 
				e.printStackTrace(); 
			} 
		} 
		try 
		{ 
			s = new String(baKeyword, "utf-8");//UTF-16le:Not 
		} 
		catch (Exception e1) 
		{ 
			e1.printStackTrace(); 
		} 
		return s; 
	} 
	
	public static void main(String[] args) {
		System.out.println(neatenMac("0:0:0:0:0:1"));
		System.out.println(neatenMac("6c 50 4d 40"));
		System.out.println(neatenMac("\n!c>"));
		System.out.println(neatenMac("lPM@7."));
		System.out.println(neatenMac("6c 50 4d 40 37 2e"));
		System.out.println(neatenMac("6c:50:4d:40:37:2e"));
		System.out.println(neatenMac("6c 50 4d 40 37 2e 33 6f"));
		System.out.println(neatenStringToMacType("6c:50:4d:40:37:2e"," "));
	}
	public static String neatenStringToMacType(String mac, String sprite) {
		if (mac == null || mac.trim().equals("")) {
			return null;
		}
		//"lPM@7."为字符串，非16进制字符串“6c 50 4d 40 37 2e”
		if (mac.length() <= 6) {//最多支持6 X 2位mac
			mac = toHexString(mac);
		}
		StringBuffer temMac = new StringBuffer();
		for (int i = 0; i < mac.length(); ++i) {
			char ch = mac.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f')) {
				temMac.append(ch);
			}
		}
		if(temMac.length() < 12){
			try{
			temMac = new StringBuffer();
			int tempPos = 0;
			for (int i = 0; i < mac.length(); ++i) {
				char ch = mac.charAt(i);
				if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f')) {
					temMac.append(ch);
					tempPos++;
				}
				else{
					if(tempPos %2 != 0 && i>0){
						temMac.insert(temMac.length()-1, "0");
					}
					tempPos=0;
				}
			}
			if(tempPos %2 != 0){
				temMac.insert(temMac.length()-1, "0");
			}
			}catch(Exception ex){}
		}
		mac = temMac.toString();
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < mac.length(); i = i + 2) {
			buffer.append(mac.substring(i, i + 2));
			if (i < mac.length() - 2) {
				buffer.append(sprite);
			}
		}
		return buffer.toString().trim().toLowerCase();
	}

	public static String neatenMac(String mac) {
		return neatenStringToMacType(mac, ":");
	}
	
	public static boolean isValidMac(String mac) {

		if (mac == null || StringUtils.isEmpty(mac)) {
			return false;
		}
		return mac
				.matches("[0-9A-Fa-f]{1,2}[:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f][:|\\s][0-9A-Fa-f][0-9A-Fa-f]");
	}

	public static boolean isNormalMac(String mac) {
		if (!NetworkTool.isValidMac(mac)) {
			return false;
		}
		if (NetworkTool.isEmptyMac(mac)) {
			return false;
		}
		if (NetworkTool.isVirutalMac(mac)) {
			return false;
		}
		return true;
	}

	public static boolean isEmptyMac(String mac) {
		if (mac == null || mac.length() == 0) {
			return true;
		} else if (mac.equalsIgnoreCase(MAC_EMPTY_SPACE)
				|| mac.equalsIgnoreCase(MAC_EMPTY_COLON)) {
			return true;
		}
		return false;
	}

	public static boolean isBroadCastMAC(String mac) {
		if (mac != null) {
			if (mac.equalsIgnoreCase("FF FF FF FF FF FF")
					|| mac.equalsIgnoreCase("FF:FF:FF:FF:FF:FF")) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEmptyIp(String ip) {
		if (ip == null || ip.length() == 0) {
			return true;
		} else if (ip.equalsIgnoreCase(IP_ZERO)) {
			return true;
		}
		return false;
	}

	public static boolean isBroadCastIp(String ip) {
		if (ip != null && ip.startsWith("224.")) {
			return true;
		}
		return false;
	}

	public static boolean isHeartbeatMac(String mac) {
		if (mac == null) {
			return false;
		}
		if (mac.startsWith("02 01 00")) {
			return true;
		} else if (mac.startsWith("02:01:00")) {
			return true;
		}
		return false;
	}

	public static boolean isVmwareMac(String mac) {
		if (mac == null) {
			return false;
		}

		String lowerMac = mac.toLowerCase();

		if (lowerMac.startsWith("00 50 56")) {
			return true;
		} else if (lowerMac.startsWith("00 05 69")) {
			return true;
		} else if (lowerMac.startsWith("00 0c 29")) {
			return true;
		} else if (lowerMac.startsWith("00 1c 14")) {
			return true;
		} else if (lowerMac.startsWith("00:50:56")) {
			return true;
		} else if (lowerMac.startsWith("00:05:69")) {
			return true;
		} else if (lowerMac.startsWith("00:0c:29")) {
			return true;
		} else if (lowerMac.startsWith("00:1c:14")) {
			return true;
		}

		return false;
	}

	public static final String MAC_EMPTY_SPACE = "00 00 00 00 00 00";
	public static final String MAC_EMPTY_COLON = "00:00:00:00:00:00";
	public static final String IP_ZERO = "0.0.0.0";
	public static final String IP_LOOP = "127.0.0.1";
	final static char[] FROM0TOF = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final String DEFAULT_NET_MASK = "255.255.255.255";
	private static final String STR_DOT = ".";

}
