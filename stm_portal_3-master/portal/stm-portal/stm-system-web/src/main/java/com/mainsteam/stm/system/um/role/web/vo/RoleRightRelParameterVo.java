package com.mainsteam.stm.system.um.role.web.vo;

import java.util.List;

import com.mainsteam.stm.system.um.role.bo.RoleRightRel;

/**
 * <li>文件名称: RoleRightRelParameterVo.java</li>
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
public class RoleRightRelParameterVo {
	private List<RoleRightRel> roleRightRels;

	public List<RoleRightRel> getRoleRightRels() {
		return roleRightRels;
	}

	public void setRoleRightRels(List<RoleRightRel> roleRightRels) {
		this.roleRightRels = roleRightRels;
	}

}
