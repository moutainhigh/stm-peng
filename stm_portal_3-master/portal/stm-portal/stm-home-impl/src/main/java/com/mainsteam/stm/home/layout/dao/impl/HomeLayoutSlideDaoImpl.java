package com.mainsteam.stm.home.layout.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutSlideDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: HomeLayoutSlideDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:08:41
 * @author   dengfuwei
 */
public class HomeLayoutSlideDaoImpl extends BaseDao<HomeLayoutSlideBo> implements IHomeLayoutSlideDao {

	public HomeLayoutSlideDaoImpl(SqlSessionTemplate session) {
		super(session, IHomeLayoutSlideDao.class.getName());
	}

	@Override
	public List<HomeLayoutSlideBo> getByUserId(long userId) {
		return select("getByUserId", userId);
	}

	@Override
	public int insert(HomeLayoutSlideBo homeLayoutSlideBo) {
		return insert("insert", homeLayoutSlideBo);
	}

	@Override
	public int deleteByUserId(long userId) {
		return del("deleteByUserId", userId);
	}

	@Override
	public int deleteByLyoutId(long id) {
	return	del("deleteByLyoutId", id);
		
	}

}
