package com.mainsteam.stm.knowledge.zip.api;

import java.io.InputStream;
import java.util.List;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.zip.bo.CloudyKnowledge;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: IKnowledgeZipApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 云端和本地知识库使用sqlite库文件存储，
 * 			内部存储知识类型、知识故障类型关联关系和知识数据
 * </li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public interface IKnowledgeZipApi {
	
	/**
	 * <pre>
	 * 导入云端知识
	 * 返回成功导入的知识条数
	 * 发生导入错误返回-1
	 * </pre>
	 * @return
	 */
	int importCloudyZip(InputStream in, ILoginUser user);
	
	/**
	 * <pre>
	 * 导出本地知识库
	 * </pre>
	 * @return
	 */
	InputStream exprotLocalZip();

	/**
	 * @param knowledge
	 * @author	ziwen
	 * @date	2019年11月10日
	 */
	void save(CloudyKnowledge knowledge);
	
	/**
	 * 通过关键字查询匹配知识信息
	 * @param keyword
	 * @return
	 * @author	ziwen
	 * @date	2019年11月24日
	 */
	List<KnowledgeBo> searchKnowledge(String keyword, Page<Byte, Byte> page);
}


