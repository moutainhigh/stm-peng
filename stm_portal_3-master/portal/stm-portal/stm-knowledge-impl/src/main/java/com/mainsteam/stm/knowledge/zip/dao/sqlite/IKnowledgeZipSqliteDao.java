package com.mainsteam.stm.knowledge.zip.dao.sqlite;

import java.util.List;

import org.apache.ibatis.annotations.Param;

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
 * @date	2019年10月13日
 */
public interface IKnowledgeZipSqliteDao {

	/**
	 * 获取缓存数据库中的所有知识
	 * @return
	 * @author	ziwen
	 * @date	2019年10月14日
	 */
	public List<CloudyKnowledge> getAllCloudyKnowledge();

//	/**
//	 * 获取缓存数据库中的所有知识分类信息
//	 * @return
//	 * @author	ziwen
//	 * @date	2019年10月15日
//	 */
//	public List<CloudyKnowledgeType> getAllCloudyKnowledgeType();
//
//	/**
//	 * 通过云端知识ID获取其附件
//	 * @param id
//	 * @author	ziwen
//	 * @date	2019年10月15日
//	 */
//	public List<CloudyKnowledgeSourceResolveRel> getCloudyKnowledgeSourceResolveRel(String id);
//
//	/**
//	 * 获取所以知识分类和故障分类的关联关系
//	 * @return
//	 * @author	ziwen
//	 * @date	2019年10月15日
//	 */
//	public List<CloudyKnowledgeFaultRel> getAllCloudyKnowledgeFaultRel();

	/**
	 * 通过表名删除表数据
	 * @param name
	 * @author	ziwen
	 * @date	2019年10月16日
	 */
	public void deleteByTableName(@Param("tableName")String tableName);

	/**
	 * 将本地知识保存到缓数据库
	 * @param knowledge
	 * @author	ziwen
	 * @date	2019年10月16日
	 */
//	public void saveKnowledge(CloudyKnowledge knowledge);

	/**
	 * @param knowledge
	 * @author	ziwen
	 * @date	2019年11月10日
	 */
	public void save(CloudyKnowledge knowledge);

	/**
	 * @param knowledges
	 * @author	ziwen
	 * @date	2019年12月8日
	 */
	public void saveKnowledges(CloudyKnowledge knowledges);

	/**
	 * 保存知识附件到缓存数据库中
	 * @param attachments
	 * @author	ziwen
	 * @date	2019年10月16日
	 */
//	public void saveKnowledgeSourceResolveRels(
//			List<CloudyKnowledgeSourceResolveRel> attachments);
}
