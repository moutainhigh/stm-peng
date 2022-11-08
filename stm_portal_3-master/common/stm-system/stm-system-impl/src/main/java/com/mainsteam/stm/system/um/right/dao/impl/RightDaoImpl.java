package com.mainsteam.stm.system.um.right.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.right.bo.SSOForRight;
import com.mainsteam.stm.system.um.right.dao.IRightDao;

/**
 * <li>文件名称: UserDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月20日
 * @author   ziwenwen
 */
public class RightDaoImpl extends BaseDao<Right> implements IRightDao{

	public RightDaoImpl(SqlSessionTemplate session) {
		super(session, IRightDao.class.getName());
	}

	@Override
	public List<Right> getAll() {
		return select("getAll", null);
	}

	@Override
	public List<Right> getRightByType(int type) {
		return select("getRightByType", type);
	}

	@Override
	public int updateSort(List<Right> rs) {
		return batchUpdate("updateSort", rs);
	}

	@Override
	public List<Right> getRights(Long roleId) {
		return select("getRights", roleId);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.right.dao.IRightDao#updateStatus(com.mainsteam.stm.system.um.right.bo.Right)
	 */
	@Override
	public int updateStatus(Right right) {
		return getSession().update(getNamespace() + "updateStatus", right);
	}

	@Override
	public List<Right> getAll4Skin() {
		return getSession().selectList("getAll4Skin");
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace()+"getCount");
	}

	@Override
	public int updateDelStatus(Long id) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() + "updateDelStatus", id);
	}

	@Override
	public int updateSSOForThirdStartState(Long[] ids, int startState) {
		SqlSession session = super.getSession();
		int count = 0;
		SSOForRight ssoForRight = null;
		for (int i = 0; i < ids.length; i++) {
			ssoForRight = new SSOForRight();
			ssoForRight.setId(ids[i]);
			ssoForRight.setIsOpen(startState);
			count += session.update(getNamespace() + "updateSSOForThirdStartState", ssoForRight);
		}
		return count;
	}

	@Override
	public int updateSSOForThird(SSOForRight ssoForRight) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() + "updateSSOForThird", ssoForRight);
	}

	@Override
	public int updateSortForInsert() {
		// TODO Auto-generated method stub
		return getSession().update("updateSortForInsert");
	}

	@Override
	public List<Long> findIdByPid(long pId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() +"findIdByPid",pId);
	}

//	/* (non-Javadoc)
//	 * @see com.mainsteam.stm.system.um.right.dao.IRightDao#getAllRightsByRoleUsedType()
//	 */
//	@Override
//	public List<Right> getAllRightsByRoleUsedType(int roleUsed) {
//		return select("getAllRightsByRoleUsedType", roleUsed);
//	}
}
