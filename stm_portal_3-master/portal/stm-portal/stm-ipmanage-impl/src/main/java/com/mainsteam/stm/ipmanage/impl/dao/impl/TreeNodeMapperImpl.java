package com.mainsteam.stm.ipmanage.impl.dao.impl;

import java.util.List;

import com.mainsteam.stm.ipmanage.impl.dao.TreeNodeMapper;
import com.mainsteam.stm.ipmanage.bo.TreeNode;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;

public class TreeNodeMapperImpl extends BaseDao<TreeNode> implements TreeNodeMapper {
	
	public TreeNodeMapperImpl(SqlSessionTemplate session) {
		super(session, TreeNodeMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	
	public List<TreeNode> getTreeNodes(Integer id) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getList",id);
	}

	
	public int insert(TreeNode treeNode) {
		// TODO Auto-generated method stub
		return super.insert(treeNode);
	}

	
	public int update(TreeNode treeNode) {
		// TODO Auto-generated method stub
		return super.update(treeNode);
	}

	
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		return super.del(id);
	}

	
	public TreeNode getParentTreeNodeByIp(TreeNode treeNode) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace()+"getParentTreeNodeByIp", treeNode);
	}

}
