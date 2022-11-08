package com.mainsteam.stm.simple.engineer.workbench.web.vo;

import java.io.Serializable;

import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.knowledge.bo.KnowledgeBo;

/**
 * <li>文件名称: ResolveFaultVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   ziwenwen
 */
public class ResolveFaultVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1942148830843924389L;
	private KnowledgeBo knowledge;
	private AlarmEvent resourceEvent;
	public KnowledgeBo getKnowledge() {
		return knowledge;
	}
	public void setKnowledge(KnowledgeBo knowledge) {
		this.knowledge = knowledge;
	}
	public AlarmEvent getResourceEvent() {
		return resourceEvent;
	}
	public void setResourceEvent(AlarmEvent resourceEvent) {
		this.resourceEvent = resourceEvent;
	}
}
