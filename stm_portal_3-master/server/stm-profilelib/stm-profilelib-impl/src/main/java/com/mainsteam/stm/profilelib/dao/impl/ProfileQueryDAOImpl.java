/**
 * 
 */
package com.mainsteam.stm.profilelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.profilelib.dao.ProfileQueryDAO;

/**
 * @author ziw
 *
 */
public class ProfileQueryDAOImpl implements ProfileQueryDAO{

	private SqlSession session;
	
	/**
	 * @param session the session to set
	 */
	public final void setSession(SqlSession session) {
		this.session = session;
	}

	/**
	 * 
	 */
	public ProfileQueryDAOImpl() {
	}

	@Override
	public List<Long> queryProfileByDCS(int dcsId) {
		return session.selectList("com.mainsteam.stm.profilelib.dao.ProfileQueryDAO.queryProfileByDCS",dcsId);
	}
}
