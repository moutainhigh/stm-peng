package com.mainsteam.stm.instancelib.service;

import java.util.List;

import com.mainsteam.stm.instancelib.obj.CompositeProp;

public interface CompositePropExtendService extends PropService{

	public void addProp(CompositeProp prop) throws Exception;
	
	public void addProps(List<CompositeProp> props) throws Exception;
	
}
