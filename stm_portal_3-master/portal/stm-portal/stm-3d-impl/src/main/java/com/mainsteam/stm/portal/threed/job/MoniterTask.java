package com.mainsteam.stm.portal.threed.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.portal.threed.api.ICabinetApi;
import com.mainsteam.stm.portal.threed.bo.BaseResult;
import com.mainsteam.stm.util.SpringBeanUtil;

public class MoniterTask {
	private static final Log logger = LogFactory.getLog(MoniterTask.class);
	
	public void start() throws Exception{
		ICabinetApi cabinetApi = (ICabinetApi) SpringBeanUtil.getObject("cabinetApi");
		BaseResult result = cabinetApi.pushMoniter();
		logger.info("3d监控推送"+(result.isSuccess()?"成功":"失败")+",说明："+result.getData());
	}
	
}
