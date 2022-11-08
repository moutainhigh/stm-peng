package com.mainsteam.stm.home.layout.api;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeLayoutDomainBo;
import com.mainsteam.stm.system.um.domain.bo.Domain;

/**
 * <li>文件名称: HomeLayoutDomainApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:10:11
 * @author   dengfuwei
 */
public interface HomeLayoutDomainApi {

	/**
	 * 获取某布局的域授权关系
	 * @param homeLayoutDomainBo
	 * @return
	 */
	List<HomeLayoutDomainBo> getLayoutDomain(HomeLayoutDomainBo homeLayoutDomainBo);
	
	/**
	 * 保存布局的域授权关系（先删除原有关系，然后新增list）
	 * @param list
	 * @param layoutId
	 */
	void saveLayoutDomain(List<HomeLayoutDomainBo> list, long layoutId, long userId);
	void copyLayoutDomain(List<HomeLayoutDomainBo> list, long layoutId, long userId);
	
	
	/**
	 * 根据布局ID查询授权域
	 * @param layoutId,seachContent
	 * @return List<Domain>
	 */
	List<Domain> getDomainByLayoutId(long layoutId, String content);
	
	/**
	 * 根据布局ID查询未授权域
	 * @param layoutId,seachContent
	 * @return List<Domain>
	 */
	List<Domain> getUnDomainByLayoutId(long layoutId, String content);
}
