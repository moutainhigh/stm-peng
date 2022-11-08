package com.mainsteam.stm.home.workbench.host.api;

import com.mainsteam.stm.home.workbench.host.vo.HostNetInfoVo;


/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.host.api.HostApi.java</li>
 * <li>文件描述: 主机信息查询</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月15日
 */
public interface HostApi {
	/**
	 * 获取主机信息
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年9月15日
	 */
	public HostNetInfoVo getHostInfo(Long id);
}
