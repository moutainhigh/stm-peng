package com.mainsteam.stm.home.workbench.main.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.home.workbench.main.bo.WorkBench;
import com.mainsteam.stm.home.workbench.main.dao.IWorkbenchDao;
import com.mainsteam.stm.platform.dao.BaseDao;

/**
 * <li>文件名称: WorkbenchDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   ziwenwen
 */
public class WorkbenchDao extends BaseDao<WorkBench> implements IWorkbenchDao {

	public WorkbenchDao(SqlSessionTemplate session) {
		super(session, IWorkbenchDao.class.getName());
	}

	@Override
	public List<WorkBench> getAllWorkBench() {
		return select("getAllWorkBench",null);
	}

	@Override
	public WorkBench getWorkBenchById(Long id) {
		return get("getWorkBenchById", id);
	}
}


