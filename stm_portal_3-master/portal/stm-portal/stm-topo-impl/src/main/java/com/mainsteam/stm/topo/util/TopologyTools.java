package com.mainsteam.stm.topo.util;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.snmp4j.smi.OID;


/**
 * 
 * @author Administrator
 * 
 */
public final class TopologyTools {

	private static AtomicLong index = new AtomicLong(0);

	static final String[] HEX_SPLIT_STRING = { ":", " ", "-" };

	public static long createUniquePrimaryKey() {
		return index.incrementAndGet();
	}

	/**
	 * 
	 * @param vcommunity
	 */
	public static boolean checkCommunity(String community) {
		boolean same = false;
		if (community != null && !community.trim().equals("")) {
			same = true;
		}
		return same;
	}

	/**
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean checkStringToInteger(String str) {
		boolean check = false;
		try {
			Integer.valueOf(str);
			check = true;
		} catch (Exception e) {
			check = false;
		}
		return check;
	}

	/**
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean checkIfIndex(int index) {
		if (index < 1) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean checkInteger(String str) {
		boolean num = false;
		try {
			Integer.valueOf(str);
			num = true;
		} catch (Exception e) {
			num = false;
		}
		return num;
	}

	public static boolean checkNetmask(String netmask) {
		if (checkIPWildcard(netmask)) {
			String[] ips = netmask.split("\\.");
			String binaryVal = "";
			for (int i = 0; i < ips.length; i++) {
				String binaryStr = Integer.toBinaryString(Integer.parseInt(ips[i]));
				Integer times = 8 - binaryStr.length();
				for (int j = 0; j < times; j++) {
					binaryStr = "0" + binaryStr;
				}
				binaryVal += binaryStr;
			}
			String regx = "^[1]*[0]*$";
			if (Pattern.matches(regx, binaryVal)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param ip
	 * @return boolean
	 */
	public static boolean checkIPWildcard(String ipwildcard) {
		if (ipwildcard == null || ipwildcard.trim().equals("")) {
			return false;
		}
		StringBuffer regex = new StringBuffer();
		regex.append("^((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)\\.").append("((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)\\.").append("((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)\\.").append(
				"((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])|\\*)");
	
		return ipwildcard.matches(regex.toString());
	}

