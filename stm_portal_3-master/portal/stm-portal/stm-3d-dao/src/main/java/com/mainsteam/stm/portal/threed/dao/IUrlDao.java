package com.mainsteam.stm.portal.threed.dao;

import com.mainsteam.stm.portal.threed.bo.UrlBo;


/**
 * 
 * <li>文件名称: IUrlDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 3D地址</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日
 * @author   liupeng
 */
public interface IUrlDao {
	/**
	 * 添加3D集成IP
	 * @param bo
	 * @return
	 */
	int add3DInfo(UrlBo bo);
	/**
	 * 更新ip或者状态
	 * @param bo
	 * @return
	 */
	int update(UrlBo bo);
	/**
	 * 查询第一条记录
	 * @return
	 */
	UrlBo get3DInfo();
}
