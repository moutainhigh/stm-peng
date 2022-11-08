package com.mainsteam.stm.instancelib.dao.pojo;

public class InstanceDependencePO {

	private long relationId;
	
	private long sourceResource;
	
	private long sourceChildResource;
	
	private long targetResource;
	
	private long targetChildResource;
	
	private String sourceResourceType;
	
	private String targetResourceType;
	
	private String relationType;

	private String isUse ="1";
	
	private String linkDataSource;
	
	private long compositeId;
	
	public long getRelationId() {
		return relationId;
	}

	public long getSourceResource() {
		return sourceResource;
	}

	public long getTargetResource() {
		return targetResource;
	}

	public String getSourceResourceType() {
		return sourceResourceType;
	}

	public String getTargetResourceType() {
		return targetResourceType;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationId(long relationId) {
		this.relationId = relationId;
	}

	public void setSourceResource(long sourceResource) {
		this.sourceResource = sourceResource;
	}

	public void setTargetResource(long targetResource) {
		this.targetResource = targetResource;
	}

	public void setSourceResourceType(String sourceResourceType) {
		this.sourceResourceType = sourceResourceType;
	}

	public void setTargetResourceType(String targetResourceType) {
		this.targetResourceType = targetResourceType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getIsUse() {
		return isUse;
	}

	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}

	public String getLinkDataSource() {
		return linkDataSource;
	}

	public void setLinkDataSource(String linkDataSource) {
		this.linkDataSource = linkDataSource;
	}

	public long getSourceChildResource() {
		return sourceChildResource;
	}

	public long getTargetChildResource() {
		return targetChildResource;
	}

	public void setSourceChildResource(long sourceChildResource) {
		this.sourceChildResource = sourceChildResource;
	}

	public void setTargetChildResource(long targetChildResource) {
		this.targetChildResource = targetChildResource;
	}

	public long getCompositeId() {
		return compositeId;
	}

	public void setCompositeId(long compositeId) {
		this.compositeId = compositeId;
	}

}
