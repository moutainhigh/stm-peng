package com.mainsteam.stm.ct.web.action;


import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mainsteam.stm.ct.api.ICtMetricsService;
import com.mainsteam.stm.ct.bo.MsCtMetrics;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

@Controller
@RequestMapping({ "/portal/ct/ctMetric" })
public class CtMetricController extends BaseAction{
	Logger log=Logger.getLogger(CtMetricController.class);
    @Resource
    private ICtMetricsService ctMetricsService;

    @RequestMapping("/getCtMetricsByType")
    @ResponseBody
    public Result getAllCtMetrics(Integer type){
    	log.error(type);
        MsCtMetrics ctMetrics=new MsCtMetrics();
        ctMetrics.setType(type);
        Page<MsCtMetrics, MsCtMetrics> page = new Page<MsCtMetrics,MsCtMetrics>(0,30);
        page.setCondition(ctMetrics);
        ctMetricsService.getAllCtMetrics(page);
        log.error("return");
        return Result.ok(page);
    }

    @RequestMapping(value="/getOne/{id}",method=RequestMethod.POST)
    @ResponseBody
    public Result getOne(@PathVariable(value = "id")Integer id){
    	log.error(id);
        return Result.ok(ctMetricsService.selectOne(id));
    }
}
