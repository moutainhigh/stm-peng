package com.mainsteam.stm.instancelib.obj;

import java.util.List;

public class BatchResourceInstanceResult {

	List<ResourceInstance> addResourceInstances;
	
	List<ResourceInstance> updateResourceInstances;

	public BatchResourceInstanceResult(){}
	
	public BatchResourceInstanceResult(List<ResourceInstance> addResourceInstances,List<ResourceInstance> updateResourceInstances){
		this.addResourceInstances = addResourceInstances;
		this.updateResourceInstances = updateResourceInstances;
	}
	
	public List<ResourceInstance> getAddResourceInstances() {
		return addResourceInstances;
	}

	public List<ResourceInstance> getUpdateResourceInstances() {
		return updateResourceInstances;
	}

	public void setAddResourceInstances(List<ResourceInstance> addResourceInstances) {
		this.addResourceInstances = addResourceInstances;
	}

	public void setUpdateResourceInstances(
			List<ResourceInstance> updateResourceInstances) {
		this.updateResourceInstances = updateResourceInstances;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(" addResourceInstnaces:").append(addResourceInstances);
		b.append(" updateResourceInstances:").append(updateResourceInstances);
		return b.toString();
	}
}
