/**
 * 
 */
package com.mainsteam.stm.resourcelog.trap;

import com.mainsteam.stm.resourcelog.vo.SnmptrapVo;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年12月4日 下午10:32:01
 * @version 1.0
 */
/** 
 */
public class SnmpTrapTransferVO extends SnmptrapVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4645995385474543215L;

	private boolean userParentTrapName = false;
	
	/**
	 * 
	 */
	public SnmpTrapTransferVO() {
	}
	
	public boolean isUserParentTrapName() {
		return userParentTrapName;
	}

	public void setUserParentTrapName(boolean userParentTrapName) {
		this.userParentTrapName = userParentTrapName;
	}



	@Override
	public String getTrapName() {
		if(userParentTrapName) {
			return super.getTrapName();
		}
		return "";
	}
}
