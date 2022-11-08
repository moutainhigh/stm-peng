package com.mainsteam.stm.lucene.mapper;

/**
 * <li>文件名称: Index.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class Index {
	String type;
	String field;
	boolean store;
	
	public Index(String field,String type,boolean store){
		this.field=field;
		this.type=type;
		this.store=store;
	}

	public String getType() {
		return type;
	}

	public String getField() {
		return field;
	}

	public boolean isStore() {
		return store;
	}
}


