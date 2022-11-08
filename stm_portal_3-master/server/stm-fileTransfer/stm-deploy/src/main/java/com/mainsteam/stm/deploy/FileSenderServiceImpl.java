package com.mainsteam.stm.deploy;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.deploy.dao.DeployRecordDAO;
import com.mainsteam.stm.deploy.obj.DeployRecord;
import com.mainsteam.stm.fileTransfer.FileTransferSender;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.mainsteam.stm.rpc.client.OCRPCClient;

public class FileSenderServiceImpl implements FileSenderService{
	private static final Log logger =LogFactory.getLog(FileSenderServiceImpl.class);
	private ExecutorService threadPool = new ThreadPoolExecutor(4,20,6L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());  
	 
	
	private LogicClient logicClient;
	private LocaleNodeService localNodeService;
	private FileTransferSender fileTransferSender;
	private OCRPCClient client;
	
	private DeployRecordDAO deployRecordDAO;
	
	public void setDeployRecordDAO(DeployRecordDAO deployRecordDAO) {
		this.deployRecordDAO = deployRecordDAO;
	}
	public void setClient(OCRPCClient client) {
		this.client = client;
	}
	public final void setLogicClient(LogicClient logicClient) {
		this.logicClient = logicClient;
	}
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}
	public void setFileTransferSender(FileTransferSender fileTransferSender) {
		this.fileTransferSender = fileTransferSender;
	}
	
	@Override
	public List<DeployRecord> getDeployRecordBySourceID(String sourceID){
		return deployRecordDAO.getDeployRecordBySourceID(sourceID);
	}
	
	@Override
	public void sendFile(String filePath,String fileName,String sourceID){
		try {
			NodeTable nodeTable=localNodeService.getLocalNodeTable();
			List<NodeGroup> groupList=nodeTable.getGroups();
			
			for(NodeGroup group:groupList){
				List<Node> nodes=group.getNodes();
				 for(Node node:nodes){
					 sendFile(filePath,fileName,node,sourceID);
				 }
				
			}
		} catch (Exception e) {
			 if(logger.isErrorEnabled())
				 logger.error("can't find the nodeTable,the Exception:"+e.getMessage(),e);
		}
	}
	@Override
	public boolean sendFile(String filePath, String fileName, Node node,String sourceID) {
		 if(logger.isInfoEnabled())
			 logger.info("start transfer to node["+node.getName()+","+node.getIp()+","+node.getPort()+"] file:"+fileName);
		 DeployRecord record=new DeployRecord();
		 record.setNodeID(node.getId());
		 record.setSourceID(sourceID);
		 try {
			 LogicConnection tcpConnection = this.logicClient.createConection(node.getIp(),node.getPort(), LogicAppEnum.FILE_TRANSFER_TCP);

			 String fileNameRecord=fileName+"."+DateFormatUtils.format(new Date(), "yyyy-MM-dd-HH-mm-ss");
			 
			 fileTransferSender.sendFile(filePath, fileNameRecord, tcpConnection);
			 
			 ZipFileDeployServiceMBean ZipFileDeploy=client.getRemoteSerivce(node, ZipFileDeployServiceMBean.class);;
			 ZipFileDeploy.unZipFile(fileNameRecord, "utf-8");
			 
			 if(logger.isInfoEnabled())
				 logger.info("finish transfer to node["+node.getName()+","+node.getIp()+","+node.getPort()+"] file:"+fileName);
			 
			 record.setResult(true);
		 } catch (Exception e) {
			 if(logger.isErrorEnabled())
				 logger.error("send file["+filePath+"] to node["+node.getIp()+":"+node.getPort()+"] failed,the Exception:"+e.getMessage(),e);
			 
			 record.setResult(false);
		}
		
		 record.setFileName(fileName);
		 record.setDeployTime(new Date());
		 deployRecordDAO.save(record);
		 
		 return record.getResult();
	}
	
	
	@Override
	public boolean[] sendFile(final String filePath, final String fileName, List<Node> nodes,final String sourceID) {
		
		CompletionService<Boolean> cs = new ExecutorCompletionService<Boolean>(threadPool);  

		boolean[] rts=new boolean[nodes.size()];
		for(final Node node:nodes){
			  cs.submit(new Callable<Boolean>() {
				  public Boolean call() throws Exception { 
					return sendFile(filePath,fileName,node,sourceID);
				}
			});
		}
		
		for(int i=0;i<rts.length;i++){
			try {
				rts[i]=cs.take().get();
			} catch (Exception e) {
				 if(logger.isErrorEnabled())
					 logger.error(" failed,the Exception:"+e.getMessage(),e);
				 
			}
		}
		return rts;
	}
	

}
