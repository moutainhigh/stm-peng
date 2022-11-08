package com.mainsteam.stm.network.telnet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;
/**
 * <li>文件名称: TelnetTestTerminal.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月30日
 * @author   caoyong
 */
public class TelnetTestTerminal extends Thread {
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
	 * 发生错误中断
	 */
	private Boolean errorStop = false;
	/**
	 * 错误信息
	 */
	private StringBuffer errorMsg = new StringBuffer("");
	
	private static final String ERROR_CONN = "连接错误"; 
	private static final String EXEC_CMD = "执行命令"; 
	private static final String ERROR_EXCEPTION = "异常"; 
	private static final String ERROR_CLOSE = "关闭连接异常"; 

	/**
	 * use default port
	 * 
	 * @param ip
	 */
	public TelnetTestTerminal(String ip) {
		this(ip, DEFAULT_TELNET_PORT);
	}

	/**
	 * create a telnet terminate
	 * 
	 * @param ip
	 * @param port
	 */
	public TelnetTestTerminal(String ip, int port) {
		try {
			telnet = new TelnetClient();			
			telnet.connect(ip, port);
			//to sure the thread must be stop and socket must be close
			telnet.setSoTimeout(3000);
			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());
			// open the thread of received
			start();
		} catch (Exception e) {
			e.printStackTrace();
			setErrorStop(true);
			setErrorMsg(errorMsg.append(ERROR_CONN));
			disconnect();
		}
	}

	/**
	 * wirte to remote
	 * 
	 * @param command
	 */
	public void write(String command) {
		try {
			// the interval of execute command
			Thread.sleep(500);
			// a command end with '\n\r'
			out.println(command);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
			setErrorStop(true);
			setErrorMsg(errorMsg.append(EXEC_CMD).append(command).append(ERROR_EXCEPTION));
			disconnect();
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
			e.printStackTrace();
			setErrorStop(true);
			setErrorMsg(errorMsg.append(ERROR_CLOSE));
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

		}
	}

	/**
	 * exceute script
	 * 
	 * @param scripts
	 */
	public void runCMD(String... scripts) {
		for (String cmd : scripts) {
			write(cmd.trim());
			if(getErrorStop()){
				break;
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
			e.printStackTrace();
		}
		return "";
	}

	public Boolean getErrorStop() {
		return errorStop;
	}

	public void setErrorStop(Boolean errorStop) {
		this.errorStop = errorStop;
	}

	public StringBuffer getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(StringBuffer errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
