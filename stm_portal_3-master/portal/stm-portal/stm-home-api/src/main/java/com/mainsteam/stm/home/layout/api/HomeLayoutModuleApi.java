package com.mainsteam.stm.home.layout.api;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;


/**
 * <li>文件名称: HomeLayoutModuleApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:10:39
 * @author   dengfuwei
 */
public interface HomeLayoutModuleApi {

	List<HomeLayoutModuleBo> get();
	
	/**
	 * 查询模块信息
	 * @param layoutId
	 * @return
	 */
	HomeLayoutModuleConfigBo query(long id);
	
	/**
	 * 根据布局Id查询关联的所有模块信息
	 * @param layoutId
	 * @return
	 */
	List<HomeLayoutModuleConfigBo> queryByLayoutId(long layoutId);
	
	/**
	 * 增一个模块
	 * @param configure
	 * @return
	 */
	Long add(HomeLayoutModuleConfigBo configure);
	
	/**
	 * 更新模块
	 * @param configure
	 */
	void update(HomeLayoutModuleConfigBo configure);
	
	/**
	 * 删除模块
	 * @param id
	 */
	void delete(long id);
	
	/**
	 * 删除指定layout下的所有模块
	 * @param layoutId
	 */
	void deleteByLayoutId(long layoutId);
	
	int updateCurrLayoutId(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo);
	
}
