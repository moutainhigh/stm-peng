package com.mainsteam.stm.knowledge.type.web.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.AccidentKMType;
import com.mainsteam.stm.knowledge.type.api.IKnowledgeTypeApi;
import com.mainsteam.stm.knowledge.type.bo.AccidentMetricBo;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: KnowledgeTypeAction</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月14日 下午5:48:49
 * @author   俊峰
 */
@Controller
@RequestMapping("knowledge/type")
public class KnowledgeTypeAction extends BaseAction {

	@Autowired
	@Qualifier("knowledgeTypeApi")
	private IKnowledgeTypeApi knowledgeTypeApi;
	
	@RequestMapping("queryKnowledgeType")
	public JSONObject queryKnowledgeType(){
		List<AccidentKMType> accidentKMTypes = knowledgeTypeApi.getAccidentKMTypes();
		return toSuccess(accidentKMTypes);
	}
	
	/**
	* @Title: queryKnoeledgeParentType
	* @Description: 获取父级知识类型
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryKnoeledgeParentType")
	public JSONObject queryKnoeledgeParentType(){
		List<AccidentKMType> accidentKMTypes = knowledgeTypeApi.queryParentAccidentKMTypes();
		return toSuccess(accidentKMTypes);
	}
	
	
	/**
	* @Title: queryKnowledgeTypeByParent
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryKnowledgeTypeByParent")
	public JSONObject queryKnowledgeTypeByParent(String parentId){
		List<AccidentMetricBo> accidentKMTypes = knowledgeTypeApi.queryMetricAccidentKMTypeByParent(parentId);
		JSONObject result = toSuccess(accidentKMTypes);
		return result;
	}
	
}
