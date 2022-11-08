package com.mainsteam.stm.ct.web.action;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.IPingMetricService;
import com.mainsteam.stm.ct.bo.MsPingMetric;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.platform.web.action.BaseAction;

import java.util.List;

import javax.annotation.Resource;

@Controller
@RequestMapping({ "/portal/ct/pingMetric" })
public class PingMetricController extends BaseAction{
	Logger log=Logger.getLogger(PingMetricController.class);
    @Resource
    private IPingMetricService pingMetricService;
    /**
     * 获取指标
     * @return
     */
    @RequestMapping(value = "/getList")
    @ResponseBody
    public Result<List<MsPingMetric>> getList(WebsiteMetricVo websiteMetricVo) {
    	log.error("resourceId+"+websiteMetricVo.getResourceId());
        Result<List<MsPingMetric>> result = new Result<>();
        List<MsPingMetric> list = pingMetricService.getList(websiteMetricVo);
//        List<MsPingMetric> list = websiteMetricService.getList(resourceId);
        if(list == null || list.size() < 1){
            result.error500("未找到资源指标信息");
            log.error("return+error");
        }else{
            result.setSuccess(true);
            result.setResult(list);
            log.error("return+true");
        }
        
        return result;
    }
}
