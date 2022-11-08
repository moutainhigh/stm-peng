package com.mainsteam.stm.lucene.bo;

import java.io.InputStream;
import java.io.Serializable;

/**
 * <li>文件名称: IndexObj.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 要索引的对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public class IndexObj implements Serializable{
	
	private static final long serialVersionUID = -5156028661228416635L;
	
	public IndexObj(Object obj) {
		this.obj=obj;
	}
	public IndexObj() {
	}
	private Object obj;
	private InputStream[] inps;
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public InputStream[] getInps() {
		return inps;
	}
	public void setInps(InputStream[] inps) {
		this.inps = inps;
	}
}


