package com.mainsteam.stm.portal.threed.dao;

import java.util.List;

import com.mainsteam.stm.portal.threed.bo.ModelBo;

/**
 * 
 * <li>文件名称: IModelDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 设备型号与3D型号关联</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日
 * @author   liupeng
 */
public interface IModelDao {
	/**
	 * 修改型号关联关系
	 * @param bo
	 * @return
	 */
	int update(ModelBo bo);
	/**
	 * 查询所有的关联关系
	 * @return
	 */
	List<ModelBo> getAll();
}
