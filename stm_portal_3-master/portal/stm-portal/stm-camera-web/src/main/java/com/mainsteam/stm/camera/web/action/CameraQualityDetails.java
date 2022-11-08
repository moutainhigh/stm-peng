package com.mainsteam.stm.camera.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.camera.api.ICameraService;
import com.mainsteam.stm.camera.bo.CameraBo;
import com.mainsteam.stm.camera.bo.CameraVo;
import com.mainsteam.stm.caplib.common.CategoryDef;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.camera.api.ICameraMonitorService;
import com.mainsteam.stm.camera.bo.CameraMonitorPageBo;
import com.mainsteam.stm.camera.bo.CaremaMonitorBo;
import com.mainsteam.stm.camera.web.vo.CameraPageVo;
import com.mainsteam.stm.camera.web.vo.CameraResourceMonitorVo;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.node.NodeGroupService;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.api.IResourceMonitorApi;
import com.mainsteam.stm.portal.resource.api.ResourceCategoryApi;
import com.mainsteam.stm.portal.resource.web.vo.zTreeVo;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.SecureUtil;
import com.mainsteam.stm.util.StringUtil;

@Controller
@RequestMapping({ "/portal/resource/cameraqualitydetails" })
public class CameraQualityDetails extends BaseAction {
	private Logger logger = Logger.getLogger(CameraQualityDetails.class);

	@Resource
	private NodeGroupService nodeGroupService;

	@Resource
	private CapacityService capacityService;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private IResourceMonitorApi resourceMonitorApi;

	@Resource
	private ResourceCategoryApi resourceCategoryApi;

	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;

	@Resource
	private IDomainApi domainApi;

	@Resource
	private IUserApi userApi;

	@Resource
	private IDomainApi stm_system_DomainApi;

	@Resource
	private CustomPropService customPropService;

	@Resource
	private DiscoverPropService discoverPropService;

	@Resource
	private ICameraMonitorService cameraMonitorService;

	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;

    @Resource
    private MetricDataService metricDataService;

    @Resource
    private ICameraService cameraService;

	@RequestMapping({ "/getResourceCategoryList4New" })
	public JSONObject getResourceCategoryList() {
		List categorys = this.cameraMonitorService.getAllResourceCategoryNew();
		return toSuccess(categorys);
	}

	@RequestMapping({ "/getHaveMonitorNew" })
	public JSONObject getHaveMonitor(CameraPageVo pageVo, HttpSession session) {
        //logger.info("-----------开始查询视频诊断的摄像头----------");
		ILoginUser user = getLoginUser(session);
		CameraMonitorPageBo pageBo = null;
		try {
			CameraResourceMonitorVo rmv = pageVo.getCondition();
			String onlineStatus = rmv.getOnlineStatus();// 页面上选取的在线状态
			  //logger.info("-----------页面上选取的在线状态----------"+onlineStatus);
			String diagnoseResult = rmv.getDiagnoseResult();// 页面上选取的诊断结果
			//logger.info("-----------页面上选取的诊断结果----------"+diagnoseResult);
			String dignoseMetrics = rmv.getDignoseMetrics();// 页面上选取的诊断指标
			//logger.info("-----------页面上选取的诊断指标----------"+dignoseMetrics);
			Map<String, List<String>> queryMap = new HashMap<String, List<String>>();
			Map<String, List<String>> statusMap = new HashMap<String, List<String>>();
			if (!StringUtil.isNull(onlineStatus)) {
				String[] statusArray = onlineStatus.split(",");
				List<String> list = Arrays.asList(statusArray);
				statusMap.put("status", list);
			}

			// 如果诊断结果是空，诊断指标不为空，则默认显示所选中的诊断指标的所有值
			if (StringUtil.isNull(diagnoseResult) && !StringUtil.isNull(dignoseMetrics)) {
				String[] metricsArray = dignoseMetrics.split(",");
				for (int i = 0; i < metricsArray.length; i++) {
					List<String> list = new ArrayList<String>();
					list.add("CRITICAL");
                    list.add("SERIOUS");
					//根据新的需求，如果诊断结果是空，诊断指标不为空，则默认显示所选中的诊断指标的所有值
					list.add("NORMAL");
                    list.add("WARN");
					queryMap.put(metricsArray[i], list);
				}

			}

			// 如果诊断指标是空，诊断结果不为空，只要有满足任何诊断结果的数据都显示出来
			else if (StringUtil.isNull(dignoseMetrics) && !StringUtil.isNull(diagnoseResult)) {
				String[] metricsResultArray = diagnoseResult.split(",");
				List<String> list = Arrays.asList(metricsResultArray);
				queryMap.put("all", list);
			}

			// 如果诊断指标不为空，诊断结果不为空
			else if (!StringUtil.isNull(dignoseMetrics) && !StringUtil.isNull(diagnoseResult)) {
				String[] metricsArray = dignoseMetrics.split(",");
				String[] metricsResultArray = diagnoseResult.split(",");
				List<String> list = Arrays.asList(metricsResultArray);
				for (int i = 0; i < metricsArray.length; i++) {
					queryMap.put(metricsArray[i], list);
				}
			}

			pageBo = cameraMonitorService.getMonitored(user, pageVo.getStartRow(), pageVo.getRowCount(),
					rmv.getInstanceStatus(), rmv.getiPorName(), rmv.getDomainId(), rmv.getCategoryId(),
					rmv.getCategoryIds(), rmv.getResourceId(), rmv.getIsCustomResGroup(), pageVo.getSort(),
					pageVo.getOrder(), queryMap, statusMap);

			pageVo.setTotalRecord(pageBo.getTotalRecord());
			logger.info("----------查询的摄像头总数是----------"+pageBo.getTotalRecord());
			List<CameraResourceMonitorVo> resourceVoList = new ArrayList<CameraResourceMonitorVo>();
			List<CaremaMonitorBo> monitorBoList = pageBo.getResourceMonitorBosExtends();
			for (int i = 0; (monitorBoList != null) && (i < monitorBoList.size()); i++) {
				CaremaMonitorBo resourceMonitorBo = monitorBoList.get(i);
				CameraResourceMonitorVo cameraResourceMonitorVo = instanceBoToResourceVo(resourceMonitorBo);
				resourceVoList.add(cameraResourceMonitorVo);
			}
			pageVo.setResourceMonitors(resourceVoList);
			pageVo.setResourceCategoryBos(pageBo.getResourceCategoryBos());
			pageVo.setAbnormalNumber(pageBo.getAbnormalNumber());
			pageVo.setOfflineNumber(pageBo.getOfflineNumber());
		} catch (Exception e) {
		    e.printStackTrace();
			logger.error("查询视频诊断的摄像头出错:", e);
		}
		return toSuccess(pageVo);
	}

