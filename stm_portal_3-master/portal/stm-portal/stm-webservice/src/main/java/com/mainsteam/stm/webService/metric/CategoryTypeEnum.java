package com.mainsteam.stm.webService.metric;
 
import java.util.HashSet;
import java.util.Set;
 
public enum CategoryTypeEnum {
	NETWORKDEVICE("NetworkDevice"),SWITCH("Switch"),ROUTER("Router"),FIREWALL("Firewall");
	private String val;
	private CategoryTypeEnum parent;
	private Set<CategoryTypeEnum> child;
	
	CategoryTypeEnum(String val){
		this.val = val;
	}
	
	CategoryTypeEnum(String val,CategoryTypeEnum parent){
		this.val = val;
		this.parent = parent;
	}
	
	CategoryTypeEnum(String val,CategoryTypeEnum parent,Set<CategoryTypeEnum> child){
		this.val = val;
		this.parent = parent;
		this.child = child;
	}  
	 
	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public CategoryTypeEnum getParent() {
		doParent();
		return parent;
	}

	public void setParent(CategoryTypeEnum parent) {
		this.parent = parent;
	}

	public Set<CategoryTypeEnum> getChild() {
		doChild();
		return child;
	}
    
	public void setChild(Set<CategoryTypeEnum> child) {
		this.child = child;
	}

	public void doParent(){ 
		if(this.parent == null){
			System.out.println("parent is null");
			for(CategoryTypeEnum e : CategoryTypeEnum.values()){
				if(e != CategoryTypeEnum.NETWORKDEVICE && e.getVal() == this.getVal()){ 
					e.setParent(CategoryTypeEnum.NETWORKDEVICE);
				}
			}
		}
	}
	
	public void doChild(){
		if(this.val == CategoryTypeEnum.NETWORKDEVICE.getVal()){
			if(this.child == null || this.child.isEmpty()){
				Set<CategoryTypeEnum> list = new HashSet<CategoryTypeEnum>(3);
				for(CategoryTypeEnum e : CategoryTypeEnum.values()){
					if(e != CategoryTypeEnum.NETWORKDEVICE){ 
						list.add(e);
					}
				}
				this.setChild(list);
			}
			
		} 
		
	} 
}
