package com.mainsteam.stm.resourcelog.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.resourcelog.dao.IStrategyDao;
import com.mainsteam.stm.resourcelog.strategy.bo.StrategyBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;

/**
 * 
 * <li>文件名称: StrategyDaoImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月8日 上午11:31:09
 * @author   xiaolei
 */
@Repository("strategyDao")
@SuppressWarnings("rawtypes")
public class StrategyDaoImpl extends BaseDao implements IStrategyDao {
	
	@Autowired
	public StrategyDaoImpl(@Qualifier(BaseDao.SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IStrategyDao.class.getName());
	}

	@Override
	public int saveStrategyBasic(StrategyBo strategy) {
		return super.getSession().insert("saveStrategyBasic", strategy);
	}

	@Override
	public List<StrategyBo> queryStrategy(Page<StrategyBo, StrategyBo> page) {
		return super.getSession().selectList("queryStratey", page);
	}

	@Override
	public List<SyslogResourceBo> getStrategyResource(Long strategyId) {
		return super.getSession().selectList("getStrategyResource", strategyId);
	}

	@Override
	public int updateMonitorState(Long[] id, int isMonitor) {
		SqlSession session = super.getSession();
		int count = 0;
		for (int i = 0; i < id.length; i++) {
			SyslogResourceBo syslogResourceBo = new SyslogResourceBo();
			syslogResourceBo.setId(id[i]);
			syslogResourceBo.setIsMonitor(isMonitor);
			count += session.update("updateMonitorState", syslogResourceBo);
		}
		return count;
	}
	
	@Override
	public StrategyBo get(Long id) {
		return (StrategyBo) super.get(id);
	}

	@Override
	public int updateStrategyBasic(StrategyBo strategy) {
		return super.getSession().update("updateStrategyBasic", strategy);
	}

	@Override
	public List<SyslogResourceBo> getResourceBos(Page<SyslogResourceBo, StrategyBo> page) {
		return super.getSession().selectList("getResourceStratey", page);
	}

	@Override
	public int updateResourceStrategy(SyslogResourceBo syslogResourceBo) {
		return super.getSession().update("updateResourceStrategy", syslogResourceBo);
	}

	@Override
	public int countRes(SyslogResourceBo resBo) {
		return super.getSession().selectOne("countRes", resBo);
	}

	@Override
	public int delResourceByStrategyId(Long strategyId) {
		return super.getSession().delete("delResourceByStrategyId", strategyId);
	}

	@Override
	public int saveStrategyResource(List<SyslogResourceBo> list) {
		return super.getSession().insert("saveStrategyResource", list);
	}

	@Override
	public int saveResourceSta(List<SyslogResourceBo> list) {
		SqlSession session = super.getSession();
		int returnCount = 0;
		for (int i = 0; i < list.size(); i++) {
			returnCount = (int)session.selectOne("countResource", list.get(i));
			if (returnCount > 0 || list.get(i).getResourceId() == 0) {
				list.remove(i);
				i=i-1;
			}
		}
		if (list != null && !list.isEmpty()) {
			return super.getSession().insert("saveResourceSta", list);
		}
		return 0;
	}

	@Override
	public int updateAllResourceSta(SyslogResourceBo resBo) {
		return super.getSession().update("updateAllResourceSta", resBo);
	}

	@Override
	public int delRelationResource(SyslogResourceBo resBo) {
		return super.getSession().delete("delRelationResource", resBo);
	}
	@Override
	public int delRelationByResourceIdAndStrategyId(SyslogResourceBo resBo) {
		return super.getSession().delete("delRelationByResourceIdAndStrategyId", resBo);
	}

	@Override
	public boolean isStrategyNameExist(StrategyBo strategyBo) {
		int countName = super.getSession().selectOne("countStrategyName", strategyBo);
		boolean isExist = false;
		if (countName > 0) {
			isExist = true;
		}
		return isExist;
	}

	@Override
	public int batchDelBase(Long[] ids) {
		SqlSession session = super.getSession();
		int count = 0;
		for (int i = 0; i < ids.length; i++) {
			count += session.delete("batchDelBase", ids[i]);
		}
		return count;
	}

	@Override
	public int batchDelRule(Long[] strategyIds) {
		SqlSession session = super.getSession();
		int count = 0;
		for (int i = 0; i < strategyIds.length; i++) {
			count += session.delete("batchDelRule", strategyIds[i]);
		}
		return count;
	}

	@Override
	public int batchDelResource(Long[] strategyIds) {
		SqlSession session = super.getSession();
		int count = 0;
		for (int i = 0; i < strategyIds.length; i++) {
			count += session.delete("delResourceByStrategyId", strategyIds[i]);
		}
		return count;
	}
}
