package com.mainsteam.stm.home.layout.api;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;

/**
 * <li>文件名称: HomeLayoutSlideApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:10:24
 * @author   dengfuwei
 */
public interface HomeLayoutSlideApi {

	/**
	 * 获取用户轮播页面
	 * @param userId
	 * @return
	 */
	List<HomeLayoutSlideBo> get(long userId);
	
	/**
	 * 保存用户轮播页面，先删除原有数据，然后新增
	 * @param userId
	 * @param list
	 */
	void save(long userId, List<HomeLayoutSlideBo> list);
	/**
	 * 删除默认页面
	 * @param id
	 */
	void deleteByLyoutId(long id);
}
