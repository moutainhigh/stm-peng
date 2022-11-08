package com.mainsteam.stm.knowledge.type.api;

import java.util.List;

import com.mainsteam.stm.caplib.dict.AccidentKMType;
import com.mainsteam.stm.knowledge.type.bo.AccidentMetricBo;
import com.mainsteam.stm.knowledge.type.bo.KnowledgeTypeBo;

/**
 * <li>文件名称: IKnowledgeTypeApi</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月14日 下午5:23:03
 * @author   俊峰
 */
public interface IKnowledgeTypeApi {

	
	/**
	* @Title: getAccidentKMTypes
	* @Description: 获取所有未组合好的知识分类
	* @return  List<AccidentKMType>
	* @throws
	*/
	List<AccidentKMType> getAccidentKMTypes();
	
	/**
	* @Title: getKnowledgeTypeByCode
	* @Description: 通过组合好的知识分类编码获取分类
	* @param code
	* @return  KnowledgeTypeBo
	* @throws
	*/
	KnowledgeTypeBo getKnowledgeTypeByCode(String code);
	
	/**
	* @Title: queryParentAccidentKMTypes
	* @Description: 获取父级知识分类类型
	* @return  List<AccidentKMType>
	* @throws
	*/
	List<AccidentKMType> queryParentAccidentKMTypes();
	
	/**
	* @Title: queryMetricAccidentKMTypeByParent
	* @Description: 通过父ID获取指标
	* @param parentId
	* @return  List<AccidentKMType>
	* @throws
	*/
	List<AccidentMetricBo> queryMetricAccidentKMTypeByParent(String parentId);
	
	/**
	* @Title: createKnowledgeTypeCodeByMetric
	* @Description: 通过指标ID和类型ID创建知识分类编码
	* @param metricId
	* @return  String
	* @throws
	*/
	String createKnowledgeTypeCodeByMetric(String metricId,String parentId);
}