	/**
	 * �ж�����ֵ�Ƿ���MAC��ʽ����Ϸ���true������Ϸ���false
	 * 
	 * @param ipwildcard
	 * @return boolean
	 */
	public static boolean checkMACWildcard(String macwildcard) {
		if (StringUtils.isNotEmpty(macwildcard)) {
			String rex = "([0-9a-fA-F][0-9a-fA-F][:| |-])+[0-9a-fA-F][0-9a-fA-F]";
			Pattern p = Pattern.compile(rex);
			if (p.matcher(macwildcard).matches()) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkPort(String port) {
		boolean ok = false;
		if (port != null && !port.trim().equals("")) {
			try {
				int index = Integer.valueOf(port.trim());
				if (index >= 1 && index <= 65535) {
					ok = true;
				}
			} catch (Exception e) {
				ok = false;
			}
		}
		return ok;
	}

	public static boolean checkPort(int port) {
		if (port > 0 && port <= 65535) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean checkString(String str) {
		return (str != null && !str.trim().equals(""));
	}

	/**
	 * 
	 * @return
	 */
	public static String getDefaultSystemId() {
		return "1.3.6.1.4.1";
	}

	public static String hexToChinese(String value) {
		if (value == null) {
			return null;
		}
		String hexString = value.trim();
		if (!isHex(hexString)) {
			return hexString;
		}
		String[] splitStrs = HEX_SPLIT_STRING;
		String[] splitResult = null;
		for (String splitStr : splitStrs) {
			if (hexString.indexOf(splitStr) > 0) {
				splitResult = hexString.split(splitStr);
				break;
			}
		}
		String result = null;
		if (splitResult != null) {
			byte[] bs = new byte[splitResult.length];
			int i = 0;
			for (String st : splitResult) {
				byte tmp = Integer.valueOf(st, 16).byteValue();
				if (Character.isISOControl(tmp)) {
					tmp = ' ';
				}
				bs[i++] = tmp;
			}
			result = new String(bs).trim();
		}
		return result;
	}

	public static String neatenIpAddress(String ip) {
		if (ip == null || ip.trim().equals("")) {
			return null;
		}
		if (checkIPWildcard(ip)) {
			return ip;
		}
		String temp = neatenOctetIpString(ip);
		if (checkIPWildcard(temp)) {
			return temp;
		}
		return null;
	}

	public static String neatenOctetIpString(String octString) {
		if (octString == null || octString.trim().equals("")) {
			return null;
		}
		for (String split : HEX_SPLIT_STRING) {
			String rex = "([0-9a-fA-F][0-9a-fA-F][" + split + "])+[0-9a-fA-F][0-9a-fA-F]";
			if (Pattern.matches(rex, octString)) {
				String[] temp = octString.split(split);
				StringBuffer buffer = new StringBuffer();
				for (String str : temp) {
					int tmp = Integer.valueOf(str, 16);
					buffer.append(String.valueOf(tmp)).append(".");
				}
				return buffer.toString().substring(0, buffer.length() - 1);
			}
		}
		return octString;
	}

	public static String neatenOctetString(String octString) throws UnsupportedEncodingException {
		if (octString == null || octString.trim().equals("")) {
			return null;
		}
		for (String split : HEX_SPLIT_STRING) {
			String rex = "([0-9a-fA-F][0-9a-fA-F][" + split + "])+[0-9a-fA-F][0-9a-fA-F]";
			if (Pattern.matches(rex, octString)) {
				String[] temp = octString.split(split);
				byte[] data = new byte[temp.length];
				for (int i = 0; i < data.length; ++i) {
					int d = Integer.valueOf(temp[i], 16);
					data[i] = (byte) d;
				}
				return new String(data,"GBK");
			}
		}
		return octString;
	}

	public static boolean neatenBoolean(String bool, boolean defaultBool) {
		try {
			return Boolean.valueOf(bool);
		} catch (Exception e) {
			return defaultBool;
		}
	}

	public static String neatenTime(String timeStr) throws Exception {
		if (timeStr == null) {
			return "";
		}
		try {
			String[] tmpDay = new String[2];
			if (timeStr.indexOf("days") >= 0) {
				tmpDay = timeStr.split("days,");
			} else {
				if (timeStr.indexOf("day") >= 0) {
					tmpDay = timeStr.split("day,");
				} else {
					tmpDay[0] = "0";
					tmpDay[1] = timeStr;
				}
			}
			long day = Long.parseLong(tmpDay[0].trim()) * 24 * 60 * 60 * 1000;
			String[] tmpHour = tmpDay[1].trim().split(":");
			long hour = Long.parseLong(tmpHour[0]) * 60 * 60 * 1000;
			String tmpMinute = tmpHour[1];
			int minute = Integer.parseInt(tmpMinute) * 60 * 1000;
			String[] tmpSecond = tmpHour[2].split("\\.");
			int second = Integer.parseInt(tmpSecond[0]) * 1000;
			int millSecond = Integer.parseInt(tmpSecond[1]);
			long time = day + hour + minute + second + millSecond;
			timeStr = String.valueOf(time);
		} catch (Exception e) {
		}
		return timeStr;
	}

	public static String neatenStringToMacType(String mac, String sprite) {
		if (mac == null || mac.trim().equals("")) {
			return null;
		}
		//"lPM@7."Ϊ�ַ���16�����ַ�6c 50 4d 40 37 4a��
		if (mac.length() < 12) {
			mac = toHexString(mac);
		}
		StringBuffer temMac = new StringBuffer();
		for (int i = 0; i < mac.length(); ++i) {
			char ch = mac.charAt(i);
			if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f')) {
				temMac.append(ch);
			}
		}
		mac = temMac.toString();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < mac.length(); i = i + 2) {
			buffer.append(mac.substring(i, i + 2));
			if (i < mac.length() - 2) {
				buffer.append(sprite);
			}
		}
		return buffer.toString().trim().toUpperCase();
	}

	public static String neatenMac(String mac) {
		return neatenStringToMacType(mac, " ");
	}

	public static String neatenMac(byte[] macBytes) {
		StringBuffer value = new StringBuffer();
		for (int i = 0; i < macBytes.length; i++) {
			String sTemp = Integer.toHexString(0xFF & macBytes[i]);
			if (sTemp.length() == 1) {
				sTemp = "0" + sTemp;
			}
			value.append(sTemp);
			if (i < macBytes.length - 1) {
				value.append(" ");
			}
		}
		return value.toString().trim().toUpperCase();
	}

	/**
	 * 
	 * @param mac
	 * @return String
	 */
	public static int neatenNumberToInteger(String num, int defaultNum) {
		if (num == null || num.trim().equals("")) {
			return defaultNum;
		}
		StringBuffer temNum = new StringBuffer();
		for (int i = 0; i < num.length(); ++i) {
			char ch = num.charAt(i);
			if ((ch >= '0' && ch <= '9') || ch == '-') {
				temNum.append(ch);
			}
		}
		try {
			return Integer.valueOf(temNum.toString());
		} catch (Exception e) {
			return defaultNum;
		}
	}

	/**
	 * 
	 * @param ifIndex
	 * @return int
	 */
	public static int neatenIfIndex(String ifIndex) {
		int index = 0;
		try {
			index = Integer.valueOf(ifIndex);
			if (index < 0) {
				index = 0;
			}
		} catch (Exception e) {
			index = 0;
		}
		return index;
	}

	/**
	 * 
	 * @param num
	 * @return long
	 */
	public static long neatenNumberToLong(String num, long defaultNum) {
		if (num == null || num.trim().equals("")) {
			return defaultNum;
		}
		StringBuffer temNum = new StringBuffer();
		for (int i = 0; i < num.length(); ++i) {
			char ch = num.charAt(i);
			if ((ch >= '0' && ch <= '9')) {
				temNum.append(ch);
			}
		}
		try {
			return Long.valueOf(temNum.toString());
		} catch (Exception e) {
			return defaultNum;
		}
	}

	/**
	 * 
	 * @param num
	 * @return float
	 */
	public static float neatenNumberToFloat(String num, float defaultNum) {
		if (num == null || num.trim().equals("")) {
			return defaultNum;
		}
		StringBuffer temNum = new StringBuffer();
		for (int i = 0; i < num.length(); ++i) {
			char ch = num.charAt(i);
			if ((ch >= '0' && ch <= '9') || ch == '.') {
				temNum.append(ch);
			}
		}
		try {
			return Float.valueOf(temNum.toString());
		} catch (Exception e) {
			return defaultNum;
		}
	}

	/**
	 * 
	 * @param oid
	 * @return String
	 */
	public static String neatenOid(OID oid) {
		if (oid == null) {
			return null;
		}
		String temOid = oid.toString();
		return neatenOid(temOid);
	}

	/**
	 * 
	 * @param oid
	 * @return String
	 */
	public static String neatenOid(String oid) {
		String tempOid = null;
		try {
			final String matchDot = "((\\.)[0-9]+)+";
			final String matchNoDot = "[0-9]+((\\.)[0-9]+)+";
			if (Pattern.matches(matchNoDot, oid)) {
				tempOid = oid;
			} else if (Pattern.matches(matchDot, oid)) {
				tempOid = oid.substring(1);
			}
		} catch (Exception e) {
			tempOid = null;
		}

		return tempOid;
	}

	public static String neatenOid(String oid, boolean returnDefault) {
		String tempOid = null;
		try {
			final String matchDot = "((\\.)[0-9]+)+";
			final String matchNoDot = "[0-9]+((\\.)[0-9]+)+";
			if (Pattern.matches(matchNoDot, oid)) {
				tempOid = oid;
			} else if (Pattern.matches(matchDot, oid)) {
				tempOid = oid.substring(1);
			}
		} catch (Exception e) {
			tempOid = null;
		}
		if (returnDefault && tempOid == null) {
			tempOid = getDefaultSystemId();
		}
		return tempOid;
	}

	/**
	 * 
	 * @param oids
	 * @return String[]
	 */
	public static String[] neatenOids(String[] oids) {
		if (oids == null || oids.length == 0) {
			return null;
		}
		String[] temps = new String[oids.length];
		for (int i = 0; i < oids.length; ++i) {
			String tempOid = neatenOid(oids[i]);
			if (tempOid != null) {
				temps[i] = tempOid;
			} else {
				return null;
			}
		}
		return temps;
	}

	public static String parseAsciiTo10(String value) {
		String[] nn = null;
		if (value.indexOf(".") >= 0) {
			nn = value.split("\\.");
		}
	
		if (nn != null) {
			byte[] bs = new byte[nn.length];
			for (int i = 0; i < nn.length; i++) {
				bs[i] = Byte.parseByte(nn[i]);
			}
			String tmp[] = new String(bs).split(" ");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < tmp.length; i++) {
				sb.append(Integer.parseInt(tmp[i], 16));
				if (i < tmp.length - 1) {
					sb.append(".");
				}
			}
			return sb.toString();
		}
		return null;
	}

