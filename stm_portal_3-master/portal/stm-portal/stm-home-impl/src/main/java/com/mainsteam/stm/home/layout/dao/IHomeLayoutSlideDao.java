package com.mainsteam.stm.home.layout.dao;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;

/**
 * <li>文件名称: IHomeLayoutSlideDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   上午10:43:06
 * @author   dengfuwei
 */
public interface IHomeLayoutSlideDao {

	/**
	 * 获取用户的轮播页面
	 * @param userId
	 * @return
	 */
	List<HomeLayoutSlideBo> getByUserId(long userId);
	
	/**
	 * 保存用户的轮播页面
	 * @param homeLayoutSlideBo
	 * @return
	 */
	int insert(HomeLayoutSlideBo homeLayoutSlideBo);
	
	/**
	 * 删除用户的轮播页面
	 * @param userId
	 * @return
	 */
	int deleteByUserId(long userId);
	
	/**
	 * 删除默认页面
	 * @param id
	 */
	int deleteByLyoutId(long id);
}
