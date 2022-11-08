package com.mainsteam.stm.home.workbench.host.vo;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.host.vo.HostPartion.java</li>
 * <li>文件描述: 主机分区信息</li>
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
public class Partition {

	private String letter;	//盘符
	private String rate;	//利用率
	private String status;	//状态
	public String getLetter() {
		return letter;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
