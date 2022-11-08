package com.mainsteam.stm.system.itsmuser.dao.imp;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.itsmuser.bo.ItsmSystemBo;
import com.mainsteam.stm.system.itsmuser.dao.IItsmSystemDao;

@Repository("itsmSystemDao")
@SuppressWarnings("rawtypes")
public class ItsmSystemDaoImpl extends BaseDao implements IItsmSystemDao {

	@Autowired
	public ItsmSystemDaoImpl(@Qualifier(BaseDao.SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IItsmSystemDao.class.getName());
	}

	@Override
	public int saveItsmSystem(ItsmSystemBo itsmSystemBo) {
		return super.getSession().insert("saveItsmSystem",itsmSystemBo);
	}

	@Override
	public List<ItsmSystemBo> queryItsmSystem(Page<ItsmSystemBo, ItsmSystemBo> page) {
		return super.getSession().selectList("queryItsmSystem",page);
	}

	@Override
	public ItsmSystemBo getItsmSystemById(Long id) {
		return (ItsmSystemBo) super.getSession().selectOne("getItsmSystemById",id);
	}

	@Override
	public int updateItsmSystem(ItsmSystemBo itsmSystemBo) {
		return super.getSession().update("updateItsmSystem", itsmSystemBo);
	}

	@Override
	public int updateSystemStartState(Long[] ids, int startState) {
		SqlSession session = super.getSession();
		int count = 0;
		ItsmSystemBo itsmSystemBo = null;
		for (int i = 0; i < ids.length; i++) {
			itsmSystemBo = new ItsmSystemBo();
			itsmSystemBo.setId(ids[i]);
			itsmSystemBo.setIsOpen(startState);
			count += session.update("updateSystemStartState", itsmSystemBo);
		}
		return count;
	}

	@Override
	public List<ItsmSystemBo> queryAllItsmSystem() {
		return super.getSession().selectList("queryAllItsmSystem");
	}

	@Override
	public int updateSyncState(ItsmSystemBo itsmSystemBo) {
		return super.getSession().update("updateSyncState", itsmSystemBo);
	}

	@Override
	public boolean isWsdlURLExist(ItsmSystemBo itsmSystemBo) {
		int countNum = super.getSession().selectOne("countWsdlUrl", itsmSystemBo);
		if (countNum > 0) {
			return true;
		}
		return false;
	}
}
