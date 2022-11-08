package com.mainsteam.stm.knowledge.cloudy.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.knowledge.cloudy.bo.CKnowledgeStaBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


/**
 * <li>文件名称: ICloudyKnowledgeDao</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日 下午7:34:41
 * @author   俊峰
 */
public interface ICloudyKnowledgeDao {

	/**
	* @Title: pageSelect
	* @Description: 查询云端知识统计列表
	* @param page
	* @return  List<CKnowledgeStaBo>
	* @throws
	*/
	List<CKnowledgeStaBo> pageSelect(Page<CKnowledgeStaBo, CKnowledgeStaBo> page);
	
	int countCloudyKnowledgeTotal();
	
	Date cloudyKnowledgeUpdateTime();
}
