package com.mainsteam.stm.system.um.service.web.vo;

import java.util.List;

public class NodeTreeVo extends NodeVo {

	private String text;
	private String iconCls;
	private List<NodeTreeVo> children;

	public List<NodeTreeVo> getChildren() {
		return children;
	}

	public void setChildren(List<NodeTreeVo> children) {
		this.children = children;
	}
	public String getText() {
		return this.getName();
	}

	public String getIconCls() {
		return iconCls;//this.isAlive()?"light-ico greenlight":"light-ico redlight";
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	
	
}
