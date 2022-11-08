package com.mainsteam.stm.home.layout.dao;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;

/**
 * <li>文件名称: IHomeLayoutModuleConfigDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   上午10:42:51
 * @author   dengfuwei
 */
public interface IHomeLayoutModuleConfigDao {

	/**
	 * 获取指定模块配置
	 * @param id
	 * @return
	 */
	HomeLayoutModuleConfigBo getById(long id);
	
	/**
	 * 获取指定布局的所有模块配置
	 * @param layoutId
	 * @return
	 */
	List<HomeLayoutModuleConfigBo> getByLayoutId(long layoutId);
	
	/**
	 * 新增布局模块配置
	 * @param homeLayoutModuleConfigBo
	 * @return
	 */
	int insert(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo);
	
	/**
	 * 修改某布局模块的属性配置（where条件：ID、USER_ID、LAYOUT_ID）
	 * @param homeLayoutModuleConfigBo
	 * @return
	 */
	int updateProps(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo);
	
	/**
	 * 删除某布局模块的属性配置（where条件：ID、USER_ID、LAYOUT_ID）
	 * @param homeLayoutModuleConfigBo
	 * @return
	 */
	int delete(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo);
	
	int updateCurrLayoutId(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo);
}
