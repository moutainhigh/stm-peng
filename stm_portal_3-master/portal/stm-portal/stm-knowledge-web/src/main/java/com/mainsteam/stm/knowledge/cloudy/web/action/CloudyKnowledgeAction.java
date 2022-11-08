/**
 * 
 */
package com.mainsteam.stm.knowledge.cloudy.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.knowledge.cloudy.api.ICloudyKnowledgeApi;
import com.mainsteam.stm.knowledge.cloudy.bo.CKnowledgeStaBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: CloudyKnowledgeAction</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月14日 上午9:22:02
 * @author   俊峰
 */
@Controller
@RequestMapping("knowledge/cloudy")
public class CloudyKnowledgeAction extends BaseAction {

	@Autowired
	@Qualifier("cloudyKnowledgeApi")
	private ICloudyKnowledgeApi cloudyKnowledgeApi;
	
	@RequestMapping("queryCloucyKnowledge")
	public JSONObject queryCloucyKnowledge(Page<CKnowledgeStaBo, CKnowledgeStaBo> page){
		cloudyKnowledgeApi.cloudyKnowledgeSta(page);
		return toSuccess(page);
	}
	
	@RequestMapping("getCloudyUpdateTimeAndTotal")
	public JSONObject getCloudyUpdateTimeAndTotal(){
		return toSuccess(cloudyKnowledgeApi.getCloudyUpdateTimeAndCount());
	}
}
