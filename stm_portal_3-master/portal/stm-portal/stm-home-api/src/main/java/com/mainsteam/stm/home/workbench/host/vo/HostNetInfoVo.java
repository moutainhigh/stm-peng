package com.mainsteam.stm.home.workbench.host.vo;

import java.util.List;

import com.mainsteam.stm.home.layout.bo.HomeDefaultInterfaceBo;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench..vo.HostNetInfoVo.java</li>
 * <li>文件描述: 主机信息封装</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月18日
 */
public class HostNetInfoVo {

	private BaseInfo base;	//主机基本信息
	
	private CPU cpu;	//CPU平均使用率
	
	private RAM ram;	//RAM平均使用率
	
	private List<Partition> partitions;	//分区信息
	
	private List<Interface> interfaces;	//接口信息
	
	private HomeDefaultInterfaceBo homeDefaultInterfaceBo;	//默认网络接口信息
	

	public BaseInfo getBase() {
		return base;
	}

	public void setBase(BaseInfo base) {
		this.base = base;
	}
	
	public List<Partition> getPartitions() {
		return partitions;
	}

	public void setPartitions(List<Partition> partitions) {
		this.partitions = partitions;
	}

	public List<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	public CPU getCpu() {
		return cpu;
	}

	public void setCpu(CPU cpu) {
		this.cpu = cpu;
	}

	public RAM getRam() {
		return ram;
	}

	public void setRam(RAM ram) {
		this.ram = ram;
	}

	public HomeDefaultInterfaceBo getHomeDefaultInterfaceBo() {
		return homeDefaultInterfaceBo;
	}

	public void setHomeDefaultInterfaceBo(
			HomeDefaultInterfaceBo homeDefaultInterfaceBo) {
		this.homeDefaultInterfaceBo = homeDefaultInterfaceBo;
	}
}
