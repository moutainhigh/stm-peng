package com.mainsteam.stm.ipmanage.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.mainsteam.stm.ipmanage.api.TreeNodeService;
import com.mainsteam.stm.ipmanage.bo.TreeNode;
import com.mainsteam.stm.ipmanage.impl.dao.TreeNodeMapper;
import org.apache.log4j.Logger;

public class TreeNodeServiceImpl implements TreeNodeService {

	private  Logger logger = Logger.getLogger(TreeNodeServiceImpl.class);
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private TreeNodeMapper treeNodeMapper;
	
	public List<TreeNode> getTreeNodes(Integer id) {
		// TODO Auto-generated method stub
		try {
			return treeNodeMapper.getTreeNodes(id);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return null;
		}
		
	}

	
	public int insertTreeNode(TreeNode treeNode) {
		// TODO Auto-generated method stub
		try {
			return treeNodeMapper.insert(treeNode);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
		
	}

	
	public int updateTreeNode(TreeNode treeNode) {
		// TODO Auto-generated method stub
		try {
			treeNode.setUpdate_time(sdf.format(new Date()));
			return treeNodeMapper.update(treeNode);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
		
	}

	
	public int deleteTreeNode(Integer id) {
		// TODO Auto-generated method stub
		try {
			return treeNodeMapper.delete(id);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
	
	}

}
