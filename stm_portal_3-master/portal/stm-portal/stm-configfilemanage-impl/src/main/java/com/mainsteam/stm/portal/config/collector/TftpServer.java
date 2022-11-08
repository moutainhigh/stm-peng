package com.mainsteam.stm.portal.config.collector;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.mainsteam.stm.network.tftp.TFTPServer;
/**
 * 
 * <li>文件名称: TftpServer.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   liupeng
 */
public class TftpServer {
	private static Logger logger = Logger.getLogger(TftpServer.class.getName());
	public static final String TFTP_DIRECTORY = "../tftp_root";
	private TFTPServer server;
	/**
	 * 启动TFTPServer
	 */
	public void start(){
		File file = new File(TFTP_DIRECTORY);
		if(!file.exists()) file.mkdir();
		try {
			server = new TFTPServer(file, file, TFTPServer.ServerMode.GET_AND_PUT);
			server.setSocketTimeout(2000);
			logger.info("TFTPServer启动成功！");
		} catch (IOException e) {
			logger.error("TFTPServer启动异常！", e);
		}
	}
	/**
	 * 停止TFTPServer
	 */
	public void destory(){
		if(server != null) server.shutdown();
	}
}
