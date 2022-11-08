package com.mainsteam.stm.knowledge.local.api;

import java.util.List;

import com.mainsteam.stm.knowledge.bo.KnowledgeBo;
import com.mainsteam.stm.knowledge.service.bo.FaultBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: ILocalKnowledgeApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public interface ILocalKnowledgeApi {
	/**
	 * <pre>
	 * 添加一条知识 成功返回1 失败返回0
	 * </pre>
	 * @param kb
	 * @return
	 */
	KnowledgeBo addKnowledge(KnowledgeBo kb);
	
	/**
	 * <pre>
	 * 根据知识id，批量删除知识
	 * 返回成功删除的条数
	 * </pre>
	 * @param ids
	 * @return
	 */
	int batchDelKnowledge(long[] ids);

	/**
	 * <pre>
	 * 修改知识
	 * </pre>
	 * @param ids
	 * @return
	 */
	int updateKnowledge(KnowledgeBo kb);
	
	/**
	 * <pre>
	 * 根据知识ID 查询知识
	 * </pre>
	 * @param kb
	 * @return
	 */
	KnowledgeBo queryKnowledge(long id);
	
	
	/**
	* @Title: queryLocalKnowledgeBos
	* @Description: 分页查询本地知识
	* @return  List<KnowledgeBo>
	* @throws
	*/
	List<KnowledgeBo> queryLocalKnowledgeBos(Page<KnowledgeBo, KnowledgeBo> page,String keywords);
	
	
	/**
	* @Title: queryKnowledgeByType
	* @Description: 分析故障，返回分析知识结果列表
	* @param fault
	* @return  List<KnowledgeBo>
	* @throws
	*/
	List<KnowledgeBo> queryKnowledgeByType(FaultBo fault);
	
	/**
	* @Title: getFaultKnowledgeDownloadAddr
	* @Description: 获取云端故障知识下载地址
	* @return  String
	* @throws
	*/
	String getFaultKnowledgeDownloadAddr();
}


