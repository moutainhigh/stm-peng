package com.mainsteam.stm.portal.config.collector.mbean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

import com.mainsteam.stm.network.snmp.SnmpTerminal;
import com.mainsteam.stm.network.ssh.SSHShellSession;
import com.mainsteam.stm.network.ssh.SshShellTerminal;
import com.mainsteam.stm.network.ssh.SshTerminal;
import com.mainsteam.stm.network.ssh.SshTestTerminal;
import com.mainsteam.stm.network.telnet.TelnetTerminal;
import com.mainsteam.stm.network.telnet.TelnetTestTerminal;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.portal.config.collector.TftpServer;
import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigReq;
import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigRsp;

/**
 * 
 * <li>文件名称: ConfigBackup.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   liupeng
 */
public class ConfigBackup implements ConfigBackupMBean{
	private static Logger logger = Logger.getLogger(ConfigBackup.class.getName());
	private static final String ENTER = "\r\n";
	private LocaleNodeService localNodeService;


	@Override
	public ConfigRsp getBySsh(ConfigReq req) throws Exception{
		//执行脚本
		String[] cmds = getCmd(req);
		ConfigRsp rsp = new ConfigRsp();
		if(cmds.length == 1){

			SshTerminal ssh = new SshTerminal();
			boolean success = ssh.login(req.getIp(), req.getUserName(), req.getPassword());
			if(!success){
				throw new Exception("login failed");
			}
			ssh.runCMD(cmds[0]);
			rsp.setRemoteInfo(ssh.getRemoteInfo());
			ssh.destory();
			rsp.setFile(checkFile(req.getFileName(),true));
		}else{
			SSHShellSession ssh = new SSHShellSession(req.getUserName(), req.getPassword(), req.getIp());
//			SshShellTerminal ssh = new SshShellTerminal(req.getUserName(), req.getPassword(), req.getIp());
			ssh.runCmd(cmds);
			rsp.setRemoteInfo(ssh.getRemoteInfo());
			rsp.setFile(checkFile(req.getFileName(),true));
		}
		return rsp;
	}	

	@Override
	public ConfigRsp getByTelnet(ConfigReq req) throws Exception {
		ConfigRsp rsp = new ConfigRsp();
		TelnetTerminal telnet = new TelnetTerminal(req.getIp());

		//先判断是否需要登录
		if(!telnet.isLoginSuccess()){
			//伪登录(为了使脚本和ssh的保持一致,ssh自带登录功能)
			//判断登录是否需要用户名
			if(telnet.isNeedUserName()){
				telnet.runCMD(req.getUserName(),req.getPassword());
			}else{
				telnet.runCMD(req.getPassword());
			}
		}
		//执行脚本
		telnet.runCMD(getCmd(req));
		rsp.setRemoteInfo(telnet.getRemoteInfo());
		telnet.disconnect();
		rsp.setFile(checkFile(req.getFileName(),true));	
		return rsp;
	}

	@Override
	public String getBySnmp(String ip, int port, String community) throws Exception{
		SnmpTerminal terminal = new SnmpTerminal(ip, port);
		String response = terminal.sendPDU(community, new int[]{1,3,6,1,2,1,1,1,0});
		return response;
	}
	/**
	 * 替换占位符并生成批量脚本
	 * @param req
	 * @return
	 * @throws NodeException
	 */
	private String[] getCmd(ConfigReq req) throws NodeException{
		String tftpIp = localNodeService.getCurrentNode().getIp();
		//加时间戳的文件名,防止文件重名导致的获取配置文件失败
		req.setFileName(req.getFileName()+System.currentTimeMillis());
		String cmd = req.getCmd()
				.replace("${enableUserName}",req.getEnableUserName())
				.replace("${enablePassword}",req.getEnablePassword())
				.replace("${tftpIp}", tftpIp)
				.replace("${fileName}",req.getFileName())
				.replace("|", "| ");
		return cmd.split("\\|");
	}

	@Override
	public ConfigRsp getReBySsh(ConfigReq req) throws Exception{
		//执行脚本
		String[] cmds = getReCmd(req);
		ConfigRsp rsp = new ConfigRsp();
		if(cmds.length == 1){

			SshTerminal ssh = new SshTerminal();
			boolean success = ssh.login(req.getIp(), req.getUserName(), req.getPassword());
			if(!success){
				throw new Exception("login failed");
			}
			ssh.runCMD(cmds[0]);
			rsp.setRemoteInfo(ssh.getRemoteInfo());
			ssh.destory();
			deleteFile(req.getFileName());
		}else{
			SshShellTerminal ssh = new SshShellTerminal(req.getUserName(), req.getPassword(), req.getIp());
			ssh.runCmd(cmds);
			rsp.setRemoteInfo(ssh.getRemoteInfo());
			deleteFile(req.getFileName());
		}
		return rsp;
	}	

	@Override
	public ConfigRsp getReByTelnet(ConfigReq req) throws Exception {
		ConfigRsp rsp = new ConfigRsp();
		TelnetTerminal telnet = new TelnetTerminal(req.getIp());

		//先判断是否需要登录
		if(!telnet.isLoginSuccess()){
			//伪登录(为了使脚本和ssh的保持一致,ssh自带登录功能)
			//判断登录是否需要用户名
			if(telnet.isNeedUserName()){
				telnet.runCMD(req.getUserName(),req.getPassword());
			}else{
				telnet.runCMD(req.getPassword());
			}
		}

		//执行脚本
		telnet.runCMD(getReCmd(req));
		rsp.setRemoteInfo(telnet.getRemoteInfo());
		logger.error("telnet.getRemoteInfo():"+telnet.getRemoteInfo()+":");
		telnet.disconnect();
		deleteFile(req.getFileName());
		return rsp;
	}

