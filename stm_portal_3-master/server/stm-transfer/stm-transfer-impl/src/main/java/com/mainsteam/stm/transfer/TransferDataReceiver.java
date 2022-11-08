/**
 * 
 */
package com.mainsteam.stm.transfer;

import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.logic.connection.ServerConnection;
import com.mainsteam.stm.transfer.obj.InnerTransferData;
import com.github.xsonorg.XSON;

/**
 * @author ziw
 * 
 */
public class TransferDataReceiver implements Runnable {

	private static final Log logger = LogFactory.getLog(TransferDataReceiver.class);

	private ServerConnection connection;

	private MetricDataTransferServer server;

	private TransferDataDispatcher dispatcher;

	/**
	 * 
	 */
	public TransferDataReceiver(ServerConnection connection,MetricDataTransferServer server, TransferDataDispatcher dispatcher) {
		this.connection = connection;
		this.server = server;
		this.dispatcher = dispatcher;
	}

	@Override
	public void run() {
		InputStream in = this.connection.getInputStream();
		byte[] lengthContent = new byte[4];
		
		int msgLength=0;
		/** 开始读取数据 */
		while (this.server.isStarted() && this.connection.isValid()) {
			try {
				/**读头信息，消息长度。 */
				for (int i = 0; i < lengthContent.length; i++) {
					try{
						lengthContent[i] = (byte) in.read();
					}catch(Exception e){
						logger.error("rec header error:"+e.getMessage(), e);
					}
				}
				 msgLength = makeInt(lengthContent[3], lengthContent[2],lengthContent[1], lengthContent[0]);
				byte[] msgContent = new byte[msgLength];
				
				/**读消息内容 */
				int offset = 0;
				while (offset < msgContent.length) {
					offset += in.read(msgContent, offset, msgLength - offset);
				}
				// convert to obj
				InnerTransferData[] datas = XSON.parse(msgContent);
				/** 将读到的数据，放到队列*/
				dispatcher.dispatch(datas);
				msgContent = null;

			} catch (Throwable e) {
				logger.error("rec data["+msgLength+"] error:"+e.getMessage(), e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("run stop read data.");
		}
	}

	static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
		return (((b3) << 24) | ((b2 & 0xff) << 16) | ((b1 & 0xff) << 8) | ((b0 & 0xff)));
	}
}
