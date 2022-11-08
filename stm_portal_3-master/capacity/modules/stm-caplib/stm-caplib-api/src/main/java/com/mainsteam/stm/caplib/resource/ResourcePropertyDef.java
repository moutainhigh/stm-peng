package com.mainsteam.stm.caplib.resource;

public class ResourcePropertyDef implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8531480884762275513L;
	private String id;
	private String name;
	private ResourceMetricDef resourceMetric;
	
	public ResourcePropertyDef(){
		
	}

	/**
	 * 获取id
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取名称
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取指标
	 * @return
	 */
	public ResourceMetricDef getResourceMetric() {
		return resourceMetric;
	}

	public void setResourceMetric(ResourceMetricDef resourceMetric) {
		this.resourceMetric = resourceMetric;
	}

	
}
