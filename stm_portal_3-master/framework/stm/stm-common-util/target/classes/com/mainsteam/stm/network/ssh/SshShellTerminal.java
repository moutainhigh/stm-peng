package com.mainsteam.stm.network.ssh;

import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SshShellTerminal {
	
	private static Logger logger = Logger.getLogger(SshShellTerminal.class);
	
	private String userName;
	private String passWord;
	private String ip;
	private final int port = 22;
	
	private ByteArrayOutputStream resultPipeOut = null;
	
	public SshShellTerminal(String userName,String passWord,String ip){
		this.userName = userName;
		this.passWord = passWord;
		this.ip = ip;
	}
	
	public void runCmd(String[] cmds){
		
		Session session = null;
		
		Channel channel = null;
		
		try {
			JSch jsch = new JSch();

			session = jsch.getSession(userName, ip, port);

			session.setPassword(passWord);

			UserInfo ui = new localUserInfo();

			session.setUserInfo(ui);

			session.connect(30000); // making a connection with timeout.

			channel = session.openChannel("shell");

			if(channel == null){
				logger.error("Jsch login " + ip + " fail!");
			}

			StringBuffer buffer = new StringBuffer();
			for(String cmd : cmds){
				buffer.append(cmd.trim()).append("\n");
			}
			/**
			 * createShellChannel() create a blocking channel ,use exit release
			 */
			PipedInputStream pipeIn = new PipedInputStream();  
	        PipedOutputStream pipeOut = new PipedOutputStream(pipeIn); 
	        resultPipeOut = new ByteArrayOutputStream();
	        pipeOut.write(buffer.toString().getBytes());
			channel.setInputStream(pipeIn);
			channel.setOutputStream(resultPipeOut);
			channel.connect();
	        Thread.sleep(cmds.length * 1000L);  
	        pipeOut.close();  
	        pipeIn.close();  
	        channel.disconnect(); 
	        session.disconnect();
	        
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	public String getRemoteInfo(){
		return resultPipeOut.toString();
	}
	
    public static class localUserInfo implements UserInfo {
        String passwd;

        public String getPassword() {
            return passwd;
        }

        public boolean promptYesNo(String str) {
            return true;
        }

        public String getPassphrase() {
            return null;
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            return true;
        }

        public void showMessage(String message) {
            
        }
    }
	
}
