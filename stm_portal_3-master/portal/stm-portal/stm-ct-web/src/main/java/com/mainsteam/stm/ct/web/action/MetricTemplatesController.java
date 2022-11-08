package com.mainsteam.stm.ct.web.action;


import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.IMetricTemplatesService;
import com.mainsteam.stm.ct.bo.MsMetricTemplates;
import com.mainsteam.stm.ct.bo.ProfileMetricVo;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

@Controller
@RequestMapping({ "/portal/ct/metricTemplates" })
public class MetricTemplatesController extends BaseAction{
	Logger log=Logger.getLogger(MetricTemplatesController.class);
    @Resource
    private IMetricTemplatesService metricTemplatesService;

    @RequestMapping(value="/get/{profilelibId}",method=RequestMethod.POST)
    @ResponseBody
    public Result get(@PathVariable(value = "profilelibId") String profilelibId){
    	log.error(profilelibId);
        MsMetricTemplates metricTemplates=new MsMetricTemplates();
        metricTemplates.setProfilelib_id(profilelibId);
        Page<MsMetricTemplates, MsMetricTemplates> page = new Page<MsMetricTemplates,MsMetricTemplates>(0, 30);
        page.setCondition(metricTemplates);
        metricTemplatesService.getMetricTemplates(page);
        log.error("return");
        return Result.ok(page);
    }

    @RequestMapping(value="/updateOrInsertTemplates")
    @ResponseBody
    public Result updateOrInsertTemplates(String obj){
    	List<MsMetricTemplates> vo = JSON.parseArray(obj, MsMetricTemplates.class);
    	//log.error("profilelibId+:"+vo.getProfilelibId());
        return Result.ok(metricTemplatesService.updateOrInsertMetricTemplates(vo));
    }

    @RequestMapping(value="/delete/{profilelibId}",method=RequestMethod.POST)
    @ResponseBody
    public Result delete(@PathVariable(value = "profilelibId") String profilelibId){
    	log.error("delete profilelibId+:"+profilelibId);
        return  Result.ok(metricTemplatesService.deleteTemplates(profilelibId));
    }
}