    @RequestMapping({ "/getVQDList" })
    public JSONObject getVQDList(CameraPageVo pageVo, Page<CameraBo, CameraVo> page, HttpSession session) {
        this.buildPage(pageVo, page);
        CameraVo condition = page.getCondition();
        //在想状态和诊断结果相反则数据为空
        String statusFlag = condition.getStatusFlag();
        String onlineFlag = condition.getOnlineFlag();
        if("CRITICAL".equals(onlineFlag) && "NORMAL".equals(statusFlag)){
            pageVo.setTotalRecord(0);
            pageVo.setResourceMonitors(new ArrayList<CameraResourceMonitorVo>());
            pageVo.setAbnormalNumber(0);
            pageVo.setOfflineNumber(0);
            return toSuccess(pageVo);
        }

        this.cameraService.selectPageForCamera(page);

        pageVo.setTotalRecord(page.getTotalRecord());
        pageVo.setResourceMonitors(this.oto(page.getDatas()));
        List<CameraBo> datas = page.getDatas();
        pageVo.setAbnormalNumber(0);
        pageVo.setOfflineNumber(0);
        page.setStartRow(0);
        page.setRowCount(1);
        if(onlineFlag.equals("ALL")){
            if(!"NORMAL".equals(statusFlag)){
                condition.setOnlineFlag("NORMAL");
                this.cameraService.getOfflineNumber(page);
                pageVo.setAbnormalNumber(page.getTotalRecord());

                condition.setOnlineFlag("CRITICAL");
                this.cameraService.getOfflineNumber(page);
                pageVo.setOfflineNumber(page.getTotalRecord());
            }
        }else{
            if("NORMAL".equals(onlineFlag)){
                condition.setOnlineFlag("NORMAL");
                if("NORMAL".equals(statusFlag)){
                    pageVo.setAbnormalNumber(0);
                }else{
                    this.cameraService.getOfflineNumber(page);
                    pageVo.setAbnormalNumber(page.getTotalRecord());
                }
                pageVo.setOfflineNumber(0);
            }else{
                condition.setOnlineFlag("CRITICAL");
                this.cameraService.getOfflineNumber(page);
                pageVo.setAbnormalNumber(0);
                pageVo.setOfflineNumber(page.getTotalRecord());
            }
        }
	    return toSuccess(pageVo);
    }

    private List<CameraResourceMonitorVo> oto(List<CameraBo> dataList){
        List<CameraResourceMonitorVo> resourceVoList = new ArrayList<>(dataList.size());
        CameraResourceMonitorVo cr = null;
        for(CameraBo cameraBo : dataList){
            cr = new CameraResourceMonitorVo();
            cr.setId(cameraBo.getId());
            cr.setSourceName(cameraBo.getShowName());
            cr.setIpAddress(cameraBo.getDevIP());
            cr.setInstanceState(cameraBo.getInstanceState());
            cr.setInstanceStatus(cameraBo.getInstanceStatus());
            cr.setBrightness(cameraBo.getBrightness());
            cr.setLegibility(cameraBo.getLegibility());
            cr.setScreenFreezed(cameraBo.getScreenFreezed());
            cr.setColourCast(cameraBo.getColourCast());
            cr.setLostSignal(cameraBo.getLostSignal());
            cr.setSightChange(cameraBo.getSightChange());
            cr.setPtzSpeed(cameraBo.getPtzSpeed());
            cr.setKeepOut(cameraBo.getKeepOut());
            cr.setStreakDisturb(cameraBo.getStreakDisturb());
            cr.setPTZDegree(cameraBo.getPTZDegree());
            cr.setSnowflakeDisturb(cameraBo.getSnowflakeDisturb());
            cr.setDignoseTime(cameraBo.getDignoseTime());
            cr.setAvailability(cameraBo.getAvailability());
            cr.setMonitorType(cameraBo.getMonitorType() == null? "--" : cameraBo.getMonitorType());
            resourceVoList.add(cr);
        }



        return resourceVoList;
    }

