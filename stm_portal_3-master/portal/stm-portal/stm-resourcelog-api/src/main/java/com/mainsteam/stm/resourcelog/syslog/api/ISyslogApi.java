package com.mainsteam.stm.resourcelog.syslog.api;

import java.util.List;

import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogBo;

/**
 * <li>文件名称: ISyslogApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月5日
 * @author   ziwenwen
 */
public interface ISyslogApi{
	/**
	 * <pre>
	 * 根据域id获取域下加入syslog策略的资源
	 * </pre>
	 * @param domainIds
	 * @return
	 */
	List<SysLogRuleBo> getResource(Long[] domainIds);
	
	/**
	 * <pre>
	 * 根据资源id、日志级别和关键字查询当日日志数据
	 * </pre>
	 * @param resourceId
	 * @param level
	 * @param keywords
	 * @return
	 */
	List<SyslogBo> getCurDateLogs(Long resourceId,int level,String keywords);
	
	/**
	 * <pre>
	 * 根据资源id、日志级别和关键字查询所有日志数据
	 * </pre>
	 * @param resourceId
	 * @param level
	 * @param keywords
	 * @return
	 */
	List<SyslogBo> getLogs(Long resourceId,int level,String keywords);
}


