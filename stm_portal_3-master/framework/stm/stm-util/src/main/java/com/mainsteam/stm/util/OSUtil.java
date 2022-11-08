/**
 * 
 */
package com.mainsteam.stm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author ziw
 * 
 */
public class OSUtil {

	/**
	 * 获取本机所有IP
	 */
	public static String[] getAllLocalHostIP() {
		List<String> res = new ArrayList<String>();
		Enumeration<NetworkInterface> netInterfaces;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				if (ni.isLoopback() || ni.isVirtual() || ni.isPointToPoint()) {
					continue;
				}
				Enumeration<InetAddress> nii = ni.getInetAddresses();
				while (nii.hasMoreElements()) {
					ip = (InetAddress) nii.nextElement();
					System.out.println(ip.getHostAddress());
					if (ip.getHostAddress().indexOf(":") == -1) {
						res.add(ip.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return (String[]) res.toArray(new String[0]);
	}

	/**
	 * 获取本机所有物理地址
	 * 
	 * @return
	 */
	public static String getMacAddress() {
		String mac = "";
		String line = "";
		String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Windows")) {
			try {
				String command = "cmd.exe /c ipconfig /all";
				Process p = Runtime.getRuntime().exec(command);

				BufferedReader br = new BufferedReader(new InputStreamReader(
						p.getInputStream()));

				while ((line = br.readLine()) != null) {
					if (line.indexOf("Physical Address") > 0) {
						int index = line.indexOf(":") + 2;

						mac = line.substring(index);

						break;
					}
				}

				br.close();

			} catch (IOException e) {
			}
		}
		return mac;
	}

	public static String getMacAddress(String host) {
		String mac = "";
		StringBuffer sb = new StringBuffer();
		try {
			NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress
					.getByName(host));
			byte[] macs = ni.getHardwareAddress();
			for (int i = 0; i < macs.length; i++) {
				mac = Integer.toHexString(macs[i] & 0xFF);
				if (mac.length() == 1) {
					mac = '0' + mac;
				}
				sb.append(mac + "-");
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		mac = sb.toString();
		mac = mac.substring(0, mac.length() - 1);

		return mac;
	}

	public static String getEnv(String key, String defaultValue) {
		String value = System.getenv(key);
		if (value == null) {
			value = System.getProperty(key);
		}
		return value == null ? defaultValue : value;
	}

	public static String getEnv(String key) {
		String value = System.getenv(key);
		if (value == null) {
			value = System.getProperty(key);
		}
		return value;
	}

	public static void main(String[] args) {
		String[] ips = getAllLocalHostIP();
		for (String ip : ips) {
			System.out.println(ip);
		}
		System.out.println(getOSName());
		System.out.println(getOSArch());
	}

	public static String getOSName() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") != -1) {
			return "Windows";
		} else if (os.indexOf("mac") != -1) {
			return "MacOSX";
		} else {
			return "Linux";
		}
	}

	public static int getOSArch() {
		String arch = System.getProperty("os.arch");
		if (arch.contains("64")) {
			return 64;
		}
		return 32;
	}

}
