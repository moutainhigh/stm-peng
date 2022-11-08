package com.mainsteam.stm.instancelib.service;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ExtendProp;

public interface ExatendPropExtendService extends PropService{

	public void updateProp(final ExtendProp prop) throws InstancelibException;
	
	public void updateProps(final List<ExtendProp> props) throws InstancelibException;
	
}
