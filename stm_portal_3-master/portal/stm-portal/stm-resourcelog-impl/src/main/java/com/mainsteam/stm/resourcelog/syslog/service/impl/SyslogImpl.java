package com.mainsteam.stm.resourcelog.syslog.service.impl;

import java.util.List;

import com.mainsteam.stm.resourcelog.syslog.api.ISyslogApi;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogBo;

/**
 * <li>文件名称: SyslogImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月11日
 * @author   ziwenwen
 */
public class SyslogImpl implements ISyslogApi {

	@Override
	public List<SysLogRuleBo> getResource(Long[] domainIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SyslogBo> getCurDateLogs(Long resourceId, int level,
			String keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SyslogBo> getLogs(Long resourceId, int level, String keywords) {
		// TODO Auto-generated method stub
		return null;
	}

}