    private void  buildPage(CameraPageVo pageVo,Page<CameraBo, CameraVo> page){
        CameraResourceMonitorVo rmv = pageVo.getCondition();
        CameraVo cameraVo = new CameraVo();
        //获取在线状态
        List<String> onList = new ArrayList<String>();
        String onlineStatus = rmv.getOnlineStatus();
        if(StringUtil.isNull(onlineStatus)){
            onList.add("NORMAL");
            onList.add("CRITICAL");
            cameraVo.setOnlineFlag("ALL");
        }else{
            String[] onSplit = onlineStatus.split(",");
            if("0".equals(onSplit[0]) || onSplit.length == 2){
                onList.add("NORMAL");
                onList.add("CRITICAL");
                cameraVo.setOnlineFlag("ALL");
            }else{
                if("NORMAL".equals(onSplit[0])){
                    onList.add("NORMAL");
                    cameraVo.setOnlineFlag("NORMAL");
                }else{
                    onList.add("CRITICAL");
                    cameraVo.setOnlineFlag("CRITICAL");
                }
            }
        }
        //获取诊断结果
        List<String> resultList = new ArrayList<String>();
        String diagnoseResult = rmv.getDiagnoseResult();
        if(StringUtil.isNull(diagnoseResult)){
            resultList.add("NORMAL");
            resultList.add("WARN");
            resultList.add("SERIOUS");
        }else{
            String[] resultSplit = diagnoseResult.split(",");
            for(String result : resultSplit){
                resultList.add(result);
            }
        }
        //获取诊断指标
        List<String> metricList = new ArrayList<String>();
        String diagnoseMetrics = rmv.getDignoseMetrics();
        String[] metrics = {"PTZDegree","PTZSpeed","brightness","colourCast","keepOut","legibility","lostSignal","screenFrozen","sightChange","snowflakeDisturb","streakDisturb"};
        if(StringUtil.isNull(diagnoseMetrics)){
            for(String metric : metrics){
                metricList.add(metric);
            }
        }else{
            String[] metricSplit = diagnoseMetrics.split(",");
            for(String metric : metricSplit){
                metricList.add(metric);
            }
        }

        //根据诊断结果判断状态查询
        if(resultList.size() == 1){
            cameraVo.setStatusFlag("NORMAL");
        }else if(resultList.size() == 2){
            cameraVo.setStatusFlag("CRITICAL");
        }else{
            cameraVo.setStatusFlag("ALL");
        }

        cameraVo.setAvailabilityStates(onList); //资源状态
        cameraVo.setStates(resultList); //诊断结果状态
        cameraVo.setMetricIds(metricList);  //指定指标集合
        cameraVo.setLiftState("MONITORED");
        String filter = pageVo.getCondition().getiPorName();
        if(!StringUtil.isNull(filter)){
            cameraVo.setFiltrateStr(filter);
        }
        String sort = pageVo.getSort();
        String s = null;
        if(!StringUtil.isNull(sort)){
            if("sourceName".equals(sort)){
                sort = "showName";
            }else if("ipAddress".equals(sort)){
                sort = "devIP";
            }else if("dignoseTime".equals(sort)){
                sort = "lastCollectTime";
            }else{
                for(String id : metrics){
                    if(id.equalsIgnoreCase(sort)){
                        sort = id;
                        break;
                    }
                }
                s = "orderStr";
            }
            cameraVo.setOrderMetric(sort);
            if(s != null){
                page.setSort(s);
            }else{
                page.setSort(sort);
            }
        }
        String order = pageVo.getOrder();
        if(!StringUtil.isNull(order)){
            cameraVo.setOrder(order);
            page.setOrder(order);
        }

        long startRow = pageVo.getStartRow();//起始行
        long rowCount = pageVo.getRowCount();//每页显示行数

        //封装查询page
        page.setStartRow(startRow);
        page.setRowCount(rowCount);
        page.setCondition(cameraVo);
    }



