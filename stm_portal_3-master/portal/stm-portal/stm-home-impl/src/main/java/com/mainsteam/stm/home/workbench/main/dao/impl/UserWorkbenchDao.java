package com.mainsteam.stm.home.workbench.main.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.workbench.main.bo.WorkBench;
import com.mainsteam.stm.home.workbench.main.dao.IUserWorkbenchDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: UserWorkbenchDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   ziwenwen
 */
public class UserWorkbenchDao extends BaseDao<WorkBench> implements IUserWorkbenchDao {

	public UserWorkbenchDao(SqlSessionTemplate session) {
		super(session, IUserWorkbenchDao.class.getName());
	}

	@Override
	public List<WorkBench> getUserWorkBenchs(Long userId) {
		return select("getUserWorkBenchs", userId);
	}

	@Override
	public int insertUserWorkBenchs(List<WorkBench> uws) {
		return batchInsert("insertUserWorkBenchs", uws);
	}

	@Override
	public int delUserWorkBench(Long userId) {
		return del("delUserWorkBench", userId);
	}

	@Override
	public int delSingleUserWorkBench(WorkBench um) {
		return del("delSingleUserWorkBench", um);
	}

	@Override
	public int setExt(WorkBench wb) {
		return update("setExt", wb);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.home.workbench.main.dao.IUserWorkbenchDao#delUsersWorkBench(java.lang.Long[])
	 */
	@Override
	public int delUsersWorkBench(Long[] userId) {
		return del("delUsersWorkBench", userId);
	}

	@Override
	public WorkBench getUserWorkBenchById(WorkBench wb) {
		return get("getUserWorkBenchById",wb);
	}

	@Override
	public List<WorkBench> getUserBenchIdByResourceId(Long id) {
		return select("getUserBenchIdByResourceId", id);
	}

	@Override
	public void setUserBenchResourceIdById(WorkBench workBench) {
		update("setUserBenchResourceIdById", workBench);
	}

	@Override
	public int setUserBenchDefaultIdById(WorkBench workBench) {
	return	update("setUserBenchDefaultIdById", workBench);
		
	}

}


