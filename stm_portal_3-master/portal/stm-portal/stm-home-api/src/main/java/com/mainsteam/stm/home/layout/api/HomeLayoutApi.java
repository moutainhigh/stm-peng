package com.mainsteam.stm.home.layout.api;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutDefaultBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;
import com.mainsteam.stm.home.layout.vo.HomeLayoutVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: HomeLayoutApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:09:53
 * @author   dengfuwei
 */
public interface HomeLayoutApi {

	void selectByPage(Page<HomeLayoutBo, HomeLayoutVo> page);
	
	/**
	 * 获取用户首页 默认布局
	 * @param userId
	 * @return
	 */
	HomeLayoutBo getUserDefaultLayout(long userId);
	
	/**
	 * 获取用户默认首页配置
	 * @param userId
	 * @return
	 */
	HomeLayoutDefaultBo getLayoutDefault(long userId);
	
	/**
	 * 根据条件获取布局（返回一条数据）
	 * @param homeLayoutBo
	 * @return
	 */
	HomeLayoutBo getLayout(HomeLayoutBo homeLayoutBo);
	
	/**
	 * 获取用户所有页面（不包含模板）
	 * @param userId
	 * @return
	 */
	List<HomeLayoutBo> getLayout(long userId);
	
	/**
	 * 获取用户布局模板
	 * @param userId
	 * @return
	 */
	List<HomeLayoutBo> getTemplateLayout(long userId);
	List<HomeLayoutBo> getTempsById(HomeLayoutVo layoutVo);
	/**
	 * 获取布局
	 * @param id
	 * @return
	 */
	HomeLayoutBo getLayoutById(long id);
	
	/**
	 * 增加页面布局
	 * @param homeLayoutBo
	 */
	void addLayout(HomeLayoutBo homeLayoutBo, List<HomeLayoutModuleConfigBo> list, HomeLayoutDefaultBo homeLayoutDefaultBo);
	
	/**
	 * 增加布局模块
	 * @param homeLayoutModuleConfigBo
	 */
	void addLayoutModuleConfig(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo);
	
	/**
	 * 修改页面布局基本信息
	 * @param homeLayoutBo 页面布局信息
	 * @param list 要删除的布局模块配置
	 */
	void updateLayoutBaseInfo(HomeLayoutBo homeLayoutBo, HomeLayoutDefaultBo homeLayoutDefaultBo);
	
	
	//
	/**
	 * 修改页面布局基本信息
	 * @param homeLayoutBo 页面布局信息
	 * @param list 要删除的布局模块配置
	 */
	void updateLayoutInfo(HomeLayoutBo homeLayoutBo);
	
	/**
	 * 修改页面布局
	 * @param homeLayoutBo 页面布局信息
	 * @param list 要删除的布局模块配置
	 */
	void updateLayout(HomeLayoutBo homeLayoutBo, List<HomeLayoutModuleConfigBo> list);
	
	/**
	 * 获取指定布局的模块属性配置
	 * @param layoutId
	 * @return
	 */
	List<HomeLayoutModuleConfigBo> getLayoutModuleConfig(long layoutId);
	/**
	 * 获取所有的模板数据
	 * @return
	 */
	List<HomeLayoutBo> getTemplates();
	
	/**
	 * 修改模块属性配置
	 * @param homeLayoutModuleConfigBo
	 */
	void updateLayoutModuleConfig(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo);
	
	/**
	 * 修改首页默认布局
	 * @param homeLayoutDefaultBo
	 */
	void updateLayoutDefault(HomeLayoutDefaultBo homeLayoutDefaultBo);
	
	/**
	 * 删除布局
	 * @param layoutId
	 * @param userId
	 */
	void deleteLayout(long layoutId, long userId);
	/**
	 * 仅删除布局
	 * @param layoutId
	 * @param userId
	 */
	void deleteLayoutOnly(long layoutId, long userId);
	/**
	 * 增加模板布局
	 */
	void addTempLayout(HomeLayoutBo homeLayoutBo, List<HomeLayoutModuleConfigBo> list, HomeLayoutDefaultBo homeLayoutDefaultBo);
}
