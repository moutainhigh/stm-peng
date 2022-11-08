package com.mainsteam.stm.system.license.bo;

import java.io.Serializable;

/**
 * <li>文件名称: Collector.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月8日
 * @author   ziwenwen
 */
public class Collector implements Serializable{
	
	private static final long serialVersionUID = -6147327867847012494L;

	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 授权数量
	 */
	private int count;

	public Collector(String name,int count){
		this.name = name;
		this.count = count;
	}
	
	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}
}


