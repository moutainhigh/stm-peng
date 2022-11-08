/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

/**
 * <li>文件名称: NetFlow.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要:</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年12月9日
 * @author lil
 */
public class Whole {

	// 全部流量
	private Long wholeFlows;
	// 全部包数
	private Long wholePackets;
	// 全部链接数
	private Long wholeConnects;

	/**
	 * @return the wholeFlows 全部流量
	 */
	public Long getWholeFlows() {
		return wholeFlows;
	}

	/**
	 * @param wholeFlows
	 *            the wholeFlows to set 全部流量
	 */
	public void setWholeFlows(Long wholeFlows) {
		this.wholeFlows = wholeFlows;
	}

	public Long getWholePackets() {
		return wholePackets;
	}

	public void setWholePackets(Long wholePackets) {
		this.wholePackets = wholePackets;
	}

	/**
	 * @return the wholeConnects 全部链接数
	 */
	public Long getWholeConnects() {
		return wholeConnects;
	}

	/**
	 * @param wholeConnects
	 *            the wholeConnects to set 全部链接数
	 */
	public void setWholeConnects(Long wholeConnects) {
		this.wholeConnects = wholeConnects;
	}

	@Override
	public String toString() {
		return "wholeFlows:" + this.wholeFlows + ", wholePacket:"
				+ this.wholePackets + ", wholeConnects:" + this.wholeConnects;
	}

}
