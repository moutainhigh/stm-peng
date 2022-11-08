package com.mainsteam.stm.deploy;

import java.util.List;

import com.mainsteam.stm.deploy.obj.DeployRecord;
import com.mainsteam.stm.node.Node;


public interface FileSenderService {
	
	/** 推送全部节点
	 * @param filePath
	 * @param fileName
	 * @param sourceID
	 */
	void sendFile(String filePath,String fileName,String sourceID);
	
	/** 推送指定节点
	 * @param filePath
	 * @param fileName
	 * @param node
	 * @param sourceID
	 */
	boolean[] sendFile(String filePath,String fileName,List<Node> nodes,String sourceID);
	
	/** 推送指定节点
	 * @param filePath
	 * @param fileName
	 * @param node
	 * @param sourceID
	 */
	boolean sendFile(String filePath,String fileName,Node node,String sourceID);
	
	
	
	List<DeployRecord> getDeployRecordBySourceID(String sourceID);
}
