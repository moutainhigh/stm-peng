package com.mainsteam.stm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.mainsteam.stm.network.telnet.TelnetTerminal;
import com.mainsteam.stm.network.util.NetWorkBean;
import com.mainsteam.stm.network.util.NetWorkUtil;
import com.mainsteam.stm.network.util.ProtocolEnum;

public class NetWorkUtilTest {
	public static void main(String[] args) {
		NetWorkBean bean = new NetWorkBean();
		bean.setIp("172.16.7.200");
		bean.setPassword("password");
		bean.setUserName("root");
//		SSHtest
//		bean.setProtocol(ProtocolEnum.SSH);
//		bean.setPort(SshTerminal.DEFAULT_SSH_PORT);
//		TELNEtest
		bean.setProtocol(ProtocolEnum.TELNET);
		bean.setPort(TelnetTerminal.DEFAULT_TELNET_PORT);
		try {
			bean.getScripts().add(checkFile("D:\\workspace\\Framework\\oc\\tftp\\test.sh",false));
//			bean.getScripts().add("arp -a"+SshTerminal.ENTER+"ifconfig"+SshTerminal.ENTER+"netstat");
			System.out.println(NetWorkUtil.execute(bean));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String checkFile(String fileName,boolean del) throws IOException{
		File file = new File(fileName);
		if(file.exists() && file.isFile()){
			try {
				StringBuffer buffer = new StringBuffer();
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String str;
				while((str=reader.readLine())!=null){
					buffer.append(str).append("\r\n");
					System.out.println(str);
				}
				reader.close();
				if(del) file.delete();
				return buffer.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return "";
			}		
		}
		return "";
	}
}