	public static String parse16To10(String value) {
		if (value == null) {
			return null;
		}
		String tmp[] = null;
		if (value.indexOf(" ") >= 0) {
			tmp = value.split(" ");
		} else {
			tmp = new String[1];
			tmp[0] = value;
		}
	
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tmp.length; i++) {
			sb.append(Integer.parseInt(tmp[i], 16));
			if (i < tmp.length - 1) {
				sb.append(".");
			}
		}
		return sb.toString();
	}

	/**
	 * ��һ�������ε�ʱ��ֵת���ɸ�ʽΪ"1 days, 19:10:10.512"���ַ�.
	 * 
	 * @param timestamp
	 *            Ҫת���ĳ�����ʱ��.
	 * @return ת������ַ��ʽ����ʱ��.
	 */
	public static String longTimeToString(long timestamp) {
		long days = timestamp / 86400000;
		timestamp = timestamp % 86400000;
		long hours = timestamp / 3600000;
		timestamp = timestamp % 3600000;
		long minutes = timestamp / 60000;
		timestamp = timestamp % 60000;
		long seconds = timestamp / 1000;
		long milliseconds = timestamp % 1000;
		return days + " days, " + hours + ":" + minutes + ":" + seconds + "." + milliseconds;
	}

	/**
	 * �ж�һ���ַ��ǣ������Ƹ�ʽ�� �������Ƹ�ʽ��Ϊ ÿ�����ַ�HEX_SPLIT_STRING�е�һ�� �� 06:9f �� 06 9f
	 * 
	 * @param value
	 * @return boolean
	 */
	public static boolean isHex(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
	
		for (String splitStr : HEX_SPLIT_STRING) {
			if (value.indexOf(splitStr) >= 0) {
				value += splitStr;
				String rex = "([0-9a-fA-F][0-9a-fA-F][" + splitStr + "])+";
				Pattern p = Pattern.compile(rex);
				if (p.matcher(value).matches()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param oid
	 * @return String[]
	 */
	public static String[] splitOid(String oid) {
		String[] indexs = null;
		if (oid != null && !oid.trim().equals("")) {
			oid = oid.replaceAll("��", ".");
			oid = oid.replaceAll("\\s+", "");
			if (!oid.equals(".")) {
				if (oid.startsWith(".")) {
					oid = oid.substring(1);
				}
				if (oid.endsWith(".")) {
					oid = oid.substring(0, oid.length() - 1);
				}
				indexs = oid.split("\\.");
				if (indexs.length == 0) {
					indexs = null;
				}
			}
		}
		return indexs;
	}

	/**
	 * 
	 * @param str
	 * @return String
	 */
	public static String trimString(String str) {
		String result = "";
		if (str != null) {
			result = str.trim();
		}
		return result;
	}

	public static String[] splitWithFirst(String str, String sp, boolean firstNothing) {
		String results[] = null;
		if (checkString(str) && checkString(sp)) {
			int startIndex = str.indexOf(sp);
			results = new String[2];
			if (startIndex > 0) {
				results[0] = str.substring(0, startIndex);
				results[1] = str.substring(startIndex + 1);
			} else if (firstNothing) {
				results[0] = "";
				results[1] = str;
			} else {
				results[0] = str;
				results[1] = "";
			}
		}
		return results;
	}

	public static String[] splitWithLast(String str, String sp, boolean firstNothing) {
		String results[] = null;
		if (checkString(str) && checkString(sp)) {
			int startIndex = str.lastIndexOf(sp);
			results = new String[2];
			if (startIndex > 0) {
				results[0] = str.substring(0, startIndex);
				results[1] = str.substring(startIndex + 1);
			} else if (firstNothing) {
				results[0] = "";
				results[1] = str;
			} else {
				results[0] = str;
				results[1] = "";
			}
		}
		return results;
	}

	public static String[] splitAll(String str, String sp) {
		String results[] = null;
		if (checkString(str) && checkString(sp)) {
			results = str.split(sp);
		}
		return results;
	}

	public static boolean nullNotEquals(String str1, String str2) {
		boolean same = false;
		if (str1 != null && str2 != null) {
			same = str1.trim().equals(str2.trim());
		}
		return same;
	}

	public static String linkString(String... str) {
		String result = "";
		if (str != null && str.length > 1) {
			if (str.length > 2) {
				StringBuffer buffer = new StringBuffer();
				String sp = str[str.length - 1];
				for (int i = 0; i < str.length - 1; ++i) {
					buffer.append(str[i]);
					if (i < str.length - 2) {
						buffer.append(sp);
					}
				}
				result = buffer.toString();
			} else {
				result = str[0];
			}
		}
		return result;
	}

	/**
	 * 
	 * @param collectA
	 * @param collectB
	 * @return boolean
	 */
	public static <T> boolean intersection(Set<T> collectA, Set<T> collectB) {
		if (collectA != null && collectA.size() > 0 && collectB != null && collectB.size() > 0) {
			for (T dataA : collectA) {
				if (collectB.contains(dataA)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param <T>
	 * @param collectA
	 * @param collectB
	 * @return
	 */
	public static <T> Set<T> intersectionElements(Set<T> collectA, Set<T> collectB) {
		Set<T> datas = new HashSet<T>();
		if (collectA != null && collectA.size() > 0 && collectB != null && collectB.size() > 0) {
			for (T dataA : collectA) {
				if (collectB.contains(dataA)) {
					datas.add(dataA);
				}
			}
		}
		return datas;
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return boolean
	 */
	public static <T> boolean isEquals(T a, T b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.equals(b)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param netAddr
	 * @param netmask
	 * @param ip
	 * @return boolean
	 */
	public static boolean ipContainSub(String netAddr, String netmask, String ip) {
		long ipToLong = TopologyTools.str2long(ip);
		long numbers = TopologyTools.getHostNumbers(netmask);
		long startIp = TopologyTools.str2long(netAddr) + 1;
		return (ipToLong >= startIp) && (ipToLong <= startIp + numbers);
	}

	/**
	 * 
	 * @param netAddr
	 * @param ip
	 * @return boolean
	 */
	public static boolean ipContainSub(String netAddr, String ip) {
		boolean contains = false;
		if (TopologyTools.checkIPWildcard(netAddr) && TopologyTools.checkIPWildcard(ip)) {
			long ipToLong = TopologyTools.str2long(ip);
			long netToLong = TopologyTools.str2long(netAddr);
			long startIp = netToLong + 1;
			int zeroNumber = 0;
			while (netToLong > 0 && (netToLong & 1) == 0) {
				netToLong = netToLong >> 1;
				++zeroNumber;
			}
			int numbers = 1;
			if (zeroNumber > 0) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < zeroNumber; ++i) {
					sb.append("1");
				}
				numbers = Integer.valueOf(sb.toString(), 2) - 1;
			}
			contains = (ipToLong >= startIp) && (ipToLong <= startIp + numbers);
		}
		return contains;
	}

	public static String iPv4String(long address) {
		StringBuffer buf = new StringBuffer();
		buf.append((int) ((address >>> 24) & 0xff)).append('.');
		buf.append((int) ((address >>> 16) & 0xff)).append('.');
		buf.append((int) ((address >>> 8) & 0xff)).append('.');
		buf.append((int) (address & 0xff));
		return buf.toString();
	}

	public static boolean isVaildMac(String macwildcard) {
		if (checkMACWildcard(macwildcard)) {
			String tmpMac = neatenStringToMacType(macwildcard, "");
			tmpMac = tmpMac.toUpperCase();
			if (tmpMac.equals("000000000000") || tmpMac.equals("FFFFFFFFFFFF")) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static boolean isValidIp(String ip) {
		if (checkIPWildcard(ip)) {
			try {
				InetAddress address = Inet4Address.getByName(ip);
				if (!address.isLoopbackAddress()) {
					return true;
				}
			} catch (UnknownHostException e) {
			}
		}
		return false;
	}

	public static long byte2Long(byte b) {
		long r = (long) b;
		if (r < 0) {
			r += 256;
		}
		return r;
	}

	public static long getHostNumbers(String netMask) {
		long hostNumbers = 0;
		if (!netMask.equals("255.255.255.255")) {
			hostNumbers = str2long("255.255.255.255") - str2long(netMask) - 1;
		} else {
			hostNumbers = 1;
		}
		return hostNumbers;
	}

	public static long str2long(String ipAddress) {
		String ipAddress1 = ipAddress;
		byte[] ip = null;
		InetAddress iNetAdd = null;
		try {
			iNetAdd = InetAddress.getByName(ipAddress1);
			ip = iNetAdd.getAddress();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
		try {
			return convert2Long(ip);
		} catch (Exception ex1) {
			return 0L;
		}
	}

	public static long convert2Long(byte[] addr) {
		if (addr == null) {
			throw new IllegalArgumentException("The passed array must not be null");
		}
		long address = 0;
		for (int i = 0; i < addr.length; i++) {
			address <<= 8;
			address |= byte2Long(addr[i]);
		}
		return address;
	}

	public static boolean startWithFrimatTime(String data) {
		if (data == null || data.trim().equals("")) {
			return false;
		}
		data = data.replaceAll("\r\n", "\n").trim();
		String[] array = data.split("\n");
		final String rgex = "\\[ [0-9]{4}(-[0-9]{2}){2} [0-9]{2}(:[0-9]{2}){2} \\] .*";
		return Pattern.matches(rgex, array[0]);
	}

//	public static <T> void stringToCollection(Collection<T> coll, String echo, String spriteStr, IValueParser<T> parser) {
//		String[] strs = echo.split(spriteStr);
//		for (String tmp : strs) {
//			if (!StringUtils.isEmpty(tmp)) {
//				coll.add(parser.parseValue(tmp));
//			}
//		}
//	}

	public static <T> String printList(Collection<T> coll, String spriteStr) {
		StringBuffer sb = new StringBuffer();
		if (!CollectionUtils.isEmpty(coll)) {
			Iterator<T> it = coll.iterator();
			while (it.hasNext()) {
				T val = it.next();
				sb.append(val);
				if (it.hasNext()) {
					sb.append(spriteStr);
				}
			}
		}
		return sb.toString();
	}

	public static String printList(String[] str) {
		StringBuffer value = new StringBuffer();
		value.append(" [ ");
		if (str == null || str.length == 0) {
			value.append(" null ]");
		} else {
			for (String temp : str) {
				if (temp == null || temp.equals("")) {
					temp = "null";
				}
				value.append(temp + " , ");
			}
			value.append("]");
		}
		return value.toString();
	}

	public static String neatenNetAddress(String ipAddr, String netMask) {
		InetAddress tempIP = null;
		InetAddress tempMask = null;
		StringBuffer buf = new StringBuffer();
		try {
			tempIP = InetAddress.getByName(ipAddr);
			tempMask = InetAddress.getByName(netMask);
			byte[] ipByte = tempIP.getAddress();
			byte[] maskByte = tempMask.getAddress();
			for (int i = 0; i < ipByte.length; i++) {
				long temp1 = byte2Long(ipByte[i]);
				long temp2 = byte2Long(maskByte[i]);
				if (i < 3) {
					buf.append((int) (temp1 & temp2)).append(".");
				} else {
					buf.append((int) (temp1 & temp2));
				}
			}
		} catch (Exception e) {
			return null;
		}
		return buf.toString();
	}

	public static void main(String[] args) {
		String netAddress = neatenNetAddress("211.137.111.207", "255.255.255.255");
	}

	public static String formatTime(long time, String pattern) {
		Date currentTime = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(currentTime);
	}

	public static String formatTime(long time) {
		return "[ " + formatTime(time, "yyyy-MM-dd HH:mm:ss") + " ] ";
	}

	public static String printErrorMessage(Throwable e) {
		StringBuffer causeMessage = new StringBuffer();
		causeMessage.append(e.toString()).append("\r\n");
		StackTraceElement[] trace = e.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			causeMessage.append("\tat " + trace[i]).append("\r\n");
		}
		Throwable ourCause = e.getCause();
		if (ourCause != null) {
			printStackTraceAsCause(ourCause, causeMessage, trace);
		}
		return causeMessage.toString();
	}

	private static void printStackTraceAsCause(Throwable e, StringBuffer causeMessage, StackTraceElement[] causedTrace) {
		StackTraceElement[] trace = e.getStackTrace();
		int m = trace.length - 1, n = causedTrace.length - 1;
		while (m >= 0 && n >= 0 && trace[m].equals(causedTrace[n])) {
			m--;
			n--;
		}
		int framesInCommon = trace.length - 1 - m;

		causeMessage.append("Caused by: " + e).append("\r\n");
		for (int i = 0; i <= m; i++) {
			causeMessage.append("\tat " + trace[i]).append("\r\n");
		}
		if (framesInCommon != 0) {
			causeMessage.append("\t... " + framesInCommon + " more").append("\r\n");
		}
		Throwable ourCause = e.getCause();
		if (ourCause != null) {
			printStackTraceAsCause(ourCause, causeMessage, trace);
		}
	}
	
	/**
	 * ת���ַ�Ϊʮ����Ʊ��� 
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
	 * ת��ʮ����Ʊ���Ϊ�ַ� 
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
}
