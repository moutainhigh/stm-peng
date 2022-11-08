package com.mainsteam.stm.portal.config.bo;

import java.io.Serializable;

public class ConfigScriptDirectoryBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4827313142435680400L;
	
	private long id;
	
	private ConfigScriptLevelEnum levelEnum;
	
	private int dirLevel;
	
	private String name;
	
	private long parentId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public ConfigScriptLevelEnum getLevelEnum() {
		return levelEnum;
	}

	public void setLevelEnum(ConfigScriptLevelEnum levelEnum) {
		this.levelEnum = levelEnum;
		this.dirLevel = levelEnum.getIndex();
	}

	public int getDirLevel() {
		return dirLevel;
	}

	public void setDirLevel(int dirLevel) {
		this.dirLevel = dirLevel;
		ConfigScriptLevelEnum levelEnumTemp = ConfigScriptLevelEnum.ONE;
		for(ConfigScriptLevelEnum csle:ConfigScriptLevelEnum.values()){
			if(dirLevel==csle.getIndex()){
				levelEnumTemp = csle;
			}
		}
		this.levelEnum = levelEnumTemp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
}
