package com.mainsteam.stm.simple.engineer.workbench.bo;

import java.io.Serializable;

/**
 * <li>文件名称: FaultProcessFlowBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public class FaultProcessFlowBo implements Serializable{
	
	private static final long serialVersionUID = -1400250632048088046L;

	
	/**
	 * 故障ID
	 */
	private Long faultId;
	
	/**
	 * 使用json格式字符串记录故障处理过程
	 * 包括分析、解决、调查、修复效果调查四个阶段
	 * 在故障处理的过程中不断丰富该json串
	 */
	private String process;

	public Long getFaultId() {
		return faultId;
	}

	public void setFaultId(Long faultId) {
		this.faultId = faultId;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}
}


