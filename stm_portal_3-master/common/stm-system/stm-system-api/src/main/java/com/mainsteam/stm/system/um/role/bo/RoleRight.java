package com.mainsteam.stm.system.um.role.bo;

/**
 * <li>文件名称: RoleRight.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月26日
 */
public class RoleRight extends RoleRightRel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4583958242959049448L;

	private String rightName;
	private int pId;
	

	public String getRightName() {
		return rightName;
	}

	public void setRightName(String rightName) {
		this.rightName = rightName;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

}
