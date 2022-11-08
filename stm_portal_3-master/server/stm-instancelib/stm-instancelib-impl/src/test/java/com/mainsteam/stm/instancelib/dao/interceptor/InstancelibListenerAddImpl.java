package com.mainsteam.stm.instancelib.dao.interceptor;

import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.objenum.EventEnum;

public class InstancelibListenerAddImpl implements InstancelibListener {

	private InstancelibEventManager	instancelibEventManager;

	/**
	 *  是否执行 true 执行 false 没执行
	 */
	private boolean isExecute = false;

	public void start(){
		instancelibEventManager.register(this);
	}
	
	@Override
	public void listen(InstancelibEvent instancelibEvent) {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_ADD_EVENT){
			isExecute = true;
			System.out.println("add listener");
		}
	}
	
	public void setInstancelibEventManager(
			InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}
	public boolean isExecute() {
		return isExecute;
	}

	public void setExecute(boolean isExecute) {
		this.isExecute = isExecute;
	}


}
