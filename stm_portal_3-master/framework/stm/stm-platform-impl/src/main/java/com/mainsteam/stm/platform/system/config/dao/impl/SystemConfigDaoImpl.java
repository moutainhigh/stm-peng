/**
 * 
 */
package com.mainsteam.stm.platform.system.config.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.dao.ISystemConfigDao;

/**
 * <li>文件名称: SystemConfigDaoImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日 下午8:15:14
 * @author   俊峰
 */
public class SystemConfigDaoImpl extends BaseDao<SystemConfigBo> implements ISystemConfigDao {

	public SystemConfigDaoImpl(SqlSessionTemplate session) {
		super(session, ISystemConfigDao.class.getName());
	}

}
