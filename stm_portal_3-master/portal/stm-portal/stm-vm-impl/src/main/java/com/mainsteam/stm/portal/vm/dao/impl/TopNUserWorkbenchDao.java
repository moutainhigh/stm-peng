package com.mainsteam.stm.portal.vm.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.vm.bo.TopNWorkBench;
import com.mainsteam.stm.portal.vm.dao.ITopNUserWorkbenchDao;
/**
 * <li>文件名称: TopNUserWorkbenchDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年4月3日
 * @author   caoyong
 */
public class TopNUserWorkbenchDao extends BaseDao<TopNWorkBench> implements ITopNUserWorkbenchDao {

	public TopNUserWorkbenchDao(SqlSessionTemplate session) {
		super(session, ITopNUserWorkbenchDao.class.getName());
	}

	@Override
	public List<TopNWorkBench> getTopNUserWorkBenchs(Long userId) {
		return select("getTopNUserWorkBenchs", userId);
	}

	@Override
	public int batchInsertWorkBenchs(List<TopNWorkBench> uws) {
		return batchInsert("batchInsertWorkBenchs", uws);
	}

	@Override
	public int delTopNUserWorkBench(Long userId) {
		return del("delTopNUserWorkBench", userId);
	}

	@Override
	public int delSingleUserWorkBench(TopNWorkBench um) {
		return del("delSingleUserWorkBench", um);
	}

	@Override
	public int setExt(TopNWorkBench wb) {
		return update("setExt", wb);
	}
	
	@Override
	public int updateTopNSetting(TopNWorkBench wb) {
		return update("updateTopNSetting", wb);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.home.workbench.main.dao.IUserWorkbenchDao#delUsersWorkBench(java.lang.Long[])
	 */
	@Override
	public int delTopNUsersWorkBench(Long[] userId) {
		return del("delTopNUsersWorkBench", userId);
	}

	@Override
	public TopNWorkBench getTopNUserWorkBenchById(TopNWorkBench wb) {
		return get("getTopNUserWorkBenchById",wb);
	}

	@Override
	public List<TopNWorkBench> getUserBenchIdByResourceId(Long id) {
		return select("getUserBenchIdByResourceId", id);
	}

	@Override
	public void setUserBenchResourceIdById(TopNWorkBench workBench) {
		update("setUserBenchResourceIdById", workBench);
	}

	@Override
	public List<TopNWorkBench> getAllWorkBench() {
		return select("getAllWorkBench",null);
	}

}


