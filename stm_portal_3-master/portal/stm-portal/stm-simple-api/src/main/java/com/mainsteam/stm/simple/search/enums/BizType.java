package com.mainsteam.stm.simple.search.enums;

import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: com.mainsteam.stm.simple.search.enums.BizType.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月28日
 */
public enum BizType {

	RESOURCE(ILoginUser.RIGHT_RESOURCE), 
	BIZ(ILoginUser.RIGHT_BIZ),
	TOPO(ILoginUser.RIGHT_TOPO),
	ALARM(ILoginUser.RIGHT_ALARM),
	NET_FLOW(ILoginUser.RIGHT_NET_FLOW),
	CONFIGL_FILE(ILoginUser.RIGHT_CONFIG_FILE),
	INSPECT(ILoginUser.RIGHT_PLAN),
	REPORT(ILoginUser.RIGHT_REPORT);
	
	private Long type;
	private BizType(Long type){
		this.type = type;
	}
	
	public Long getType(){
		return this.type;
	}
}
