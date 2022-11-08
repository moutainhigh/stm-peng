package com.mainsteam.stm.simple.engineer.workbench.dao;

import com.mainsteam.stm.simple.engineer.workbench.bo.FaultProcessFlowBo;

/**
 * <li>文件名称: IFaultProcessFlowDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public interface IFaultProcessFlowDao {
	int insert(FaultProcessFlowBo faultProcessFlowBo);
	
	int del(Long flowId);
	
	FaultProcessFlowBo get(Long faultId);
}


