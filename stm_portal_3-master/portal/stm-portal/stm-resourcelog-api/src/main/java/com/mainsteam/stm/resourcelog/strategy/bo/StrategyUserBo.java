package com.mainsteam.stm.resourcelog.strategy.bo;

import java.io.Serializable;

/**
 * <li>文件名称: StrategyUserBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 策略人员</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月5日
 * @author   ziwenwen
 */
public class StrategyUserBo implements Serializable{
	private static final long serialVersionUID = -6997949947090057421L;

	/**
	 * 人员id
	 */
	Long userId;
	
	/**
	 * 策略id
	 */
	Long strategyId;
	
	/**
	 * 是否短信 1发送短信 0不发送短信
	 */
	int isMsg;
	
	/**
	 * 是否发送邮件 1发送 0不发送
	 */
	int isMail;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getIsMsg() {
		return isMsg;
	}

	public void setIsMsg(int isMsg) {
		this.isMsg = isMsg;
	}

	public int getIsMail() {
		return isMail;
	}

	public void setIsMail(int isMail) {
		this.isMail = isMail;
	}

	public Long getStrategyId() {
		return strategyId;
	}

	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}
}


