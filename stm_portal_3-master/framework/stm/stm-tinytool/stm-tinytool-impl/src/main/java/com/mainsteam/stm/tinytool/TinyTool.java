package com.mainsteam.stm.tinytool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.mainsteam.stm.tinytool.obj.SnmpTarget;

public class TinyTool implements TinyToolMBean {
	
	public TinyTool(){}
	
	//多操作系统预留验证
	private static String OS = System.getProperty("os.name").toLowerCase();  
	
	public String ping(String pingCmd) {
		return getCmd(pingCmd);
	}

	public String tracert() {
		String cmd="tracert";
		return getCmd(cmd);
	}

	public String cmd(String cmd) {
		return getCmd(cmd);
	}
	
	public List<String> snmpTest(SnmpTarget snmpTarget) throws IOException, InterruptedException{
		SnmpUtil util=SnmpUtil.getInstance();
		return util.SNMPTest(snmpTarget);
	}
	
	private String getCmd(String cmd) {

		Process process = null;
		InputStream in = null;
		InputStreamReader inReader = null;
		BufferedReader reader = null;
		StringBuffer msg = new StringBuffer();
		try {
			process = Runtime.getRuntime().exec(cmd);
			in = process.getInputStream();
			inReader = new InputStreamReader(in, "gbk");
			reader = new BufferedReader(inReader);

			msg.append("<br>");
			String s = "";
			while ((s = reader.readLine()) != null)
				if (s.length() != 0) {
					msg.append(s);
					msg.append("<br>");
				}
		} catch (Exception e) {

		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
			}
			try {
				if (inReader != null)
					inReader.close();
			} catch (Exception e) {
			}
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
			}
			try {
				if (process != null)
					process.destroy();
			} catch (Exception e) {
			}
		}

		return msg.toString();
	}

}
