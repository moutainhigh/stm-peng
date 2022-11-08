/**
 * 
 */
package com.mainsteam.stm.node.dao.pojo;

/**
 * @author ziw
 * 
 */
public class NodeGroupDO {

	private Integer id;
	private Integer parentId;
	private String name;
	private String func;
	private long updateTime;

	/**
	 * 级别
	 */
	private Integer level;

	/**
	 * 
	 */
	public NodeGroupDO() {
	}

	/**
	 * @return the id
	 */
	public final Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the parentId
	 */
	public final Integer getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public final void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the func
	 */
	public final String getFunc() {
		return func;
	}

	/**
	 * @param func
	 *            the func to set
	 */
	public final void setFunc(String func) {
		this.func = func;
	}

	/**
	 * @return the level
	 */
	public final Integer getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public final void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the updateTime
	 */
	public final long getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime the updateTime to set
	 */
	public final void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
}
