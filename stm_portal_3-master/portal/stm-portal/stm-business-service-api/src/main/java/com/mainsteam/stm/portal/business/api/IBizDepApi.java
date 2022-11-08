package com.mainsteam.stm.portal.business.api;


import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizDepBo;

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
public interface IBizDepApi {
	/**
	 * 新增业务单位(业务服务)
	 * 
	 * @param bizDepBo
	 * @return
	 */
	BizDepBo insert(BizDepBo bizDepBo) throws Exception;
	/**
	 * 获取所有业务单位
	 * @param type 类型(0:业务单位;1:业务服务)
	 * @return
	 */
	List<BizDepBo> getList(Integer type) throws Exception;
	/**
	 * 通过业务单位或者业务服务id查询生成拓扑图的所有关系数据
	 * @param id 业务单位或者业务服务id
	 * @param type 0：业务单位；1：业务服务
	 * @return
	 * @throws Exception
	 */
	Object getAllRelationsByIdAndType(Long id,Integer type) throws Exception;
	/**
	 * 删除业务单位(业务服务)
	 * @param id
	 * @param type
	 * @return
	 */
	int del(long id,int type) throws Exception;
	/**
	 * 通过ids获取对应业务单位(业务服务)
	 * @param ids ids
	 * @param resourceIds resourceIds
	 * @param picIds picIds
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	List getListByIds(List<Long> ids,List<Long> resourceIds,List<Long> picIds) throws Exception;
	/**
	 * 根据业务单位或者业务服务id查询当前记录对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	BizDepBo get(long id) throws Exception;
	/**
	 * 更新业务单位或者业务服务
	 * @param bo
	 * @return
	 */
	int update(BizDepBo bo); 
}
