package com.mainsteam.stm.lucene.api;

import java.io.IOException;

/**
 * <li>文件名称: IIndexManageApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   ziwenwen
 */
public interface IIndexManageApi {
	
	/**
	 * <pre>
	 * 根据lucene配置文件中的doc元素中obj配置的名称获取lucene引擎
	 * </pre>
	 * @param doc_obj
	 * @return
	 */
	IIndexApi getIndexApi(String doc_obj) throws IOException ;
	
	/**
	 * <pre>
	 * 根据lucene配置文件中的doc元素中obj配置的名称及自定义autoKey获取lucene引擎
	 * </pre>
	 * @param doc_obj
	 * @return
	 */
	IIndexApi getIndexApi(String doc_obj,String autoKey) throws IOException ;
}


