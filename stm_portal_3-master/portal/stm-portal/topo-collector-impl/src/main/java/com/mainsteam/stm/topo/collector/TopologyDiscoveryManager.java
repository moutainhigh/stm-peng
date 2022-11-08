package com.mainsteam.stm.topo.collector;
import java.util.Set;

import com.qwserv.itm.netprober.api.IDiscoveryService;
import com.qwserv.itm.netprober.bean.Layer2Topology;
import com.qwserv.itm.netprober.bean.LinkBean;
import com.qwserv.itm.netprober.bean.MyException;
import com.qwserv.itm.netprober.bean.StatusProcess;
import com.qwserv.itm.netprober.bean.TopoDisBean;
import com.qwserv.itm.netprober.context.FrontgroundCfg;
import com.qwserv.itm.netprober.devdef.NetElementBean;
import com.qwserv.itm.netprober.impl.DiscoveryImpl;
import com.qwserv.itm.netprober.message.ProcessMsg;
import com.qwserv.itm.util.toolkit.DebugUtil;

/**
 * 
 * <li>文件名称: TopologyDiscoveryManager.java</li>
 * <li>文件描述: 拓扑新发现算法入口</li>
 * <li>版权所有: 版权所有(C)2005-2006</li>
 * <li>公司: 成都勤智数码有限公司</li>
 * <li>内容摘要: // 简要描述本文件的内容，包括主要模块、函数及其功能的说明</li>
 * <li>其他说明: // 其它内容的说明</li>
 * <li>完成日期：// 输入完成日期，例：2000年2月25日</li>
 * <li>修改记录1: // 修改历史记录，包括修改日期、修改者及修改内容</li>
 * @version 6.2
 * @author caiyongjun
 */
public class TopologyDiscoveryManager {

	private final static DebugUtil log = DebugUtil.getInstances("pfl-webtopo", TopologyDiscoveryManager.class);
	
	private static TopologyDiscoveryManager manager = new TopologyDiscoveryManager();
	
	/**
	 * 拓扑发现算法接口类
	 */
	private IDiscoveryService discoveryService = new DiscoveryImpl();

	/**
	 * 同步service
	 */
	private TopoCollector tdsvc;
	

	public TopoCollector getTdsvc() {
		return tdsvc;
	}

	public void setTdsvc(TopoCollector tdsvc) {
		this.tdsvc = tdsvc;
	}

	/**
	 * 获取发现算法发现结果线程
	 */
	private GetResultThread getResultThread;
	
	/**
	 * 发现完成状态
	 */
	private boolean discoveryCompleted = true;
	
	private TopologyDiscoveryManager() {
	}
	
	public static TopologyDiscoveryManager getInstance() {
		return manager;
	}
	/**
	 * 初始化拓扑发现环境参数（同步数据库的发现参数到拓扑算法配置）
	 * @param disConfig 
	 * @return 0：成功，-1：失败
	 */
	/*public int init() {
		try {
			String par = null;
			TarConfigHelper.init(par);
		} catch (Exception e) {
			log.error("初始化拓扑发现参数异常",e);
		}
		
		FrontgroundCfg cfg = new FrontgroundCfg();
		cfg.setCollectorParams(getDiscoveryConfig());
		
		return saveConfigFile(cfg);
		return 0;
	}*/
	
	/**
	 * 保存发现配置参数
	 * @param disConfig
	 * @return 0：成功，-1：失败
	 */
	/*private int saveConfigFile(FrontgroundCfg disConfig) {
		try {
			return discoveryService.saveConfigFile(disConfig);
		} catch (MyException e) {
			log.error("保存拓扑发现参数异常",e);
		} 
		
		return -1;
	}*/
	
	/**
	 * 同步发现参数到拓扑算法
	 * @return 0：成功，-1：失败
	 */
	/*public int synchronizeDiscoveryParam() {
		FrontgroundCfg cfg = new FrontgroundCfg();
		cfg.setCollectorParams(getDiscoveryConfig());
		
		return saveConfigFile(cfg);
	}*/
	
	/**
	 * 保存后台发现参数
	 * @param snmpVersion
	 * @param snmpTimeout
	 * @param useCdp
	 * @return
	 */
	/*public boolean updateBackCfgParam(boolean useCdp) {
		
		try {
			BackgroundConfig config = TarConfig.getInstance().getDisSvrConfig();
			
			config.setCdpLink(useCdp);
			
			return discoveryService.saveBackGrdConfig(config);
		} catch (MyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}*/
	
