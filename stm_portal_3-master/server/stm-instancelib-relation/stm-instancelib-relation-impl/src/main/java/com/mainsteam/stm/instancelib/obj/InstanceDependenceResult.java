package com.mainsteam.stm.instancelib.obj;

import java.util.ArrayList;
import java.util.List;


public class InstanceDependenceResult{

	private List<RelationLink> links = new ArrayList<>();
	
	private List<RelationNode> nodes = new ArrayList<>();

	public List<RelationLink> getLinks() {
		return links;
	}

	public List<RelationNode> getNodes() {
		return nodes;
	}

	public void setLinks(List<RelationLink> links) {
		this.links = links;
	}

	public void setNodes(List<RelationNode> nodes) {
		this.nodes = nodes;
	}
	
	
	
}
