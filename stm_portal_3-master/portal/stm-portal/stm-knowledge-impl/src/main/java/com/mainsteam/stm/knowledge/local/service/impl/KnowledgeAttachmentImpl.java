/**
 * 
 */
package com.mainsteam.stm.knowledge.local.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.knowledge.local.api.IKnowledgeAttachmentApi;
import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.knowledge.local.dao.IKnowledgeAttachmentDao;
import com.mainsteam.stm.platform.file.service.IFileClientApi;

/**
 * <li>文件名称: KnowledgeAttachmentImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月16日 下午5:48:39
 * @author   俊峰
 */
@Service("knowledgeAttachmentApi")
public class KnowledgeAttachmentImpl implements IKnowledgeAttachmentApi {

	@Autowired
	@Qualifier("knowledgeAttachmentDao")
	private IKnowledgeAttachmentDao knowledgeAttachmentDao;

	@Autowired
	private IFileClientApi fileClient;
	
	@Override
	public int insertKnowledgeAttachent(KnowledgeAttachmentBo attachment) {
		return knowledgeAttachmentDao.insert(attachment);
	}

	@Override
	public int deleteKnowledgeAttachment(KnowledgeAttachmentBo attachment) {
		try {
			if(attachment!=null){
				if(attachment.getFileId()!=0){
					fileClient.deleteFile(attachment.getFileId());
				}
				knowledgeAttachmentDao.delete(attachment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}


	@Override
	public List<KnowledgeAttachmentBo> selectResolveAttachment(long resolveId) {
		return knowledgeAttachmentDao.select(resolveId);
	}
	
	

}
