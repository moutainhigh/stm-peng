/**
 * 
 */
package com.mainsteam.stm.common.instance.dao;

import java.util.List;

import com.mainsteam.stm.common.instance.dao.obj.MetricAvailDataDO;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

/**
 * @author ziw
 *
 */
public interface MetricAvailQueryDAO {
	public List<MetricAvailDataDO> getAvailDataDOs(Page<MetricAvailDataDO, String> page);
}
