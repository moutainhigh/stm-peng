package com.mainsteam.stm.topo.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.dao.ITopoFindDao;
public class TopoFindDaoImpl extends BaseDao<NodeBo> implements ITopoFindDao{
	Logger logger = Logger.getLogger(TopoFindDaoImpl.class);
	public TopoFindDaoImpl(SqlSessionTemplate session){
		super(session, ITopoFindDao.class.getName());
	}
	@Override
	public void trunkLinkAll() {
		try {
			getSession().delete(getNamespace()+"trunkLinkAll");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void trunkOtherNodeAll() {
		try {
			getSession().delete(getNamespace()+"trunkOtherNodeAll");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void trunkNodeAll() {
		try {
			getSession().delete(getNamespace()+"trunkNodeAll");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void trunkSubTopoAll() {
		try {
			getSession().delete(getNamespace()+"trunkSubTopoAll");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void trunkUnInstanceLink() {
		try {
			getSession().delete(getNamespace()+"trunkUnInstanceLink");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public void deleteSubTopo(Long subTopoId) {
		try {
			Map<String,Long> map = new HashMap<String,Long>();
			map.put("id", subTopoId);
			getSession().delete(getNamespace()+"trunkLinkBySubtopoId",map);
			getSession().delete(getNamespace()+"trunkNodeBySubtopoId",map);
			getSession().delete(getNamespace()+"trunkOtherBySubtopoId",map);
			if(subTopoId!=null){
				getSession().delete(getNamespace()+"deleteSubtopo",map);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	@Override
	public Map<Long, String> findIp() {
		Map<Long,String> retn = new HashMap<Long,String>();
		try {
			List<NodeBo> nodes = getSession().selectList(getNamespace()+"findIp");
			for(NodeBo nb:nodes){
				retn.put(nb.getId(),nb.getIp());
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return retn;
	}
	@Override
	public void deleteNodes(List<Long> prepareDelete) {
		try {
			if(!prepareDelete.isEmpty()){
				//删除链路
				getSession().delete(getNamespace()+"deleteLinkByList",prepareDelete);
				//删除节点
				getSession().delete(getNamespace()+"deleteNodeByList",prepareDelete);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	@Override
	public void trunkGroupAll() {
		try {
			getSession().delete(getNamespace()+"trunkGroupAll");
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	@Override
	public void deleteAllLink() {
		getSession().delete(getNamespace()+"deleteAllLink");
	}
	
}
