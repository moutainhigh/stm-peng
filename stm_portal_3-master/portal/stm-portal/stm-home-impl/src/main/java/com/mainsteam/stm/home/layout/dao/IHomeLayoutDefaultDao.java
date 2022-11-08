package com.mainsteam.stm.home.layout.dao;

import com.mainsteam.stm.home.layout.bo.HomeLayoutDefaultBo;

/**
 * <li>文件名称: IHomeLayoutDefaultDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   上午10:42:06
 * @author   dengfuwei
 */
public interface IHomeLayoutDefaultDao {

	/**
	 * 获取指定用户的默认布局
	 * @param userId
	 * @return
	 */
	HomeLayoutDefaultBo getByUserId(long userId);
	
	/**
	 * 保存用户默认布局
	 * @param homeLayoutDefaultBo
	 * @return
	 */
	int insert(HomeLayoutDefaultBo homeLayoutDefaultBo);
	
	/**
	 * 修改用户默认布局
	 * @param homeLayoutDefaultBo
	 * @return
	 */
	int update(HomeLayoutDefaultBo homeLayoutDefaultBo);
	
	/**
	 * 删除用户默认布局
	 * @param userId
	 * @return
	 */
	int delete(long userId);
}
