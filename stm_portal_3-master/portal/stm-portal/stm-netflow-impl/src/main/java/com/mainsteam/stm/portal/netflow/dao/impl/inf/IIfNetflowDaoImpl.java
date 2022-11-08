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
import com.mainsteam.stm.portal.netflow.dao.inf.IIfNetflowDao;

/**
 * <li>文件名称: IIfNetflowDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月16日
 * @author   lil
 */
public class IIfNetflowDaoImpl extends BaseDao<NetflowBo> implements IIfNetflowDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfNetflowDaoImpl(SqlSessionTemplate session) {
		super(session, IIfNetflowDao.class.getName());
	}

	@Override
	public long getTotalIfNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfNetflows", bo);
	}

	@Override
	public List<NetflowBo> ifListPageSelect(Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifListPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIfNetflowChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfNetflowChartData", bo);
	}
	
	@Override
	public List<Long> getIfIdsByIfGroupId(long ifGroupId) {
		return getSession().selectList(getNamespace() + "getIfIdsByIfGroupId", ifGroupId);
	}
	
	@Override
	public long getTotalIfPackets(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfPackets", bo);	
	}

	@Override
	public long getTotalConnects(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalConnects", bo);
	}

}
