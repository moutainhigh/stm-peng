package com.mainsteam.stm.home.screen.api;

import java.util.List;

import com.mainsteam.stm.home.screen.bo.Biz;

/**
 * <li>文件名称: IScreenApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月20日
 * @author   ziwenwen
 */
public interface IScreenApi {
	/**
	 * <pre>
	 * 根据用户id获取用户要展示的业务对象集合
	 * </pre>
	 * @param userId
	 * @return
	 */
	List<Biz> getBizs(Long userId);
	
	/**
	 * <pre>
	 * 保存用户大屏设置
	 * </pre>
	 * @param uBizRels
	 * @return
	 */
	int saveBizs(Long userId,List<Biz> uBizRels);
	
	int updateBiz(Biz ubr);
	
	Biz getBizByID(Long id);
}


