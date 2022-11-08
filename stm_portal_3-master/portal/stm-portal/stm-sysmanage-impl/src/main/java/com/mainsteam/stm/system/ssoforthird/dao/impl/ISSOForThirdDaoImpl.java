package com.mainsteam.stm.system.ssoforthird.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.ssoforthird.bo.SSOForThirdBo;
import com.mainsteam.stm.system.ssoforthird.dao.ISSOForThirdDao;

@Repository("ssoForThirdDao")
@SuppressWarnings("rawtypes")
public class ISSOForThirdDaoImpl extends BaseDao implements ISSOForThirdDao {

	@Autowired
	public ISSOForThirdDaoImpl(@Qualifier(BaseDao.SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, ISSOForThirdDao.class.getName());
	}

	@Override
	public int saveSSOForThird(SSOForThirdBo ssoForThirdBo) {
		// TODO Auto-generated method stub
		return super.getSession().insert(getNamespace()+"saveSSOForThird",ssoForThirdBo);
	}

	@Override
	public List<SSOForThirdBo> querySSOForThird(
			Page<SSOForThirdBo, SSOForThirdBo> page) {
		// TODO Auto-generated method stub
		return super.getSession().selectList(getNamespace()+"querySSOForThird",page);
	}

	@Override
	public List<SSOForThirdBo> queryAllSSOForThird() {
		// TODO Auto-generated method stub
		return super.getSession().selectList(getNamespace()+"queryAllSSOForThird");
	}

	@Override
	public SSOForThirdBo getSSOForThirdById(Long id) {
		// TODO Auto-generated method stub
		return (SSOForThirdBo)super.getSession().selectOne(getNamespace()+"getSSOForThirdById",id);
	}

	@Override
	public int updateSSOForThird(SSOForThirdBo ssoForThirdBo) {
		// TODO Auto-generated method stub
		return super.getSession().update(getNamespace()+"updateSSOForThird",ssoForThirdBo);
	}

	@Override
	public int updateSSOForThirdStartState(Long[] ids, int startState) {
		SqlSession session = super.getSession();
		int count = 0;
		SSOForThirdBo ssoForThirdBo = null;
		for (int i = 0; i < ids.length; i++) {
			ssoForThirdBo = new SSOForThirdBo();
			ssoForThirdBo.setId(ids[i]);
			ssoForThirdBo.setIsOpen(startState);
			count += session.update(getNamespace()+"updateSSOForThirdStartState", ssoForThirdBo);
		}
		return count;
	}

	@Override
	public boolean isWsdlURLExist(SSOForThirdBo ssoForThirdBo) {
		int countNum = super.getSession().selectOne(getNamespace()+"countssoWsdlUrl", ssoForThirdBo);
		if (countNum > 0) {
			return true;
		}
		return false;
	}

}
