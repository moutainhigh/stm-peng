package com.mainsteam.stm.portal.business.api;


import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizStatusSelfBo;

/**
 * <li>文件名称: IBizDepApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月23日
 * @author   caoyong
 */
public interface IBizStatusSelfApi {
	/**
	 * 
	 * @param bizStatusSelfBo
	 * @return
	 */
	int insert(BizStatusSelfBo bizStatusSelfBo);

	/**
	 * 根据业务应用id获取自定义状态规则记录集合
	 * @return
	 */
	List<BizStatusSelfBo> getByBizSerId(long bizSerId);
	/**
	 * 根据业务应用id删除自定义状态规则记录
	 * @param bizSerId
	 */
	void delByBizSerId (long bizSerId);
	/**
	 * 根据业务应用id和资源instanceIds删除自定义状态规则记录
	 * @param bizSerId
	 * @param instanceIds
	 */
	void delByBizSerIdAndInstanceIds (long bizSerId,Long[] instanceIds);
}
