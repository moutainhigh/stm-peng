package com.mainsteam.stm.instancelib.obj;

import com.mainsteam.stm.instancelib.ojbenum.LinkDataRelationSourceEnum;
import com.mainsteam.stm.instancelib.ojbenum.RelationshipTypeEnum;

public class InstanceDependence {

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

	public RelationshipTypeEnum getRelationType() {
		return relationType;
	}

	public boolean isUse() {
		return isUse;
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

	public void setRelationType(RelationshipTypeEnum relationType) {
		this.relationType = relationType;
	}

	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
	
	public LinkDataRelationSourceEnum getDataSource() {
		return dataSource;
	}

	public void setDataSource(LinkDataRelationSourceEnum dataSource) {
		this.dataSource = dataSource;
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
	
	private long relationId;
	
	private long sourceResource;
	
	private long sourceChildResource;
	
	private long targetResource;
	
	private long targetChildResource;
	
	private String sourceResourceType;
	
	private String targetResourceType;
	
	private RelationshipTypeEnum relationType;

	private boolean isUse;
	
	private LinkDataRelationSourceEnum dataSource;
	
	private long compositeId;

}
