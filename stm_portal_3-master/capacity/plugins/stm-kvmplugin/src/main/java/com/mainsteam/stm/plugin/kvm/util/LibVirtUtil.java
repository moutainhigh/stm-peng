package com.mainsteam.stm.plugin.kvm.util;

import java.io.IOException;

/**
 * 
 * @author yuanlb TODO desc 下午3:16:30
 */
public class LibVirtUtil extends SSHUtil {

	public LibVirtUtil(String host, String username, String passwd) {
		super(host, username, passwd);
	}

	public String[] getOut(String cmd) throws IOException {
		String[] res = null;
		String[] cmds = { cmd };
		writeCmd(cmds);
		String out = getOut();
		res = out.split("\n");
		return res;
	}
	/*
	 * public static void main(String[] args) throws IOException { //
	 * LibVirtUtil util = new LibVirtUtil("192.168.1.206","root","edi");
	 * LibVirtUtil util = new LibVirtUtil("192.168.1.186","root","123456");
	 * util.login(); String[] list = util.getOut("virsh domiflist 6");
	 * System.out.println(list.length); for(int i=0;i<list.length;i++)
	 * System.out.println(list[i]); util.close(); }
	 */
}
