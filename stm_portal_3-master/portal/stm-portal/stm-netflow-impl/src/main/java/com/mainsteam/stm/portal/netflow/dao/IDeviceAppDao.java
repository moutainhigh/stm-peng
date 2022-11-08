/**
 * 
 */
package com.mainsteam.stm.portal.netflow.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.bo.ApplicationBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowBo;
import com.mainsteam.stm.portal.netflow.bo.NetflowParamBo;
import com.mainsteam.stm.portal.netflow.bo.Whole;

/**
 * <li>文件名称: IDeviceAppDao.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月16日
 * @author lil
 */
public interface IDeviceAppDao {

	/**
	 * 分页查询设备应用流量使用-datagrid
	 * 
	 * @param page
	 * @return List<DeviceFlowBo> 返回类型
	 */
	List<NetflowBo> deviceAppNetflowPageSelect(
			Page<NetflowBo, NetflowParamBo> page);

	/**
	 * 获取某一设备所有应用的流量
	 * 
	 * @param bo
	 * @return long 返回类型
	 */
	Whole getTotalAppNetflowsOfDevice(NetflowParamBo bo);

	/**
	 * 查询某一应用在各时间点的流量
	 * 
	 * @param m
	 * @return List<Long> 返回类型
	 */
	List<NetflowBo> queryDeviceAppNetflowOfTimepoint(NetflowParamBo bo);

	List<ApplicationBo> getAppId(long startRow, long number);

}
