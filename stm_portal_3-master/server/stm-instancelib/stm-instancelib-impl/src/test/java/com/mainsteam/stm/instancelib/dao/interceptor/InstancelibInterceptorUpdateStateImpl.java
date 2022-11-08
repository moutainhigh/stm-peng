package com.mainsteam.stm.instancelib.dao.interceptor;

import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.objenum.EventEnum;

public class InstancelibInterceptorUpdateStateImpl implements InstancelibInterceptor {

	private InstancelibEventManager	instancelibEventManager;
	/**
	 *  是否执行 true 执行 false 没执行
	 */
	private boolean isExecute = false;
	public boolean isExecute() {
		return isExecute;
	}

	public void setExecute(boolean isExecute) {
		this.isExecute = isExecute;
	}

	public void setInstancelibEventManager(
			InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}

	public void start(){
		instancelibEventManager.register(this);
	}
	
	@Override
	public void interceptor(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_UPDATE_STATE_EVENT){
			isExecute = true;
			System.out.println("upadate state interceptor");
		}
	}


}
