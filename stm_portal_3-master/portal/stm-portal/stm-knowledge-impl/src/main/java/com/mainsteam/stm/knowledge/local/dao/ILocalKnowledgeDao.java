package com.mainsteam.stm.knowledge.local.dao;

import java.util.List;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: ILocalKnowledgeDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日 下午4:01:28
 * @author   俊峰
 */
public interface ILocalKnowledgeDao {

	/**
	* @Title: pageSelect
	* @Description: 分页查询本地知识列表
	* @param page
	* @return  List<KnowledgeBo>
	* @throws
	*/
	List<KnowledgeBo> pageSelect(Page<KnowledgeBo, KnowledgeBo> page);
	
	List<KnowledgeBo> getAll();
	
	/**
	* @Title: get
	* @Description: 通过ID获取知识详细信息
	* @param id
	* @return  KnowledgeBo
	* @throws
	*/
	KnowledgeBo get(long id);
	
	/**
	* @Title: insert
	* @Description: 新增知识
	* @param knowledge
	* @return  int
	* @throws
	*/
	int insert(KnowledgeBo knowledge);
	
	/**
	* @Title: update
	* @Description: 更新知识信息
	* @param knowledge
	* @return  int
	* @throws
	*/
	int update(KnowledgeBo knowledge);
	
	/**
	* @Title: batchDel
	* @Description: 通过知识ID批量删除知识
	* @param ids
	* @return  int
	* @throws
	*/
	int batchDel(long[] ids);
	
	
	/**
	* @Title: queryKnowledgeByType
	* @Description: 分析故障，返回分析知识结果列表
	* @param fault
	* @return  List<KnowledgeBo>
	* @throws
	*/
	List<KnowledgeBo> queryKnowledgeByType(FaultBo fault);
}
