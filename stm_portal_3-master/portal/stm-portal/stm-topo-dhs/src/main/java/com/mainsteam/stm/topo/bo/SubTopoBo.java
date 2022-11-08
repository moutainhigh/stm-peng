package com.mainsteam.stm.topo.bo;
/**
 * 子拓扑
 * @author 富强
 *
 */
public class SubTopoBo {
	private Long id;
	/**
	 * 子拓扑名称
	 */
	private String name;
	/**
	 * 父节点id
	 */
	private Long parentId;
	/**
	 * 原生的svg节点
	 */
	private String svgdom;
	/**
	 * svg的背景原地址
	 */
	private String bgsrc;
	/**
	 * 子拓扑属性
	 */
	private String attr;
	/**
	 * 子拓扑的节点id
	 */
	private Long[] children;
	private Long[] toAdd;
	private Long[] toDelete;
	private Long[] ids;
	private Boolean flag;
	
	public Long[] getToAdd() {
		return toAdd;
	}
	public void setToAdd(Long[] toAdd) {
		this.toAdd = toAdd;
	}
	public Long[] getToDelete() {
		return toDelete;
	}
	public void setToDelete(Long[] toDelete) {
		this.toDelete = toDelete;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getParentId() {
		if(parentId==null) return 0l;
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getSvgdom() {
		return svgdom;
	}
	public void setSvgdom(String svgdom) {
		this.svgdom = svgdom;
	}
	public String getBgsrc() {
		return bgsrc;
	}
	public void setBgsrc(String bgsrc) {
		this.bgsrc = bgsrc;
	}
	public Long[] getChildren() {
		return children;
	}
	public void setChildren(Long[] children) {
		this.children = children;
	}
	public Long[] getIds() {
		if(ids==null){
			return new Long[]{};
		}
		return ids;
	}
	public void setIds(Long[] ids) {
		this.ids = ids;
	}
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
}
