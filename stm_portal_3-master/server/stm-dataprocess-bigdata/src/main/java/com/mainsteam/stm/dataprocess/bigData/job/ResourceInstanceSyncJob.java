package com.mainsteam.stm.dataprocess.bigData.job;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.bigData.dao.pojo.ResourceType;
import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.system.bigdata.bo.BigdataBo;
import com.mainsteam.stm.util.SpringBeanUtil;


@DisallowConcurrentExecution
public class ResourceInstanceSyncJob implements Job{

	private static final Log logger = LogFactory.getLog(ResourceInstanceSyncJob.class);
	
	private static final int RESOURCE_TYPE=3;
	
	
	private ScheduleManager scheduleManager;
	
	private  BigdataBo bigdataBo;

	
	private static String JOBKEY = "job_bigData_Instance";
	
	private static final String PATH = "/properties/bigdata.properties";
	
	
	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}

	public void start() {
		int[] param = getValue();
		String cornExpress="0 0 "+param[0]+" * * ?";
		try {
			if(scheduleManager.isExists(JOBKEY)){
				scheduleManager.updateJob(JOBKEY,new IJob(JOBKEY,this, cornExpress));
			}else{
				scheduleManager.scheduleJob(new IJob(JOBKEY,this, cornExpress));
			}
			
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ProfileDispatchJob error!", e);
			}
		}
	}
	
	private int[] getValue(){
		int hour = 0;
		int size = 0;
		InputStream inputStream = null;
		try {
			inputStream =ResourceInstanceSyncJob.class.getResourceAsStream(PATH);
			Properties propertie = new Properties();
			propertie.load(inputStream);
			hour = Integer.parseInt(propertie.getProperty("stm.bigdata.hour","1"));
			size = Integer.parseInt(propertie.getProperty("stm.bigdata.hour","5000"));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("get bigdata.properties error!", e);
			}
			hour = 1;
			size = 5000;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (logger.isErrorEnabled()) {
						logger.error("start", e);
					}
				}
			}
		}
		return new int[]{hour,size};
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("ResourceInstanceSync Job start.");
		}
		
		return;
	}
	
	public void syncResourceType() {
		List<ResourceType> list=new ArrayList<ResourceType>();
		
		Map<String,ResourceType> map=new HashMap<String,ResourceType>();
		
		CapacityService capacityService = SpringBeanUtil.getBean(CapacityService.class);
		
		for(ResourceDef rdef:capacityService.getResourceDefList()){
			try{
				ResourceType ctype=new ResourceType();
				ctype.setLevel(rdef.isMain()?4:5);
				ctype.setId(rdef.getId());
				ctype.setName(rdef.getName());
				ctype.setKmTypeEnum(rdef.isMain()?"MainResource":"SubResource");
				
				
				ctype.setParentId(rdef.isMain()?rdef.getCategory().getId():rdef.getParentResourceDef().getId());
				
				list.add(ctype);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
			
			
			if( rdef.getCategory()!=null){
				CategoryDef cdef=rdef.getCategory();
				CategoryDef parentDef=cdef.getParentCategory();
				
				String id=cdef.getId();
				String parent=cdef.getParentCategory().getId();
			
				
				ResourceType ct=map.get(id+"_"+parent);
				if(ct==null){
					ct=new ResourceType();
					ct.setLevel(3);
					ct.setId(cdef.getId());
					ct.setParentId(parentDef!=null?parentDef.getId():null);
					ct.setName(cdef.getName());
					ct.setDescription(cdef.getName());
					ct.setKmTypeEnum("Category");
					
					map.put(id+"_"+parent,ct);
				}
				
				if(parentDef!=null && !"Resource".equals(parentDef.getId())){
					ct=new ResourceType();
					ct.setLevel(2);
					ct.setId(parentDef.getId());
					ct.setName(parentDef.getName());
					ct.setDescription(parentDef.getName());
					ct.setKmTypeEnum("Category");
					ct.setParentId("Resource");
					
					map.put(parentDef.getId()+"_",ct);
				}
			}
			
			for(ResourceMetricDef mdef:rdef.getMetricDefs()){
				if(MetricTypeEnum.PerformanceMetric ==mdef.getMetricType()){
					ResourceType ctype=new ResourceType();
					ctype.setLevel(6);
					ctype.setId(mdef.getId());
					ctype.setName(mdef.getName());
					ctype.setKmTypeEnum("Metric");
					ctype.setParentId(rdef.getId());
					
					list.add(ctype);

				}
			}
		}
		
		sendMag(RESOURCE_TYPE,JSON.toJSONString(map.values()));
		
		sendMag(RESOURCE_TYPE,JSON.toJSONString(list));
		
	}

	
	private void sendMag(int type,String msg) {
		
		if(logger.isDebugEnabled())
			logger.debug("send:"+msg);
		
		String ip = bigdataBo.getIp();
		int port = bigdataBo.getPort();
		try(Socket s = new Socket();){
			//连接默认2秒超时
			s.connect(new InetSocketAddress(ip, port), 2000);
			//处理socket数据超时60秒
			s.setSoTimeout(60000);

			try(OutputStream out= s.getOutputStream();) {
				
				out.write(1);//版本
				out.write(type);//消息类型
				byte[] cont=msg.getBytes("UTF-8");
				out.write(intToByte(cont.length));//消息长度
				out.write(cont);
				out.flush();
				
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage(), e);
				}
			}
		}catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(), e);
			}
		} 
	}
	
	private static byte[] intToByte(int i) {   
		  byte[] result = new byte[4];   
		  result[0] = (byte)((i >> 24) & 0xFF);
		  result[1] = (byte)((i >> 16) & 0xFF);
		  result[2] = (byte)((i >> 8) & 0xFF); 
		  result[3] = (byte)(i & 0xFF);
		  return result;
	}
}
