package com.mainsteam.stm.knowledge.local.api;

import java.util.List;

import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;


/**
 * <li>文件名称: IKnowledgeAttachmentApi</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月16日 下午5:48:45
 * @author   俊峰
 */
public interface IKnowledgeAttachmentApi {
	/**
	* @Title: insert
	* @Description: 添加文件与附件关联关系
	* @param attachment
	* @return  int
	* @throws
	*/
	int insertKnowledgeAttachent(KnowledgeAttachmentBo attachment);
	
	/**
	* @Title: delete
	* @Description: 删除文件与附件关联关系
	* @param attachment
	* @return  int
	* @throws
	*/
	int deleteKnowledgeAttachment(KnowledgeAttachmentBo attachment);
	
	/**
	* @Title: select
	* @Description: 获取解决方案附件列表
	* @param resolveId
	* @return  List<KnowledgeAttachment>
	* @throws
	*/
	List<KnowledgeAttachmentBo> selectResolveAttachment(long resolveId);
	
}
