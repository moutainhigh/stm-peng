package com.mainsteam.stm.home.layout.dao;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutDomainBo;
import com.mainsteam.stm.system.um.domain.bo.Domain;

/**
 * <li>文件名称: IHomeLayoutDomainDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   上午10:42:26
 * @author   dengfuwei
 */
public interface IHomeLayoutDomainDao {

	/**
	 * 查询
	 * @param homeLayoutDomainBo
	 * @return
	 */
	List<HomeLayoutDomainBo> get(HomeLayoutDomainBo homeLayoutDomainBo);
	
	/**
	 * 保存布局域授权关系
	 * @param homeLayoutDomainBo
	 * @return
	 */
	int insert(HomeLayoutDomainBo homeLayoutDomainBo);
	
	/**
	 * 删除布局授权关系
	 * @param homeLayoutDomainBo
	 * @return
	 */
	int delete(long userId, long layoutId);
	
	/**
	 * 根据布局ID查询授权域
	 * @param homeLayoutDomainBo
	 * @return
	 */
	List<Domain> getDomainByLayoutId(long layoutId, String content);
	
	/**
	 * 根据布局ID查询未授权域
	 * @param homeLayoutDomainBo
	 * @return
	 */
	List<Domain> getUnDomainByLayoutId(long layoutId, String content);
}
