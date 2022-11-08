package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
import java.util.List;

public class BizCanvasDataBo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5006908666236435058L;
	
	private long id;

	private long bizId;
	
	private String attr;
	
	private List<BizCanvasNodeBo> nodes;
	
	private List<BizCanvasLinkBo> links;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBizId() {
		return bizId;
	}

	public void setBizId(long bizId) {
		this.bizId = bizId;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public List<BizCanvasNodeBo> getNodes() {
		return nodes;
	}

	public void setNodes(List<BizCanvasNodeBo> nodes) {
		this.nodes = nodes;
	}

	public List<BizCanvasLinkBo> getLinks() {
		return links;
	}

	public void setLinks(List<BizCanvasLinkBo> links) {
		this.links = links;
	}
	
	
	
}
