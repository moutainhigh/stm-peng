package com.mainsteam.stm.event;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.profile.fault.execute.obj.FaultScriptExecuteResult;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.AlarmFile;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.profilelib.fault.ProfileFaultService;
import com.mainsteam.stm.util.DateUtil;

public class AlarmSnapshotUtils {
	private static final Log logger=LogFactory.getLog(AlarmSnapshotUtils.class);
	
	private final String tmpFilePath=System.getProperty("java.io.tmpdir");
	private IFileClientApi fileClientApi;
	private ProfileFaultService profilefaultService;
	
	
	
	public String handleAlarm(long instanceID,String metricID,String ip,InstanceStateEnum state){
		try{
			FaultScriptExecuteResult result=profilefaultService.checkProfileFaultIsAlarmByInstanceAndMetric(String.valueOf(instanceID), metricID,state);
			AlarmFile m=new AlarmFile();
			
			boolean tag=false;
			
			if(StringUtils.isNotEmpty(result.getSnapshotFileContent())){
				syncFileServer(result.getSnapshotFileContent().getBytes(),ip,"snapshot",false,m);
				tag=true;
			}else{
				logger.debug("not find SnapshotFile content for["+instanceID+","+metricID+"]");
			}
			if(StringUtils.isNotEmpty(result.getRecoveryFileContent())){
				syncFileServer(result.getRecoveryFileContent().getBytes(),ip,"recovery",true,m);
				tag=true;
			}else{
				logger.debug("not find RecoveryFile content for["+instanceID+","+metricID+"]");
			}
			return tag?JSON.toJSONString(m):null;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return null;
	}
	
	
	private void syncFileServer(byte[] result,String ip,String prefix,boolean isRecover,AlarmFile alarmFile) {
		
		String fileName=prefix+"_"+(StringUtils.isEmpty(ip)?"":(ip))+"_";
		fileName+=DateUtil.formatWithSimple(new Date(),"yyyyMMddHHmmss")+".txt";
		
		if(logger.isInfoEnabled()){
			logger.info("create result file:"+fileName);
		}
		
		File tmpFile=new File(tmpFilePath+File.separator+fileName);
		long fileID=0;
		if(logger.isInfoEnabled()){
			logger.info("create tmpFile filePath:"+tmpFile.getAbsolutePath());
		}
		try {
			if(tmpFile.createNewFile()){
				FileOutputStream fout=new FileOutputStream(tmpFile);
				fout.write(result);
				fout.close();
				
				if(logger.isInfoEnabled()){
					logger.info("create tmpFile filePath:"+tmpFile.getAbsolutePath());
				}
				
				fileID= fileClientApi.upLoadFile(FileGroupEnum.STM_ALARM, tmpFile);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		if(isRecover){
			alarmFile.setRecoverFileId(fileID);
			alarmFile.setRecoverFileName(fileName);
		}else{
			alarmFile.setSnapshotFileId(fileID);
			alarmFile.setSnapshotFileName(fileName);
		}
		
	}
	
	public void setProfilefaultService(ProfileFaultService profilefaultService) {
		this.profilefaultService = profilefaultService;
	}
	public void setFileClientApi(IFileClientApi fileClientApi) {
		this.fileClientApi = fileClientApi;
	}
}
