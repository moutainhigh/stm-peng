package com.mainsteam.stm.fileTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.github.xsonorg.XSON;

public class FileTransferSender{
	private static final Log logger = LogFactory.getLog(FileTransferSender.class);

	private LogicClient logicClient;
	private LocaleNodeService localNodeService;
	
	public final void setLogicClient(LogicClient logicClient) {
		this.logicClient = logicClient;
	}
	
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}
	
	public void sendFile(String filePath,String fileName,LogicConnection logicConnection) throws IOException{
		File file=new File(filePath);
		if(!file.exists()){
			throw new FileNotFoundException("can't find filePath:"+filePath);
		}
		
		FileInputStream fin=new FileInputStream(file);
		
		byte[] bts=new byte[1024*32]; 
		int len=0;
		long from=0;
		while((len=fin.read(bts))>0){

			if(logger.isDebugEnabled())
				 logger.debug("start transfer data["+from+","+len+"]");
			
			InnerTransferData data=new InnerTransferData();
			data.setCharset("utf-8");
			data.setFileName(fileName);
			data.setPoint(from);
			
			if(len==bts.length){
				data.setData(bts);
				data.setFinish(false);
			}else{
				byte[] nbts=new byte[len]; 
				System.arraycopy(bts, 0, nbts, 0, len);
				data.setData(nbts);
				data.setFinish(true);
			}
			doSend(data,logicConnection);
			
			if(logger.isDebugEnabled())
				logger.debug("finish transfer data["+from+","+len+"]");
			from+=len;
		}
		fin.close();
	}


	private int doSend(InnerTransferData datas,LogicConnection tcpConnection) {
		if (tcpConnection.isValid()) {
			byte[] data = XSON.write(datas);
			int length = data.length;

			byte[] lengths = toByte(length);
			
			OutputStream out = tcpConnection.getOutputStream();
			try {
				out.write(lengths);
				out.write(data);
				out.flush();
				return 1;
			} catch (IOException e) {
				if (logger.isErrorEnabled())
					logger.error("doSend error:"+e.getMessage(), e);
				return -1;
			}
		} else {
			if (logger.isErrorEnabled())
				logger.error("doSend error: tcpConnection is not valid!");
		}
		return 0;
	}
	
//	@Override
	public void sendFileToAll(String filePath,String fileName){
		try {
			NodeTable nodeTable=localNodeService.getLocalNodeTable();
			NodeGroup curGroup=localNodeService.getCurrentNodeGroup();
			List<NodeGroup> groupList=nodeTable.getGroups();
			
			for(NodeGroup group:groupList){
				if(curGroup.getId()==group.getId()){
					continue;
				}
				List<Node> nodes=group.getNodes();
				 for(Node node:nodes){
					 if(logger.isInfoEnabled()){
						 logger.info("start transfer to node["+node.getName()+","+node.getIp()+","+node.getPort()+"] file:"+fileName);
					 }
					 try {
						 LogicConnection tcpConnection = this.logicClient.createConection(node.getIp(),node.getPort(), LogicAppEnum.FILE_TRANSFER_TCP);

						 sendFile(filePath, fileName, tcpConnection);
						 if(logger.isInfoEnabled())
							 logger.info("finish transfer to node["+node.getName()+","+node.getIp()+","+node.getPort()+"] file:"+fileName);
					 } catch (Exception e) {
						 if(logger.isErrorEnabled())
							 logger.error("can't connect the node["+node.getIp()+":"+node.getPort()+"],the Exception:"+e.getMessage(),e);
					}
				 }
				
			}
		} catch (Exception e) {
			 if(logger.isErrorEnabled())
				 logger.error("can't find the nodeTable,the Exception:"+e.getMessage(),e);
		}
		
	}
	
	
	private static byte[] toByte(int x) {
		byte[] bts=new byte[4];
		bts[0]=(byte) (x);
		bts[1]=(byte) (x>> 8);
		bts[2]=(byte) (x>> 16);
		bts[3]=(byte) (x>> 24);
		
		return bts;
	}
}
