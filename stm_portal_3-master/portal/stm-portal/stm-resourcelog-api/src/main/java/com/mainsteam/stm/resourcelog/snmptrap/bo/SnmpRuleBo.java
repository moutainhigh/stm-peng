package com.mainsteam.stm.resourcelog.snmptrap.bo;

import java.io.Serializable;

/**
 * <li>文件名称: SnmpRuleBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月10日
 * @author   ziwenwen
 */
public class SnmpRuleBo implements Serializable{
	
	private static final long serialVersionUID = 30155152453888615L;

	/**
	 * 规则id
	 */
	Long id;

	/**
	 * 策略id
	 */
	Long strategyId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}
	
}


