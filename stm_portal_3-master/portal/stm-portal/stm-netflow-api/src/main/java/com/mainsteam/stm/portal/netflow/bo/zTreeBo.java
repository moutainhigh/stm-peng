package com.mainsteam.stm.portal.netflow.bo;

import java.util.List;

public class zTreeBo {

	/**节点ID*/
	private String id;
	/**节点父ID*/
	private String pId;
	/**节点名称*/
	private String name;
	/**是否为父节点*/
	private Boolean isParent = false;
	/**是否展开节点 */
	private Boolean open;
	/**是否选中节点*/
	private Boolean checked;
	/**是否不可选*/
	private Boolean nocheck;
	
	private List<zTreeBo> children;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPId() {
		return pId;
	}
	public void setPId(String pId) {
		this.pId = pId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public Boolean getChecked() {
		return checked;
	}
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	public Boolean getNocheck() {
		return nocheck;
	}
	public void setNocheck(Boolean nocheck) {
		this.nocheck = nocheck;
	}
	public List<zTreeBo> getChildren() {
		return children;
	}
	public void setChildren(List<zTreeBo> children) {
		this.children = children;
	}
	@Override
	public int hashCode() {
		
		int result = 17;
		result = 37 * result + id.hashCode();
		result = 37 * result + name.hashCode();
		
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof zTreeBo)){
			return false;
		}
		
		zTreeBo tree = (zTreeBo)obj;
		
		if(tree.getId().equals(id) && tree.getName().equals(name)){
			return true;
		}
		
		return false;
	}
	
	
	
}
