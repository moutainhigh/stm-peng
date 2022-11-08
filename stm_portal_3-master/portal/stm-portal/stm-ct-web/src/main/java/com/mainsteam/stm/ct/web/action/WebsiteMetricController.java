package com.mainsteam.stm.ct.web.action;

import com.alibaba.fastjson.JSONObject;

import com.mainsteam.stm.ct.api.IWebsiteMetricService;
import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.ct.bo.WebsiteMetricVo;
import com.mainsteam.stm.platform.web.action.BaseAction;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.annotation.Resource;

@Controller
@RequestMapping({ "/portal/ct/websiteMetric" })
public class WebsiteMetricController extends BaseAction{
	Logger log=Logger.getLogger(WebsiteMetricController.class);
    @Resource
    private IWebsiteMetricService websiteMetricService;
    /**
     * 获取指标7
     * @return
     */
    @RequestMapping(value = "/getList")
    @ResponseBody
    public Result<List<MsWebsiteMetric>> getList(WebsiteMetricVo websiteMetricVo) {
//        String resourceId = jsonObject.getString("id");
        Result<List<MsWebsiteMetric>> result = new Result<>();
//        List<MsWebsiteMetric> list = websiteMetricService.list(new QueryWrapper<MsWebsiteMetric>().lambda().eq(MsWebsiteMetric::getResourceId, resourceId).orderByDesc(MsWebsiteMetric::getCreateTime).last("limit20"));
       try {
    	   List<MsWebsiteMetric> list = websiteMetricService.getList(websiteMetricVo);
           if(list == null || list.size() < 1){
               result.error500("未找到资源指标信息");
           }else{
               result.setSuccess(true);
               result.setResult(list);
           }
       } catch (Exception e) {
		// TODO: handle exception
    	   log.error(e.getMessage()+"\t"+e.getStackTrace());
    	   result.error500("查询出错");
       }
        
        return result;
    }
    @RequestMapping(value = "/getAvg")
    @ResponseBody
    public Result<MsWebsiteMetric> getAvg(WebsiteMetricVo websiteMetricVo) {
    	 Result<MsWebsiteMetric> result = new Result<>();
    	 try {
      	   MsWebsiteMetric msMetric = websiteMetricService.getAvg(websiteMetricVo);
             if(msMetric == null){
                 result.error500("未找到资源指标信息");
             }else{
                 result.setSuccess(true);
                 result.setResult(msMetric);
             }
         } catch (Exception e) {
  		// TODO: handle exception
      	   log.error(e.getMessage()+"\t"+e.getStackTrace());
      	   result.error500("查询出错");
         }
          
          return result;
    }
    @RequestMapping(value = "/getAvgByResourceId")
    @ResponseBody
    public Result<MsWebsiteMetric> getAvgByResourceId(WebsiteMetricVo websiteMetricVo) {
    	 Result<MsWebsiteMetric> result = new Result<>();
    	 try {
      	   MsWebsiteMetric msMetric = websiteMetricService.getAvgByResourceId(websiteMetricVo);
             if(msMetric == null){
                 result.error500("未找到资源指标信息");
             }else{
                 result.setSuccess(true);
                 result.setResult(msMetric);
             }
         } catch (Exception e) {
  		// TODO: handle exception
      	   log.error(e.getMessage()+"\t"+e.getStackTrace());
      	   result.error500("查询出错");
         }
          
          return result;
    }
}
