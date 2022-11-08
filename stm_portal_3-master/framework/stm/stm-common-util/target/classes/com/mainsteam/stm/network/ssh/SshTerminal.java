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
import org.apache.sshd.client.future.ConnectFuture;
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
public class SshTerminal {
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
	public SshTerminal() {
		client = SshClient.setUpDefaultClient();
		client.start();
	}
	public boolean login(String ip,String userName,String password) throws InterruptedException, IOException{
		return login(ip, DEFAULT_SSH_PORT, userName, password);
	}
	
	public boolean login(String ip,int port,String userName,String password) throws InterruptedException, IOException{
		closeSession();
		ConnectFuture contentFuture = client.connect(userName,ip,port);
		contentFuture.verify(60000);
		session = contentFuture.getSession();
		session.addPasswordIdentity(password);
		AuthFuture future = session.auth();
		future.verify(60000);
		isLogin = future.isSuccess();
		return isLogin;
	}
	
	public void runCMD(String...scripts) throws Exception{
		if(!isLogin) throw new Exception("not login!");
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
		channel = session.createShellChannel();
		channel.setIn(new NoCloseInputStream(input));
		open();
	}
	
	public void runCMD(String cmd) throws Exception{
		if(!isLogin) throw new Exception("not login!");
		closeChannel();
		channel = session.createExecChannel(cmd);
		open();
	}
	private void open() throws IOException{
		//session will close,so need to new
		output = new ByteArrayOutputStream();
		channel.setOut(new NoCloseOutputStream(output));
		channel.setErr(new NoCloseOutputStream(output));
		channel.open();
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
}
