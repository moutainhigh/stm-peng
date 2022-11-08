package com.mainsteam.stm.lucene.mapper;

import com.mainsteam.stm.util.IConstant;
/**
 * <li>文件名称: Doc.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class Doc {
	String obj;
	String keyField;
	String lang;
	String directory;
	int searchMaxDocs;
	Index[] indexes;
	String[] files;
	String[] queryFields;
	
	public Doc() {
	}
	
	Doc(String obj,String keyField,String lang,int maxFiles,String directory,int searchMaxDocs,Index[] indexes){
		this.obj=obj;
		this.keyField=keyField;
		this.lang=lang;
		this.directory=directory;
		this.indexes=indexes;
		this.searchMaxDocs=searchMaxDocs;
		
		int indexesLength=this.indexes.length;
		queryFields=new String[indexesLength+maxFiles];
		int i=0;
		for(;i<indexesLength;i++){
			queryFields[i]=this.indexes[i].getField();
		}
		
		files=new String[maxFiles];
		for(int j=0;j<maxFiles;j++){
			queryFields[i++]=files[j]=IConstant.str_file+j;
		}
	}
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Index[] getIndexes() {
		return indexes;
	}

	public String getLang() {
		return lang;
	}

	public String[] getFiles() {
		return files;
	}

	public String[] getQueryFields() {
		return queryFields;
	}

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public int getSearchMaxDocs() {
		return searchMaxDocs;
	}

	public void setSearchMaxDocs(int searchMaxDocs) {
		this.searchMaxDocs = searchMaxDocs;
	}
	
}


