package com.mainsteam.stm.instancelib.service;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.Relation;

public interface RelationExtendService{

	public void insertRelationPOs(List<Relation> listRelations) throws InstancelibException; 
	public void removeRelationPOByInstanceId(long instanceId) throws InstancelibException;
}
