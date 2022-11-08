package com.mainsteam.stm.knowledge.cloudy.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.knowledge.cloudy.bo.CKnowledgeStaBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * <li>文件名称: ICloudyKnowledgeApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public interface ICloudyKnowledgeApi {
	/**
	* @Title: cloudyKnowledgeSta
	* @Description: 查询云端知识库统计列表
	* @param page
	* @return  List<CKnowledgeStaBo>
	* @throws
	*/
	List<CKnowledgeStaBo> cloudyKnowledgeSta(Page<CKnowledgeStaBo, CKnowledgeStaBo> page);
	
	/**
	* @Title: getCloudyUpdateTimeAndCount
	* @Description: 获取云端知识最新更新时间和云端知识总数
	* @return  List<Map<String,String>>
	* @throws
	*/
	Map<String, String> getCloudyUpdateTimeAndCount();
}


