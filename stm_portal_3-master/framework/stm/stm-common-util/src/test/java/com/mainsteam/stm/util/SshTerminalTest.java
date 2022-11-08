package com.mainsteam.stm.util;

import java.io.IOException;

import com.mainsteam.stm.network.ssh.SshTerminal;

public class SshTerminalTest {
	public static void main(String[] args) {
		SshTerminal terminal = new SshTerminal();
		try {
			boolean login = terminal.login("172.16.7.200", "root", "password");
			if(login){	
				terminal.runCMD("ifconfig ","arp -a ");
				terminal.runCMD("netstat");
			}
			System.out.println(terminal.getRemoteInfo());
			boolean isLogin = terminal.login("172.16.10.254", "test", "test");
			if(isLogin){
				terminal.runCMD("ifconfig");
			}			
			terminal.destory();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
