/**
 * 
 */
package com.mainsteam.stm.knowledge.capacity.dao;

import java.util.List;

import com.mainsteam.stm.knowledge.capacity.bo.CapacityKnowledgeBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: ICapacityKnowledgeDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月3日 上午11:54:25
 * @author   俊峰
 */
public interface ICapacityKnowledgeDao {

	/**
	* @Title: insert
	* @Description: 添加能力知识部署记录
	* @param knowledge
	* @return  int
	* @throws
	*/
	int insert(CapacityKnowledgeBo knowledge);
	
	/**
	* @Title: pageSelect
	* @Description: 分页查询能力知识部署记录
	* @param page
	* @return  List<CapacityKnowledgeBo>
	* @throws
	*/
	List<CapacityKnowledgeBo> pageSelect(Page<CapacityKnowledgeBo, CapacityKnowledgeBo> page);
	
	/**
	* @Title: get
	* @Description: 通过ID获取能力知识详细
	* @param id
	* @return  CapacityKnowledgeBo
	* @throws
	*/
	CapacityKnowledgeBo get(long id);
	
	int update(CapacityKnowledgeBo capacityKnowledgeBo);
}
