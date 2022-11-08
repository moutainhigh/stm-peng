package com.mainsteam.stm.home.del.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.home.workbench.main.bo.WorkBench;
import com.mainsteam.stm.home.workbench.main.dao.IUserWorkbenchDao;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.objenum.EventEnum;

/**
 * <li>文件名称: DelResourceListener.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月20日
 * @author   ziwenwen
 */
@Service
public class DelResourceListener implements InstancelibListener{
	
	private static final Logger log=Logger.getLogger(DelResourceListener.class);
	
	private static final String delMsg="删除资源实例-清除用户工作台设置：";
	
	@Autowired
	IUserWorkbenchDao workbenchDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		if(instancelibEvent.getEventType()==EventEnum.INSTANCE_DELETE_EVENT){
			delRelation((List<Long>)instancelibEvent.getSource());
		}
	}

	public void delRelation(List<Long> ids){
		for(Long id:ids){
			List<WorkBench> wbs=workbenchDao.getUserBenchIdByResourceId(id);
			for(WorkBench wb:wbs){
				if(wb.getSelfExt()==null)continue;
				if(wb.getWorkbenchId()!=6){
					wb.setSelfExt(null);
				}else{
					String self="";
					for(String s:wb.getSelfExt().split(",")){
						if(s.equals(id + ""))continue;
						self+=s+',';
					}
					if(self.length()>0)self=self.substring(0,self.length()-1);
					wb.setSelfExt(self);
				}
				workbenchDao.setUserBenchResourceIdById(wb);
			}
		}
	}
}


