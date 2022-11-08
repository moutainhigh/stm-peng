package com.mainsteam.stm.home.layout.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.home.layout.api.HomeLayoutSlideApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutSlideDao;

/**
 * <li>文件名称: HomeLayoutSlideImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午4:54:33
 * @author   dengfuwei
 */
public class HomeLayoutSlideImpl implements HomeLayoutSlideApi {
	
	@Resource(name="stm_home_homeLayoutSlideDao")
	private IHomeLayoutSlideDao homeLayoutSlideDao;

	@Override
	public List<HomeLayoutSlideBo> get(long userId) {
		return homeLayoutSlideDao.getByUserId(userId);
	}

	@Override
	public void save(long userId, List<HomeLayoutSlideBo> list) {
		homeLayoutSlideDao.deleteByUserId(userId);
		if(!CollectionUtils.isEmpty(list)){
			for(HomeLayoutSlideBo item : list){
				homeLayoutSlideDao.insert(item);
			}
		}
	}

	@Override
	public void deleteByLyoutId(long id) {
		 homeLayoutSlideDao.deleteByLyoutId(id);
		
	}

}
