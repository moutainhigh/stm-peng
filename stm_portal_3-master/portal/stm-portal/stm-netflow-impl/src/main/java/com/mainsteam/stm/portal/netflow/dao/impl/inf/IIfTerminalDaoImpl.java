/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl.inf;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.inf.IIfTerminalDao;

/**
 * <li>文件名称: IIfTerminalDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfTerminalDaoImpl extends BaseDao<NetflowBo> implements IIfTerminalDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfTerminalDaoImpl(SqlSessionTemplate session) {
		super(session, IIfTerminalDao.class.getName());
	}

	@Override
	public List<NetflowBo> ifTerminalPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifTerminalPageSelect", page);
	}

	@Override
	public long getTotalIfTerminalNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfTerminalNetflows", bo);
	}

	@Override
	public List<NetflowBo> getIfTerminalChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfTerminalChartData", bo);
	}

	@Override
	public Long getTotalIfTerminalPackets(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfTerminalPackets", bo);
	}

	@Override
	public Long getTotalIfTerminalConnects(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfTerminalConnects", bo);
	}

	@Override
	public Whole getIfTerminalTotals(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfTerminalTotals", bo);
	}

}
