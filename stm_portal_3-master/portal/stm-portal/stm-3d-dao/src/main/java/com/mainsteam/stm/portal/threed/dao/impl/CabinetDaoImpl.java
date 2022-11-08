//package com.mainsteam.stm.portal.threed.dao.impl;
//
//import java.util.List;
//
//import org.mybatis.spring.SqlSessionTemplate;
//
//import com.mainsteam.stm.platform.dao.BaseDao;
//import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
//import com.mainsteam.stm.portal.threed.bo.CabinetBo;
//import com.mainsteam.stm.portal.threed.dao.ICabinetDao;
//
//public class CabinetDaoImpl extends BaseDao<CabinetBo> implements ICabinetDao{
//	
//	public CabinetDaoImpl(SqlSessionTemplate session) {
//		super(session, ICabinetDao.class.getName());
//	}
//
//	@Override
//	public int batchAdd(List<CabinetBo> boList) {
//		return getSession().insert("batchAdd",boList);
//	}
//
//	@Override
//	public int batchRemoveByArray(long[] ids) {
//		return getSession().delete("batchRemoveByArray", ids);
//	}
//
//	@Override
//	public int batchRemoveByList(List<CabinetBo> boList) {
//		return getSession().delete("batchRemoveByList",boList);
//	}
//
//	@Override
//	public void getPage(Page<CabinetBo, String> page) {
//		this.select("getPage", page);
//	}
//
//	@Override
//	public List<CabinetBo> queryAllDevice() {
//		return getSession().selectList("queryAllDevice");
//	}
//	
//}
