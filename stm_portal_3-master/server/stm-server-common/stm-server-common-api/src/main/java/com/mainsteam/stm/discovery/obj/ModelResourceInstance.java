/**
 * 
 */
package com.mainsteam.stm.discovery.obj;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 模型资源实例对象
 * 
 * @author ziw
 * 
 */
public class ModelResourceInstance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5505340342276059526L;

	private long id;

	private String name;

	private String resourceId;

	private Map<String, String[]> propValues;

	private List<ModelResourceInstance> children;

	private String categoryId;
	
	private String childType;
	
	//private String 
	
	/**
	 * 
	 */
	public ModelResourceInstance() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public Map<String, String[]> getPropValues() {
		return propValues;
	}

	public void setPropValues(Map<String, String[]> propValues) {
		this.propValues = propValues;
	}

	public List<ModelResourceInstance> getChildren() {
		return children;
	}

	public void setChildren(List<ModelResourceInstance> children) {
		this.children = children;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getChildType() {
		return childType;
	}

	public void setChildType(String childType) {
		this.childType = childType;
	}
}
