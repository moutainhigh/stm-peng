package com.mainsteam.stm.home.workbench.netdevice.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.home.layout.bo.HomeDefaultInterfaceBo;
import com.mainsteam.stm.home.workbench.host.vo.HostNetInfoVo;
import com.mainsteam.stm.home.workbench.host.vo.InterfaceIndicators;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.netdevice.api.NetDeviceApi.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月17日
 */
public interface NetDeviceApi {
	/**
	 * 获取网络设备信息
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年9月20日
	 */
	public HostNetInfoVo getNetDeviceInfo(Long id, String interfaceType);
	
	/**
	 * 获取网络设备信息
	 * @param id
	 * @return
	 * @author	zhouw
	 * @date	2017年6月13日
	 */
	public HostNetInfoVo getNewNetDeviceInfo(Long id, String interfaceType,long userId);

	/**
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年12月17日
	 */
	public List<InterfaceIndicators> getInterfaceIndicators(Long id);
	
	/**
	 * 设置或取消默认接口
	 * @param hb
	 * @return
	 */
	public HomeDefaultInterfaceBo setDefaultInterface(HomeDefaultInterfaceBo hb);
	
}
