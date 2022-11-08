package com.mainsteam.stm.ipmanage.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacUtil {
	public static String getLocalMac() throws SocketException,
	UnknownHostException {
		// TODO Auto-generated method stub
		String str = "";
		String macAddress = "";

		// 获取非本地IP的MAC地址
		try {
			Process p = Runtime.getRuntime()
					.exec("arp -A");
			InputStreamReader ir = new InputStreamReader(p.getInputStream(), "gbk");

			BufferedReader br = new BufferedReader(ir);
			StringBuffer sb = new StringBuffer();

			while ((str = br.readLine()) != null) {

				sb.append(str + "\n");

			}
			//System.err.println(sb.toString());
			macAddress = sb.toString();
			p.destroy();
			br.close();
			ir.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return macAddress;

	}

	public static boolean ping() {
		try {
			String localMac = getLocalMac();
			String regex = "([^:]*)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(localMac);
			MacUtil macUtil = new MacUtil();
			while (matcher.find()) {
				for (int i = 0; i < matcher.groupCount(); i++) {
					String s = matcher.group(i);
					String ipForNetSegment = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\ *-";
					Pattern netSegment = Pattern.compile(ipForNetSegment);
					Matcher netSegmentMatcher = netSegment.matcher(s);
					while (netSegmentMatcher.find()) {
						//此时调用ping IP，然后再获取一遍arp表
						macUtil.PingAll(netSegmentMatcher.group(0));
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	//当前线程的数量, 防止过多线程摧毁电脑
	static int threadCount = 0;
	public synchronized void changeCount(int i){
		if(i==1){
			threadCount+=1;
		}else {
			threadCount -=1;
		}
	}
	public void pingIP(String ip) throws Exception {
		//最多30个线程


		while (threadCount > 12)
			Thread.sleep(50);
		changeCount(1);


		PingIp p = new PingIp(ip);
		p.start();
	}

	public void PingAll(String ip) throws Exception {
		//首先得到本机的IP，得到网段

		int k = 0;
		k = ip.lastIndexOf(".");
		String ss = ip.substring(0, k + 1);
		for (int i = 1; i <= 255; i++) { //对所有局域网Ip
			String iip = ss + i;
			pingIP(iip);
		}
		//等着所有Ping结束
		while (threadCount > 0)
			Thread.sleep(50);
	}

	class PingIp extends Thread {
		public String ip; // IP

		public PingIp(String ip) {
			this.ip = ip;
		}

		public void run() {
			try {
				//Process p =
				Runtime.getRuntime().exec("ping " + ip + " -w 300 -n 1");
				//                InputStreamReader ir = new InputStreamReader(p.getInputStream(), "gbk");
				//                BufferedReader br = new BufferedReader(ir);
				//                StringBuffer sb = new StringBuffer();
				//                for (int i = 1; i < 7; i++)
				//                    br.readLine();
				//                String line = br.readLine();
				//System.err.println(ip);
				//线程结束
				changeCount(0);
			} catch (IOException e) {
			}
		}
	}

}
