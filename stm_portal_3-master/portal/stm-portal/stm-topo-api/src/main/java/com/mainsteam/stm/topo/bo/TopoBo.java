package com.mainsteam.stm.topo.bo;

import java.util.ArrayList;
import java.util.List;

public class TopoBo {
	//节点
	private List<NodeBo> nodes = new ArrayList<NodeBo>();
	//链路
	private List<LinkBo> links = new ArrayList<LinkBo>();
	
	public List<NodeBo> getNodes() {
		return nodes;
	}
	public void setNodes(List<NodeBo> nodes) {
		this.nodes = nodes;
	}
	public List<LinkBo> getLinks() {
		return links;
	}
	public void setLinks(List<LinkBo> links) {
		this.links = links;
	}
	
}
