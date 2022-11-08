package com.mainsteam.stm.pluginsession.parameter;

/**
 * 
 * @author yuanlb
 *
 */
public class JBrokerESXServerParameter {
	private String name;
	private String cpufrequency;// cpu频率
	private String netWorks;
	
//	ESXi指标如下：
//
//	可用性指标
//	性能指标:
//	CPU使用情况 (%)
//	内存总容量（MB）
//	内存使用情况（%）
//	换入速率(KBps)
//	换出速率(KBps)
//	磁盘读取速度(KBps)
//	存储写入滞后时间（ms)
//	网络数据接收速度(KBps)
//	网络数据传输速度（KBps)
//	网络使用情况(KBps)
//	信息指标
//	制造商    Dell Inc
//	型号   PowerEdge R520
//	CPU内核  4个CPU x2.2GHz
//	CPU使用情况（MHz)  2048
//	处理器类型  Intel(R) Xeon(R) CPU E5-24070 @2.2GHz
//	处理器插槽 1
//	每个插槽内核数 4
//	逻辑处理器  5
//	网卡数目 2
//	虚拟机和模板数 20
//	vMotion已启用   否
//	EVC模式   已禁用 
//	连续运行时间（天) 52天
	public String getNetWorks() {
		return netWorks;
	}

	public void setNetWorks(String netWorks) {
		this.netWorks = netWorks;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "\n(ESX服务器) [(名称)name=" + name + "[netWorks=" + netWorks + "]"
				+ ",(CPU外频)cpufrequency=" + cpufrequency + "]\n";
	}

	public String getCpufrequency() {
		return cpufrequency;
	}

	public void setCpufrequency(String cpufrequency) {
		this.cpufrequency = cpufrequency;
	}
}
