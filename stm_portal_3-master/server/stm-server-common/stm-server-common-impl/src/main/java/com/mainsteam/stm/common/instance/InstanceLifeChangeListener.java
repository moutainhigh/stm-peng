package com.mainsteam.stm.common.instance;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.instance.dao.InstanceLifeCycleDAO;
import com.mainsteam.stm.common.instance.dao.obj.InstanceLifeCycle;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

public class InstanceLifeChangeListener implements InstancelibInterceptor {
	private InstanceLifeCycleDAO instanceLifeCycleDAO;

	public void setInstanceLifeCycleDAO(InstanceLifeCycleDAO instanceLifeCycleDAO) {
		this.instanceLifeCycleDAO = instanceLifeCycleDAO;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) throws Exception {
		EventEnum eventType=instancelibEvent.getEventType();
		if(eventType == EventEnum.INSTANCE_UPDATE_STATE_EVENT ){
			List<ResourceInstance> instances=(List<ResourceInstance>)instancelibEvent.getCurrent();
			for (ResourceInstance instance : instances) {
				InstanceLifeCycle po=new InstanceLifeCycle();
				po.setInstanceID(instance.getId());
				po.setState(instance.getLifeState()==InstanceLifeStateEnum .MONITORED?InstanceStateEnum.MONITORED:InstanceStateEnum.NOT_MONITORED);
				po.setChangeTime(new Date());
				instanceLifeCycleDAO.addLifeCycle(po);
			}
		}
	}
}
