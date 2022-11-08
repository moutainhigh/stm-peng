package com.mainsteam.stm.knowledge.capacity.web.action;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.knowledge.capacity.api.ICapacityKnowledgeApi;
import com.mainsteam.stm.knowledge.capacity.bo.CapacityKnowledgeBo;
import com.mainsteam.stm.knowledge.capacity.bo.DeployResultBo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: CapacityKnowledgeAction</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 能力知识Action</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月3日 上午11:36:20
 * @author   俊峰
 */
@Controller
@RequestMapping("knowledge/capacity")
public class CapacityKnowledgeAction extends BaseAction {

	@Resource(name="capacityKnowlwdgeApi")
	private ICapacityKnowledgeApi capacityKnowledgeApi;
	
	/**
	* @Title: deployment
	* @Description: 部署模型
	* @param capacity
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("deployment")
	public JSONObject deployment(CapacityKnowledgeBo capacity){
		capacity = capacityKnowledgeApi.deploy(capacity);
		return toSuccess(capacity);
	}
	
	
	/**
	* @Title: getCapacityKnowledgeInfo
	* @Description: 通过ID获取能力知识
	* @param id
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getCapacityKnowledgeInfo")
	public JSONObject getCapacityKnowledgeInfo(long id){
		return toSuccess(capacityKnowledgeApi.getCapacityKnowledgeById(id));
	}
	
	
	/**
	* @Title: queryDeployment
	* @Description: 通过能力知识ID部署结果
	* @param id
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryDeploymentResultById")
	public JSONObject queryDeploymentResultById(long id){
		Page<DeployResultBo,DeployResultBo> page = new Page<DeployResultBo,DeployResultBo>();
		page.setDatas(capacityKnowledgeApi.getDeployResults(id));
		return toSuccess(page);
	}
	
	
	/**
	* @Title: queryAllCapacityKnowledge
	* @Description: 分页获取能力知识列表
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryAllCapacityKnowledge")
	public JSONObject queryAllCapacityKnowledge(Page<CapacityKnowledgeBo, CapacityKnowledgeBo> page){
		if(page.getSort()!=null && page.getSort()!=""){
			page.setSort("name");
		}
		page.setDatas(capacityKnowledgeApi.query(page));
		return toSuccess(page);
	}
	
	/**
	* @Title: reDeployment
	* @Description: 重新部署模型
	* @param id
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("reDeployment")
	public JSONObject reDeployment(long id){
		CapacityKnowledgeBo capacityKnowledgeBo = new CapacityKnowledgeBo();
		capacityKnowledgeBo.setId(id);
		return toSuccess(capacityKnowledgeApi.reDeploy(capacityKnowledgeBo));
	}
	
	
	/**
	* @Title: getDownloadAddr
	* @Description: 获取云端知识下载地址
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getDownloadAddr")
	public JSONObject getDownloadAddr(){
		return toSuccess(capacityKnowledgeApi.getCapacityKnowledgeDownloadAddr());
	}
}
