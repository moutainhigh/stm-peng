package com.mainsteam.stm.portal.business.report.api;

import com.mainsteam.stm.portal.business.report.obj.BizSerReportEvent;

/**
 * <li>文件名称: BizSerReportListener.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ..业务应用报告监听接口.</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   caoyong
 */
public interface BizSerReportListener {
	/**
	 * 实现接口，处理自己业务
	 * @param event
	 */
	void listen(BizSerReportEvent event);
}
