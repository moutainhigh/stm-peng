package com.mainsteam.stm.ct.web.action;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.IAverageWebService;
import com.mainsteam.stm.ct.bo.MsAverageWeb;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.platform.web.action.BaseAction;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

@Controller
@RequestMapping({ "/portal/ct/averageWeb" })
public class AverageWebController extends BaseAction{
	Logger log=Logger.getLogger(AverageWebController.class);
    @Resource
    private IAverageWebService averageWebService;
    /**
     * 获取指标
     * @return
     */
    @RequestMapping(value = "/getList")
    @ResponseBody							//WebsiteMetricVo
    public Result<List<MsAverageWeb>> getList(String resourceId) {
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        ca.setTime(new Date());
        ca.add(Calendar.DATE, -1);
        Date date = ca.getTime();

        Result<List<MsAverageWeb>> result = new Result<>();
        List<MsAverageWeb> list = null;
        MsAverageWeb msAverageWeb=new MsAverageWeb();
        msAverageWeb.setCreate_time(sdf.format(date));
        msAverageWeb.setResource_id(resourceId);
        log.error("resourceId:"+resourceId);
        list = averageWebService.getList(msAverageWeb);
        log.error("list.size():"+list.size());
        if(list == null || list.size() < 1){
            result.error500("未找到信息");
        }else{
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }
}
