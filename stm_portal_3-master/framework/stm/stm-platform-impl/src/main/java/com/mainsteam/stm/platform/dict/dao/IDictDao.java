package com.mainsteam.stm.platform.dict.dao;

import java.util.List;

import com.mainsteam.stm.platform.dict.bo.Dict;

/**
 * <li>文件名称: IDictDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月8日
 * @author   ziwenwen
 */
public interface IDictDao {
	List<Dict> getByType(String type);
}


