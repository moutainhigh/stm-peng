package com.mainsteam.stm.home.layout.dao;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.vo.HomeLayoutVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: IHomeLayoutDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   上午10:35:14
 * @author   dengfuwei
 */
public interface IHomeLayoutDao {

	void selectByPage(Page<HomeLayoutBo, HomeLayoutVo> page);
	/**
	 * 查询
	 * @param homeLayoutBo
	 * @return
	 */
	List<HomeLayoutBo> get(HomeLayoutBo homeLayoutBo);
	List<HomeLayoutBo> getTempsById(HomeLayoutVo layoutVo);
	/**
	 * 查询返回一条
	 * @param homeLayoutBo
	 * @return
	 */
	HomeLayoutBo getOne(HomeLayoutBo homeLayoutBo);
	
	/**
	 * 根据ID获取布局
	 * @param id
	 * @return
	 */
	HomeLayoutBo getById(long id);
	
	/**
	 * 获取指定用户的布局（不包含模板）
	 * @param userId
	 * @return
	 */
	List<HomeLayoutBo> getByUserId(long userId);
	
	/**
	 * 获取所有模板数据
	 * @return
	 */
	List<HomeLayoutBo> getTemplates();
	
	/**
	 * 获取指定用户的布局模板
	 * @param userId
	 * @return
	 */
	List<HomeLayoutBo> getTemplateByUserId(long userId);
	
	/**
	 * 新增布局
	 * @param homeLayoutBo
	 * @return
	 */
	int insert(HomeLayoutBo homeLayoutBo);
	
	/**
	 * 修改布局（where条件：ID、USER_ID）
	 * @param homeLayoutBo
	 * @return
	 */
	int updateLayout(HomeLayoutBo homeLayoutBo);
	
	/**
	 * 修改布局基本信息（不包含布局，where条件：ID、USER_ID）
	 * @param homeLayoutBo
	 * @return
	 */
	int updateBaseInfo(HomeLayoutBo homeLayoutBo);
	
	/**
	 * 修改页面布局基本信息
	 * @param homeLayoutBo 页面布局信息
	 * @param list 要删除的布局模块配置
	 */
	int updateLayoutInfo(HomeLayoutBo homeLayoutBo);
	
	/**
	 * 删除布局（where条件：ID、USER_ID）
	 * @param homeLayoutBo
	 * @return
	 */
	int delete(long id, long userId);
}
