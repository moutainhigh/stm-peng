package com.mainsteam.stm.system.scriptmanage.bo;


public enum ScriptManageTypeEnum {
	//快照
	SNAPSHOT(1),
	
	//告警恢复
	RECOVER(2);
	
	// 成员变量  
	private int scriptCode;  
    
	private ScriptManageTypeEnum(int scriptCode) {
		this.scriptCode = scriptCode;
	}
	
	public static ScriptManageTypeEnum getByScriptCode(int scriptCode){
    	
		ScriptManageTypeEnum[] scriptManageEnums=values();
    	for(ScriptManageTypeEnum scriptManageEnum:scriptManageEnums){
    		if(scriptCode == scriptManageEnum.getScriptCode()){
    			return scriptManageEnum;
    		}
    	}
    	return null;
    }

	public int getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(int scriptCode) {
		this.scriptCode = scriptCode;
	}
	
}
