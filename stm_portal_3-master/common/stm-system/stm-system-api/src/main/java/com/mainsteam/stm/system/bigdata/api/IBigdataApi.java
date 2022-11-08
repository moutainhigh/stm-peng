package com.mainsteam.stm.system.bigdata.api;

import com.mainsteam.stm.system.bigdata.bo.BigdataBo;

/**
 * <li>文件名称: IBigdataApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月26日
 * @author   ziwenwen
 */
public interface IBigdataApi {
	/**
	 * <pre>
	 * 获取配置
	 * </pre>
	 * @return
	 */
	BigdataBo get();
	
	/**
	 * <pre>
	 * 保存配置
	 * </pre>
	 * @param bigdata
	 */
	int save(BigdataBo bigdata);
}


