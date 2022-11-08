package com.mainsteam.stm.simple.engineer.workbench.dao.impl;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.simple.engineer.workbench.bo.FaultProcessFlowBo;
import com.mainsteam.stm.simple.engineer.workbench.dao.IFaultProcessFlowDao;

/**
 * <li>文件名称: FaultProcessFlowDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
@Repository
public class FaultProcessFlowDaoImpl extends BaseDao<FaultProcessFlowBo> implements IFaultProcessFlowDao{
	
	@Autowired
	public FaultProcessFlowDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IFaultProcessFlowDao.class.getName());
	}
}