	// 导出
	@RequestMapping({ "/downloadCameras" })
	public void downloadCameras(HttpServletResponse response, HttpServletRequest request, HttpSession session){
		logger.info("-----------开始视频诊断的导出----------");
		List<CaremaMonitorBo> cameraList = cameraMonitorService.getCameraList();
		List<CaremaMonitorBo> dataList = new ArrayList<CaremaMonitorBo>();
		for (CaremaMonitorBo c : cameraList) {
			CaremaMonitorBo caremaMonitorBo = new CaremaMonitorBo();
			caremaMonitorBo.setShowName(c.getShowName());
			caremaMonitorBo.setIp(c.getIp());
			caremaMonitorBo.setMonitorType(c.getMonitorType());;
			// 在线状态
			if (c.getAvailability().equals("NORMAL") || c.getAvailability().equals("SERIOUS")) {
				caremaMonitorBo.setAvailability("是");
			} else if (c.getAvailability().equals("CRITICAL")) {
				caremaMonitorBo.setAvailability("否");
			} else
				caremaMonitorBo.setAvailability("--");
			// 亮度
			if (c.getBrightness().equals("NORMAL")) {
				caremaMonitorBo.setBrightness("正常");
			} else if (c.getBrightness().equals("SERIOUS")) {
				caremaMonitorBo.setBrightness("异常");
			} else
				caremaMonitorBo.setBrightness("--");

			// 清晰度
			if (c.getLegibility().equals("NORMAL")) {
				caremaMonitorBo.setLegibility("正常");
			} else if (c.getLegibility().equals("SERIOUS")) {
				caremaMonitorBo.setLegibility("异常");
			} else
				caremaMonitorBo.setLegibility("--");

			// 画面冻结
			if (c.getScreenFreezed().equals("NORMAL")) {
				caremaMonitorBo.setScreenFreezed("正常");
			} else if (c.getScreenFreezed().equals("SERIOUS")) {
				caremaMonitorBo.setScreenFreezed("异常");
			} else
				caremaMonitorBo.setScreenFreezed("--");

			// 画面偏色
			if (c.getColourCast().equals("NORMAL")) {
				caremaMonitorBo.setColourCast("正常");
			} else if (c.getColourCast().equals("SERIOUS")) {
				caremaMonitorBo.setColourCast("异常");
			} else
				caremaMonitorBo.setColourCast("--");

			// 信号缺失
			if (c.getLostSignal().equals("NORMAL")) {
				caremaMonitorBo.setLostSignal("正常");
			} else if (c.getLostSignal().equals("SERIOUS")) {
				caremaMonitorBo.setLostSignal("异常");
			} else
				caremaMonitorBo.setLostSignal("--");

			// 场景变换
			if (c.getSightChange().equals("NORMAL")) {
				caremaMonitorBo.setSightChange("正常");
			} else if (c.getSightChange().equals("SERIOUS")) {
				caremaMonitorBo.setSightChange("异常");
			} else
				caremaMonitorBo.setSightChange("--");

			// PTZ速度
			if (c.getPtzSpeed().equals("NORMAL")) {
				caremaMonitorBo.setPtzSpeed("正常");
			} else if (c.getPtzSpeed().equals("SERIOUS")) {
				caremaMonitorBo.setPtzSpeed("异常");
			} else
				caremaMonitorBo.setPtzSpeed("--");

			// 人为遮挡
			if (c.getKeepOut().equals("NORMAL")) {
				caremaMonitorBo.setKeepOut("正常");
			} else if (c.getKeepOut().equals("SERIOUS")) {
				caremaMonitorBo.setKeepOut("异常");
			} else
				caremaMonitorBo.setKeepOut("--");

			// 条纹干扰
			if (c.getStreakDisturb().equals("NORMAL")) {
				caremaMonitorBo.setStreakDisturb("正常");
			} else if (c.getStreakDisturb().equals("SERIOUS")) {
				caremaMonitorBo.setStreakDisturb("异常");
			} else
				caremaMonitorBo.setStreakDisturb("--");

			// 雪花干扰
			if (c.getSnowflakeDisturb().equals("NORMAL")) {
				caremaMonitorBo.setSnowflakeDisturb("正常");
			} else if (c.getSnowflakeDisturb().equals("SERIOUS")) {
				caremaMonitorBo.setSnowflakeDisturb("异常");
			} else
				caremaMonitorBo.setSnowflakeDisturb("--");

			// PTZ角度
			if (c.getPTZDegree().equals("NORMAL")) {
				caremaMonitorBo.setPTZDegree("正常");
			} else if (c.getPTZDegree().equals("SERIOUS")) {
				caremaMonitorBo.setPTZDegree("异常");
			} else
				caremaMonitorBo.setPTZDegree("--");

			// 诊断时间
			caremaMonitorBo.setDignoseTime(c.getDignoseTime());
			dataList.add(caremaMonitorBo);
			caremaMonitorBo = null;
		}

		String[] columns = { "showName", "ip", "monitorType", "availability", "brightness", "legibility",
				"screenFreezed", "colourCast", "lostSignal", "sightChange", "ptzSpeed", "keepOut", "streakDisturb",
				"PTZDegree", "snowflakeDisturb", "dignoseTime" };

		try{
		    String filePath = this.getRootDirPath();
            FileInputStream fileInputStream = new FileInputStream(filePath + "camera.xls");
            //URL url = new URL(filePath + "camera.xls");
			POIFSFileSystem ps = new POIFSFileSystem(fileInputStream);

			HSSFWorkbook wb = new HSSFWorkbook(ps);

			HSSFSheet sheet = wb.getSheetAt(0);

			HSSFRow row = null;

			for (CaremaMonitorBo c : dataList) {
				row = sheet.createRow((short) (sheet.getLastRowNum() + 1));

				Class clazz = c.getClass();

				int i = 0;

				for (int len = columns.length; i < len; ++i) {
					String value = "";
					try {
						Field feild = clazz.getDeclaredField(columns[i]);
						feild.setAccessible(true);
						if (null != feild.get(c)) {
							value = feild.get(c).toString();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					row.createCell(i).setCellValue(value);

				}
			}

			String encoding = "UTF-8";

			String filename = "摄像头信息.xls";

			String agent = request.getHeader("USER-AGENT");

			if (null == agent) {
				filename = URLEncoder.encode(filename, encoding);
			} else if ((agent = agent.toLowerCase()).indexOf("firefox") > -1) {
				filename = new String(filename.getBytes(encoding), "iso-8859-1");
			} else {
				filename = URLEncoder.encode(filename, encoding);
				if ((agent.indexOf("msie") > -1) && (filename.length() > 150))
					filename = new String(filename.getBytes(encoding), "ISO-8859-1");
			}
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			response.setContentType("application/octet-stream");
			response.addHeader("Content-Type", "text/html; charset=" + encoding);
			OutputStream os = response.getOutputStream();

			os.flush();
			wb.write(os);
			os.close();
		}catch(Exception e){
			logger.error("视频诊断导出报错:", e);
		}
		


	}

	private CameraResourceMonitorVo instanceBoToResourceVo(CaremaMonitorBo resourceMonitorBo) {
		//logger.info("-----------开始通过类型转换得到摄像头信息----------");
		CameraResourceMonitorVo resourceVo = new CameraResourceMonitorVo();
		resourceVo.setId(resourceMonitorBo.getInstanceId());
		resourceVo.setSourceName(resourceMonitorBo.getShowName());
		resourceVo.setIpAddress(resourceMonitorBo.getIp());
		resourceVo.setDomainName(null == domainApi.get(resourceMonitorBo.getDomainId().longValue()) ? ""
				: domainApi.get(resourceMonitorBo.getDomainId().longValue()).getName());

		resourceVo.setDomainId(resourceMonitorBo.getDomainId());

		resourceVo.setMonitorType(resourceMonitorBo.getMonitorType());
		resourceVo.setInstanceState(resourceMonitorBo.getLifeState().toString());
		resourceVo.setCpuStatus(resourceMonitorBo.getCpuStatus());
		resourceVo.setCpuAvailability(resourceMonitorBo.getCpuAvailability());
		resourceVo.setMemoryStatus(resourceMonitorBo.getMemoryStatus());
		resourceVo.setMemoryAvailability(resourceMonitorBo.getMemoryAvailability());
		resourceVo.setResponseTime(resourceMonitorBo.getResponseTime());
		resourceVo.setInstanceStatus(resourceMonitorBo.getInstanceStatus());
		resourceVo.setResourceId(resourceMonitorBo.getResourceId());
		resourceVo.setHasRight(resourceMonitorBo.getHasRight());
		resourceVo.setCpuIsAlarm(resourceMonitorBo.getCpuIsAlarm());
		resourceVo.setMemoryIsAlarm(resourceMonitorBo.getMemoryIsAlarm());
		resourceVo.setDcsGroupName(resourceMonitorBo.getDcsGroupName());
		resourceVo.setHasTelSSHParams(resourceMonitorBo.getHasTelSSHParams());
		resourceVo.setAvailability(resourceMonitorBo.getAvailability());
		resourceVo.setBrightness(resourceMonitorBo.getBrightness());
		resourceVo.setPTZDegree(resourceMonitorBo.getPTZDegree());
		resourceVo.setColourCast(resourceMonitorBo.getColourCast());
		resourceVo.setDignoseTime(resourceMonitorBo.getDignoseTime());
		resourceVo.setKeepOut(resourceMonitorBo.getKeepOut());
		resourceVo.setLegibility(resourceMonitorBo.getLegibility());
		resourceVo.setLostSignal(resourceMonitorBo.getLostSignal());
		resourceVo.setSightChange(resourceMonitorBo.getSightChange());
		resourceVo.setPtzSpeed(resourceMonitorBo.getPtzSpeed());
		resourceVo.setScreenFreezed(resourceMonitorBo.getScreenFreezed());
		resourceVo.setSnowflakeDisturb(resourceMonitorBo.getSnowflakeDisturb());
		resourceVo.setStreakDisturb(resourceMonitorBo.getStreakDisturb());
		resourceVo.setAlarmTips(resourceMonitorBo.getAlarmTips());
		resourceVo.setGisx(resourceMonitorBo.getGisx());
		resourceVo.setGisy(resourceMonitorBo.getGisy());
		resourceVo.setGroupId(resourceMonitorBo.getGroupId());
		if (resourceMonitorBo.getResourceId().equalsIgnoreCase("EastWitCamera")) {
			try {
				DiscoverProp typeProp = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"cameraType");
				if (typeProp != null) {
					resourceVo.setCameraType(typeProp.getValues()[0]);
				}
                String[] metS = {"groupName","address","devUser","devPwd","devPort"};
                List<MetricData> metricInfoDatas = metricDataService.getMetricInfoDatas(resourceMonitorBo.getInstanceId(), metS);
                for(MetricData md : metricInfoDatas){
                    String data = md.getData()[0];
                    String metricId = md.getMetricId();
                    if("groupName".equals(metricId)){
                        resourceVo.setCameraGroup(data);
                    }else if("address".equals(metricId)){
                        resourceVo.setCameraAddress(data);
                    }else if("devUser".equals(metricId)){
                        resourceVo.setLoginUser(data);
                    }else if("devPwd".equals(metricId)){
                        resourceVo.setLoginPassword(data);
                    }else if("devPort".equals(metricId)){
                        resourceVo.setCameraPort(Integer.parseInt(data));
                    }
                }

				/*DiscoverProp groupProp = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"groupName");
				if (groupProp != null) {
					resourceVo.setCameraGroup(groupProp.getValues()[0]);
				}
				DiscoverProp addressProp = discoverPropService
						.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "address");
				if (addressProp != null) {
					resourceVo.setCameraAddress(addressProp.getValues()[0]);
				}
				DiscoverProp loginUser = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"loginuser");
				if (loginUser != null) {
					resourceVo.setLoginUser(loginUser.getValues()[0]);
				}
				DiscoverProp loginPassword = discoverPropService
						.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(), "loginpassword");
				if (loginPassword != null) {
					resourceVo.setLoginPassword(SecureUtil.pwdDecrypt(loginPassword.getValues()[0]));
				}
				DiscoverProp cameraPort = discoverPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId(),
						"cameraPort");
				if (addressProp != null) {
					resourceVo.setCameraPort(Integer.parseInt(cameraPort.getValues()[0]));
				}*/
			} catch (InstancelibException e) {
				logger.error("获取摄像头属性失败：" + e.getMessage());
			}

		}

		/*CustomProp prop = null;
		try {
			prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
					"liablePerson");
		} catch (InstancelibException e) {
			logger.error("获取摄像头属性失败：" + e.getMessage());
		}

		User user = new User();
		if (prop != null) {
			String[] accountIds = prop.getValues();
			if (accountIds.length > 0) {
				user = userApi.get(Long.valueOf(Long.parseLong(accountIds[0])));
				if (user != null) {
					resourceVo.setLiablePerson(user.getName());
				}
			}
		}*/
		/*try {
			prop = null;
			prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
					"maintainStaus");
		} catch (InstancelibException e) {
			logger.error("获取摄像头属性失败：" + e.getMessage());
		}

		if (prop != null) {
			resourceVo.setMaintainStaus(prop.getValues()[0]);
			prop = null;
			try {
				prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
						"maintainStartTime");
			} catch (InstancelibException e) {
				logger.error("获取摄像头属性失败：" + e.getMessage());
			}

			if (prop != null) {
				resourceVo.setMaintainStartTime(prop.getValues()[0]);
			}
			prop = null;
			try {
				prop = customPropService.getPropByInstanceAndKey(resourceMonitorBo.getInstanceId().longValue(),
						"maintainEndTime");
			} catch (InstancelibException e) {
				logger.error("获取摄像头属性失败：" + e.getMessage());
			}

			if (prop != null) {
				resourceVo.setMaintainEndTime(prop.getValues()[0]);
			}
		}*/
		return resourceVo;
	}

	/**
	 * 得到所有的摄像头资源
	 * @param session
	 * @return
	 */
	@RequestMapping({ "/getCameraResourceDef" })
	public JSONObject getCameraResourceDef(HttpSession session) {
		List resourceInstanceList = this.resourceCategoryApi.getAllResourceInstanceList(getLoginUser(session));

		List treeList = getTreeListByResourceInstanceList(resourceInstanceList, true);
		return toSuccess(JSONObject.toJSON(treeList));
	}
	
	
	/**
	 * 得到所有的非摄像头资源
	 * @param session
	 * @return
	 */
	  @RequestMapping({"/getLeftResourceDef"})
	   public JSONObject getLeftResourceDef(String ids, HttpSession session)
	    {
		//得到所有的资源
	     List resourceInstanceList = this.resourceCategoryApi.getExceptResourceInstanceListByIds(ids, getLoginUser(session));
	 
	   //过滤掉摄像头的资源
	  List treeList = getTreeListByResourceInstanceListExceptCamera(resourceInstanceList, true);
	
	     return toSuccess(JSONObject.toJSON(treeList));
	     }
	  
	  
	  
	  
	  /**
	   * 得到非摄像头的资源
	   * @param session
	   * @return
	   */
	   @RequestMapping({"/getAllResourceDefExceptCamera"})
		 public JSONObject getAllResourceDefExceptCamera(HttpSession session)
		{
		   //得到所有的资源
		 List resourceInstanceList = this.resourceCategoryApi
					.getAllResourceInstanceList(getLoginUser(session));
			
		 //过滤掉摄像头的资源
			List treeList = getTreeListByResourceInstanceListExceptCamera(resourceInstanceList, true);
			
			 return toSuccess(JSONObject.toJSON(treeList));
			 }
	
	
	
/**
 * 添加或者修改分组后，只允许添加摄像头，所以结果集只查询摄像头的信息
 * @param instanceList
 * @param isFilterDisplay
 * @return
 */
	private List<zTreeVo> getTreeListByResourceInstanceList(List<ResourceInstanceBo> instanceList,
			boolean isFilterDisplay) {
		List<zTreeVo> treeOneList = new ArrayList<zTreeVo>();
		List<zTreeVo> treeTwoList = new ArrayList<zTreeVo>();

		Map secondTreeAndInstanceMap = new HashMap();

		Map firstTreeAndSecondTreeMap = new HashMap();

		if ((instanceList == null) || (instanceList.size() <= 0)) {
			return treeOneList;
		}
		for (int i = 0; i < instanceList.size(); ++i) {
			ResourceInstanceBo resourceInstance = (ResourceInstanceBo) instanceList.get(i);

			if ("Cameras".equals(resourceInstance.getCategoryId())) {
				CategoryDef secondCategory = this.capacityService.getCategoryById(resourceInstance.getCategoryId());

				CategoryDef firstCategory = secondCategory.getParentCategory();

				if ((!(DefIsdisplay(firstCategory))) && (isFilterDisplay)) {
					continue;
				}
				zTreeVo instanceTree = defTozTreeVo(resourceInstance, false, secondCategory.getId());

				zTreeVo secondTree = defTozTreeVo(secondCategory, true, firstCategory.getId());

				zTreeVo firstTree = defTozTreeVo(firstCategory, true, "0");

				if (!(secondTreeAndInstanceMap.containsKey(secondTree.getId()))) {
					secondTreeAndInstanceMap.put(secondTree.getId(), new ArrayList());

				}

				((List) secondTreeAndInstanceMap.get(secondTree.getId())).add(instanceTree);

				if (!(treeTwoList.contains(secondTree))) {
					treeTwoList.add(secondTree);

				}

				if (!(treeOneList.contains(firstTree))) {
					treeOneList.add(firstTree);
				}
			}

		}

		for (zTreeVo secondTree : treeTwoList) {
			secondTree.setChildren((List) secondTreeAndInstanceMap.get(secondTree.getId()));

			if (!(firstTreeAndSecondTreeMap.containsKey(secondTree.getPId()))) {
				firstTreeAndSecondTreeMap.put(secondTree.getPId(), new ArrayList());

			}

			((List) firstTreeAndSecondTreeMap.get(secondTree.getPId())).add(secondTree);

		}

		for (zTreeVo firstTree : treeOneList) {
			firstTree.setChildren((List) firstTreeAndSecondTreeMap.get(firstTree.getId()));

		}

		return treeOneList;
	}
	
	/**
	 * 得到所有非摄像头的资源
	 * @param instanceList
	 * @param isFilterDisplay
	 * @return
	 */
	private List<zTreeVo> getTreeListByResourceInstanceListExceptCamera(List<ResourceInstanceBo> instanceList,
			boolean isFilterDisplay) {
		List<zTreeVo> treeOneList = new ArrayList<zTreeVo>();
		List<zTreeVo> treeTwoList = new ArrayList<zTreeVo>();

		Map secondTreeAndInstanceMap = new HashMap();

		Map firstTreeAndSecondTreeMap = new HashMap();

		if ((instanceList == null) || (instanceList.size() <= 0)) {
			return treeOneList;
		}
		for (int i = 0; i < instanceList.size(); ++i) {
			ResourceInstanceBo resourceInstance = (ResourceInstanceBo) instanceList.get(i);
            //如果不是摄像头资源
			if (!"Cameras".equals(resourceInstance.getCategoryId())) {
				CategoryDef secondCategory = this.capacityService.getCategoryById(resourceInstance.getCategoryId());

				CategoryDef firstCategory = secondCategory.getParentCategory();

				if ((!(DefIsdisplay(firstCategory))) && (isFilterDisplay)) {
					continue;
				}
				zTreeVo instanceTree = defTozTreeVo(resourceInstance, false, secondCategory.getId());

				zTreeVo secondTree = defTozTreeVo(secondCategory, true, firstCategory.getId());

				zTreeVo firstTree = defTozTreeVo(firstCategory, true, "0");

				if (!(secondTreeAndInstanceMap.containsKey(secondTree.getId()))) {
					secondTreeAndInstanceMap.put(secondTree.getId(), new ArrayList());

				}

				((List) secondTreeAndInstanceMap.get(secondTree.getId())).add(instanceTree);

				if (!(treeTwoList.contains(secondTree))) {
					treeTwoList.add(secondTree);

				}

				if (!(treeOneList.contains(firstTree))) {
					treeOneList.add(firstTree);
				}
			}

		}

		for (zTreeVo secondTree : treeTwoList) {
			secondTree.setChildren((List) secondTreeAndInstanceMap.get(secondTree.getId()));

			if (!(firstTreeAndSecondTreeMap.containsKey(secondTree.getPId()))) {
				firstTreeAndSecondTreeMap.put(secondTree.getPId(), new ArrayList());

			}

			((List) firstTreeAndSecondTreeMap.get(secondTree.getPId())).add(secondTree);

		}

		for (zTreeVo firstTree : treeOneList) {
			firstTree.setChildren((List) firstTreeAndSecondTreeMap.get(firstTree.getId()));

		}

		return treeOneList;
	}
	
	
	

	private boolean DefIsdisplay(CategoryDef def) {
		if (!(def.isDisplay())) {
			return false;
		}
		if (def.getParentCategory() != null) {
			return DefIsdisplay(def.getParentCategory());
		}
		return true;
	}

	private zTreeVo defTozTreeVo(Object def, boolean isParent, String pid) {
		zTreeVo tree = new zTreeVo();

		if (def instanceof ResourceInstanceBo) {
			ResourceInstanceBo instance = (ResourceInstanceBo) def;
			tree.setId(instance.getId() + "");
			tree.setIsParent(Boolean.valueOf(isParent));
			String showName = "";
			if (instance.getShowName() != null) {
				showName = instance.getShowName();
			}
			String ip = "";
			if (instance.getDiscoverIP() != null) {
				ip = instance.getDiscoverIP();
				/*     */ }
			tree.setName("(" + ip + ")" + showName);
			tree.setNocheck(Boolean.valueOf(false));
			tree.setOpen(Boolean.valueOf(false));
			tree.setPId(pid);
		} else if (def instanceof CategoryDef) {
			CategoryDef instance = (CategoryDef) def;
			tree.setId(instance.getId());
			tree.setIsParent(Boolean.valueOf(isParent));
			tree.setName(instance.getName());
			tree.setNocheck(Boolean.valueOf(false));
			tree.setOpen(Boolean.valueOf(false));
			tree.setPId(pid);
		}

		return tree;
	}

	@RequestMapping({ "/addTemplate" })
	public JSONObject addTemplate(String template, HttpSession session) {
		logger.info("-----------开始添加摄像头的性能报表模板----------");
		ILoginUser user = getLoginUser(session);

		ReportTemplate templateInstance = (ReportTemplate) JSONObject.parseObject(template, ReportTemplate.class);
		long newTemplateId = -1L;

		newTemplateId = cameraMonitorService.addReportTemplate(templateInstance, user);
		logger.info("-----------结束添加摄像头的性能报表模板----------");
		return toSuccess(Long.valueOf(newTemplateId));
	}

	@RequestMapping({ "/updateTemplate" })
	public JSONObject updateTemplate(String template, HttpSession session) {
		logger.info("-----------开始修改摄像头的性能报表模板----------");
		ILoginUser user = getLoginUser(session);

		ReportTemplate templateInstance = (ReportTemplate) JSONObject.parseObject(template, ReportTemplate.class);
		boolean isSuccess = true;
		try {
			isSuccess = cameraMonitorService.updateReportTemplate(templateInstance, user);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			isSuccess = false;
		}
		logger.info("-----------结束修改摄像头的性能报表模板----------");
		return toSuccess(Boolean.valueOf(isSuccess));
	}
    @Value("${stm.file.islocal}")
    private String islocal;
    @Value("${stm.file.server_ip}")
    private String serverIP;
    @Value("${stm.file.server_dir}")
    private String serverDir;
    @Value("${stm.file.root_path}")
    private String rootPath;
    private static final String DOWLOAD_PATH_CAM = "common" + File.separator
            + "classes" + File.separator + "config";
    private static final String CATALINA_HOME_CAM = "catalina.home";
    private static final String LOCAL_FILE_SERVER_CAM = System.getProperties().getProperty(CATALINA_HOME_CAM) + File.separatorChar + DOWLOAD_PATH_CAM + File.separatorChar + "STM_VIDEO_REPORT" + File.separatorChar;
    private static final String OS_CAM = System.getProperty("os.name");

    private String getRootDirPath() {
        String root = null;
        if ("on".equals(islocal.toLowerCase())) {
            root = LOCAL_FILE_SERVER_CAM;
        } else {
            if ((OS_CAM != null) && (OS_CAM.toLowerCase().indexOf("windows") != -1)) {
                root = "\\\\" + serverIP + File.separator + serverDir;
            }
            if ((OS_CAM != null) && (OS_CAM.toLowerCase().indexOf("linux") != -1)) {
                root = rootPath;
            }
        }
        if (!root.endsWith(File.separator)) {
            root = root + File.separator;
        }
        return root;
    }
}
