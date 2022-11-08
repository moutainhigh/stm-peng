package com.mainsteam.stm.portal.config.util.jaxb;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Scripts")
public class Scripts implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4944849153819586189L;
	@XStreamImplicit(itemFieldName="Model")
	private List<Model> models;

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}
	
	
}
