package com.mainsteam.stm.topo.collector;

import java.util.List;

import com.qwserv.itm.netprober.bean.IpmacItemBean;

/**
 * <li>IP-MAC-PORT数据采集器接口</li>
 * <li>文件名称: ITopoMacCollectorApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月29日
 * @author zwx
 */
public interface TopoMacCollectorMBean {
	
	/**
	 * 获取刷新的设备ipmac数据
	 * @return List<IpmacItemBean>
	 */
	public List<IpmacItemBean> getIpmacItems();
	
	/**
	 * 启动采集器，刷新设备ipmac数据
	 * @param diviceIps	设备ips
	 * @param cfgUrl	生成配置地址
	 * @return int
	 */
	public int startRefresh(List<String> diviceIps,String cfgUrl);
	
	/**
	 * 是否正在运行
	 * @return 运行状态
	 */
	boolean isRunning();
}
