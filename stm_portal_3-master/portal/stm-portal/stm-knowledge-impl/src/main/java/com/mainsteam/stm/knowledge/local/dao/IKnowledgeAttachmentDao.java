package com.mainsteam.stm.knowledge.local.dao;

import java.util.List;

import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;


/**
 * <li>文件名称: IKnowledgeAttachmentDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月16日 下午3:14:49
 * @author   俊峰
 */
public interface IKnowledgeAttachmentDao {

	/**
	* @Title: insert
	* @Description: 添加文件与附件关联关系
	* @param attachment
	* @return  int
	* @throws
	*/
	int insert(KnowledgeAttachmentBo attachment);
	
	/**
	* @Title: delete
	* @Description: 删除文件与附件关联关系
	* @param attachment
	* @return  int
	* @throws
	*/
	int delete(KnowledgeAttachmentBo attachment);
	
	/**
	* @Title: select
	* @Description: 通过知识查询附件
	* @param attachment
	* @return  List<KnowledgeAttachment>
	* @throws
	*/
	/**
	* @Title: select
	* @Description: 获取解决方案附件列表
	* @param resolveId
	* @return  List<KnowledgeAttachment>
	* @throws
	*/
	List<KnowledgeAttachmentBo> select(long resolveId);
	
}
