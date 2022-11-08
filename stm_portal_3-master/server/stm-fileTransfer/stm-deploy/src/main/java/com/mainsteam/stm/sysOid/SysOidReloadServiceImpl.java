package com.mainsteam.stm.sysOid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.caplib.dict.CaplibAPIErrorCode;
import com.mainsteam.stm.caplib.dict.CaplibAPIResult;
import com.mainsteam.stm.deploy.dao.DeployRecordDAO;
import com.mainsteam.stm.deploy.obj.DeployRecord;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.sysOid.obj.ModuleBo;

public class SysOidReloadServiceImpl implements SysOidReloadService {
	private static final Log logger =LogFactory.getLog(SysOidReloadServiceImpl.class);
	
	private OCRPCClient client;
	private LocaleNodeService localNodeService;
	private DeployRecordDAO deployRecordDAO;
	
	private ExecutorService threadExecutor =new ThreadPoolExecutor(2,500,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>(),new ThreadFactory() {
		private int counter = 0;
		@Override
		public Thread newThread(Runnable runnable) {
			Thread t = new Thread(runnable,"SysOidReloadThead-"+ counter++);
			if (t.isDaemon())
				t.setDaemon(false);
			return t;
		}
	});

	
	public void setDeployRecordDAO(DeployRecordDAO deployRecordDAO) {
		this.deployRecordDAO = deployRecordDAO;
	}
	public void setClient(OCRPCClient client) {
		this.client = client;
	}
	public void setLocalNodeService(LocaleNodeService localNodeService) {
		this.localNodeService = localNodeService;
	}
	
	@Override
	public List<DeployRecord> getDeployRecordBySourceID(String sourceID){
		return deployRecordDAO.getDeployRecordBySourceID(sourceID);
	}
	
	@Override
	public List<DeployRecord> sysOidReload(final ModuleBo moduleBo) {
		List<DeployRecord> rstList=null;
		
		logger.info("sourceid:"+moduleBo.getResourceId());
		try {
			NodeTable nodeTable=localNodeService.getLocalNodeTable();
			List<NodeGroup> groupList=nodeTable.getGroups();
			
			List<Future<DeployRecord>> callRstList=new ArrayList<>(groupList.size());
			for(NodeGroup group:groupList){
				System.out.println("group:"+group.getId());
				List<Node> nodes=group.getNodes();
				for(final Node node:nodes){
					final int nodeID=node.getId();
					
					Future<DeployRecord> fs=threadExecutor.submit(new Callable<DeployRecord>() {
						@Override public DeployRecord call() throws Exception {
							
							DeployRecord record=new DeployRecord();
							record.setSourceID(moduleBo.getResourceId());
							record.setRetryNum(0);
							record.setNodeID(nodeID);
							record.setNodeFun(node.getFunc());
							
							sync(node,moduleBo,record);
							//不成功情况;
							if(!record.getResult() && record.getResultCode()==null){
								sync(node,moduleBo,record);
								record.setRetryNum(1);
							}
							deployRecordDAO.save(record);
							return record;
						}
					});
					callRstList.add(fs);
				}
			}
			rstList=new ArrayList<>(callRstList.size());
			for (Future<DeployRecord> fs : callRstList) {  
				if (fs.isDone()) {  
					 while(!fs.isDone()){
						 Thread.sleep(300);
					 }
				} 
				 rstList.add(fs.get());
			 }
		} catch (Exception e) {
			 if(logger.isErrorEnabled())
				 logger.error("can't find the nodeTable,the Exception:"+e.getMessage(),e);
		}
		return rstList;
	}
	
	private DeployRecord sync(Node node,ModuleBo moduleBo,DeployRecord record){
		try{
			SysOidDeployServiceMBean deploy=client.getRemoteSerivce(node, SysOidDeployServiceMBean.class);;
			CaplibAPIResult  rst=deploy.SysOidReload(moduleBo);
			record.setResult(CaplibAPIErrorCode.OK==rst.getErrorCode());
			record.setResultCode(rst.getErrorCode()!=null?rst.getErrorCode().name():null);
			logger.info("sync to node["+node.getIp()+",nodePort:"+node.getPort()+"] success! the code:"+rst.getErrorCode());
		}catch(Exception e){
			logger.info("sync to nodeIP:"+node.getIp()+",nodePort:"+node.getPort()+"  fail!");
			record.setResult(false);
			if(logger.isErrorEnabled())
				logger.error(e.getMessage(),e);
		 }
		record.setFileName(moduleBo.getSysOid());
		record.setDeployTime(new Date());
		return record;
	}

}
