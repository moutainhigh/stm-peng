package com.mainsteam.stm.resourcelog.data;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.AlarmSenderProp;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.dict.api.IDictApi;
import com.mainsteam.stm.platform.dict.bo.Dict;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.resourcelog.dao.SyslogDao;
import com.mainsteam.stm.resourcelog.data.bigData.LogDataLine;
import com.mainsteam.stm.resourcelog.data.bigData.UdpSenderForBigData;
import com.mainsteam.stm.resourcelog.syslog.api.ISyslogStrategyApi;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;
import com.mainsteam.stm.resourcelog.util.MsgEnum;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.transfer.TransferDataHandler;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * <li>文件名称: SyslogDataHandler.java</li>
 * <li>公 司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年12月3日
 * @author ziwenwen
 */
public class SyslogTransferHandler implements TransferDataHandler {
	
	private static final Logger log = Logger.getLogger(SyslogTransferHandler.class);

	private IDictApi iDictApi;

	private ISystemConfigApi configApi;

	private AlarmService alarmService;

	private IResourceApi resourceApi;

	private SyslogDao syslogDao;

	private ISyslogStrategyApi strategyApi;

	private CapacityService capacityService;

	public void setiDictApi(IDictApi iDictApi) {
		this.iDictApi = iDictApi;
	}

	public void setConfigApi(ISystemConfigApi configApi) {
		this.configApi = configApi;
	}

