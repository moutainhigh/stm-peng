package com.mainsteam.stm.system.um.user.web.vo;

import java.util.List;

import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.bo.UserResourceRel;

/**
 * <li>文件名称: com.mainsteam.stm.system.um.user.web.vo.ParameterVo.java</li>
 * <li>文件描述: 接收list类型的参数</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月10日
 */
public class ParameterVo {
	private List<UmRelation> umRelations;
	
	private List<UserResourceRel> userResources;
	
	private Long id;
	
	private List<User> users;

	public List<UmRelation> getUmRelations() {
		return umRelations;
	}

	public void setUmRelations(List<UmRelation> umRelations) {
		this.umRelations = umRelations;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<UserResourceRel> getUserResources() {
		return userResources;
	}

	public void setUserResources(List<UserResourceRel> userResources) {
		this.userResources = userResources;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
