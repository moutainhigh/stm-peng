package com.mainsteam.stm.lucene.bo;

/**
 * <li>文件名称: IndexPage.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月24日
 * @author   ziwenwen
 */
public class IndexPage {
	/**
	 * 总量
	 */
	private long total;
	
	/**
	 * 分页查询到的量
	 */
	private long[] keys;

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long[] getKeys() {
		return keys;
	}

	public void setKeys(long[] keys) {
		this.keys = keys;
	}
}


