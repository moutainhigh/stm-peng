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
import com.mainsteam.stm.portal.netflow.dao.inf.IIfAppDao;

/**
 * <li>文件名称: IIfAppDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月17日
 * @author   lil
 */
public class IIfAppDaoImpl extends BaseDao<NetflowBo> implements IIfAppDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIfAppDaoImpl(SqlSessionTemplate session) {
		super(session, IIfAppDao.class.getName());
	}

	@Override
	public List<NetflowBo> ifAppPageSelect(Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ifAppPageSelect", page);
	}

	@Override
	public long getTotalIfAppNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIfAppNetflows", bo);
	}

	@Override
	public List<NetflowBo> getIfAppChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIfAppChartData", bo);
	}

	@Override
	public Whole getIfAppTotals(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getIfAppTotals", bo);
	}

}
