package com.mainsteam.stm.topo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourcePropertyDef;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.topo.api.IResourceInstanceExApi;
import com.mainsteam.stm.topo.api.ITopoFindHandler;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.MapLineBo;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.collector.TopoCollectorMBean;
import com.mainsteam.stm.topo.dao.ILinkDao;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.dao.MapLineDao;
import com.mainsteam.stm.topo.enums.IconState;
import com.mainsteam.stm.util.ResourceOrMetricConst;
import com.mainsteam.stm.util.SecureUtil;
import com.qwserv.itm.netprober.bean.DevType;
import com.qwserv.itm.netprober.bean.StatusProcess;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ThirdServiceImpl implements ThirdService, InstancelibInterceptor {
    Logger logger = Logger.getLogger(ThirdServiceImpl.class);
    Logger serverLogger = Logger.getLogger("com.mainsteam.stm.toposerver");
    //??????????????????
    @Resource(name = "resourceInstanceService")
    private ResourceInstanceService rsvc;
    @Autowired
    private IResourceInstanceExApi resourceExApi;
    @Autowired
    @Qualifier("licenseCalcService")
    private ILicenseCalcService license;
    //???????????????
    @Autowired
    private CapacityService csvc;
    //????????????
    @Autowired
    private ProfileService psvc;
    private int memFlagCount = 0;
    @Autowired
    private NodeService nsvc;
    //Domain ??????
    @Autowired
    private IDomainApi dapi;
    @Autowired
    //?????????????????????
    private ModulePropService moudlePropSvc;
    //????????????
    @Autowired
    private MetricDataService mdsvc;
    //????????????????????????
    @Autowired
    private InstanceStateService instStateSvc;
    @Resource
    private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
    //??????dao
    @Autowired
    private INodeDao ndao;
    //??????dao
    @Autowired
    private ILinkDao ldao;
    //????????????????????????
    @Autowired
    private ResourceInstanceDiscoveryService resDiscoverSvc;
    @Autowired
    private ModulePropService modulePropSvc;
    @Autowired
    private ISearchApi searchApi;
    //????????????
    @Autowired
    private ProfileService profileSvc;
    //??????????????????
    @Autowired
    private OCRPCClient client;
    //?????????????????????
    @Autowired
    private MetricStateService metricStateSvc;
    @Autowired
    private ISettingDao settingDao;
    @Autowired
    private DataHelper dataHelper;
    @Resource
    private CapacityService capacityService;
    //????????????????????????
    private Stack<NodeBo> nodeStack = new Stack<NodeBo>();
    //?????????????????????
    private Queue<List<LinkBo>> linkQueue = new ConcurrentLinkedQueue<List<LinkBo>>();
    //??????????????????????????????
    private JSONArray links = new JSONArray();
    @Autowired
    private LinkService linkService;
    @Resource
    private IResourceApi resApi;
    @Autowired
    private MapLineDao maplineDao;

    private TopoCollectorMBean collector;
    private Thread topoFindThread = null;
    private Thread topoInstanceThread = null;
    private boolean topoInstanceThreadOver = false;
    private boolean topoFindThreadOver = false;

    /**
     * ????????????categoryId????????????????????????
     *
     * @param categoryId
     * @return
     */
    public Map<String, String> getDeviceType(List<Map<String, String>> categorys, String categoryId) {
        Map<String, String> result = new HashMap<String, String>();
        for (Map<String, String> category : categorys) {
            if (category.get("id").toString().equals(categoryId)) {
                result = category;
                break;
            }
        }
        return result;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @return {"id": "Windows","name": "Windows?????????","pid": "Host","type": "??????"}
     */
    public List<Map<String, String>> getAllCategory() {
        CategoryDef categoryDef = capacityService.getRootCategory();
        CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
        List<Map<String, String>> baseList = new ArrayList<Map<String, String>>();
        if (null != baseCategoryDef && baseCategoryDef.length > 0) {
            for (CategoryDef base : baseCategoryDef) {
                CategoryDef[] childCategoryDef = base.getChildCategorys();
                if (null != childCategoryDef && childCategoryDef.length > 0) {
                    for (CategoryDef child : childCategoryDef) {
                        Map<String, String> result = new HashMap<String, String>();
                        result.put("id", child.getId());        //????????????(???????????????)
                        result.put("name", child.getName());    //??????????????????
                        result.put("pid", base.getId());        //????????????
                        result.put("type", base.getName());        //????????????
                        baseList.add(result);
                    }
                }
            }
        }
        return baseList;
    }

    @Override
    public void monitorBatch(Long[] instanceIds, String type) throws ProfilelibException {
        List<Long> ids = new ArrayList<Long>();
        if ("add".equals(type)) {    //????????????
            long[] instanceIdsTmp = new long[instanceIds.length];
            for (int i = 0; i < instanceIds.length; i++) {
                instanceIdsTmp[i] = instanceIds[i];
            }
            List<LinkBo> links = ldao.getLinksByInstanceIds(instanceIdsTmp);

            for (LinkBo link : links) {
                if (null != link.getFromIfIndex() && null != link.getToIfIndex()) {    //????????????????????????????????????????????????
                    ids.add(link.getInstanceId());
                } else {
                    logger.error("????????????????????????????????????????????????,?????????????????????????????????instanceId=" + link.getInstanceId());
                }
            }
            profileSvc.enableMonitorForLink(ids);
        } else if ("cancel".equals(type)) {    //????????????
            profileSvc.cancleMonitor(ids);
        }
    }

    public String[] getMetrics(Long instId, String metricId, MetricTypeEnum type) {
        MetricData md = null;
        switch (type) {
            case AvailabilityMetric:
                md = mdsvc.getMetricAvailableData(instId, metricId);
                break;
            case InformationMetric:
                md = infoMetricQueryAdaptService.getMetricInfoData(instId, metricId);
                break;
            case PerformanceMetric:
                md = mdsvc.getMetricPerformanceData(instId, metricId);
                break;
        }
        if (md != null) {
            return md.getData();
        } else {
            return null;
        }
    }

    public JSONArray getMetric(String oid) {
        ResourceDef def = csvc.getResourceDefById(getResourceId(oid));
        JSONArray retn = new JSONArray();
        for (ResourceDef d : def.getChildResourceDefs()) {
            JSONObject tmp = new JSONObject();
            tmp.put("name", d.getName());
            tmp.put("id", d.getId());
            retn.add(tmp);
        }
        return retn;
    }

    @Override
    public JSONArray getResourceDef(String oid) {
        ResourceDef def = csvc.getResourceDefById(getResourceId(oid));
        JSONArray retn = new JSONArray();
        for (ResourcePropertyDef rp : def.getPropertyDefs()) {
            JSONObject tmp = new JSONObject();
            tmp.put("id", rp.getId());
            tmp.put("name", rp.getName());
            retn.add(tmp);
        }
        return retn;
    }

    @Override
    public String getResourceId(String oid) {
        String retn = csvc.getResourceId(oid);
        if (retn == null) {
            logger.error("capacityService.getResourceId()" + oid + "??????????????????resourceId");
        }
        return retn;
    }

    public JSONObject getModuleProps(NodeBo nb) {
        JSONObject retn = new JSONObject();
        try {
            ResourceInstance inst = rsvc.getResourceInstance(nb.getInstanceId());
            if (null != inst) {
                for (Object obj : getResourceDef(nb.getOid())) {
                    JSONObject tmp = (JSONObject) obj;
                    String key = tmp.getString("name");
                    retn.put(key, inst.getModulePropBykey(key));
                }
            }
        } catch (InstancelibException e) {
            logger.error(new Date().toString() + "??????????????????" + nb.getInstanceId() + "??????");
        }
        return retn;
    }

    @Override
    public JSONObject getResourceInstance(NodeBo nb) {
        try {
            ResourceInstance ri = rsvc.getResourceInstance(nb.getInstanceId());
            JSONObject retn = new JSONObject();
            if (ri != null) {
                retn.put("categoryId", ri.getCategoryId());
                retn.put("discoverWay", ri.getDiscoverWay());
                retn.put("domainId", ri.getDomainId());
                retn.put("discoverIp", ri.getShowIP());
                retn.put("lifeState", ri.getLifeState().name().toLowerCase());
                String showName = ri.getShowName();
                if (null == showName) {
                    showName = ri.getName();
                }
                retn.put("showName", showName);
            }
			/*List<Map<String,?>> datas = resourceExApi.getMerictRealTimeVals(new String[]{MetricIdConsts.METRIC_CPU_RATE,MetricIdConsts.METRIC_MEME_RATE}, new long[]{instanceId});
			for(Map<String,?> data:datas){
				//cpu?????????
				String cpuRate = resourceExApi.getMetricVal(data, MetricIdConsts.METRIC_CPU_RATE, null);
				try {
					retn.put("cpuRate",Float.parseFloat(cpuRate)/100);
				} catch (NumberFormatException e) {
					retn.put("cpuRate",0);
				}
				//???????????????
				String rameRate = resourceExApi.getMetricVal(data, MetricIdConsts.METRIC_MEME_RATE,null);
				try {
					retn.put("ramRate",Float.parseFloat(rameRate)/100);
				} catch (NumberFormatException e) {
					retn.put("ramRate",0);
				}
			}*/
			/*//??????profileId
			ProfileInfo pinfo = profileSvc.getBasicInfoByResourceInstanceId(instanceId);
			if(null!=pinfo){
				retn.put("profileId", pinfo.getProfileId());
			}*/
            return retn;
        } catch (InstancelibException e) {
            logger.error(e);
        }
        return null;
    }

    //????????????
    public boolean addMonitor(Long instanceId) {
        try {
            psvc.enableMonitor(instanceId);
            ResourceInstance res = rsvc.getResourceInstance(instanceId);
            InstanceLifeStateEnum state = res.getLifeState();
            if (state == InstanceLifeStateEnum.MONITORED) {
                return true;
            } else {
                logger.error(instanceId + "??????????????????");
                return false;
            }
        } catch (ProfilelibException | InstancelibException e) {
            logger.error(instanceId + "??????????????????");
            return false;
        } catch (Exception e) {
            logger.error(instanceId + "??????????????????");
            return false;
        }
    }

    //????????????
    public boolean cancelMonitor(Long instanceId, JSONObject retn) {
        try {
            psvc.cancleMonitor(instanceId);
            ResourceInstance res = rsvc.getResourceInstance(instanceId);
            if (res == null) {
                retn.put("msg", "????????????????????????????????????");
                return false;
            }
            InstanceLifeStateEnum state = res.getLifeState();
            if (state == InstanceLifeStateEnum.NOT_MONITORED) {
                retn.put("msg", "??????????????????");
                return true;
            } else {
                retn.put("msg", "??????????????????");
                logger.error(instanceId + "??????????????????");
                return false;
            }
        } catch (Exception e) {
            retn.put("msg", "??????????????????");
            logger.error(instanceId + "??????????????????");
            return false;
        }
    }

    @Override
    public JSONObject extractDomainInfo() {
        JSONObject retn = new JSONObject();
        SettingBo sp = settingDao.getCfg("topoSetting");
        if (sp != null) {
            String cfg = sp.getValue();
            if (!"".equals(cfg)) {
                retn = JSON.parseObject(cfg);
                SettingBo commonBody = settingDao.getCfg("commonBody");
                if (commonBody != null) {
                    JSONObject cbJson = JSON.parseObject(commonBody.getValue());
                    Integer snmpversion = cbJson.getInteger("snmpversion");
                    retn.put("snmpversion", snmpversion);
                }
            } else {
                logger.error("??????????????????GroupId???domainId");
            }
        }
        return retn;
    }

    //	@Transactional
    private String getCoreIp() {
        String ip = null;
        SettingBo wholeNet = settingDao.getCfg("wholeNet");
        if (null != wholeNet) {
            JSONObject wholeNetJson = (JSONObject) JSONObject.parse(wholeNet.getValue());
            JSONArray ips = wholeNetJson.getJSONArray("ips");
            if (!ips.isEmpty()) {
                ip = ips.getString(0);
            }
        }
        if (null == ip) {//??????????????????????????????????????????????????????????????????
            SettingBo extend = settingDao.getCfg("extend");
            if (null != extend) {
                JSONArray ips = (JSONArray) JSONObject.parse(extend.getValue());
                if (!ips.isEmpty()) {
                    ip = ips.getString(0);
                }
            }
        }
        return ip;
    }

    private TopoCollectorMBean getRemoveService() {
        JSONObject dinfo = extractDomainInfo();
        try {
            TopoCollectorMBean collector = client.getRemoteSerivce(dinfo.getIntValue("groupId"), TopoCollectorMBean.class);
            return collector;
        } catch (IOException e) {
            logger.error("getRemoteService failed");
            return null;
        }
    }

    public StatusProcess newTopoFind(JSONObject params, final ITopoFindHandler handler) {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        TopoCollectorMBean removeService = getRemoveService();
        Assert.notNull(removeService);
        //???????????????
        String result = collector.start(params.toJSONString());
        //??????????????????
        if (!StatusProcess.Failed.name().equals(result) && !StatusProcess.Busy.name().equals(result)) {
            //?????????????????????????????????
            handler.resetMsg();
            handler.buildMessage("????????????????????????");
            handler.setRunning(true);
            //????????????????????????
            executor.execute(new Runnable() {
                public void run() {
                    links.clear();
                    while (handler.isRunning()) {
                        JSONObject result = collector.next();
                        handler.setRunning(result.getBooleanValue("isRunning"));
                        JSONArray data = result.getJSONArray("data");
                        for (Object obj : data) {
                            JSONObject info = (JSONObject) obj;
                            //????????????
                            JSONArray linkSet = info.getJSONArray("link");
                            if (null != linkSet) {
                                for (Object tmp : linkSet) {
                                    links.add(tmp);
                                }
                            }
                            //????????????
                            JSONObject netBean = info.getJSONObject("device");
                            if (null != netBean) {
                                logger.info("??????????????????");
                                try {
                                    handler.addNode(netBean);
                                } catch (Throwable e) {
                                    logger.error("", e);
                                }
                                logger.info("??????????????????");
                            }
                        }
                    }
                }
            });
        }
        executor.shutdown();
        return StatusProcess.valueOf(result);
    }

    public boolean isTopoRunning() {
        if (collector != null) {
            return collector.isRunning();
        }
        return false;
    }

    @Override
    public StatusProcess topoFind(JSONObject params, final ITopoFindHandler handler) {
        if (handler.isRunning()) return StatusProcess.Busy;
        this.memFlagCount++;

        final JSONObject dinfo = this.extractDomainInfo();    //????????????????????????
        logger.error("***????????????????????????=" + dinfo.toJSONString());
        Integer groupId = dinfo.getIntValue("groupId");
        try {
            collector = client.getRemoteSerivce(groupId, TopoCollectorMBean.class);
            String result = "";    //????????????
            try {
                result = collector.start(params.toJSONString());    //???????????????
            } catch (NullPointerException e) {
                logger.error("RPCClient.getRemoteSerivce()??????????????????MBean?????????????????????????????????????????????????????????[" + groupId + "]?????????", e);
            } catch (Exception e1) {
                logger.error("????????????????????????,???????????????[" + groupId + "]", e1);
            }

            //??????????????????
            if (!"".equals(result) && !StatusProcess.Failed.name().equals(result) && !StatusProcess.Busy.name().equals(result)) {
                handler.resetMsg();    //?????????????????????????????????
                handler.buildMessage("????????????????????????");
                handler.setRunning(true);

                //??????????????????
                this.topoFindThread = new Thread(new Runnable() {
                    int flagCount = memFlagCount;

                    @Override
                    public void run() {
                        links.clear();    //????????????????????????????????????
                        boolean errorFlag = false;
                        int count = 10;//????????????
                        while (handler.isRunning() && flagCount >= memFlagCount) {
                            try {
                                JSONObject result = collector.next();
                                logger.error("????????????????????????DCS??????????????????:\n" + result.toJSONString());

                                handler.setRunning(result.getBooleanValue("isRunning"));
                                JSONArray data = result.getJSONArray("data");
                                for (Object obj : data) {
                                    JSONObject info = (JSONObject) obj;
                                    JSONArray linkSet = info.getJSONArray("link");
                                    if (null != linkSet) {
                                        for (Object tmp : linkSet) {
                                            links.add(tmp);    //?????????????????????????????????
                                        }
                                    }
                                    //????????????
                                    JSONObject netBean = info.getJSONObject("device");
                                    if (null != netBean) {
                                        logger.error("**************??????????????????****************");
                                        try {
                                            handler.addNode(netBean);    //????????????????????????
                                        } catch (Throwable e) {
                                            logger.error("thirdService.addNode()??????????????????????????????", e);
                                        }
                                        logger.error("**************???????????????****************");
                                    }
                                }
                            } catch (Exception e1) {
                                while (count > 0) {
                                    logger.error("????????????,???????????????" + count, e1);
                                    try {
                                        collector = client.getRemoteSerivce(dinfo.getIntValue("groupId"), TopoCollectorMBean.class);
                                        count--;
                                        break;
                                    } catch (Exception e) {
                                        count--;
                                    }
                                }
                                if (count <= 0) {
                                    errorFlag = true;//????????????
                                    handler.stop(true);
                                    logger.error("????????????,????????????" + count, e1);
                                    break;
                                }
                            } finally {
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    logger.error("??????????????????????????????", e);
                                }
                            }
                        }

                        //??????????????????????????????????????????????????????
                        if (!errorFlag) {
                            handler.addLinks(links);    //??????????????????
                            handler.buildMessage("??????????????????");

                            //????????????????????????-???????????????????????????????????????;
                            logger.error("***???????????????????????????????????????????????????????????????");
                            handler.stop(false);
                        }
                        topoFindThreadOver = true;
                        logger.error("topoFindThread-????????????????????????" + topoFindThreadOver);
                        logger.error("topoFindThread-???????????????????????????" + topoInstanceThreadOver);
                        instanceLink(handler);
                    }
                }, "topoFindThread-" + System.currentTimeMillis());
                this.topoFindThreadOver = false;
                this.topoFindThread.start();

                logger.error("????????????ip??????getCoreIp()=====>");
                final String coreIp = this.getCoreIp();
                logger.error("coreIp=" + coreIp);

                //???????????????????????????????????????????????????????????????????????????????????????
                this.topoInstanceThread = new Thread(new Runnable() {
                    int flagCount = memFlagCount;

                    @Override
                    public void run() {
                        HashSet<Long> nodeInstanceIds = new HashSet<Long>();
                        while ((handler.isRunning() || !nodeStack.isEmpty() || linkQueue == null) && flagCount >= memFlagCount) {
                            if (nodeStack.isEmpty()) {    //??????????????????????????????
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    logger.error(new Date().toString() + "???????????????sleep??????");
                                }
                            } else {
                                try {
                                    NodeBo nb = nodeStack.pop();
                                    logger.info("****???????????????????????????=???" + JSON.toJSONString(nb));
                                    //??????????????????????????????????????????????????????
                                    ResourceInstanceDiscoveryParameter param = new ResourceInstanceDiscoveryParameter();
                                    Map<String, String> info = new HashMap<String, String>();
                                    JSONObject rawInfo = nb.getRawInfo();    //??????????????????
                                    String ip = nb.getIp();
                                    String port = "161";
                                    String community = "public";
                                    Integer snmpversion = 0;
                                    if (rawInfo != null) {
                                        JSONObject conninfo = rawInfo.getJSONObject("conninfo");
                                        if (conninfo != null) {
                                            port = conninfo.getString("port");
                                            community = conninfo.getString("readOnlyCommunity");
                                            snmpversion = conninfo.getInteger("version");
                                            if (snmpversion == 3) {
                                                info.put("privacyProtocol", conninfo.getString("authPrivateProtocol"));
                                                info.put("authPassphrase", conninfo.getString("authPassword"));
                                                info.put("securityName", conninfo.getString("securityName"));
                                                info.put("authProtocol", conninfo.getString("authProtocol"));
                                                info.put("securityLevel", conninfo.getString("securityLevel"));
                                                info.put("privacyPassphrase", conninfo.getString("authPrivatePassword"));
                                            }
                                        }
                                    }
                                    info.put("IP", ip);
                                    info.put("snmpPort", port);
                                    info.put("community", SecureUtil.pwdEncrypt(community));
                                    info.put("collectType", "SNMP");
                                    info.put("snmpRetry", "3");
                                    info.put("IcmpRetry", "1");
                                    info.put("IcmpTimeout", "3000");
                                    info.put("snmpTimeout", "3000");
                                    info.put("snmpRetry", "3");
                                    info.put("snmpVersion", snmpversion.toString());
                                    param.setDiscoveryInfos(info);
                                    param.setDomainId(dinfo.getLongValue("domainId"));
                                    param.setNodeGroupId(dinfo.getIntValue("groupId"));
                                    param.setResourceInstanceId(nb.getInstanceId());
                                    if (nb.isNetDevice()) {    //??????????????????
                                        param.setAnonymousNetworkDevice(true);
                                        info.put("sysObjectID", nb.getOid());
                                    }
                                    //??????????????????ip
                                    if (coreIp != null) param.setCoreNodeIp(coreIp);

                                    //??????????????????id?????????????????????????????????
//									logger.error("???????????????????????????instanId=["+(nb.getInstanceId()==null?"null":nb.getInstanceId())+"],(???instanceId???????????????????????????)");
                                    if (null != nb.getResourceId() && !nodeInstanceIds.contains(nb.getInstanceId())) {
                                        param.setResourceId(nb.getResourceId());
                                        try {
                                            long begin = new Date().getTime();
                                            logger.error("***resDiscoverSvc.topoDiscoveryResourceInstance()??????????????????\n" + JSONObject.toJSONString(param));
                                            resDiscoverSvc.topoDiscoveryResourceInstance(param);

                                            logger.error(new StringBuilder("***profileSvc.enableMonitorForTopo([").append(nb.getInstanceId()).append("])???????????????***"));
                                            profileSvc.enableMonitorForTopo(Arrays.asList(nb.getInstanceId()));

                                            nodeInstanceIds.add(nb.getInstanceId());
                                            logger.error("??????????????????????????????" + (new Date().getTime() - begin) + "ms");
                                        } catch (Throwable e) {
                                            logger.error("????????????????????????????????????->???????????????????????????instanceId=null,???????????????:" + JSON.toJSONString(param), e);
                                            nb.setInstanceId(null);
                                            ndao.updateInstanceId(nb);
                                        }
                                    }
                                } catch (Throwable e) {
                                    logger.error("?????????????????????", e);
                                }
                            }
                        }
                        //????????????????????????????????????????????????????????????>???????????????????????????
                        topoInstanceThreadOver = true;
                        logger.error("topoInstanceThread-?????????????????????????????????" + topoFindThreadOver);
                        instanceLink(handler);
                    }
                }, "topoInstanceThread");
                topoInstanceThreadOver = false;
                this.topoInstanceThread.start();
            }
            return StatusProcess.valueOf(result);
        } catch (Exception e) {
            logger.error("topo find error:", e);
        }
        return StatusProcess.Failed;
    }

    private void instanceLink(ITopoFindHandler handler) {
        logger.error("**************begin instance link*********************");
        if (!this.topoInstanceThreadOver || !this.topoFindThreadOver) return;

        JSONObject dinfo = this.extractDomainInfo();
        //????????????id?????????????????????????????????
        Map<LinkBo, ResourceInstance> relation = new HashMap<LinkBo, ResourceInstance>();
        Set<String> repatFlag = new HashSet<String>(500);
        Set<Long> filterMonitorLinkIds = new HashSet<Long>();    //??????????????????????????????????????????????????????
        while (!linkQueue.isEmpty()) {
            List<LinkBo> tmpLinks = linkQueue.poll();
            for (LinkBo lb : tmpLinks) {
                logger.error("****begin package instance single link??????=" + JSONObject.toJSONString(lb));
                try {
                    JSONObject link = lb.getRawInfo();
                    String srcIf = link.getString("srcIf");    //????????????????????? srcIfIndex
                    String dstIf = link.getString("desIf");    //???????????????????????? destIfIndex
                    if ((null == srcIf || Integer.parseInt(srcIf) <= 0) || (null == dstIf || Integer.parseInt(dstIf) <= 0)) {
                        ldao.delete(lb.getId());    //??????????????????(????????????????????????????????????)????????????????????????????????????
                        filterMonitorLinkIds.add(lb.getId());
                        logger.error("srcIf=" + srcIf + ",dstIf=" + dstIf + "?????????");
                        continue;
                    }

                    NodeBo src = ndao.getById(lb.getFrom());
                    NodeBo des = ndao.getById(lb.getTo());
                    //????????????????????????
                    logger.error("??????????????????srcNode=" + JSONObject.toJSONString(src));
                    logger.error("?????????????????????descNode=" + JSONObject.toJSONString(des));
                    if ((src == null || src.getInstanceId() == null) && (des == null || des.getInstanceId() == null)) {
                        throw new RuntimeException("???????????????????????????????????????????????????????????????????????????instanceId??????");
                    }
                    ResourceInstance srcInstance = null, desInstance = null;
                    if (src != null && null != src.getInstanceId())
                        srcInstance = rsvc.getResourceInstance(src.getInstanceId());
                    if (des != null && null != des.getInstanceId())
                        desInstance = rsvc.getResourceInstance(des.getInstanceId());

                    //????????????categoryId
                    ResourceDef def = csvc.getResourceDefById(LinkResourceConsts.RESOURCE_LAYER2LINK_ID);
                    ResourceInstance res = new ResourceInstance();
                    if (def != null) {
                        CategoryDef cdef = def.getCategory();
                        if (null != cdef) res.setCategoryId(cdef.getId());
                    }
                    //?????????????????????????????????
                    res.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);    //???????????????
                    res.setResourceId(LinkResourceConsts.RESOURCE_LAYER2LINK_ID);
                    res.setDomainId(dinfo.getLongValue("domainId"));
                    res.setDiscoverNode(dinfo.getString("groupId"));
                    res.setRepeatValidate(false);
                    //???????????????????????????
                    StringBuilder name = new StringBuilder();    //????????????
                    List<ModuleProp> props = new ArrayList<ModuleProp>();
                    if (srcInstance != null) {
                        long srcSubInstId = this.getSubInstId(srcInstance, link.getLong("srcIf"));
                        ResourceInstance srcSubInst = rsvc.getResourceInstance(srcSubInstId);
                        //????????????ID collMainInstId
                        props.add(this.getModulProp("collMainInstId", new String[]{String.valueOf(srcInstance.getId())}));
                        //?????????????????????id collSubInstId
                        props.add(this.getModulProp(LinkResourceConsts.PROP_COLL_SUBINST_ID, new String[]{String.valueOf(srcSubInstId)}));
                        //?????????ID srcMainInstId
                        props.add(this.getModulProp(LinkResourceConsts.PROP_SRC_MAININST_ID, new String[]{String.valueOf(srcInstance.getId())}));
                        //?????????IP?????? srcMainInstIP
                        props.add(this.getModulProp(LinkResourceConsts.PROP_SRC_MAININST_IP, new String[]{srcInstance.getShowIP()}));
                        //???????????????  srcDeviceName
                        props.add(this.getModulProp(LinkResourceConsts.PROP_SRC_DEVICE_NAME, new String[]{srcInstance.getName()}));
                        //??????????????????ID srcSubInstId
                        props.add(this.getModulProp(LinkResourceConsts.PROP_SRC_SUBINST_ID, new String[]{String.valueOf(srcSubInstId)}));
                        props.add(this.getModulProp(LinkResourceConsts.PROP_SRC_IFINDEX, new String[]{srcIf}));
                        //????????????????????? srcIfName
                        props.add(this.getModulProp(LinkResourceConsts.PROP_SRC_IFNAME, new String[]{srcSubInst.getName()}));
                        //???????????? deviceName
                        props.add(this.getModulProp(LinkResourceConsts.PROP_DEVCIE_NAME, new String[]{srcInstance.getName()}));
                        //????????????
                        props.add(getModulProp("downSubInstId", new String[]{String.valueOf(srcSubInstId)}));
                        //???????????????????????????
                        String srcIfName = !StringUtils.isEmpty(srcSubInst.getShowName()) ? srcSubInst.getShowName() : (!StringUtils.isEmpty(srcSubInst.getName()) ? srcSubInst.getName() : "??????");
                        name.append(srcInstance.getShowIP() == null ? "??????" : srcInstance.getShowIP()).append(':').append(srcIfName).append('-');
                    } else {
                        filterMonitorLinkIds.add(lb.getId());
                        logger.error("??????????????????srcInstance=null->?????????????????????????????????????????????");
                    }
                    if (desInstance != null) {
                        //?????????????????????ID destSubInstId
                        long destSubInstId = this.getSubInstId(desInstance, link.getLong("desIf"));
                        ResourceInstance destSubInst = rsvc.getResourceInstance(destSubInstId);
                        //????????????ID destMainInstId
                        props.add(getModulProp(LinkResourceConsts.PROP_DEST_MAININST_ID, new String[]{String.valueOf(desInstance.getId())}));
                        //????????????IP?????? destMainInstIP
                        props.add(getModulProp(LinkResourceConsts.PROP_DEST_MAININST_IP, new String[]{desInstance.getShowIP()}));
                        //?????????????????? destDeviceName
                        props.add(getModulProp(LinkResourceConsts.PROP_DEST_DEVICE_NAME, new String[]{desInstance.getName()}));
                        props.add(getModulProp(LinkResourceConsts.PROP_DEST_SUBINST_ID, new String[]{String.valueOf(destSubInstId)}));
                        props.add(getModulProp(LinkResourceConsts.PROP_DEST_IFINDEX, new String[]{dstIf}));
                        //???????????????????????? destIfName
                        props.add(getModulProp(LinkResourceConsts.PROP_DEST_IFNAME, new String[]{destSubInst.getName()}));
                        if (srcInstance == null) {
                            //????????????ID collMainInstId
                            props.add(getModulProp("collMainInstId", new String[]{String.valueOf(desInstance.getId())}));
                            //?????????????????????id collSubInstId
                            props.add(getModulProp(LinkResourceConsts.PROP_COLL_SUBINST_ID, new String[]{String.valueOf(destSubInstId)}));
                        }
                        //????????????????????????
                        String desIfName = !StringUtils.isEmpty(destSubInst.getShowName()) ? destSubInst.getShowName() : (!StringUtils.isEmpty(destSubInst.getName()) ? destSubInst.getName() : "??????");
                        name.append(desInstance.getShowIP() == null ? "??????" : desInstance.getShowIP()).append(':').append(desIfName);
                    } else {
                        filterMonitorLinkIds.add(lb.getId());
                        logger.error("?????????????????????desInstance=null->?????????????????????????????????????????????");
                    }
                    //??????
                    props.add(this.getModulProp("note", new String[]{""}));
                    //??????????????????
                    if ("".equals(name.toString())) name = new StringBuilder("--");
                    res.setName(name.toString().trim());
                    res.setShowName(res.getName());
                    //??????????????????
                    res.setModuleProps(props);
                    //???????????????
                    relation.put(lb, res);
                    repatFlag.add(res.getName());
                } catch (Exception e) {
                    JSONObject result = new JSONObject();
                    logger.error("*****??????????????????????????????instance???????????????catch?????????linkService.addMonitor()", e);
                    try {
                        if (!filterMonitorLinkIds.contains(lb.getId()) || (null != lb.getFromIfIndex() && null != lb.getToIfIndex())) {
                            List<Long> linkIds = Arrays.asList(lb.getId());
                            logger.error("addMonitor([" + JSONObject.toJSONString(linkIds) + "])");
                            result = linkService.addMonitor(linkIds);
                        }
                    } catch (Exception e1) {
                        logger.error("linkService.addMonitor([" + lb.getId() + "])????????????", e1);
                    }
                    logger.error("??????????????????????????????instance???????????????linkService.addMonitor()????????????=" + result.toString());
                }
            }
        }

        //?????????
        try {
            //???????????????????????????????????????instanceId??????
            List<LinkBo> links = ldao.getAllLinkInstances();
            //???????????????????????????????????????????????????????????????
            for (LinkBo link : links) {
                Long instanceId = link.getInstanceId();
                if (null != instanceId) {
                    ResourceInstance dbri = rsvc.getResourceInstance(instanceId);
                    if (null != dbri && !repatFlag.contains(dbri.getName())) {
                        ResourceInstance ri = new ResourceInstance();
                        BeanUtils.copyProperties(dbri, ri);
                        ri.setModuleProps(modulePropSvc.getPropByInstanceId(instanceId));
                        relation.put(link, ri);
                    }
                }
            }

            logger.error("****really begin to instance link");
            //?????????????????????
            List<ResourceInstance> instances = new ArrayList<ResourceInstance>(relation.values());
            //TODO:???????????????????????????????????????????????????server????????????????????????????????????????????????????????????????????????
            boolean isWholeNetFind = handler.isWholeNetFind();
            if (isWholeNetFind) {
                List<ResourceInstance> mapLinks = this.getMapLinks();
                if (null != mapLinks) {
                    instances.addAll(mapLinks);
                    logger.error("***??????????????????????????????=" + JSONObject.toJSONString(mapLinks));
                }
            }

            logger.error("****really instance links = " + JSONObject.toJSONString(instances));
            long start = new Date().getTime();
            //?????????????????????????????????????????????true????????????????????????????????????
            rsvc.addResourceInstanceForLink(instances, isWholeNetFind);
            logger.error("****really instance resultLinks = " + JSONObject.toJSONString(instances));
            logger.error("?????????????????????" + (new Date().getTime() - start) + "ms");

            List<Long> monitorInstanceIds = new ArrayList<Long>();
            StringBuilder addmonitorLinks = new StringBuilder("??????????????????????????????");
            //?????????????????????????????????????????????id
            for (Map.Entry<LinkBo, ResourceInstance> entry : relation.entrySet()) {
                LinkBo lb = entry.getKey();
                lb.setInstanceId(entry.getValue().getId());
                ldao.updateInstanceId(lb);
                if (!filterMonitorLinkIds.contains(lb.getId()) || (null != lb.getFromIfIndex() && null != lb.getToIfIndex())) {
                    monitorInstanceIds.add(lb.getInstanceId());
                }
                addmonitorLinks.append("node.id=").append(lb.getId()).append("linkInstanceId=").append(lb.getInstanceId()).append("---");
            }
            logger.error(addmonitorLinks.toString());

            try {    //??????????????????
                logger.error("***the link ids that will addmonitor=" + JSON.toJSONString(monitorInstanceIds));
                long begin = new Date().getTime();
                profileSvc.enableMonitorForLink(monitorInstanceIds);
                logger.error("????????????????????????" + (new Date().getTime() - begin) + "ms");
            } catch (Exception e) {
                logger.error("addmonitor failed", e);
            }
        } catch (InstancelibException e) {
            logger.error("topofind module adding link to database failed", e);
        } catch (Throwable e) {
            logger.error("Exception happend during changing linkBo to ResourceInstance", e);
        }
        logger.error("**************end instance link*********************");
    }

    /**
     * ????????????????????????????????????????????????indstanceId????????????
     *
     * @return
     * @throws InstancelibException
     */
    private List<ResourceInstance> getMapLinks() throws InstancelibException {//TODO:??????????????????
        List<ResourceInstance> list = new ArrayList<ResourceInstance>();
        List<MapLineBo> links = maplineDao.getLinks();
        if (null == links || links.size() == 0) return null;

        StringBuffer instanceIds = new StringBuffer("?????????????????????????????????instandIds???[");
        for (MapLineBo link : links) {
            Long instanceId = link.getInstanceId();
            ResourceInstance instance = rsvc.getResourceInstance(instanceId);
            if (null != instance) {
                ResourceInstance resource = new ResourceInstance();
                BeanUtils.copyProperties(instance, resource);
                resource.setModuleProps(modulePropSvc.getPropByInstanceId(instanceId));
                list.add(resource);
            }
            instanceIds.append(instanceId).append(",");
        }
        instanceIds.append("]");
        logger.error(instanceIds.replace(instanceIds.length() - 2, instanceIds.length() - 1, ""));
        return list;
    }

    @Override
    public void addInstanceLinks(List<LinkBo> lbs) {
        this.linkQueue.add(lbs);
    }

    @Override
    public void addInstanceNode(NodeBo node) {
        this.nodeStack.push(node.clone());
    }

    @Override
    public void addExtremSimpleTopoRes(Long bid, List<Long> resIds) {
        try {
            ResourceBizRel mode = new ResourceBizRel();
            mode.setBizId(bid);
            mode.setResourceIds(resIds);
            mode.setNav("topo");
            searchApi.saveSearchTopo(mode);
        } catch (Throwable e) {
            logger.error("addExtremSimpleTopoRes exception", e);
        }
    }

    @Override
    public void delExtremSimpleTopoRes(Long bid, List<Long> resIds) {
        try {
            ResourceBizRel mode = new ResourceBizRel();
            mode.setBizId(bid);
            mode.setResourceIds(resIds);
            mode.setNav("topo");
            searchApi.delSearchTopo(mode);
        } catch (Throwable e) {
            logger.error("delExtremSimpleTopoRes exception", e);
        }
    }

    private ModuleProp getModulProp(String key, String[] values) {
        ModuleProp prop = new ModuleProp();
        prop.setKey(key);
        prop.setValues(values);
        return prop;
    }

    /**
     * ???????????????id
     *
     * @param ri    ?????????
     * @param ifIdx ???????????????
     * @return ?????????id
     */
    private long getSubInstId(ResourceInstance ri, Long ifIdx) {
        if (null != ri.getChildren()) {
            StringBuilder ifIndexMsg = new StringBuilder("????????????id??????????????????ifIdx=").append(ifIdx).append(",???????????????id=").append(ri.getId()).append("???ifIndex=");
            for (ResourceInstance cd : ri.getChildren()) {
                String childType = cd.getChildType();
                if (ResourceTypeConsts.TYPE_NETINTERFACE.equals(childType)) {//?????????????????????
                    String[] ifs = cd.getModulePropBykey("ifIndex");
                    if (ifs == null) {
                        ifs = cd.getModulePropBykey("hostInterfaceIndex");
                    }
                    if (null != ifs && ifs.length > 0) {
                        long idx = Long.parseLong(ifs[0]);
                        for (String inf : ifs) {
                            ifIndexMsg.append(inf).append("--");
                            idx = Long.parseLong(inf);
                        }
                        if (ifIdx.longValue() == idx) {
                            return cd.getId();
                        }
                    }
                }
            }
            logger.error(ifIndexMsg.toString());
            throw new RuntimeException("?????????instancerId=" + ri.getId() + ",ip=" + ri.getShowIP() + "???????????????????????????ifIndex=" + ifIdx);
        }
        throw new RuntimeException(ri.getId() + "????????????????????????????????????????????????");
    }

    @Override
    public JSONObject getInstanceState(Long instId) {
        JSONObject retn = new JSONObject();
        //?????????????????????????????????
        ResourceInstance ri = dataHelper.getResourceInstance(instId);
        if (ri != null && InstanceLifeStateEnum.NOT_MONITORED.equals(ri.getLifeState())) {
            retn.put("state", "not_monitored");
            return retn;
        }
        InstanceStateEnum istate = dataHelper.getInstAlarmInstanceStateEnum(instId);
        if (null != istate) {
            String state = istate.name();
            retn.put("state", state);
        } else {
            retn.put("state", IconState.NODATA.getName());
        }
        return retn;
    }

    /**
     * ?????????????????????
     *
     * @param instId
     * @return
     */
    public JSONObject getIfInstanceState(Long instId) {
        JSONObject retn = new JSONObject();
        //?????????????????????????????????
        ResourceInstance ri = dataHelper.getResourceInstance(instId);
        if (ri != null && InstanceLifeStateEnum.NOT_MONITORED.equals(ri.getLifeState())) {
            retn.put("state", "not_monitored");
            return retn;
        }

        //???????????????????????????????????????
        String[] infoMetrics = {"ifAdminStatus", "ifOperStatus"};
        long[] instIdArr = {instId};
        List<MetricData> metricDataList = infoMetricQueryAdaptService.getMetricInfoDatas(instIdArr, infoMetrics);
        if (null != metricDataList && metricDataList.size() > 0) {
            String ifAdminStatus = "", ifOperStatus = "";
            for (int i = 0; i < metricDataList.size(); i++) {
                MetricData metricData = metricDataList.get(i);
                JSONObject data = (JSONObject) JSONObject.toJSON(metricData);
                String metricId = metricData.getMetricId();
                if ("ifAdminStatus".equals(metricId)) {
                    ifAdminStatus = data.getString("metricData");
                } else if ("ifOperStatus".equals(metricId)) {
                    ifOperStatus = data.getString("metricData");
                }
            }
            String state = InstanceStateEnum.NORMAL.name();
            if ("up".equals(ifAdminStatus) && "up".equals(ifOperStatus)) {
                state = InstanceStateEnum.NORMAL.toString();
            } else if ("down".equals(ifAdminStatus) && "down".equals(ifOperStatus)) {
                state = InstanceStateEnum.CRITICAL.toString();
            } else if ("up".equals(ifAdminStatus) && "down".equals(ifOperStatus)) {
                state = "adminormal_opercritical";
            }
            retn.put("state", state);
        } else {
            retn.put("state", IconState.NODATA.getName());
        }

        return retn;
    }

    public JSONObject getMetricState(Long instId, String metricId, boolean isLink) {
        JSONObject retn = new JSONObject();
        //?????????????????????????????????
        ResourceInstance ri = dataHelper.getResourceInstance(instId);
        if (ri != null && InstanceLifeStateEnum.NOT_MONITORED.equals(ri.getLifeState())) {
            retn.put("state", InstanceLifeStateEnum.NOT_MONITORED.name());
            return retn;
        }
        MetricStateData stateData = metricStateSvc.getMetricState(instId, metricId);
        String state = MetricStateEnum.NORMAL.name();
        if (null != stateData) {
            state = stateData.getState().name();
        }
        retn.put("state", state);
        return retn;
    }

    @Override
    public JSONObject getLinkModuleProps(Long instanceId) {
        Assert.notNull(instanceId);
        JSONObject retn = new JSONObject();
        try {
            ResourceInstance ist = rsvc.getResourceInstance(instanceId);

            if (null != ist) {
                String[] keys = new String[]{LinkResourceConsts.PROP_SRC_IFINDEX, LinkResourceConsts.PROP_DEST_IFINDEX, LinkResourceConsts.PROP_SRC_IFNAME,
                        LinkResourceConsts.PROP_DEST_IFNAME, "srcDeviceName", "destDeviceName", "downIp",
                        LinkResourceConsts.PROP_SRC_SUBINST_ID, LinkResourceConsts.PROP_DEST_SUBINST_ID, LinkResourceConsts.PROP_COLL_SUBINST_ID, "downSubInstId", "note", "collSubInstId",
                        LinkResourceConsts.PROP_SRC_MAININST_IP, LinkResourceConsts.PROP_DEST_MAININST_IP, LinkResourceConsts.PROP_SRC_MAININST_ID, LinkResourceConsts.PROP_DEST_MAININST_ID};

                for (String key : keys) {
                    String[] tmp = ist.getModulePropBykey(key);
                    if (tmp != null && tmp.length > 0) {
                        StringBuffer sb = new StringBuffer();
                        for (String str : tmp) {
                            if (str != null) {
                                sb.append(str);
                            }
                        }
                        retn.put(key, sb.toString());
                    } else {
                        retn.put(key, "");
                    }
                }
            } else {
                logger.error("????????????????????????,instanceId=" + instanceId);
            }
        } catch (Exception e) {
            logger.error("???????????????????????????", e);
            e.printStackTrace();
        }
        return retn;
    }

    public JSONArray getInstancesIfs(Long instanceId, boolean isConvert) {
        JSONArray retn = new JSONArray();
        try {
            ResourceInstance ri = rsvc.getResourceInstance(instanceId);
            List<ResourceInstance> children = ri.getChildren();
            //??????????????????
            if (null != children) {
//				logger.error("***********discoverIp="+ri.getShowIP()+",instanceId="+instanceId+" :before convert children size : "+children.size());
                Set<String> ifs = new HashSet<String>();
                if (isConvert) {
                    //??????????????????????????????????????????,???????????????????????????
                    List<LinkBo> selectedLinks = ldao.getAll();
                    for (LinkBo link : selectedLinks) {
                        if (link.getDeleteFlag() != 1) {//????????????????????????
                            if (null != link.getFrom() && null != link.getFromIfIndex()) {
                                NodeBo fromNode = ndao.getById(link.getFrom());
                                if (null != fromNode && null != fromNode.getInstanceId())
                                    ifs.add(fromNode.getInstanceId() + "_" + link.getFromIfIndex());
                            }
                            if (null != link.getTo() && null != link.getToIfIndex()) {
                                NodeBo toNode = ndao.getById(link.getTo());
                                if (null != toNode && null != toNode.getInstanceId())
                                    ifs.add(toNode.getInstanceId() + "_" + link.getToIfIndex());
                            }
                        }
                    }
//					logger.error("instanceId="+instanceId+",before convert selectedLinks ifs ="+JSONObject.toJSONString(ifs));
                }

                for (ResourceInstance child : children) {
                    JSONObject tmp = new JSONObject();
                    String type = child.getChildType();
                    //???????????????????????????????????????
                    String[] ifindex = child.getModulePropBykey("ifIndex");
                    String[] ifType = child.getModulePropBykey("ifType");
//					logger.error("parentInstanceId="+instanceId+",childInstanceId="+child.getId()
//							+",???????????????child type="+type+" ,ifindex="+JSON.toJSONString(ifindex)+",monitored="
//							+ri.getLifeState().name()+",name="+child.getName()+",child.getShowName()="+child.getShowName());
//					if(ResourceTypeConsts.TYPE_NETINTERFACE.equals(type) && ri.getLifeState()==InstanceLifeStateEnum.MONITORED){//???????????????
                    if (ResourceTypeConsts.TYPE_NETINTERFACE.equalsIgnoreCase(type) && child.getLifeState() != null && child.getLifeState().ordinal() == InstanceLifeStateEnum.MONITORED.ordinal()) {//???????????????
                        //						logger.error("???????????????????????????NetInterface?????????"+instanceId+"_"+((null!=ifindex)?ifindex[0]:"null"));
                        if (!isConvert || (isConvert && !ifs.contains(instanceId + "_" + Long.valueOf(ifindex[0])))) {
                            String showName = StringUtils.isEmpty(child.getShowName()) ? child.getName() : child.getShowName();
                            tmp.put("showName", showName);
                            tmp.put("name", child.getName());
                            tmp.put("instanceId", child.getId());
                            tmp.put("lifeState", child.getLifeState().name().toLowerCase());
                            if (ifindex != null && ifindex.length > 0) {
                                tmp.put("ifIndex", ifindex[0]);
                                //logger.error("??????ifIndex:"+ifindex[0]);
                            }

                            //??????????????????
                            JSONObject stateJson = this.getIfInstanceState(child.getId());
                            tmp.put("state", stateJson.getString("state"));
                            retn.add(tmp);
                        }
                    }
                }

                //??????
            } else {
                logger.error("????????????[" + instanceId + "].getChildren()??????(??????????????????)");
            }
        } catch (InstancelibException e) {
            logger.error("??????????????????" + instanceId + "????????????");
        } catch (Exception e) {
            logger.error("getInstancesIfs ????????????", e);
        }
//		logger.error(instanceId+" :after convert children size : "+retn.size());

        //??????????????????ASC
        this.sortByIndex(retn);
        return retn;
    }

    @Override
    public JSONArray getInstancesIfs2(Long instanceId, boolean isConvert) {
        JSONArray retn = new JSONArray();
        try {
            ResourceInstance ri = rsvc.getResourceInstance(instanceId);
            List<ResourceInstance> children = ri.getChildren();
            //??????????????????
            if (null != children) {
//				logger.error("***********discoverIp="+ri.getShowIP()+",instanceId="+instanceId+" :before convert children size : "+children.size());
                Set<String> ifs = new HashSet<String>();
                if (isConvert) {
                    //??????????????????????????????????????????,???????????????????????????
                    List<LinkBo> selectedLinks = ldao.getAll();
                    for (LinkBo link : selectedLinks) {
                        if (link.getDeleteFlag() != 1) {//????????????????????????
                            if (null != link.getFrom() && null != link.getFromIfIndex()) {
                                NodeBo fromNode = ndao.getById(link.getFrom());
                                if (null != fromNode && null != fromNode.getInstanceId())
                                    ifs.add(fromNode.getInstanceId() + "_" + link.getFromIfIndex());
                            }
                            if (null != link.getTo() && null != link.getToIfIndex()) {
                                NodeBo toNode = ndao.getById(link.getTo());
                                if (null != toNode && null != toNode.getInstanceId())
                                    ifs.add(toNode.getInstanceId() + "_" + link.getToIfIndex());
                            }
                        }
                    }
//					logger.error("instanceId="+instanceId+",before convert selectedLinks ifs ="+JSONObject.toJSONString(ifs));
                }

                for (ResourceInstance child : children) {
                    JSONObject tmp = new JSONObject();
                    String type = child.getChildType();
                    //???????????????????????????????????????
                    String[] ifindex = child.getModulePropBykey("ifIndex");
                    String[] ifType = child.getModulePropBykey("ifType");
//					logger.error("parentInstanceId="+instanceId+",childInstanceId="+child.getId()
//							+",???????????????child type="+type+" ,ifindex="+JSON.toJSONString(ifindex)+",monitored="
//							+ri.getLifeState().name()+",name="+child.getName()+",child.getShowName()="+child.getShowName());
//					if(ResourceTypeConsts.TYPE_NETINTERFACE.equals(type) && ri.getLifeState()==InstanceLifeStateEnum.MONITORED){//???????????????
                    if (ResourceTypeConsts.TYPE_NETINTERFACE.equalsIgnoreCase(type) && child.getLifeState() != null) {//???????????????
                        //child.getLifeState().ordinal() == InstanceLifeStateEnum.MONITORED.ordinal();
//    						logger.error("???????????????????????????NetInterface?????????"+instanceId+"_"+((null!=ifindex)?ifindex[0]:"null"));
                        if (!isConvert || (isConvert && !ifs.contains(instanceId + "_" + Long.valueOf(ifindex[0])))) {
                            String showName = StringUtils.isEmpty(child.getShowName()) ? child.getName() : child.getShowName();
                            tmp.put("showName", showName);
                            tmp.put("name", child.getName());
                            tmp.put("instanceId", child.getId());
                            tmp.put("lifeState", child.getLifeState().name().toLowerCase());
                            if (ifindex != null && ifindex.length > 0) {
                                tmp.put("ifIndex", ifindex[0]);
//                                    logger.error("??????ifIndex:"+ifindex[0]);
                            }
                            //??????
                            if (ifType != null && null != ifType[0]
                                    && !"".equals(ifType[0])) {
                                tmp.put("ifType", ifType[0]);
                            }
                            //??????????????????
                            JSONObject stateJson = this.getIfInstanceState(child.getId());
                            tmp.put("state", stateJson.getString("state"));

                            if (tmp.get("ifType").equals("ethernetCsmacd") || tmp.get("ifType").equals("gigabitEthernet") || tmp.get("ifType").equals("fibreChannel")) {
                                retn.add(tmp);
                            }
                        }
                    }
                }

                //??????
            } else {
                logger.error("????????????[" + instanceId + "].getChildren()??????(??????????????????)");
            }
        } catch (InstancelibException e) {
            logger.error("??????????????????" + instanceId + "????????????");
        } catch (Exception e) {
            logger.error("getInstancesIfs ????????????", e);
        }
//		logger.error(instanceId+" :after convert children size : "+retn.size());

        //??????????????????ASC
        this.sortByIndex(retn);
        return retn;
    }

    /**
     * ????????????????????????????????????
     *
     * @param list
     */
    private void sortByIndex(JSONArray list) {
        Collections.sort(list, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                JSONObject tmp1 = (JSONObject) o1;
                JSONObject tmp2 = (JSONObject) o2;
                return tmp1.getIntValue("ifIndex") - tmp2.getIntValue("ifIndex");
            }
        });
        /*int size = list.size();
        if(null != list && size > 0){
			for(int i=0;i<size-1;i++){
				  for(int j = 0 ;j < size - i - 1; j++){
					  JSONObject tmp1 = (JSONObject)JSONObject.toJSON(list.get(j));
					  JSONObject tmp2 = (JSONObject)JSONObject.toJSON(list.get(j+1));
					  if(tmp1.getLongValue("ifIndex") > tmp2.getLongValue("ifIndex")){
						  list.set(j, tmp2);
						  list.set(j+1, tmp1);
				  }
				}
			}
		}*/
    }

    public JSONObject getThreshold(Long instId, String metricId) {
        JSONObject retn = new JSONObject();
        try {
            List<ProfileThreshold> holds = profileSvc.getThresholdByInstanceIdAndMetricId(instId, metricId);
            if (null != holds) {
                for (ProfileThreshold hold : holds) {
                    retn.put(hold.getPerfMetricStateEnum().name(), JSON.toJSON(hold));
                }
            } else {
                logger.error("????????????id=" + instId + ",metricId=" + metricId + "???????????????");
            }
        } catch (ProfilelibException e) {
            logger.error("??????" + instId + "???????????????", e);
        }
        return retn;
    }

    @SuppressWarnings("incomplete-switch")
    public JSONObject refreshThreshold(Long instId, String metricId, Float min, Float max) {
        JSONObject retn = new JSONObject();
        try {
            logger.info("????????????,metricId=" + metricId + ",instanceId=" + instId);
            ProfileInfo pinfo = profileSvc.getBasicInfoByResourceInstanceId(instId);
            Map<String, Boolean> map = new HashMap<String, Boolean>(1);
            map.put(metricId, true);
            profileSvc.updateProfileMetricAlarm(instId, map);
            if (null != pinfo) {
                //??????pinfo??????????????????????????????????????????
                if (pinfo.getProfileType() == ProfileTypeEnum.DEFAULT) {
                    //?????????????????????
                    Profile profile = profileSvc.getEmptyPersonalizeProfile(LinkResourceConsts.RESOURCE_LAYER2LINK_ID, instId);
                    profileSvc.createPersonalizeProfile(profile);
                }
                //??????????????????
                List<Threshold> thresholds = new ArrayList<Threshold>();
                List<ProfileThreshold> holds = profileSvc.getThresholdByInstanceIdAndMetricId(instId, metricId);
                if (holds == null) {
                    logger.error(String.format("profileSvc.getThresholdByInstanceIdAndMetricId(instanceId=%s,metricId=%s) return null", instId, metricId));
                } else {
                    //????????????
                    for (ProfileThreshold hold : holds) {
                        switch (hold.getPerfMetricStateEnum()) {
                            case Normal:
                                hold.setExpressionOperator(OperatorEnum.Less.toString());
                                hold.setThresholdValue(min.toString());
                                /*BUG #51053 ??????????????????????????????????????????????????????????????????????????? huangping 2017/01/22 start*/
                                hold.setThresholdExpression(String.format("%s %s %s", metricId, OperatorEnum.Less.toString(), min.toString()));
                                /*BUG #51053 ??????????????????????????????????????????????????????????????????????????? huangping 2017/01/22 end*/
                                break;
                            case Major:
                                hold.setExpressionOperator(OperatorEnum.GreatEqual.toString());
                                hold.setThresholdValue(max.toString());
                                /*BUG #51053 ??????????????????????????????????????????????????????????????????????????? huangping 2017/01/22 start*/
                                hold.setThresholdExpression(String.format("%s %s %s", metricId, OperatorEnum.GreatEqual.toString(), max.toString()));
                                /*BUG #51053 ??????????????????????????????????????????????????????????????????????????? huangping 2017/01/22 end*/
                                break;
                            case Minor:
                                hold.setExpressionOperator(OperatorEnum.GreatEqual.toString());
                                hold.setThresholdValue(min.toString());
                                /*BUG #51053 ??????????????????????????????????????????????????????????????????????????? huangping 2017/01/22 start*/
                                hold.setThresholdExpression(String.format("%s %s %s", metricId, OperatorEnum.GreatEqual.toString(), min.toString()));
                                /*BUG #51053 ??????????????????????????????????????????????????????????????????????????? huangping 2017/01/22 end*/
                                break;
                        }
                        thresholds.add(hold);
                    }
                    List<Long> linkIds = new ArrayList<Long>(1);
                    linkIds.add(instId);
                    //?????????????????????
                    profileSvc.updateProfileMetricThreshold(pinfo.getProfileId(), thresholds);
                    retn.put("state", 200);
                }
            } else {
                retn.put("state", 700);
                retn.put("msg", "????????????" + instId + "??????profile???null");
                logger.error("????????????" + instId + "??????profile???null");
            }
        } catch (ProfilelibException e) {
            retn.put("state", 700);
            retn.put("msg", "??????" + instId + "???????????????");
            logger.error("??????" + instId + "???????????????", e);
        }
        return retn;
    }

    public void refreshModuleProp(Long instanceId, String key, String[] values) {
        if (instanceId == null) return;
        ModuleProp prop = new ModuleProp();
        prop.setInstanceId(instanceId);
        prop.setKey(key);
        prop.setValues(values);
        try {
            moudlePropSvc.updateProp(prop);
        } catch (Exception e) {
            logger.error("???????????????" + instanceId + "??????????????????" + key + ":" + values[0] + "??????", e);
        }
    }

    @Override
    public JSONObject getLinkResourceInstanceAttr(Long instanceId) {
        JSONObject retn = new JSONObject();
        try {
            ResourceInstance inst = rsvc.getResourceInstance(instanceId);
            if (null != inst) {
                InstanceLifeStateEnum lifeState = inst.getLifeState();
                if (lifeState == null) {
                    lifeState = InstanceLifeStateEnum.NOT_MONITORED;
                }
                retn.put("lifeState", inst.getLifeState().name().toLowerCase());
				/*List<Map<String,?>> datas = resourceExApi.getMerictRealTimeVals(new String[]{"ifInOctetsSpeed","ifOutOctetsSpeed",MetricIdConsts.METRIC_THROUGHPUT}, new long[]{instanceId});
				for(Map<String,?> data:datas){
					//????????????
					String upSpeed = resourceExApi.getMetricVal(data, "ifInOctetsSpeed", null);
					retn.put("upSpeed",upSpeed);
					//????????????
					String downspeed = resourceExApi.getMetricVal(data,"ifOutOctetsSpeed",null);
					retn.put("downSpeed",downspeed);
					//?????????
					String totalSpeed = resourceExApi.getMetricVal(data,MetricIdConsts.METRIC_THROUGHPUT,null);
					retn.put("totalSpeed",totalSpeed);
				}*/
                //??????
                String[] note = inst.getModulePropBykey("note");
                if (null != note && note.length > 0) {
                    retn.put("note", note[0]);
                } else {
                    retn.put("note", "??????");
                }
                //??????????????????
                retn.put("lifeState", inst.getLifeState().name().toLowerCase());
            } else {
                logger.error("ResourceInstance " + instanceId + " not exsisted");
            }
        } catch (InstancelibException e) {
            logger.error("??????????????????????????????" + instanceId);
        }
        return retn;
    }

    public String getCategoryByResourceId(String resourceId) {
        ResourceDef resDef = csvc.getResourceDefById(resourceId);
        if (null != resDef) {
            CategoryDef cateDef = resDef.getCategory();
            if (cateDef != null) {
                return cateDef.getId();
            } else {
                logger.error(resourceId + "??????CategoryDef??????");
                return null;
            }
        } else {
            logger.error(resourceId + "??????ResourceDef??????");
            return null;
        }
    }

    @Override
    public Long addResourceInstance(NodeBo nb) {
        //??????license
        //??????id
        String resourceId = getResourceId(nb.getOid());
        String categoryId = getCategoryByResourceId(resourceId);
        JSONObject dinfo = extractDomainInfo();
        //??????????????????
        ResourceInstance instance = new ResourceInstance();
        instance.setAutoRefresh(false);
        instance.setCategoryId(categoryId);
        instance.setResourceId(resourceId);
        instance.setShowIP(nb.getIp());
        instance.setDomainId(dinfo.getLongValue("domainId"));
        String name = null;
        //???????????????????????????
        JSONObject attr = (JSONObject) JSON.parse(nb.getAttr());
        if (attr.containsKey("name")) {
            name = attr.getString("name");
        } else {
            if (nb.getShowName() != null) {
                name = nb.getShowName();
            } else {
                name = nb.getIp();
            }
        }
        instance.setShowName(name);
        instance.setName(name);
        instance.setResourceId(getResourceId(nb.getOid()));
        List<ModuleProp> props = new ArrayList<ModuleProp>();
        if (nb.getMacAddress() == null || nb.getMacAddress().length == 0) {
            logger.error("???????????????Node???mac?????????null,????????????????????????????????????");
        }
        props.add(this.getModulProp(MetricIdConsts.METRIC_MACADDRESS, nb.getMacAddress()));
        props.add(this.getModulProp(MetricIdConsts.METRIC_IP, nb.getIps()));
        props.add(this.getModulProp(ResourceOrMetricConst.RESOURCE_SYSOID, new String[]{nb.getOid()}));

        instance.setModuleProps(props);
        Long instanceId = null;
        try {
            logger.error("???????????????????????????????????????instance??????=" + JSONObject.toJSONString(instance));
            long begin = new Date().getTime();
            ResourceInstanceResult result = rsvc.addResourceInstance(instance);
            logger.error("????????????Node????????????????????????" + (new Date().getTime() - begin) + "ms");
            logger.error("????????????????????????????????????result=" + JSONObject.toJSONString(result));

            instanceId = result.getResourceInstanceId();
        } catch (InstancelibException e) {
            if (e.getCode() == 704) {//license??????????????????
                logger.error("license??????????????????????????????????????????");
                throw new RuntimeException(e);
            }
        } catch (Throwable e) {
            logger.error("ResourceInstanceService.addResourceInstance???%s???", e);
        }
        logger.error("????????????????????????new?????????????????????instanceId=" + instanceId + ",categoryId=" + categoryId + ",resourceId=" + resourceId + ",mac??????" + nb.getMacAddress());
        if (null != instanceId && instanceId.longValue() != 0) {
            nb.setInstanceId(instanceId);
            nb.setResourceId(resourceId);
            nb.setSubTopoId(NodeBo.SECONDTOPO_ID);
        }
        return instanceId;
    }

    @Override
    public JSONObject getProfileIdByInstanceId(Long instanceId) {
        JSONObject retn = new JSONObject();
        try {
            ProfileInfo pinfo = profileSvc.getBasicInfoByResourceInstanceId(instanceId);
            if (null != pinfo) {
                retn.put("type", pinfo.getProfileType().name());
                retn.put("profileId", pinfo.getProfileId());
                retn.put("instanceId", instanceId);
            }
        } catch (ProfilelibException e) {
            logger.error(instanceId + "??????????????????id??????profileid??????");
        }
        return retn;
    }

    @Override
    public JSONArray refreshLifeState(long[] ids) {
        JSONArray retn = new JSONArray();
        List<Long> idlist = new ArrayList<Long>();
        for (long id : ids) {
            idlist.add(id);
        }
        try {
            List<ResourceInstance> reses = rsvc.getResourceInstances(idlist);
            for (ResourceInstance re : reses) {
                JSONObject tmp = new JSONObject();
                tmp.put("lifeState", re.getLifeState().name().toLowerCase());
                tmp.put("instanceId", re.getId());
                tmp.put("showName", re.getShowName());
                retn.add(tmp);
            }
        } catch (Exception e) {
            logger.error("ThirdServiceImpl.refreshLifeState", e);
        }
        return retn;
    }

    @Override
    public int cancelDiscover() {
        return this.collector.stopDiscover();
    }

    @Override
    public JSONObject getVendorInfo(String oid) {
        JSONObject retn = new JSONObject();
        DeviceType devType = csvc.getDeviceType(oid);
        if (null != devType) {
            String verdorName = devType.getVendorName();
            if (!StringUtils.hasText(verdorName)) {
                logger.error(new StringBuffer("oid=").append(oid).append("?????????????????????"));
                retn.put("vendorName", "- -");
            } else {
                retn.put("vendorName", verdorName);
            }
            String series = devType.getModelNumber();
            if (!StringUtils.hasText(series)) {
                logger.error(new StringBuffer("oid=").append(oid).append("?????????????????????"));
                retn.put("series", "- -");
            } else {
                retn.put("series", series);
            }
        } else {
            logger.error(new StringBuffer("oid").append(oid).append("?????????????????????"));
        }
        return retn;
    }

    @Override
    public Long newLink(JSONObject linkInfo) {
        ResourceInstance res = new ResourceInstance();
        JSONObject dinfo = this.extractDomainInfo();
        Long srcMainInstanceId = linkInfo.getLong("srcMainInstanceId");    //????????????id
        Long desMainInstanceId = linkInfo.getLong("desMainInstanceId");    //???????????????id
        Long valueMainInstanceId = linkInfo.getLong("valueMainInstanceId");
        Long srcIfInstanceId = linkInfo.getLong("srcIfInstanceId");    //??????????????????id
        Long desIfInstanceId = linkInfo.getLong("desIfInstanceId");    //?????????????????????id
        Long valueIfInstanceId = linkInfo.getLong("valueInstanceId");
        if (valueIfInstanceId == null) {
            valueIfInstanceId = srcIfInstanceId;
        }
        ResourceInstance srcMainInstance = null;
        if (null != srcMainInstanceId) {
            try {
                srcMainInstance = rsvc.getResourceInstance(srcMainInstanceId);
            } catch (InstancelibException e) {
                logger.error("???????????????newLink()?????????Exception happend during getResourceInstance(srcMainInstanceId)", e);
            }
        }
        ResourceInstance desMainInstance = null;
        if (null != desMainInstanceId) {
            try {
                desMainInstance = rsvc.getResourceInstance(desMainInstanceId);
            } catch (InstancelibException e) {
                logger.error("???????????????newLink()?????????Exception happend during getResourceInstance(desMainInstanceId)", e);
            }
        }
        ResourceInstance srcSubInstance = null;
        if (null != srcIfInstanceId) {
            try {
                srcSubInstance = rsvc.getResourceInstance(srcIfInstanceId);
            } catch (InstancelibException e) {
                logger.error("???????????????newLink()?????????Exception happend during getResourceInstance(srcIfInstanceId)", e);
            }
        }
        ResourceInstance desSubInstance = null;
        if (null != desIfInstanceId) {
            try {
                desSubInstance = rsvc.getResourceInstance(desIfInstanceId);
            } catch (InstancelibException e) {
                logger.error("???????????????newLink()?????????Exception happend during getResourceInstance(desIfInstanceId)", e);
            }
        }
        //????????????categoryId
        ResourceDef def = csvc.getResourceDefById(LinkResourceConsts.RESOURCE_LAYER2LINK_ID);
        if (def != null) {
            CategoryDef cdef = def.getCategory();
            if (null != cdef) {
                res.setCategoryId(cdef.getId());
            }
        }
        //?????????????????????????????????
        StringBuilder name = new StringBuilder();
        res.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
        res.setResourceId(LinkResourceConsts.RESOURCE_LAYER2LINK_ID);
        res.setDomainId(dinfo.getLongValue("domainId"));
        res.setDiscoverNode(dinfo.getString("groupId"));
        res.setRepeatValidate(false);
        //???????????????????????????
        List<ModuleProp> props = new ArrayList<ModuleProp>();
        if (null != valueMainInstanceId) {
            //????????????ID collMainInstId
            props.add(getModulProp("collMainInstId", new String[]{String.valueOf(valueMainInstanceId)}));
        }
        //?????????????????????id collSubInstId
        props.add(getModulProp(LinkResourceConsts.PROP_COLL_SUBINST_ID, new String[]{String.valueOf(valueIfInstanceId)}));
        if (null != srcMainInstance) {
            //?????????IP?????? srcMainInstIP
            props.add(getModulProp(LinkResourceConsts.PROP_SRC_MAININST_IP, new String[]{srcMainInstance.getShowIP()}));
            //???????????????  srcDeviceName
            props.add(getModulProp(LinkResourceConsts.PROP_SRC_DEVICE_NAME, new String[]{srcMainInstance.getName()}));
            //???????????? deviceName
            props.add(getModulProp(LinkResourceConsts.PROP_DEVCIE_NAME, new String[]{srcMainInstance.getName()}));
            //?????????ID srcMainInstId
            props.add(getModulProp(LinkResourceConsts.PROP_SRC_MAININST_ID, new String[]{String.valueOf(srcMainInstanceId)}));
            //????????????????????? srcIfIndex
            props.add(getModulProp(LinkResourceConsts.PROP_SRC_IFINDEX, new String[]{linkInfo.getString("srcIfIndex")}));
        }
        if (null != desMainInstance) {
            //????????????IP?????? destMainInstIP
            props.add(getModulProp(LinkResourceConsts.PROP_DEST_MAININST_IP, new String[]{desMainInstance.getShowIP()}));
            //?????????????????? destDeviceName
            props.add(getModulProp(LinkResourceConsts.PROP_DEST_DEVICE_NAME, new String[]{desMainInstance.getName()}));
            //????????????ID destMainInstId
            props.add(getModulProp(LinkResourceConsts.PROP_DEST_MAININST_ID, new String[]{String.valueOf(desMainInstanceId)}));
            //???????????????????????? destIfIndex
            props.add(getModulProp(LinkResourceConsts.PROP_DEST_IFINDEX, new String[]{linkInfo.getString("desIfIndex")}));
        }
        Set<Long> filterMonitorLinkIds = new HashSet<Long>();    //??????????????????????????????????????????????????????
        if (null != srcSubInstance) {
            //????????????????????? srcIfName
            props.add(getModulProp(LinkResourceConsts.PROP_SRC_IFNAME, new String[]{srcSubInstance.getName()}));
            //??????????????????ID srcSubInstId
            props.add(getModulProp(LinkResourceConsts.PROP_SRC_SUBINST_ID, new String[]{String.valueOf(srcIfInstanceId)}));
            //????????????
            props.add(getModulProp("downSubInstId", new String[]{String.valueOf(srcSubInstance.getId())}));
            String srcName = !StringUtils.isEmpty(srcSubInstance.getShowName()) ? srcSubInstance.getShowName()
                    : (!StringUtils.isEmpty(srcSubInstance.getName()) ? srcSubInstance.getName() : "");
            name.append(srcSubInstance.getShowIP()).append(':').append(srcName).append("-");
        } else {
            filterMonitorLinkIds.add(res.getId());
        }
        if (null != desSubInstance) {
            //???????????????????????? destIfName
            props.add(getModulProp(LinkResourceConsts.PROP_DEST_IFNAME, new String[]{desSubInstance.getName()}));
            //?????????????????????ID destSubInstId
            props.add(getModulProp(LinkResourceConsts.PROP_DEST_SUBINST_ID, new String[]{String.valueOf(desIfInstanceId)}));
            String desName = !StringUtils.isEmpty(desSubInstance.getShowName()) ? desSubInstance.getShowName()
                    : (!StringUtils.isEmpty(desSubInstance.getName()) ? desSubInstance.getName() : "??????");
            name.append(desSubInstance.getShowIP()).append(':').append(desName);
        } else {
            filterMonitorLinkIds.add(res.getId());
        }
        //????????????
        res.setName(name.toString());
        res.setShowName(res.getName());
        //??????
        props.add(getModulProp("note", new String[]{""}));
        //??????????????????
        res.setModuleProps(props);
        //???????????????
        try {
            logger.error("newLink()??????rsvc.addResourceInstanceForLink(res)???????????????res=" + res);
            rsvc.addResourceInstanceForLink(Arrays.asList(res), false);
            logger.error("newLink()???addResourceInstanceForLink()????????????????????????????????????profileSvc.enableMonitorForLink([" + res.getId() + "])");
            if (!filterMonitorLinkIds.contains(res.getId())) {
                profileSvc.enableMonitorForLink(Arrays.asList(res.getId()));
            }
            logger.error("newLink()?????????????????????");
            return res.getId();
        } catch (InstancelibException | ProfilelibException e) {
            logger.error("???????????????newLink()?????????addResourceInstanceForLink()??????enableMonitorForLink()????????????", e);
        }
        return null;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void interceptor(InstancelibEvent e) throws Exception {
        if (e.getEventType() == EventEnum.INSTANCE_DELETE_EVENT) {
            List<Long> instanceIds = (List<Long>) e.getSource();
            if (null != instanceIds && !instanceIds.isEmpty()) {
                //????????????????????????????????????
                List<Long> nodeIds = ndao.getNodeIdByInstanceIds(instanceIds);
                //???????????????????????????id
                List<Long> linkInstIds = ldao.getLinkInstancesIdByNodeIds(nodeIds);
                //????????????????????????
                ldao.deleteByNodeIds(nodeIds, true);
                //?????????????????????????????????????????????instance_id???null,????????????????????????
                ndao.updateNodeRelationOnResourceDelete(instanceIds);
                //??????????????????????????????
                try {
                    rsvc.removeResourceInstanceByLinks(linkInstIds);
                    //????????????????????????????????????
                    ndao.unbindAllRelation(instanceIds);
                } catch (Throwable e1) {
                    logger.error("ResourceInstanceService.removeResourceInstanceByLinks", e1);
                }
            }
        } else if (e.getEventType() == EventEnum.INSTANCE_ADD_EVENT) {
            List<ResourceInstance> instances = (List<ResourceInstance>) e.getSource();
            if (instances == null || instances.size() <= 0) {
                return;
            }

            for (ResourceInstance instance : instances) {
                if (null != instance) {
                    String categoryId = instance.getCategoryId();
                    if (null != categoryId) {
                        String[] categoryParents = resApi.getCategoryParents(categoryId);
                        if (null != categoryParents && categoryParents.length > 1) {
                            int index = categoryParents.length - 2;
                            if (index < 0) {
                                index = 0;
                            }
                            String catParentId = categoryParents[index];
                            Long id = instance.getId();
                            if (null != id && null != catParentId) {
                                //???????????????????????????????????????
                                NodeBo nb = new NodeBo();
                                DevType type = DevType.OTHER;
                                switch (catParentId) {
                                    case CapacityConst.HOST:
                                        type = DevType.HOST;
                                        break;
                                    case CapacityConst.STORAGE:
                                        type = DevType.STORAGE;
                                        break;
                                    case CapacityConst.FIREWALL:
                                        type = DevType.FIREWALL;
                                        break;
                                    case CapacityConst.ROUTER:
                                        type = DevType.ROUTER;
                                        break;
                                    case CapacityConst.SWITCH:
                                        type = DevType.SWITCH;
                                        break;
                                    default:
                                        break;
                                }
                                String[] oids = instance.getModulePropBykey(MetricIdConsts.SYS_OBJECT_ID);
                                nb.setIp(instance.getShowIP());
                                nb.setOid((null != oids && 0 != oids.length) ? oids[0] : "--");
                                nb.setType(type.name());
                                nb.setInstanceId(id);
                                nb.mapAttr();
                                nb.setX(0d);
                                nb.setY(0d);
                                //ndao.save(Arrays.asList(nb));
                            }
                        }
                    }
                }
            }

        } else if (e.getEventType() == EventEnum.INSTANCE_REFRESH_EVENT) {

        }
    }

    @Override
    public JSONObject checkUserInstanceAuth(ILoginUser user, Long instanceId) {
        JSONObject retn = new JSONObject();
        //??????????????????????????????
        ResourceInstance instance = null;
        try {
            instance = rsvc.getResourceInstance(instanceId);
        } catch (InstancelibException e) {
            retn.put("state", 700);
            retn.put("msg", "????????????????????????");
            retn.put("moreMsg", e.getMessage());
        }
        if (null == instance) {
            retn.put("state", 700);
            retn.put("msg", "?????????????????????");
        } else {
            //ResourceQueryBo queryBo = new ResourceQueryBo(user);
            List<Long> instaceIds = new ArrayList<>();
            instaceIds.add(instanceId);
            //List<Long> result = resApi.accessFilter(queryBo, instaceIds);
            retn.put("state", 200);
			/*if(result!=null && result.size()>0){
				retn.put("state", 200);
			}else{
				retn.put("state", 700);
				retn.put("msg", "????????????");
			}*/
        }
        return retn;
    }

    public CapacityService getCsvc() {
        return csvc;
    }

    public void setCsvc(CapacityService csvc) {
        this.csvc = csvc;
    }
}
