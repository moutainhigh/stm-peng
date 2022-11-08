package com.mainsteam.stm.network.ssh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.AuthFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.io.NoCloseInputStream;
import org.apache.sshd.common.util.io.NoCloseOutputStream;
/**
 * 
 * <li>文件名称: SshTerminal.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月25日
 * @author   liupeng
 */
public class SshTestTerminal {
	public static final int DEFAULT_SSH_PORT = 22;
	public static final String ENTER = "\r";
	public static final String EXIT = "exit";
	private SshClient client;
	private ClientSession session;
	private ClientChannel channel;
	private ByteArrayInputStream input;
	private ByteArrayOutputStream output;
	private StringBuffer remoteInfo = new StringBuffer();
	private boolean isLogin = false;
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
	
	public SshTestTerminal() {
		client = SshClient.setUpDefaultClient();
		client.start();
	}
	public boolean login(String ip,String userName,String password){
		try {
			return login(ip, DEFAULT_SSH_PORT, userName, password);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return errorStop;
	}
	
	public boolean login(String ip,int port,String userName,String password) throws InterruptedException{
		closeSession();
		try {
			session = client.connect(userName, new InetSocketAddress(ip, port)).getSession();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.addPasswordIdentity(password);
		AuthFuture future;
		try {
			future = session.auth();
			isLogin = future.isSuccess();
		} catch (IOException e) {
			e.printStackTrace();
			setErrorStop(true);
			setErrorMsg(errorMsg.append(ERROR_CONN));
		}
		return isLogin;
	}
	
	public void runCMD(String...scripts){
		if(!isLogin) return;
		StringBuffer buffer = new StringBuffer();
		for(String cmd : scripts){
			buffer.append(cmd.trim()).append(ENTER);
		}
		/**
		 * createShellChannel() create a blocking channel ,use exit release
		 */
		buffer.append(EXIT).append(ENTER);
		closeChannel();
		input = new ByteArrayInputStream(buffer.toString().getBytes());
		try {
			channel = session.createShellChannel();
		} catch (IOException e) {
			e.printStackTrace();
			setErrorStop(true);
			setErrorMsg(errorMsg.append(EXEC_CMD).append(ERROR_EXCEPTION));
		}
		channel.setIn(new NoCloseInputStream(input));
		open();
	}
	
	public void runCMD(String cmd){
		if(!isLogin) return;
		closeChannel();
		try {
			channel = session.createExecChannel(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		open();
	}
	private void open(){
		//session will close,so need to new
		output = new ByteArrayOutputStream();
		channel.setOut(new NoCloseOutputStream(output));
		channel.setErr(new NoCloseOutputStream(output));
		try {
			channel.open();
		} catch (IOException e) {
			e.printStackTrace();
			setErrorStop(true);
			setErrorMsg(errorMsg.append(EXEC_CMD).append(ERROR_EXCEPTION));
		}
		List<ClientChannelEvent> event = new ArrayList<ClientChannelEvent>();
		event.add(ClientChannelEvent.CLOSED);
		channel.waitFor(event, 0l);
	}
	
	private void closeStream(){
		try{
			if(input != null){
				input.close();
				input = null;
			}
			if(output != null){
				remoteInfo.append(output.toString());
				output.close();
				output = null;
			}
		}catch(IOException e){
			e.printStackTrace();
			setErrorStop(true);
			setErrorMsg(errorMsg.append(ERROR_CLOSE));
		}
	}
	
	private void closeChannel(){
		closeStream();
		if(channel != null){
			channel.close(true);
			channel = null;
		}	
	}
	
	private void closeSession(){
		closeChannel();
		if (session != null) {
			session.close(true);
			session = null;
		}
	}
	
	public void destory() {
		closeSession();
		client.stop();
		client = null;
	}
	public String getRemoteInfo() throws Exception{
		//the end od if the output not close
		if(output != null) remoteInfo.append(output.toString());
		return remoteInfo.toString();
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
