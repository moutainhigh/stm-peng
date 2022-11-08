package com.mainsteam.stm.home.layout.dao;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleBo;

/**
 * <li>文件名称: IHomeLayoutModuleDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   上午10:42:38
 * @author   dengfuwei
 */
public interface IHomeLayoutModuleDao {

	/**
	 * 获取所有布局模块
	 * @return
	 */
	List<HomeLayoutModuleBo> get();
	
	/**
	 * 获取指定布局模块
	 * @param id
	 * @return
	 */
	HomeLayoutModuleBo getById(long id);
	
	/**
	 * 新增布局模块
	 * @param homeLayoutModuleBo
	 * @return
	 */
	int insert(HomeLayoutModuleBo homeLayoutModuleBo);
	
	/**
	 * 修改布局模块
	 * @param homeLayoutModuleBo
	 * @return
	 */
	int update(HomeLayoutModuleBo homeLayoutModuleBo);
	
	/**
	 * 删除指定布局模块
	 * @param id
	 * @return
	 */
	int delete(long id);
}
