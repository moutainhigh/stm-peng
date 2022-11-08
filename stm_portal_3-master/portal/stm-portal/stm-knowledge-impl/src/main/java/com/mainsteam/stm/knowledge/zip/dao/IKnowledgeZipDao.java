package com.mainsteam.stm.knowledge.zip.dao;

import java.util.List;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.bo.KnowledgeResolveBo;
import com.mainsteam.stm.knowledge.local.bo.KnowledgeAttachmentBo;
import com.mainsteam.stm.knowledge.zip.bo.CloudyKnowledge;

/**
 * <li>文件名称: com.mainsteam.stm.knowledge.zip.dao.IKnowledgeZipDao.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月14日
 */
public interface IKnowledgeZipDao {

	/**
	 * 保存远端知识到本地库
	 * @param knowledgeBo
	 * @author	ziwen
	 * @date	2019年10月14日
	 */
	public void saveKnowledgeBo(List<KnowledgeBo> knowledgeBos);

	/**
	 * 删除本地库中所有的云端知识
	 * @param knowledgeTypeCloudy
	 * @author	ziwen
	 * @date	2019年10月15日
	 */
	public void deleteLocalCloudyKnowledge(int type);

	/**
	 * 获取本地库云端知识附件的fileids
	 * @param knowledgeTypeCloudy
	 * @return
	 * @author	ziwen
	 * @date	2019年10月15日
	 */
	public List<Long> selectLocalCloudyKnowledgeAttachmentFileIds(
			int type);

	/**
	 * 删除本地库中云端知识附件信息
	 * @param knowledgeTypeCloudy
	 * @author	ziwen
	 * @date	2019年10月15日
	 */
	public void deleteLocalCloudyKnowledgeAttachment(int type);

	/**
	 * 将云端知识附件存储到本地库
	 * @param attachments
	 * @author	ziwen
	 * @date	2019年10月15日
	 */
	public void saveResolveAttachment(List<KnowledgeAttachmentBo> attachments);

	/**
	 * 删除本地库知识分类信息
	 * @author	ziwen
	 * @date	2019年10月15日
	 */
//	public void deleteLocalKnowledgeType();

	/**
	 * 保存本地库知识分类信息
	 * @param cloudyKnowledgeFaultTypes
	 * @author	ziwen
	 * @date	2019年10月15日
	 */
//	public void saveKnowledgeType(
//			List<CloudyKnowledgeType> cloudyKnowledgeTypes);

//	/**
//	 * 删除本地库知识分类和故障知识分类关联关系
//	 * @author	ziwen
//	 * @date	2019年10月15日
//	 */
//	public void deleteLocalKnowledgeFaultRel();

	/**
	 * 保存本地知识分类和故障类型关联关系
	 * @param cloudyKnowledgeFaultRel
	 * @author	ziwen
	 * @date	2019年10月15日
	 */
//	public void saveKnowledgeFaultRel(
//			List<CloudyKnowledgeFaultRel> cloudyKnowledgeFaultRel);

	/**
	 * 通过知识类型获取知识
	 * @return
	 * @author	ziwen
	 * @date	2019年10月16日
	 */
	public List<CloudyKnowledge> getKnowledgeResolve(int type);
	/**
	 * 获取所有知识
	 * @return
	 * @author	ziwen
	 * @date	2019年12月25日
	 */
	public List<KnowledgeBo> getAllKnowledges();

	/**
	 * 通过知识ID获取附件信息
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年10月16日
	 */
	public List<KnowledgeAttachmentBo> getKnowledgeAttachment(Long id);

	/**
	 * 通过原因内容获取知识
	 * @param sourceContent
	 * @author	ziwen
	 * @date	2019年11月10日
	 */
	public KnowledgeBo getKnowledgeBySourceContent(String sourceContent);

	/**
	 * 保存原因解决方案关联表
	 * @param resolve
	 * @author	ziwen
	 * @date	2019年11月10日
	 */
	public int saveKnowledgeResolveBo(List<KnowledgeResolveBo> resolve);

	/**
	 * 通过解决方案内容获取解决方案条数
	 * @param resolveContent
	 * @return
	 * @author	ziwen
	 * @date	2019年11月10日
	 */
	public int getResolveCountByContent(String resolveContent);

	/**
	 * 通过一组id获取知识
	 * @param knowledgeIds
	 * @return
	 * @author	ziwen
	 * @date	2019年11月24日
	 */
	public List<KnowledgeBo> getKnowledgeByIds(long[] knowledgeIds);

	/**
	 * @param knowledgeBos
	 * @author	ziwen
	 * @date	2019年12月22日
	 */
	public void saveLocalKnowledges(List<KnowledgeBo> knowledgeBos);

	/**
	 * 检测是否已经初始化
	 * @return
	 * @author	ziwen
	 * @date	2019年12月22日
	 */
//	public int checkIsInit();

//	/**
//	 * @param subList
//	 * @author	ziwen
//	 * @date	2019年11月10日
//	 */
//	public void saveResolveAttachment(List<KnowledgeAttachmentBo> subList);

}
