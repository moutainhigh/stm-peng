package com.mainsteam.stm.portal.threed.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.alarm.event.AlarmEventMonitor;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.portal.threed.bo.BaseResult;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;
import com.mainsteam.stm.portal.threed.dao.ICabinetDao;
import com.mainsteam.stm.portal.threed.dao.impl.Adapter3DInterfaceImpl;
import com.mainsteam.stm.portal.threed.dao.impl.DataStreamImpl;
import com.mainsteam.stm.util.SpringBeanUtil;

public class ThreeDAlarmImpl implements AlarmEventMonitor{
	private static final Log log = LogFactory.getLog(ThreeDAlarmImpl.class);
//	private  DataStreamImpl dataStreamImpl;
	@Resource
	private Adapter3DInterfaceImpl adapter3DInterfaceImpl;
	private ICabinetDao cabinetDao;
	@Override
	public void handleEvent(AlarmEvent event) {
		try {
			CabinetBo bo = cabinetDao.get(Long.parseLong(event.getSourceID()));
			if(bo!=null){
				if(event.getLevel()==InstanceStateEnum.NORMAL){//恢复告警、关闭3D告警
					BaseResult result = adapter3DInterfaceImpl.closeAlarm(event);
					log.info("3d报警关闭"+(result.isSuccess()?"成功":"失败")+",说明："+result.getData());
				}else{
					BaseResult result = adapter3DInterfaceImpl.pushAlarm(event);
					log.info("3d报警推送"+(result.isSuccess()?"成功":"失败")+",说明："+result.getData());
				}				
			}else{
				log.warn("3D机房无设备id为"+event.getSourceID()+"的设备");
			}
		} catch (Exception e) {
			log.error("3D报警数据推送异常", e);
		}
	}
//	public void setDataStreamImpl(DataStreamImpl dataStreamImpl) {
//		this.dataStreamImpl = dataStreamImpl;
//	}
	@PostConstruct
	public void init(){
		this.cabinetDao = SpringBeanUtil.getBean(ICabinetDao.class);
		log.info("com.mainsteam.stm.portal.threed.service.impl.ThreeDAlarmImpl init");
	}
	
}
