package com.mainsteam.stm.common.metric.sync;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.sync.DataSyncService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

public class InstanceLifeChangeMonitor  implements InstancelibInterceptor {
	Log logger=LogFactory.getLog(InstanceLifeChangeMonitor.class);
	private DataSyncService dataSyncService;

	public void setDataSyncService(DataSyncService dataSyncService) {
		this.dataSyncService = dataSyncService;
	}

	@Override
	public void interceptor(InstancelibEvent instancelibEvent) throws Exception {
		
		logger.info("InstanceLifeChangeMonitor:"+JSON.toJSONString(instancelibEvent)+"\n"+JSON.toJSONString(instancelibEvent.getCurrent()));
		
		EventEnum eventType=instancelibEvent.getEventType(); 
		
		try{
			if(eventType == EventEnum.INSTANCE_UPDATE_STATE_EVENT ){
				List<ResourceInstance> list= (List<ResourceInstance>)instancelibEvent.getCurrent();
				for (ResourceInstance instance : list) {
					handle(instance.getId(),instance.getLifeState(),0L);
				}
			}else if(eventType ==EventEnum.INSTANCE_DELETE_EVENT){
				List<Long> list= (List<Long>) instancelibEvent.getSource();
				for(Long insID:list){
					handle(insID,InstanceLifeStateEnum.DELETED,0L);
				}
			}else if(eventType == EventEnum.INSTANCE_CHILDREN_DELETE_EVENT) {
				List<ResourceInstance> list= (List<ResourceInstance>) instancelibEvent.getSource();
				for(ResourceInstance ins:list){
					handle(ins.getId(),InstanceLifeStateEnum.DELETED,ins.getParentId());
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	private void handle(Long instanceID,InstanceLifeStateEnum lifeState, Long parentId) {
		
		InstanceProfileChange pc=new InstanceProfileChange();
		pc.setInstanceID(instanceID);
		if(parentId.longValue() != 0L)
			pc.setParentID(parentId);
		pc.setLifeState(lifeState);
		DataSyncPO po=new DataSyncPO();
		po.setCreateTime(new Date());
		po.setUpdateTime(po.getCreateTime());
		po.setType(DataSyncTypeEnum.INSTANCE_STATE);
		po.setData(JSON.toJSONString(pc));
		
		dataSyncService.save(po);
		logger.info("save instance profile Change: " + po);
	}
	
	

}
