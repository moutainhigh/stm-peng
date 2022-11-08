package com.mainsteam.stm.portal.config.web.vo;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.portal.config.bo.ConfigScriptBo;
import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;

public class ConfigScriptTreeVo {
	
	private String text;
	
	private long id;
	
	//1:文件夹,2:文件
	private int type;
	//true 全部结构显示 , false 只显示有文件的目录
	private boolean showType = true;
	
	private boolean ifHaveScript = false;
	
	private int level;
	
	private String oid;
	
	private static List<ConfigScriptBo> configScriptList;
	
	private static List<ConfigScriptDirectoryBo> configScriptDirectoryList;
	
	private List<ConfigScriptTreeVo> children;

	public ConfigScriptTreeVo(List<ConfigScriptBo> csbList , List<ConfigScriptDirectoryBo> csdList,boolean showType){
		configScriptList = csbList;
		configScriptDirectoryList = csdList;
		this.showType = showType;
		
		parseList();
	}
	
	private ConfigScriptTreeVo(){
	}
	
	
	
	private void parseList(){
		List<ConfigScriptTreeVo> cstvList = new ArrayList<ConfigScriptTreeVo>();
		
		for(ConfigScriptDirectoryBo csdb:configScriptDirectoryList){
			if(csdb.getLevelEnum().getIndex()==1){
				ConfigScriptTreeVo cstv = new ConfigScriptTreeVo();
				cstv.setType(1);
				cstv.setText(csdb.getName());
				cstv.setId(csdb.getId());
				cstv.setLevel(csdb.getDirLevel());
				
				List<ConfigScriptTreeVo> cstvListTemp = parseTree(cstv);
				if(null!=cstvListTemp && cstvListTemp.size()>0){
					cstv.setChildren(cstvListTemp);
				}
				
				cstvList.add(cstv);
			}
		}
		
		this.children = cstvList;
	}
	
	private List<ConfigScriptTreeVo> handle(List<ConfigScriptTreeVo> cstvList){
		if(null==cstvList || cstvList.size()==0) return cstvList;
		if(!showType){
			List<ConfigScriptTreeVo> temp = new ArrayList<ConfigScriptTreeVo>();
			for(ConfigScriptTreeVo cstv:cstvList){
				if(cstv.getType()==2){
					temp.add(cstv);
					continue;
				}
				if(cstv.getType()==1 && cstv.isIfHaveScript()){
					temp.add(cstv);
					continue;
				}
			}
			cstvList = temp;
		}
		return cstvList;
	}
	
	private void getConfigScriptBo(long id,List<ConfigScriptTreeVo> cstvList){
		List<ConfigScriptBo> csbList = configScriptList;
		
		for(ConfigScriptBo csb:csbList){
			if(csb.getDirectoryId()==id){
				ConfigScriptTreeVo cstv = new ConfigScriptTreeVo();
				cstv.setType(2);
				cstv.setId(csb.getId());
				cstv.setText(csb.getName());
				cstv.setOid(csb.getOid());
				cstvList.add(cstv);
			}
		}
	}
	
	private List<ConfigScriptTreeVo> parseTree(ConfigScriptTreeVo parentVo){
		List<ConfigScriptTreeVo> cstvList = new ArrayList<ConfigScriptTreeVo>();
			
			for(ConfigScriptDirectoryBo csdbIn:configScriptDirectoryList){
				if(parentVo.getId()==csdbIn.getParentId()){
					ConfigScriptTreeVo cstvTemp = new ConfigScriptTreeVo();
					cstvTemp.setType(1);
					cstvTemp.setText(csdbIn.getName());
					cstvTemp.setId(csdbIn.getId());
					cstvTemp.setLevel(csdbIn.getDirLevel());
					cstvList.add(cstvTemp);
					
					//迭代
					List<ConfigScriptTreeVo> cstvListTemp = parseTree(cstvTemp);
					if(null!=cstvListTemp && cstvListTemp.size()>0){
						cstvTemp.setChildren(cstvListTemp);
					}
				}
			}
			
			getConfigScriptBo(parentVo.getId(),cstvList);
			
			cstvList = handle(cstvList);
		return cstvList;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<ConfigScriptTreeVo> getChildren() {
		return children;
	}

	public void setChildren(List<ConfigScriptTreeVo> children) {
		this.children = children;
		this.ifHaveScript = true;
	}

	public boolean isShowType() {
		return showType;
	}

	public void setShowType(boolean showType) {
		this.showType = showType;
	}

	public boolean isIfHaveScript() {
		return ifHaveScript;
	}

	public void setIfHaveScript(boolean ifHaveScript) {
		this.ifHaveScript = ifHaveScript;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
}
