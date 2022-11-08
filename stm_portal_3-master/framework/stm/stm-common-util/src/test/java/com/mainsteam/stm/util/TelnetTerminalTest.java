package com.mainsteam.stm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.SocketException;

import com.mainsteam.stm.network.telnet.TelnetTerminal;

public class TelnetTerminalTest {
	public static void main(String[] args) throws SocketException {
		TelnetTerminal telnet = null;
		try {
			telnet = new TelnetTerminal("192.168.1.1");
//			telnet = new TelnetTerminal("172.16.105.1");
//			telnet = new TelnetTerminal("192.168.10.22");
//			telnet.runCMD("h3c","root3306","tftp 172.16.8.62 put test.cfg abc.txt");
			String temp = "test|mainsteam321|copy running-config tftp://172.16.8.62/ciscco11.text||||";
//			String temp = "yunwei|33333333|enable|Qzpass123!@#|copy running-config tftp://172.16.8.62/ciscco105.text||";
			String cmd = temp.replace("|", "| ");
			String[] cmds = cmd.split("\\|");
			for(int i=0;i<cmds.length;i++){
				System.out.println("\""+cmds[i]+"\"");
			}
			telnet.runCMD(cmds);
//			telnet.runCMD("test","mainsteam321","copy running-config tftp://172.16.8.62/ciscco1.text"," "," "," "," ");
//			Thread.sleep(1000);
			System.out.println(telnet.getRemoteInfo());
			telnet.disconnect();
//			File file = new File("F:/install_path/eclipse/workspace/Framework/oc/oc-util/tftpTemp/"+"ciscco105.text");
			File file = new File("F:/install_path/eclipse/workspace/Framework/oc/oc-util/tftpTemp/"+"ciscco11.text");
//			File file = new File(TFTPServerTest.TFTP_DIRECTORY+"/"+"ciscco11.text");
			//假如没检测到文件、线程休眠、等一秒钟再检测一次
			if(file.exists() && file.isFile()){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(file.exists() && file.isFile()){
				try {
					StringBuffer buffer = new StringBuffer();
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String str;
					while((str=reader.readLine())!=null){
						buffer.append(str).append("\r\n");
					}
					reader.close();
					if(true) file.delete();
					System.out.println(buffer.toString());
					System.out.println("复制文件成功");
				} catch (FileNotFoundException e) {
					System.out.println("异常");
				}		
			}else{
				System.out.println("文件没有找到");
			}
//			StringBuffer buffer = new StringBuffer();
//			buffer.append(" ,").append(" ,");
//			System.out.println(buffer.toString().split(",").length);
			

		}catch(Exception e){
			System.out.println("-----------");
		}	
	}
}
