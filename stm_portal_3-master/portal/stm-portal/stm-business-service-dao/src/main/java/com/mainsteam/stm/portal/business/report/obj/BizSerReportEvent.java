package com.mainsteam.stm.portal.business.report.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * <li>文件名称: BizSerReportEvent.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ..业务应用报表事件.</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月25日
 * @author   caoyong
 */
public class BizSerReportEvent {
	/**
	 * 事件sourceIds集合
	 */
	private List<Long> sourceIds = new ArrayList<Long>();
	public BizSerReportEvent(BizSerReport source){
		sourceIds.add(source.getId());
	}
	public List<Long> getSourceIds() {
		return sourceIds;
	}
	public void setSourceIds(List<Long> sourceIds) {
		this.sourceIds = sourceIds;
	}
}
