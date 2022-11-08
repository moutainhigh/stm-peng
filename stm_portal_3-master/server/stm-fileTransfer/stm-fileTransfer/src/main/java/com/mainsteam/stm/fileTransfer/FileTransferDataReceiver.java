/**
 * 
 */
package com.mainsteam.stm.fileTransfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.logic.connection.ServerConnection;
import com.mainsteam.stm.util.OSUtil;
import com.github.xsonorg.XSON;

/**
 * @author ziw
 * 
 */
public class FileTransferDataReceiver{

	private static final Log logger = LogFactory.getLog(FileTransferDataReceiver.class);
	private static final String FIEL_TMP_SUFFIX=".tmp";
	private String transferBaseDirectory=null;
	private LogicServer logicServer;

	public final void setLogicServer(LogicServer logicServer) {
		this.logicServer = logicServer;
	}
	
	/**
	 * 
	 */
	public void init() {
		logicServer.startServer(LogicAppEnum.FILE_TRANSFER_TCP);
		
		Thread acc=new Thread(new Runnable() {
			@Override public void run() {
				while(true){
					ServerConnection connection = logicServer.accept(LogicAppEnum.FILE_TRANSFER_TCP);
					accept(connection);
				}
			}
		});
		
		acc.start();
		
		String basePath=OSUtil.getEnv("transferPath", "../transfer");
		
		File baseDirectory=new File(basePath);
		if(!baseDirectory.exists()){
			if(logger.isDebugEnabled()){
				logger.debug("create base directory:"+basePath);
			}
			baseDirectory.mkdirs();
		}
		transferBaseDirectory=baseDirectory.getPath();
		if(logger.isDebugEnabled()){
			logger.debug("transfer start. the basePath:"+transferBaseDirectory);
		}
		
	}
	
	/**
	 * @param connection
	 */
	public void accept(ServerConnection connection){
	
		InputStream in = connection.getInputStream();
		byte[] lengthContent = new byte[4];
		int msgLength = 0;
		byte[] msgContent = null;
		/**开始读取数据 */
		int offset = 0;
		while (connection.isValid()) {
			try {
				if (msgContent != null) {
					/**读消息内容 */
					offset += in.read(msgContent, offset, msgLength - offset);
					if (offset == msgContent.length) {
						// convert to obj
						InnerTransferData datas = XSON.parse(msgContent);
						/**将读到的数据，放到队列	 */
						try{
							dispatch(datas);
						}catch(Exception e){
							 if(logger.isErrorEnabled())
								 logger.error("write data fail:"+e.getMessage(),e);
							 OutputStream out= connection.getOutputStream();
							 out.write("F".getBytes());
						}
						msgContent = null;
						offset = 0;
						msgLength = 0;
					}
				} else {
					/**读头信息，消息长度。 */
					for (int i = 0; i < lengthContent.length; i++) {
						lengthContent[i] = (byte) in.read();
					}
					msgLength = makeInt(lengthContent[3], lengthContent[2],lengthContent[1], lengthContent[0]);
					msgContent = new byte[msgLength];
				}
			} catch (Throwable e) {
				if (logger.isErrorEnabled()) {
					logger.error("run error:"+e.getMessage(), e);
				}
				msgContent = null;
				msgLength = 0;
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("run stop read data.");
		}
	}

	public void dispatch(InnerTransferData data) throws IOException {
		if(logger.isDebugEnabled()){
			logger.debug("transfer start.[file:"+data.getFileName()+",from:"+data.getPoint()+",len:"+data.getData().length+"]");
		}
		String transferPath=transferBaseDirectory+File.separator+data.getFileName();
		
		File transferFile=new File(transferPath);
		if(transferFile.exists()){
			throw new FileAlreadyExistsException("file["+transferFile.getAbsolutePath()+"]has exist,please check!");
		}
		
		File fransferFileTmp=new File(transferPath+FIEL_TMP_SUFFIX);
		
		if(!fransferFileTmp.exists()){
			if(logger.isDebugEnabled())
				logger.debug("create new file:"+transferPath);
			fransferFileTmp.createNewFile();
			FileReadAndWriter rdf=FileReadAndWriter.create(fransferFileTmp.getAbsolutePath());
			rdf.setLength(data.getSize());
		}
		//写入数据到文件
		FileReadAndWriter rdf=FileReadAndWriter.create(fransferFileTmp.getAbsolutePath());
		rdf.write(data.getPoint(),data.getData());
		
		//传输完成后的业务;
		if(data.isFinish()){
			rdf.close();
			fransferFileTmp.renameTo(new File(transferPath));
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("transfer finish...");
		}
	}

	private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
	}
}
