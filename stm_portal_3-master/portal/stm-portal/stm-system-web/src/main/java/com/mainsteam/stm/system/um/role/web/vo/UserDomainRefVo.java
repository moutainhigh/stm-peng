package com.mainsteam.stm.system.um.role.web.vo;

import java.util.List;

import com.mainsteam.stm.system.um.relation.bo.UmRelation;

/**
 * <li>文件名称: UserDomainRef.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月22日
 */
public class UserDomainRefVo {
	private List<UmRelation> umRelations;

	public List<UmRelation> getUmRelations() {
		return umRelations;
	}

	public void setUmRelations(List<UmRelation> umRelations) {
		this.umRelations = umRelations;
	}

}
