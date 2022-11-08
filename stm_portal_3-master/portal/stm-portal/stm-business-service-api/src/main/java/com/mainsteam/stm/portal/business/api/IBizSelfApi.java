package com.mainsteam.stm.portal.business.api;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizSelfBo;
/**
 * 
 * <li>文件名称: IBizSelfApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月20日
 * @author   liupeng
 */
public interface IBizSelfApi {
	/**
	 * 新增自定义元素
	 * @param bizSelfBo
	 */
	void insert(BizSelfBo bizSelfBo);
	/**
	 * 获取添加的自定义元素
	 * @return
	 */
	List<BizSelfBo> getList();
	/**
	 * 根据ID删除自定义元素图片
	 * @param id
	 */
	void deleteById(long id);
}