	/**
	 * 恢复配置文件使用 不加时间戳
	 * @param req
	 * @return
	 * @throws NodeException
	 */
	private String[] getReCmd(ConfigReq req) throws NodeException{
		String tftpIp = localNodeService.getCurrentNode().getIp();
		String FileName = req.getFileName();
		String[] split = FileName.split("_");
		String newFileName = split[0];
		String cmd = req.getCmd()
				.replace("${enableUserName}",req.getEnableUserName())
				.replace("${enablePassword}",req.getEnablePassword())
				.replace("${tftpIp}", tftpIp)
				.replace("${fileName}",req.getFileName())
				.replace("${newFileName}", newFileName)
				.replace("|", "| ");
		logger.error("cmd : "+cmd);
		String[] cmds = cmd.split("\\|");
		for(int i = 0; i < cmds.length; i++){
			System.out.println(cmds[i]);
			logger.error("cmd.split():"+cmds[i]);
		}
		return cmd.split("\\|");
	}

	/**
	 * 检查文件是否存在且读取为String返回
	 * @param fileName
	 * @param del 是否读取过后删除
	 * @return
	 * @throws IOException
	 */
	private String checkFile(String fileName,boolean del) throws IOException{
		File file = new File(TftpServer.TFTP_DIRECTORY+"/"+fileName);
		int time = 0;
		//假如没检测到文件、线程休眠300ms重新检测、最多检测10次,最长检测时间为10*300ms
		while((!file.exists() || file.length() <= 0) & time++<40){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info(fileName + " file.exists() " + file.exists());
		logger.info(fileName + " file.isFile() " + file.isFile());
		if(file.exists() && file.isFile() && file.length() > 0){
			try {
				StringBuffer buffer = new StringBuffer();
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String str;
				while((str=reader.readLine())!=null){
					buffer.append(str).append(ENTER);
				}
				String result = null;
				if(!(buffer.toString().equals("")) && buffer != null && buffer.lastIndexOf(ENTER) != -1){
					//去除掉最后的换行符
					result = buffer.substring(0, buffer.lastIndexOf(ENTER));
				}else{
					result = buffer.toString();
				}
				reader.close();
				if(del) file.delete();
				return result;
			} catch (FileNotFoundException e) {
				logger.error(e);
				return "";
			}		
		}
		return "";
	}
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}

	@Override
	public String testByTelnet(ConfigReq req) throws Exception {
		String result = "";
		TelnetTestTerminal telnetTest = new TelnetTestTerminal(req.getIp());
		if(telnetTest.getErrorStop()){
			return telnetTest.getErrorMsg().toString();
		}
		telnetTest.runCMD(req.getUserName(),req.getPassword());
		if(telnetTest.getErrorStop()){
			return telnetTest.getErrorMsg().toString();
		}
		//执行脚本
		telnetTest.runCMD(getCmd(req));
		if(telnetTest.getErrorStop()){
			return telnetTest.getErrorMsg().toString();
		}
		telnetTest.disconnect();
		return result;
	}

	@Override
	public String testBySsh(ConfigReq req) throws Exception {
		String result = "";
		SshTestTerminal sshTest = new SshTestTerminal();
		boolean success = sshTest.login(req.getIp(), req.getUserName(), req.getPassword());
		if(!success) throw new Exception("login failed");
		if(sshTest.getErrorStop()) return sshTest.getErrorMsg().toString();
		//执行脚本
		sshTest.runCMD(getCmd(req));
		if(sshTest.getErrorStop()) return sshTest.getErrorMsg().toString();
		sshTest.destory();
		if(sshTest.getErrorStop()) return sshTest.getErrorMsg().toString();
		return result;
	}

	@Override
	public boolean checkLoginStatus(String ip, String userName, String password,
			String type) throws Exception {

		if(type.equals("1")){
			//ssh
			if(userName == null || userName.equals("") || password == null || password.equals("")){
				return false;
			}
			SshTerminal ssh = new SshTerminal();
			return ssh.login(ip, userName, password);
		}else{
			//telnet
			TelnetTerminal telnet = new TelnetTerminal(ip);
			if(password == null || password.equals("")){
				return telnet.isLoginSuccess();
			}else{
				if(userName == null || userName.equals("")){
					//无用户名登录
					telnet.runCMD(password);
				}else{
					//有用户名登录
					telnet.runCMD(userName,password);
				}
				return telnet.isLoginSuccess();
			}
		}
	}

	@Override	
	public void copyFile(File file, String fileName) { 
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			File newFile = new File(TftpServer.TFTP_DIRECTORY+"/"+fileName);
			if(!newFile.exists()){
				newFile.createNewFile(); 
			}
			InputStream inStream = new FileInputStream(file);
			FileOutputStream fs = new FileOutputStream(newFile); 
			byte[] buffer = new byte[1444];  
			while ( (byteread = inStream.read(buffer)) != -1) { 
				bytesum += byteread; 
				System.out.println(bytesum); 
				fs.write(buffer, 0, byteread); 
			} 
			inStream.close();
			fs.close();
		}  catch (Exception e) {  
			logger.error("复制文件出错",e);
		} 
	} 
	
	/**
	 * 删除 恢复配置 过程中产生的文件
	 * @param fileName
	 */
	private void deleteFile(String fileName) {
		File file = new File(TftpServer.TFTP_DIRECTORY+"/"+fileName);
		if(file.exists() && file.isFile() && file.length() > 0){
			file.delete();
		}
		if(!file.exists()){
			logger.info("删除文件成功!");
		}else{
			logger.error("删除文件失败!");
		}
	}
}
