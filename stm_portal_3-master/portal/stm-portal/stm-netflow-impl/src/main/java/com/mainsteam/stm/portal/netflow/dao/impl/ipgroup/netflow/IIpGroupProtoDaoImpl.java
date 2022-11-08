/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.impl.ipgroup.netflow;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;
import com.mainsteam.stm.portal.netflow.dao.ipgroup.netflow.IIpGroupProtoDao;

/**
 * <li>文件名称: IIpGroupProtoDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月22日
 * @author   lil
 */
public class IIpGroupProtoDaoImpl extends BaseDao<NetflowBo> implements IIpGroupProtoDao {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public IIpGroupProtoDaoImpl(SqlSessionTemplate session) {
		super(session, IIpGroupProtoDao.class.getName());
	}

	@Override
	public Whole getTotalIpGroupProtoNetflows(NetflowParamBo bo) {
		return getSession().selectOne(getNamespace() + "getTotalIpGroupProtoNetflows", bo);
	}

	@Override
	public List<NetflowBo> ipGroupProtoPageSelect(
			Page<NetflowBo, NetflowParamBo> page) {
		return getSession().selectList(getNamespace() + "ipGroupProtoPageSelect", page);
	}

	@Override
	public List<NetflowBo> getIpGroupProtoChartData(NetflowParamBo bo) {
		return getSession().selectList(getNamespace() + "getIpGroupProtoChartData", bo);
	}

}
