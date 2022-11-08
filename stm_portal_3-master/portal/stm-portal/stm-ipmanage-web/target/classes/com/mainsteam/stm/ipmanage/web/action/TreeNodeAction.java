package com.mainsteam.stm.ipmanage.web.action;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ipmanage.api.TreeNodeService;
import com.mainsteam.stm.ipmanage.bo.TreeNode;
import com.mainsteam.stm.platform.web.action.BaseAction;

@Controller
@RequestMapping({ "/portal/ipmanage/treeNode" })
public class TreeNodeAction extends BaseAction{
	Logger logger=Logger.getLogger(TreeNodeAction.class);
	@Resource
	private TreeNodeService treeNodeService;
	@RequestMapping("/getTreeNodes")
	@ResponseBody
	public JSONObject getTreeNodes(Integer id){
		return toSuccess(treeNodeService.getTreeNodes(id));
	}
	@RequestMapping("/insertTreeNode")
	@ResponseBody
	public JSONObject insertTreeNode(TreeNode treeNode){
		
		return toSuccess(treeNodeService.insertTreeNode(treeNode));
	}
	@RequestMapping("/updateTreeNode")
	@ResponseBody
	public JSONObject updateTreeNode(TreeNode treeNode){
		if(treeNode==null){
			logger.error("treeNode is null");
		}else {
			logger.error("id"+treeNode.getId());
			logger.error(treeNode.getHas_child()+"\t" +treeNode.getHas_subnet());
		}
		return toSuccess(treeNodeService.updateTreeNode(treeNode));
	}
	@RequestMapping("/deleteTreeNode")
	@ResponseBody
	public JSONObject deleteTreeNode(Integer id){
		return toSuccess(treeNodeService.deleteTreeNode(id));
	}
}
