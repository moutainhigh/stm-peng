package com.mainsteam.stm.instancelib.obj;

import java.io.Serializable;
import java.util.List;

import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;

/**
 * 复合实例类
 * 
 */
public class CompositeInstance extends Instance implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5182812614147489703L;

	/**
	 * 资源实例列表
	 */
	private List<Instance> elements;
	/**
	 * 复合资源属性
	 */
	private List<CompositeProp> props;
	/**
	 * 复合实例关系
	 */
	private InstanceRelation instanceReatiom;
	
	private InstanceTypeEnum instanceType;

	public CompositeInstance(){
		instanceReatiom = new InstanceRelation();
	}
	
	public List<Instance> getElements() {
		return elements;
	}

	public void setElements(List<Instance> elements) {
		this.elements = elements;
	}

	public List<CompositeProp> getProps() {
		return props;
	}

	public void setProps(List<CompositeProp> props) {
		this.props = props;
	}

	public InstanceRelation getInstanceReatiom() {
		return instanceReatiom;
	}

	public void setInstanceReatiom(InstanceRelation instanceReatiom) {
		this.instanceReatiom = instanceReatiom;
	}

	public InstanceTypeEnum getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(InstanceTypeEnum instanceType) {
		this.instanceType = instanceType;
	}
	
}
