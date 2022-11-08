package com.mainsteam.stm.home.layout.dao;

import com.mainsteam.stm.home.layout.bo.HomeDefaultInterfaceBo;

/**
 * <li>文件名称: IHomeDefaultInterfaceDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017/6/13
 * @author   zhouw
 */
public interface IHomeDefaultInterfaceDao {
	/**
	 *	查询网络接口资源默认网络接口
	 * @param homeDefaultInterfaceBo
	 * @return
	 */
	HomeDefaultInterfaceBo getByUserIdAndResourceId(HomeDefaultInterfaceBo homeDefaultInterfaceBo);
	/**
	 * 修改网络接口资源默认接口
	 * @param homeDefaultInterfaceBo
	 * @return
	 */
	int updateByUserIdAndResourceId(HomeDefaultInterfaceBo homeDefaultInterfaceBo);
	/**
	 * 添加网络接口资源默认接口
	 * @param homeDefaultInterfaceBo
	 * @return
	 */
	int insert(HomeDefaultInterfaceBo homeDefaultInterfaceBo);
	/**
	 * 根据用户ID和资源ID删除默认接口
	 * @param homeDefaultInterfaceBo
	 * @return
	 */
	int delete(HomeDefaultInterfaceBo homeDefaultInterfaceBo);
	
}