	public void setAlarmService(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

	public void setResourceApi(IResourceApi resourceApi) {
		this.resourceApi = resourceApi;
	}

	public void setSyslogDao(SyslogDao syslogDao) {
		this.syslogDao = syslogDao;
	}

	public void setStrategyApi(ISyslogStrategyApi strategyApi) {
		this.strategyApi = strategyApi;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public int getPort() {
		int port = Integer.parseInt(configApi
				.getSystemConfigById(SystemConfigConstantEnum.SYSTEM_CONFIG_LOG_SYSLOG.getCfgId()).getContent());
		return port;
	}

	private void handleData(String ip, byte[] arg1) {
		//记录大于号的位置
		int tempPosition = 0;
		int value = 0;
		int level;
		for (int i = 0; i < arg1.length; i++) {
			if (62 == arg1[i]) {
				tempPosition = i;
				break;
			}
		}
		byte[] msgByte = new byte[arg1.length - tempPosition - 1];
		for (int i = 1; i < tempPosition; i++) {
			value = (value * 10) + arg1[i];
		}
		level = value & 7;
		if(log.isDebugEnabled()){
			log.debug("compute device " + ip + " syslog level is " + level);
		}
		String levelName = "";
		List<Dict> listDicts = iDictApi.get("syslog_level");
		for (Dict dict : listDicts) {
			if (dict.getCode().equals(String.valueOf(level))) {
				levelName = dict.getName();
				break;
			}
		}
		for (int i = 0; i < msgByte.length; i++) {
			msgByte[i] = arg1[i + tempPosition + 1];
		}

		String msgContent = "";
		try {
			msgContent = new String(msgByte, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			log.error("handleData", e1);
			return;
		}
		if(log.isDebugEnabled()) {
			log.debug("receive syslog message from device " + ip + ",content:" + msgContent);
		}

		//设置查询条件
		SyslogResourceBo resBo = new SyslogResourceBo();
		resBo.setResourceIp(ip);
		resBo.setStrategyType(1);//syslog

		List<SyslogResourceBo> listResBo = this.strategyApi.getResource(resBo);
		if (listResBo != null && !listResBo.isEmpty()) {
			if(log.isDebugEnabled()) {
				log.debug("find syslog resource, device ip is " + ip);
			}
			//是否有日志更新
			boolean isSyslogUpdate = false;
			//如果有多条只取第一条
			resBo = listResBo.get(0);
			//如果为监听状态
			if (1 == resBo.getIsMonitor()) {
				//根据策略ID得到规则
				List<SysLogRuleBo> listRule = this.strategyApi.getRules(resBo.getStrategyId());
				String[] keyWords;
				for (SysLogRuleBo ruleBo : listRule) {
					if (1 == ruleBo.getIsOpen()) {//如果规则为打开状态，则进行以下操作
						keyWords = ruleBo.getKeywords().split(",");
						boolean isMatched = false;
						boolean isLevelMatch = false;

						//是否匹配关键字
						for (int i = 0; i < keyWords.length; i++) {
							if (0 == ruleBo.getLogicType()) {//判断关键字的逻辑--0表示或
								if (msgContent.indexOf(keyWords[i]) != -1) {
									isMatched = true;
									if(log.isDebugEnabled()) {
										StringBuffer sb = new StringBuffer(50);
										sb.append("match the keyword ");
										sb.append(keyWords[i]);
										sb.append(", device is ");
										sb.append(ip);
										log.debug(sb.toString());
									}
									break;
								}
							}else if (1 == ruleBo.getLogicType()) {//1--表示与
								if (msgContent.indexOf(keyWords[i]) == -1) {//有一个不匹配则返回false
									isMatched = false;
									if(log.isDebugEnabled()) {
										StringBuffer sb = new StringBuffer(50);
										sb.append("don't match the keyword ");
										sb.append(keyWords[i]);
										sb.append(", device is ");
										sb.append(ip);
										log.debug(sb.toString());
									}
									break;
								}else {
									isMatched = true;
								}
							}
						}
						if(log.isDebugEnabled() && !isMatched) {
							log.debug("Can not match keywords " + keyWords + ", rule(id/logicType) is {"+ruleBo.getId() + "/"+
									ruleBo.getLogicType()+", device ip is " + ip);
						}
						String logLevel = ruleBo.getLogLevel();
						if (StringUtils.isNotBlank(logLevel)) {
							if (logLevel.contains(String.valueOf(level))) {//判断日志级别是否匹配
								isLevelMatch = true;
							}else {
								if(log.isDebugEnabled()) {
									StringBuffer sb = new StringBuffer(50);
									sb.append("don't match the syslog level ");
									sb.append(level);
									sb.append(", setting level is ");
									sb.append(logLevel);
									sb.append(",device is");
									sb.append(ip);
									log.debug(sb.toString());
								}
							}
						} else {//如果loglevel为空，则忽略该条件
							isLevelMatch = true;
						}

						if (isMatched && isLevelMatch) {//如果关键字和日志级别都匹配则把消息内容插入历史记录

							isSyslogUpdate = true;
							SyslogBo syslogBo = new SyslogBo();
							syslogBo.setResourceId(resBo.getResourceId());
							syslogBo.setAlertTime(new Date());
							resBo.setLastDate(new Date());
							syslogBo.setKeyWords(ruleBo.getKeywords());
							syslogBo.setLevel(String.valueOf(level));
							syslogBo.setMsg(msgContent);
							if(log.isDebugEnabled()) {
								StringBuffer stringBuffer = new StringBuffer();
								stringBuffer.append("starts to save syslog message, device is ");
								stringBuffer.append(ip);
								stringBuffer.append("message is ");
								stringBuffer.append(JSONObject.toJSONString(syslogBo));
								log.debug(stringBuffer.toString());
							}
							// malachi syslog日志入库
							this.syslogDao.saveSyslogHistroy(syslogBo);
							//同步到ITBA
							sendITBA(syslogBo, resBo, msgContent);
							//判断告警是否打开
							if (1 == ruleBo.getIsAlarm()) {
								sendAlarm(resBo,levelName,ruleBo,msgContent);
							}else {
								if(log.isInfoEnabled())
									log.info("close to send alarm syslog(strategyId/ruleId) {" + ruleBo.getStrategyId() + "/" + ruleBo.getId() + "}");
							}
						}
					}else {
						if(log.isInfoEnabled())
							log.info(ruleBo.getId() + ruleBo.getName()+" is closed");
					}
				}
				//更新当日产生数量和产生总量
				if (isSyslogUpdate) {//如果有日志插入
					SyslogBo syslogBo = new SyslogBo();
					syslogBo.setResourceId(resBo.getResourceId());
					int allCount = this.syslogDao.countHistory(syslogBo);
					//查询当天产生的数量
					syslogBo.setAlertTime(new Date());
					int curDateCount = this.syslogDao.countHistory(syslogBo);
					resBo.setAllCount(allCount);
					resBo.setCurDateCount(curDateCount);
					this.syslogDao.updateResourceSta(resBo);
				}
			}else {
				if(log.isInfoEnabled()) {
					StringBuffer sb = new StringBuffer(50);
					sb.append("device ip is ").append(ip);
					sb.append(", strategy is no monitored ").append(resBo.getStrategyId());
					log.info(sb.toString());
				}
			}
		}else {
			if(log.isInfoEnabled()){
				StringBuffer sb = new StringBuffer(100);
				sb.append("Can not find syslog profile ").append(ip);
				log.info(sb.toString());
			}
		}
	}

	private void sendAlarm(SyslogResourceBo resBo, String levelName, SysLogRuleBo ruleBo, String msgContent) {
		ResourceInstanceBo res =  resourceApi.getResource(resBo.getResourceId());
		String deviceType = capacityService.getResourceDefById(res.getResourceId()).getName();
		//String[] parentResource = resourceApi.getCategoryParents(res.getCategoryId());
		List<AlarmSenderProp> listProps = new ArrayList<AlarmSenderProp>();
		AlarmSenderProp alarmSenderProp = new AlarmSenderProp();
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(MsgEnum.ORIGIN.toString(), deviceType + "(" + resBo.getResourceIp() +")");
		parameterMap.put(MsgEnum.LEVEL.toString(), levelName);
		parameterMap.put(MsgEnum.KEYWORDS.toString(), ruleBo.getKeywords());
		alarmSenderProp.setMap(parameterMap);
		listProps.add(alarmSenderProp);

		//发送用户告警
		AlarmSenderParamter alarmParamter = new AlarmSenderParamter();
		alarmParamter.setSourceID(String.valueOf(resBo.getResourceId()));
		alarmParamter.setSourceIP(resBo.getResourceIp());
		alarmParamter.setSourceName(res.getName());
		alarmParamter.setGenerateTime(new Date());
		alarmParamter.setDefaultMsg(msgContent);
		alarmParamter.setDefaultMsgTitle(deviceType + "(" + resBo.getResourceIp() +")产生"+levelName+"级别日志，关键字为"+ruleBo.getKeywords());
		alarmParamter.setProfileID(resBo.getStrategyId());//策略ID
		alarmParamter.setProp(listProps);
		alarmParamter.setRecoverKeyValue(null);

		alarmParamter.setExt0(res.getResourceId());
		alarmParamter.setExt2(SysModuleEnum.SYSLOG.toString());

		alarmParamter.setLevel(InstanceStateEnum.valueOf(ruleBo.getAlarmLevel()));
		alarmParamter.setProvider(AlarmProviderEnum.OTHER_SYS);
		alarmParamter.setRuleType(AlarmRuleProfileEnum.sysLog);
		alarmParamter.setSysID(SysModuleEnum.SYSLOG);
		try {
			alarmService.notify(alarmParamter);
		} catch (Exception e) {
			log.error("Send syslog alarm failed," + e.getMessage(), e);
		}
	}

	private void sendITBA(SyslogBo syslogBo, SyslogResourceBo resBo, String msgContent) {
		if(UdpSenderForBigData.getInstance().allowSync()){
			LogDataLine dataLine=new LogDataLine();
			dataLine.setLevel(syslogBo.getLevel());
			dataLine.setHostIp(resBo.getResourceIp());
			dataLine.setDateTime(System.currentTimeMillis());
			dataLine.setContent(msgContent);

			try {
				byte[] msg = JSON.toJSONString(dataLine).getBytes("UTF-8");
				UdpSenderForBigData.getInstance().sendMsg(msg, UdpSenderForBigData.SYNC_DATA_SYSLOG);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
	}

	@Override
	public TransferDataType getDataTransferType() {
		return TransferDataType.SyslogTrap;
	}

	@Override
	public void handle(TransferData arg0) {
		SyslogTransferData data = (SyslogTransferData) arg0.getData();
		handleData(data.getIp(), data.getMsg());
	}
}
