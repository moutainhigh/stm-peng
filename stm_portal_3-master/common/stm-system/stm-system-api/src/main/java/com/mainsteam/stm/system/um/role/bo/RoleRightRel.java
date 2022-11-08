package com.mainsteam.stm.system.um.role.bo;

/**
 * <li>文件名称: RoleRightRel.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月23日
 */
public class RoleRightRel implements java.io.Serializable{
	private static final long serialVersionUID = -5892391958564689122L;
	private Long roleId;
	private Long rightId;
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Long getRightId() {
		return rightId;
	}
	public void setRightId(Long rightId) {
		this.rightId = rightId;
	}
}
