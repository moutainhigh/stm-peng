package com.mainsteam.stm.ipmanage.api;

import java.util.List;

import com.mainsteam.stm.ipmanage.bo.TreeNode;

public interface TreeNodeService {
	
	public List<TreeNode> getTreeNodes(Integer id);
    
    public int insertTreeNode(TreeNode treeNode);
    
    public int updateTreeNode(TreeNode treeNode);
    
    public int deleteTreeNode(Integer id);

}
