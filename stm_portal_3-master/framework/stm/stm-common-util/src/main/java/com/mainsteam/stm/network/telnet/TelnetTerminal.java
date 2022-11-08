package com.mainsteam.stm.network.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.TelnetClient;
/**
 * 
 * <li>文件名称: TelnetTerminal.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月24日
 * @author   liupeng
 */
public class TelnetTerminal extends Thread {
	
	private static final Log logger = LogFactory.getLog(TelnetTerminal.class);
	
	public static final int DEFAULT_TELNET_PORT = 23;
	/**
	 * the TelnetClient of commons-net
	 */
	public TelnetClient telnet;
	/**
	 * the output of remote
	 */
	private StringBuffer remoteInfo = new StringBuffer();
	private InputStream in;
	private PrintStream out;

	/**
	 * use default port
	 * 
	 * @param ip
	 * @throws Exception 
	 */
	public TelnetTerminal(String ip) throws Exception {
		this(ip, DEFAULT_TELNET_PORT);
	}

	/**
	 * create a telnet terminate
	 * 
	 * @param ip
	 * @param port
	 */
	public TelnetTerminal(String ip, int port) throws Exception {
		logger.info("TelnetTerminal ....ip : " + ip + " ,port : " + port);
		telnet = new TelnetClient();			
		telnet.connect(ip, port);
		//to sure the thread must be stop and socket must be close
		telnet.setSoTimeout(20000);
		in = telnet.getInputStream();
		out = new PrintStream(telnet.getOutputStream());
		// open the thread of received
		start();
	}

	/**
	 * wirte to remote
	 * 
	 * @param command
	 */
	public void write(String command) {
		try {
			String[] remoteInfo = getRemoteInfo().split("\\r?\\n");
			if(remoteInfo[remoteInfo.length - 1].contains("...")){
				Thread.sleep(10000);
			}else{
				// the interval of execute command
				Thread.sleep(500);
			}
			// a command end with '\n\r'
			out.println(command);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * close connect
	 */
	public void disconnect() {
		try {
			telnet.disconnect();
			telnet = null;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public boolean isNeedUserName(){
		String[] remoteInfo = getRemoteInfo().split("\\r?\\n");
		if(remoteInfo[remoteInfo.length - 1].contains("assword")){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean isLoginSuccess(){
		String[] remoteInfo = getRemoteInfo().split("\\r?\\n");
		if(!remoteInfo[remoteInfo.length - 1].contains("#") &&
			!remoteInfo[remoteInfo.length - 1].contains(">") &&
			!remoteInfo[remoteInfo.length - 1].contains("/$")){
			
			//再判断是否回车后可以正常登陆，针对某些特殊设备
			if(!(remoteInfo[remoteInfo.length - 1].trim().endsWith(":"))){
				write("\n\r");
				
				String[] remoteInfo_2 = getRemoteInfo().split("\\r?\\n");
				if(!remoteInfo_2[remoteInfo_2.length - 1].contains("#") &&
						!remoteInfo_2[remoteInfo_2.length - 1].contains(">") &&
						!remoteInfo_2[remoteInfo_2.length - 1].contains("/$")){
					return false;
				}else{
					return true;
				}
			}
			
			return false;
			
		}else{
			return true;
		}
	}
	
	@Override
	public void run() {
		byte[] buff = new byte[1024];
		int len = 0;
		try {
			while ((len = in.read(buff)) != -1) {
				String str = new String(buff, 0, len);
				remoteInfo.append(str);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * exceute script
	 * 
	 * @param scripts
	 */
	public void runCMD(String... scripts) {
		for (String cmd : scripts) {
			if(cmd != null){
				write(cmd.trim());
			}
		}
	}

	/**
	 * get output info of the remote, but this method will block 1000ms
	 * 
	 * @return
	 */
	public String getRemoteInfo() {
		try {
			Thread.sleep(1000);
			return remoteInfo.toString();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
		return "";
	}
}
