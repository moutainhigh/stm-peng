package com.mainsteam.stm.ct.web.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.Vo.ResultVo;
import com.mainsteam.stm.ct.api.ICtAlarmService;
import com.mainsteam.stm.ct.api.ICtMetricsService;
import com.mainsteam.stm.ct.api.IMetricTemplatesService;
import com.mainsteam.stm.ct.api.IPingMetricService;
import com.mainsteam.stm.ct.api.IProfilelibService;
import com.mainsteam.stm.ct.api.IResourceService;
import com.mainsteam.stm.ct.api.IResourceinfoService;
import com.mainsteam.stm.ct.api.IWebsiteMetricService;
import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.ct.bo.MsCtMetrics;
import com.mainsteam.stm.ct.bo.MsCtResourceinfo;
import com.mainsteam.stm.ct.bo.MsMetricTemplates;
import com.mainsteam.stm.ct.bo.MsPingMetric;
import com.mainsteam.stm.ct.bo.MsResourceMain;
import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({ "/portal/ct/ctThird" })
public class CtThirdController extends BaseAction{
	Logger log=Logger.getLogger(CtThirdController.class);
    @Resource
    private IResourceService resourceService;

    @Resource
    private IProfilelibService profilelibService;

    @Resource
    private ICtMetricsService ctMetricsService;

    @Resource
    private ICtAlarmService ctAlarmService;

    @Resource
    private IMetricTemplatesService metricTemplatesService;

    @Resource
    private IWebsiteMetricService websiteMetricService;

    @Resource
    private IPingMetricService pingMetricService;
    
