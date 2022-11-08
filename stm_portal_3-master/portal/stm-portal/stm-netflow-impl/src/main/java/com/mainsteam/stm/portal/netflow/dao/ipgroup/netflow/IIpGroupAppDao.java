/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao.ipgroup.netflow;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IIpGroupAppDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 接口流量统计</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月21日
 * @author   lil
 */
public interface IIpGroupAppDao {
	
	List<NetflowBo> ipGroupAppPageSelect(Page<NetflowBo, NetflowParamBo> page);
	
	List<NetflowBo> getIpGroupAppChartData(NetflowParamBo bo);
	
	Whole getTotalIpGroupAppNetflows(NetflowParamBo bo);

}
