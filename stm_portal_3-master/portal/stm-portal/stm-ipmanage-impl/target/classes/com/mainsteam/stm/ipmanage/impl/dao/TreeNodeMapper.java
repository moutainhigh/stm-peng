package com.mainsteam.stm.ipmanage.impl.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ipmanage.bo.TreeNode;

public interface TreeNodeMapper {
    public List<TreeNode> getTreeNodes(@Param("id")Integer id);
    
    public int insert(@Param("treeNode")TreeNode treeNode);
    
    public int update(@Param("treeNode")TreeNode treeNode);
    
    public int delete(@Param("id")Integer id);

	public TreeNode getParentTreeNodeByIp(@Param("treeNode")TreeNode treeNode);
}