    @Resource
	private IResourceinfoService resourceinfoService;

    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @RequestMapping("/sendResult")
    @ResponseBody
    @Transactional
    public JSONObject sendResult(String json){
//    	String json = vo.getValue();
    	json=json.replaceAll("kongge"," ");
    	json=json.replaceAll("zuokuohao","\\{");
    	json=json.replaceAll("youkuohao","\\}");
    	json=json.replaceAll("yinhao","\"");
        JSONObject jsonObject=new JSONObject();
        log.error("json:"+json);
        String resourceId =null;
        try {
        	JSONObject object = JSON.parseObject(json);
            resourceId = object.getString("resource_id");
            if(resourceId==null){
            	jsonObject.put("code","404");
                return jsonObject;
            }
            MsResourceMain msResource = resourceService.getById(resourceId);
            //指标入库
            if("website".equals(msResource.getResource_type())){
                MsWebsiteMetric wm = JSON.parseObject(json.toString(), MsWebsiteMetric.class);
                websiteMetricService.insertWebsiteMetric(wm);
            }else if("ping".equals(msResource.getResource_type())){
                MsPingMetric pm = JSON.parseObject(json.toString(), MsPingMetric.class);
                pingMetricService.insertPingMetric(pm);
            }else{
                jsonObject.put("code","500");
                return jsonObject;
            }
            log.error("入库完成");
            resourceService.success(resourceId);
            String profilelibId = msResource.getProfilelib_id();
            MsMetricTemplates templates=new MsMetricTemplates();
            templates.setProfilelib_id(profilelibId);
            Page<MsMetricTemplates, MsMetricTemplates> page = new Page<MsMetricTemplates,MsMetricTemplates>(1, 30);
            page.setCondition(templates);
            metricTemplatesService.getMetricTemplates(page);
           
            List<MsMetricTemplates> records = page.getDatas();
            log.error("records:"+records.size());
            MsCtAlarm alarm=new MsCtAlarm();
            StringBuffer sb = new StringBuffer();
            boolean flag = false;
            log.error("开始检查指标");
            for (MsMetricTemplates record : records) {
                if(object.getString(record.getMetric_name())!=null){
                    switch (record.getCompare_type()){
                        case ">=":
                            if(object.getInteger(record.getMetric_name())>=record.getAlarm_value()){
                                MsCtMetrics ctMetric = ctMetricsService.selectByName(record.getMetric_name());
                                sb.append("【");
                                sb.append(record.getAlarm_message().replace("${name}",ctMetric.getC_name()));
                                sb.append("】,");
                            }
                            break;
                        case "<=":
                            if(object.getInteger(record.getMetric_name())<=record.getAlarm_value()){
                                //创建告警并入库
                                MsCtMetrics ctMetric = ctMetricsService.selectByName(record.getMetric_name());
                                sb.append("【");
                                sb.append(record.getAlarm_message().replace("${name}",ctMetric.getC_name()));
                                sb.append("】,");
                            }
                            break;
                        case "!=":
                            if(object.getInteger(record.getMetric_name())!=record.getAlarm_value()){
                                //创建告警并入库
                                MsCtMetrics ctMetric = ctMetricsService.selectByName(record.getMetric_name());
                                sb.append("【");
                                sb.append(record.getAlarm_message().replace("${name}",ctMetric.getC_name()));
                                sb.append("】,");
                                flag = true;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            log.error("创建告警并入库");
            //创建告警并入库
            if(sb.toString().length() > 0){
                if(flag){
                    alarm.setAlarm_level(3);
                }else{
                    alarm.setAlarm_level(1);
                }
                alarm.setAlarm_time(sdf.format(new Date()));
                alarm.setConfirmed(0);
                alarm.setMessage(sb.toString().substring(0,sb.toString().length()-1));
                alarm.setResult_id(object.getString("id"));
                alarm.setResource_id(resourceId);
                ctAlarmService.insertAlarm(alarm);
                msResource.setStatus(1);
                resourceService.editResource(msResource);
            }

            jsonObject.put("code","200");
            return jsonObject;
        }catch (Exception e){
            log.error(e.getMessage());
            jsonObject.put("code","400");
            if(resourceId!=null){
            	int i=resourceService.fail(resourceId);
            }
            return jsonObject;
        }

    }
    
    @RequestMapping("/sendInfos")
    @ResponseBody
    public JSONObject sendInfos(String json){
    	json=json.replaceAll("kongge"," ");
    	json=json.replaceAll("zuokuohao","\\{");
    	json=json.replaceAll("youkuohao","\\}");
    	json=json.replaceAll("yinhao","\"");
    	JSONObject jsonObject=new JSONObject();
    	log.debug("json:"+json);
    	try {
    		List<MsCtResourceinfo> list = JSON.parseArray(json, MsCtResourceinfo.class);
    		for(MsCtResourceinfo resourceinfo : list){
    			log.debug("---info---:"+resourceinfo.toString());
    		}
    		for(MsCtResourceinfo resourceinfo : list){
        		resourceinfoService.addInfo(resourceinfo);
        	}
    		jsonObject.put("code","200");
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
			jsonObject.put("code","500");
		}
    	return jsonObject;
    }
    @RequestMapping("/sendAlarm")
    @ResponseBody
    @Transactional
    public JSONObject sendAlarm(String resourceId){
    	JSONObject jsonObject=new JSONObject();
    	MsResourceMain main = resourceService.getById(resourceId);
    	main.setStatus(2);
    	resourceService.editResource(main);
    	MsCtAlarm alarm=new MsCtAlarm();
    	alarm.setAlarm_level(2);
    	alarm.setAlarm_time(sdf.format(new Date()));
    	alarm.setCreate_time(new Date());
    	alarm.setConfirmed(0);
    	alarm.setMessage("资源【"+main.getTest_name()+"】不可用");
    	alarm.setResource_id(resourceId);
    	alarm.setResult_id("resourceAlarm");
    	int insertAlarm = ctAlarmService.insertAlarm(alarm);
    	if(insertAlarm>0){
    		jsonObject.put("code", 200);
    	}else{
    		jsonObject.put("code", 500);
    	}
    	return jsonObject;
    }
}