	/**
	 * 获取后台发现参数
	 * @return
	 */
	/*public BackgroundConfig getBackCfgParam() {
		return TarConfig.getInstance().getDisSvrConfig();
	}*/
	/**
	 * 启动发现
	 * @param para
	 * @return
	 */
	public synchronized String discoveryL2Topology(TopoDisBean para,FrontgroundCfg cfg,String pathRoot,boolean useCdp) {
		try {
			if (discoveryCompleted) {
				// malach in topo
				String result = discoveryService.discoveryL2Topology(para,cfg,pathRoot,useCdp);
				
				//启动成功时,启动采集线程
				if (StatusProcess.Running.name().equals(result)) {
					discoveryCompleted = false;
					getResultThread = new GetResultThread();
					getResultThread.startToGetResult();
				}
				return result;
			}
		} catch (MyException e) {
			log.error("启动发现异常",e);
			return StatusProcess.Failed.name();
		} 
//		discoveryCompleted = true;
//		return StatusProcess.Failed.name();
		return StatusProcess.Busy.name();
	}
	
//	/**
//	 * 获取发现2层拓扑
//	 * @return
//	 */
//	public Layer2Topology getL2Topology() {
//		try {
//			return discoveryService.getL2TopologyThisTime();
//		} catch (MyException e) {
//			log.error("获取发现2层拓扑异常",e);
//		}
//		
//		return null;
//	}
	
	/**
	 * 停止发现
	 * @return 0：成功，-1：失败
	 */
	public int stopDiscovery() {
		try {
			int result = discoveryService.stopDiscovery();
//			if (getResultThread != null) {
//				getResultThread.stopToGetResult();
//			}
			return result;
		} catch (MyException e) {
			log.error("停止发现异常",e);
		}
		return -1;
	}
	
	/**
	 * 判断当前是否在发现过程中
	 * @return
	 */
	public boolean isRunning() {
		String state = "";
		try {
			state = discoveryService.checkSvrStatus();
//			log.error("获取发现状态："+state+" : " +discoveryCompleted);
			//发现状态为running肯定在发现过程中
			if (StatusProcess.Running.name().equals(state)) {
				return true;
			} 
			
			//完成状态为false，为发现算法已经发现完成，正在同步数据到数据库
			if (!discoveryCompleted) {
				return true;
			}
		} catch (MyException e) {
			log.error("获取发现状态异常",e);
		}
//		log.error("获取发现状态-----------："+state+":" +discoveryCompleted);
		return false;
	}
	
	/**
	 * 
	 * <li>文件名称: TopologyDiscoveryManager.java</li>
	 * <li>文件描述: 拓扑算法发现结果获取线程</li>
	 * <li>版权所有: 版权所有(C)2005-2006</li>
	 * <li>公司: 成都勤智数码有限公司</li>
	 * <li>内容摘要: // 简要描述本文件的内容，包括主要模块、函数及其功能的说明</li>
	 * <li>其他说明: // 其它内容的说明</li>
	 * <li>完成日期：// 输入完成日期，例：2000年2月25日</li>
	 * <li>修改记录1: // 修改历史记录，包括修改日期、修改者及修改内容</li>
	 * @version 6.4
	 * @author caiyongjun
	 */
	class GetResultThread extends Thread {
		/**
		 * 是否停止本线程参数
		 */
		private boolean stopped = false;
		
		
		public GetResultThread(){}
		
		/**
		 * 启动获取结果数据线程
		 */
		public void startToGetResult() {
			stopped = false;
			if (!this.isAlive()) {
				this.start();
			}
		}
		
 		/*
 		 * portal拓扑发现采集线程
 		 * 1.循环获取DCS采集的设备信息
 		 * 2.获取设备和链路信息加入satack
 		 * 	2.1. 新发现设备加入设备stack
 		 * 	2.2. 发现完成,获取链路消息加入链路stack-->重置完成标志
 		 * 3.打印发现异常日志信息
 		 */
 		@SuppressWarnings("static-access")
		public void run() {
			while(!stopped) {
				try {
					//1.获取DCS采集的设备信息
					ProcessMsg[] msgs = discoveryService.takeDiscoveryMsgs();
					
					//2.获取设备和链路信息加入satack
					if (msgs != null) {
						for (ProcessMsg msg:msgs) {
							if (msg.getDisProcess() == ProcessMsg.DISCOVERY_NEWDEV) {
								NetElementBean device = msg.getResInstance();
								tdsvc.addSnmpNode(device);	//新发现设备加入设备stack
							} else if (msg.getDisProcess() == ProcessMsg.DISCOVERY_FINISH) {
								//发现发成,获取链路消息加入链路stack
								Layer2Topology l2Topo = discoveryService.getL2TopologyThisTime();
								Set<LinkBean> links = l2Topo.getLinks();
								/*Set<LinkBean>  routerLinks = l2Topo.getRouterLinks();
                                if (routerLinks != null) {
                                    tdsvc.addVirtualLinks(routerLinks);
                                }*/
								if (links != null) {
									tdsvc.addLinks(links);
								}
								stopped = true;	//重置完成标志
								break;
							} else if (msg.getDisProcess() == ProcessMsg.DISCOVERY_FAILED){
								stopped = true;
								break;
							}else{
								//3.打印发现异常日志信息
								tdsvc.buildMessage(msgs);
							}
							log.info(msg.getDisProcess() + ":" + msg.getDisMsg());
						}
					}
					Thread.currentThread().sleep(2000);
				} catch (MyException e) {
					log.error("同步发现设备连线到数据库异常",e);
				} catch (InterruptedException e) {
					log.error("同步发现设备连线到数据库异常",e);
				} catch (Exception e) {
					log.error("同步发现设备连线到数据库异常",e);
				}
			}
			discoveryCompleted = true;
		}
	}
	
}
