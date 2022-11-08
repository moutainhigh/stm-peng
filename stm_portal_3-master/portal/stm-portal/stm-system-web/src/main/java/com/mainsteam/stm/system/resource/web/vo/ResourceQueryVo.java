package com.mainsteam.stm.system.resource.web.vo;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.resource.bo.ResourceQueryBo;

/**
 * <li>文件名称: ResourceQueryVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月28日
 * @author   ziwenwen
 */
public class ResourceQueryVo extends ResourceQueryBo {

	public ResourceQueryVo() {
		super(null);
	}

	public void setUser(ILoginUser user){
		this.user=user;
	}
}


