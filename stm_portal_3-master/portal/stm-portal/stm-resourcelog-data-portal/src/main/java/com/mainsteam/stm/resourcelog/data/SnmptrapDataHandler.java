package com.mainsteam.stm.resourcelog.data;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.AlarmSenderProp;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.platform.dict.api.IDictApi;
import com.mainsteam.stm.platform.dict.bo.Dict;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.resourcelog.dao.IStrategyDao;
import com.mainsteam.stm.resourcelog.dao.SnmpTrapDao;
import com.mainsteam.stm.resourcelog.dao.SyslogDao;
import com.mainsteam.stm.resourcelog.snmptrap.bo.SnmpLogBo;
import com.mainsteam.stm.resourcelog.syslog.api.ISyslogStrategyApi;
import com.mainsteam.stm.resourcelog.syslog.bo.SysLogRuleBo;
import com.mainsteam.stm.resourcelog.syslog.bo.SyslogResourceBo;
import com.mainsteam.stm.resourcelog.trap.SnmpTrapTransferVO;
import com.mainsteam.stm.resourcelog.util.MsgEnum;
import com.mainsteam.stm.resourcelog.vo.SnmptrapVo;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;

import com.mainsteam.stm.transfer.TransferDataHandler;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;


/**
 * <li>文件名称: TrapDataHandler.java</li>
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
// @Service("snmptrapDataHandler")
public class SnmptrapDataHandler implements TransferDataHandler {

	private static final Logger LOGGER = Logger.getLogger(SnmptrapDataHandler.class);

	private SyslogDao syslogDao;

	private SnmpTrapDao snmpTrapDao;

	private ISyslogStrategyApi strategyApi;

	private IResourceApi resourceApi;

	private IDictApi iDictApi;

	private AlarmService alarmService;

	public void setSyslogDao(SyslogDao syslogDao) {
		this.syslogDao = syslogDao;
	}

	public void setSnmpTrapDao(SnmpTrapDao snmpTrapDao) {
		this.snmpTrapDao = snmpTrapDao;
	}

	public void setResourceApi(IResourceApi resourceApi) {
		this.resourceApi = resourceApi;
	}

	public void setiDictApi(IDictApi iDictApi) {
		this.iDictApi = iDictApi;
	}

	public void setAlarmService(AlarmService alarmService) {
		this.alarmService = alarmService;
	}

	public void setStrategyApi(ISyslogStrategyApi strategyApi) {
		this.strategyApi = strategyApi;
	}

	public void handleData(SnmptrapVo snmptrapVo) {
		if(snmptrapVo instanceof SnmpTrapTransferVO) {
			((SnmpTrapTransferVO)snmptrapVo).setUserParentTrapName(true);
		}
		String msgContent = snmptrapVo.getMessage();
		//设置查询条件
		SyslogResourceBo resBo = new SyslogResourceBo();
		resBo.setResourceIp(snmptrapVo.getAddressIP());
		resBo.setStrategyType(2);//snmptrap
		if(LOGGER.isDebugEnabled())
			LOGGER.debug("receive trap message : " + msgContent + ", src ip:" + snmptrapVo.getAddressIP());

		List<SyslogResourceBo> listResBo = this.syslogDao.getResourceBo(resBo);
		if (listResBo != null && !listResBo.isEmpty()) {
			//只取第一条
			resBo = listResBo.get(0);
			if(StringUtils.isBlank(resBo.getResourceIp()))
				resBo.setResourceIp(snmptrapVo.getAddressIP());
			ResourceInstanceBo res = new ResourceInstanceBo();
			if(resBo.getResourceId() != 0) {
				res =  resourceApi.getResource(resBo.getResourceId());
			}
			LOGGER.debug("found trap strategy id : " + resBo.getStrategyId());
			//如果为监听状态
			try{
				if (1 == resBo.getIsMonitor()) {

					List<SysLogRuleBo> listRule = this.strategyApi.getRules(resBo.getStrategyId());
					//判断trap级别，逻辑或的关系
					int trapType = snmptrapVo.getGenericType();
					String levelName = "";
					boolean isMatched = false;
					boolean isLevelMatch = false;
					String[] keyWords = null;
					boolean isUpdateCount = false;
					for (SysLogRuleBo rule : listRule) {
						if (1 == rule.getIsOpen()) {//如果规则为打开状态，则进行以下操作
							if(StringUtils.isNotEmpty(rule.getKeywords())) {
								keyWords = rule.getKeywords().split(",");
								//是否匹配关键字
								for (int i = 0; i < keyWords.length; i++) {
									if (0 == rule.getLogicType()) {//判断关键字的逻辑--0表示或
										if (msgContent.indexOf(keyWords[i]) != -1) {
											isMatched = true;
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("match the keyword " + keyWords[i] + ", device is " + snmptrapVo.getAddressIP());
											}
											break;
										}
									} else if (1 == rule.getLogicType()) {//1--表示与
										if (msgContent.indexOf(keyWords[i]) == -1) {//有一个不匹配则返回false
											isMatched = false;
											if (LOGGER.isDebugEnabled()) {
												LOGGER.debug("can't match the keyword " + keyWords[i] + ", device is " + snmptrapVo.getAddressIP());
											}
											break;
										} else {
											isMatched = true;
										}
									}
								}

							}else{
								isMatched = true;
							}
							if (LOGGER.isDebugEnabled() && !isMatched) {
								LOGGER.debug("Can not match keywords " + keyWords + ", rule(id/logicType) is {"+rule.getId() + "/"+
										rule.getLogicType()+", device ip is " + snmptrapVo.getAddressIP());
							}
							String logLevel = rule.getLogLevel();
							if (StringUtils.isNotBlank(logLevel)) {
								if (logLevel.contains(String.valueOf(snmptrapVo.getGenericType()))) {//判断日志级别是否匹配
									isLevelMatch = true;
								} else {
									if (LOGGER.isDebugEnabled()) {
										LOGGER.debug("don't match the syslog level " + snmptrapVo.getGenericType() +
												", setting level is " + logLevel + ",device is"
												+ snmptrapVo.getAddressIP());
									}
								}
							} else {//如果loglevel为空，则忽略该条件
								isLevelMatch = true;
							}
						}
						if (isMatched && isLevelMatch) { //匹配就把日志存到历史记录中并发送告警
							SnmpLogBo snmpLogBo = new SnmpLogBo();
							isUpdateCount = true;
							if (resBo.getResourceId() == 0) {
								snmpLogBo.setResourceId(resBo.getResourceIp());
							}else {
								snmpLogBo.setResourceId(String.valueOf(resBo.getResourceId()));
							}
							List<Dict> listDicts = iDictApi.get("snmptrap_type");
							for (Dict dict : listDicts) {
								if (dict.getCode().equals(String.valueOf(trapType))) {
									levelName = dict.getName();
									break;
								}
							}
							snmpLogBo.setAlertTime(new Date());
							resBo.setLastDate(new Date());
							snmpLogBo.setLevel(String.valueOf(trapType));
							snmpLogBo.setMsg(msgContent);
							this.snmpTrapDao.saveSnmptrapHistroy(snmpLogBo);
							//发送告警信息
							if(rule.getIsAlarm() == 1){
								createAlarmSender(resBo, res, snmptrapVo.getAlertID(), levelName, rule.getAlarmLevel(), msgContent);
							}
						}else {
							LOGGER.debug("Can not match strategy, source ip is " + snmptrapVo.getAddressIP() + ",(ruleId/keywords/alarmLevel) is {"
								+ rule.getId() + "/" + Arrays.deepToString(keyWords) + "/" + rule.getLogLevel());
						}

					}
					if(isUpdateCount) {
						//以上操作完成后，更新当日产生数量和产生总量
						SnmpLogBo updateBo = new SnmpLogBo();
						if (resBo.getResourceId() == 0) {
							updateBo.setResourceId(resBo.getResourceIp());
						}else {
							updateBo.setResourceId(String.valueOf(resBo.getResourceId()));
						}
						int allCount = this.snmpTrapDao.countSnmptrapHistory(updateBo);
						//查询当天的
						updateBo.setAlertTime(new Date());
						int curDateCount = this.snmpTrapDao.countSnmptrapHistory(updateBo);

						LOGGER.debug("resource : "+resBo.getResourceId()+",current count : "+curDateCount+",total count : " + allCount);
						resBo.setAllCount(allCount);
						resBo.setCurDateCount(curDateCount);
						//如果资源ID为零
						if (resBo.getResourceId() == 0) {
							resBo.setResourceIdStr(resBo.getResourceIp());
						} else {
							resBo.setResourceIdStr(String.valueOf(resBo.getResourceId()));
						}
						//更新当日产生数量及历史产生数量
						this.snmpTrapDao.updateTrapLogNum(resBo);
					}

				}else {
					LOGGER.info("strategy ["+resBo.getStrategyId()+"] is not listening...");
				}
			}catch (Exception e) {
				LOGGER.warn("occur error while dealing trap message, " + e.getMessage(), e);
			}

		}else {
			LOGGER.info("Can not find strategy..." + resBo.getResourceIp());
		}
	}

	private void createAlarmSender(SyslogResourceBo resBo, ResourceInstanceBo res, String alertID,
                                                  String levelName, String alermLevel, String msgContent) {
		List<AlarmSenderProp> listProps = new ArrayList<AlarmSenderProp>();
		AlarmSenderProp alarmSenderProp = new AlarmSenderProp();
		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(MsgEnum.LEVEL.toString(), levelName);
		parameterMap.put(MsgEnum.RESNAME.toString(), res.getName());
		parameterMap.put(MsgEnum.RESIP.toString(), resBo.getResourceIp());
		parameterMap.put(MsgEnum.OCCURTIME.toString(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		alarmSenderProp.setMap(parameterMap);
		listProps.add(alarmSenderProp);
		//发送用户告警
		AlarmSenderParamter alarmParamter = new AlarmSenderParamter();
		alarmParamter.setSourceID(String.valueOf(resBo.getResourceId()));
		alarmParamter.setSourceIP(resBo.getResourceIp());
		alarmParamter.setSourceName(res.getName());
		alarmParamter.setGenerateTime(new Date());
		alarmParamter.setDefaultMsg(msgContent);
		alarmParamter.setDefaultMsgTitle(res.getName() + "("+resBo.getResourceIp()+")产生"+levelName + "类型的告警");
		alarmParamter.setProfileID(resBo.getStrategyId());//策略ID
		alarmParamter.setProp(listProps);
		alarmParamter.setRecoverKeyValue(null);
		alarmParamter.setExt0(res.getResourceId());
		alarmParamter.setExt2(SysModuleEnum.TRAP.name());//由于分类查询问题，强制设置为TRAP
		alarmParamter.setExt9(alertID);
		alarmParamter.setLevel(InstanceStateEnum.valueOf(alermLevel));
		alarmParamter.setProvider(AlarmProviderEnum.OTHER_SYS);
		alarmParamter.setRuleType(AlarmRuleProfileEnum.sysLog);
		alarmParamter.setSysID(SysModuleEnum.TRAP);

		try {
			alarmService.notify(alarmParamter);
		} catch (Exception e) {
			LOGGER.error("发送告警消息失败!", e);
		}
	}


	@Override
	public TransferDataType getDataTransferType() {
		return TransferDataType.SnmpTrap;
	}

	@Override
	public void handle(TransferData td) {
		handleData((SnmptrapVo) td.getData());
	}
}
