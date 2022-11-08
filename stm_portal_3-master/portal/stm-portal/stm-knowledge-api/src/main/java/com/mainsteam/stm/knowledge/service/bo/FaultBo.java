package com.mainsteam.stm.knowledge.service.bo;

import java.io.Serializable;

/**
 * <li>文件名称: FaultBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public class FaultBo implements Serializable{
	private static final long serialVersionUID = -43062975180286248L;

	/**
	 * 本地故障类型
	 */
	private String localType;
	
	/**
	 * 云端故障类型
	 */
	private String coludyType;
	
	/**
	 * 关键字
	 */
	private String keywords;

	

	public String getLocalType() {
		return localType;
	}

	public void setLocalType(String localType) {
		this.localType = localType;
	}

	public String getColudyType() {
		return coludyType;
	}

	public void setColudyType(String coludyType) {
		this.coludyType = coludyType;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
}


