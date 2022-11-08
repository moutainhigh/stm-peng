package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.List;

public class BizCanvasAutoBuildDataBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5006908666236435058L;

	private List<List<BizCanvasNodeBo>> nodes;

	private List<BizCanvasLinkBo> links;

	public List<List<BizCanvasNodeBo>> getNodes() {
		return nodes;
	}

	public void setNodes(List<List<BizCanvasNodeBo>> nodes) {
		this.nodes = nodes;
	}

	public List<BizCanvasLinkBo> getLinks() {
		return links;
	}

	public void setLinks(List<BizCanvasLinkBo> links) {
		this.links = links;
	}
	
	
	
}
