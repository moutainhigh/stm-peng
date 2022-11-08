package com.mainsteam.stm.portal.business.report.api;

import com.mainsteam.stm.portal.business.report.obj.BizSerReportEvent;

/**
 * <li>文件名称: BizSerReportListenerEngine.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: .业务应用报告监听引擎接口..</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   caoyong
 */
public interface BizSerReportListenerEngine {
	/**
	 * 处理自己的业务
	 * @param event
	 */
	void handleListen(BizSerReportEvent event);
}
