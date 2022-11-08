package com.mainsteam.stm.topo.collector;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.qwserv.itm.netprober.api.IIpmac;
import com.qwserv.itm.netprober.api.IIpmacMsgListener;
import com.qwserv.itm.netprober.bean.IpmacItemBean;
import com.qwserv.itm.netprober.message.ProcessMsg;

/**
 * <li>IP-MAC-PORT数据采集器接口</li>
 * <li>文件名称: TopoMacCollectorImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年10月29日
 * @author zwx
 */
public class TopoMacCollector implements TopoMacCollectorMBean,IIpmacMsgListener{
	@Autowired
	private  IIpmac ipmac;
	private final static Logger logger = Logger.getLogger(TopoMacCollector.class);
	
	//ipmac集合
	private List<IpmacItemBean> ipmacItems = new ArrayList<IpmacItemBean>();
	//是否正在运行
	private boolean isRunning = true;

	/**
	 * 解析刷新的ip-mac-port数据
	 * @param msg
	 */
	@Override
	public void onMessage(ProcessMsg msg) {
		if (msg.getDisProcess() == ProcessMsg.IPMAC_REFRESHED) {
			List<IpmacItemBean> items =  msg.readAliveItems();	//从DCS中获取aliveTable数据
			this.setIpmacItems(items);
        } else if( msg.getDisProcess() == ProcessMsg.DISCOVERY_FINISH) {
        	isRunning = false;	//停止运行
        }
	}

	/**
	 * 刷新ipmacport数据，采集完成后会调用本类中onMessage()设置采集的数据
	 * @param diviceIps
	 * @param cfgUrl 配置文件位置
	 * @return int
	 */
	@Override
	public int startRefresh(List<String> diviceIps, String cfgUrl) {
		isRunning = true;	//重置运行状态
		return ipmac.discoveryIpMac(this,diviceIps,TopoCollector.cfgPath);
	}
	
	@Override
	public List<IpmacItemBean> getIpmacItems() {
		logger.error("ip-mac-port采集数据： "+JSONObject.toJSONString(ipmacItems));
		return ipmacItems;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * 初始化采集器
	 */
	@SuppressWarnings("unused")
	private void init() {
		logger.error("******IP_MAC_PORT采集器启动成功!******");
	}
	
	public void setIpmacItems(List<IpmacItemBean> ipmacItems) {
		this.ipmacItems = ipmacItems;
	}
	
	public IIpmac getIpmac() {
		return ipmac;
	}

	public void setIpmac(IIpmac ipmac) {
		this.ipmac = ipmac;
	}
}
