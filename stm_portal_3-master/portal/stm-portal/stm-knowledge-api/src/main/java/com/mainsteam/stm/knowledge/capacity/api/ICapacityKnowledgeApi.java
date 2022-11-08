package com.mainsteam.stm.knowledge.capacity.api;

import java.util.List;

import com.mainsteam.stm.knowledge.capacity.bo.CapacityKnowledgeBo;
import com.mainsteam.stm.knowledge.capacity.bo.DeployResultBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: ICapacityKnowledgeApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月2日
 * @author   ziwenwen
 */
public interface ICapacityKnowledgeApi {
	
	/**
	 * <pre>
	 * 部署一个新的模型
	 * 1、往数据库持久化数据
	 * 2、调用server接口进行部署
	 * 3、获取部署结果并返回
	 * </pre>
	 * @param capacityKnowledgeBo
	 * @return
	 */
	CapacityKnowledgeBo deploy(CapacityKnowledgeBo capacityKnowledgeBo);
	
	
	/**
	* @Title: getCapacityKnowledgeById
	* @Description: 通过ID获取能力知识
	* @param id
	* @return  CapacityKnowledgeBo
	* @throws
	*/
	CapacityKnowledgeBo getCapacityKnowledgeById(Long id);
	
	/**
	 * <pre>
	 * 根据模型知识id获取部署结果
	 * </pre>
	 * @param capacityKnowledgeId
	 * @return
	 */
	List<DeployResultBo> getDeployResults(Long capacityKnowledgeId);
	
	/**
	 * <pre>
	 * 将部署失败的组件重新进行部署
	 * 返回新的部署结果
	 * </pre>
	 * @param capacityKnowledgeBo
	 * @return
	 */
	boolean reDeploy(CapacityKnowledgeBo capacityKnowledgeBo);
	
	/**
	 * <pre>
	 * 分页查询部署记录
	 * </pre>
	 * @param page
	 * @return
	 */
	List<CapacityKnowledgeBo> query(Page<CapacityKnowledgeBo, CapacityKnowledgeBo> page);
	
	/**
	* @Title: getCapacityKnowledgeDownloadAddr
	* @Description: 获取云端能力知识下载地址
	* @return  String
	* @throws
	*/
	String getCapacityKnowledgeDownloadAddr();
}


