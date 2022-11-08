package com.mainsteam.stm.knowledge.cloudy.bo;

import java.io.Serializable;

/**
 * <li>文件名称: CKnowledgeStaBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 云端知识统计bo对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public class CKnowledgeStaBo implements Serializable{
	private static final long serialVersionUID = -5916179836164022916L;
	
	/**
	 * 知识类型编码
	 */
	private String typeCode;
	
	
	/**
	 * 知识分类名称
	 */
	private String typeName;
	
	
	/**
	 * 知识统计数量
	 */
	private int count;


	public String getTypeCode() {
		return typeCode;
	}


	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}


	public String getTypeName() {
		return typeName;
	}


	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}


