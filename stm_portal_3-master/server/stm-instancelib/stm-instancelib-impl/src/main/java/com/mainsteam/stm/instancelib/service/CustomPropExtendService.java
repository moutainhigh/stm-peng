package com.mainsteam.stm.instancelib.service;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomProp;

public interface CustomPropExtendService extends PropService{

	public void updateProp(final CustomProp prop) throws InstancelibException;
	
	public void updateProps(final List<CustomProp> props) throws InstancelibException;
	
	
}
