package com.mainsteam.stm.deploy.obj;

import java.util.Date;

import com.mainsteam.stm.node.NodeFunc;

public class DeployRecord {
	private String sourceID;
	private int nodeID;
	private NodeFunc nodeFun;
	private String fileName;
	private Date deployTime;
	private String resultCode;
	private boolean result;
	private int retryNum;
	private String content;
	
	public String getSourceID() {
		return sourceID;
	}
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}
	public int getNodeID() {
		return nodeID;
	}
	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Date getDeployTime() {
		return deployTime;
	}
	public void setDeployTime(Date deployTime) {
		this.deployTime = deployTime;
	}
	public boolean getResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public int getRetryNum() {
		return retryNum;
	}
	public void setRetryNum(int retryNum) {
		this.retryNum = retryNum;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public NodeFunc getNodeFun() {
		return nodeFun;
	}
	public void setNodeFun(NodeFunc nodeFun) {
		this.nodeFun = nodeFun;
	}
}
