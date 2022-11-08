package com.mainsteam.stm.ct.web.action;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.ICtAlarmService;
import com.mainsteam.stm.ct.api.IPingMetricService;
import com.mainsteam.stm.ct.api.IProbeService;
import com.mainsteam.stm.ct.api.IResourceService;
import com.mainsteam.stm.ct.api.IWebsiteMetricService;
import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.ct.bo.MsPingMetric;
import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.ct.bo.MsResourceMain;
import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.ct.util.HttpUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping({ "/portal/ct/ctResource" })
public class ResourceController extends BaseAction{

	Logger log=Logger.getLogger(ResourceController.class);
    @Resource
    private IResourceService resourceService;

    @Resource
    private IProbeService probeService;
    @Resource
    private ICtAlarmService ctAlarmService;
    @Resource
    private IWebsiteMetricService websiteMetricService;
    @Resource
    private IPingMetricService pingMetricService;
    /**
     * 获取资源信息页面
     * @param pageNo
     * @param pageSize
     * @param msResourceParams
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Page<MsResourceMain,MsResourceMain>> userRoleList(@RequestParam(defaultValue="1") Integer pageNo,
                                                      @RequestParam(defaultValue="10") Integer pageSize, MsResourceMain msResourceParams) {
        Result<Page<MsResourceMain,MsResourceMain>> result = new Result<Page<MsResourceMain,MsResourceMain>>();
        Page<MsResourceMain,MsResourceMain> page = new Page<MsResourceMain,MsResourceMain>((pageNo-1)*pageSize, pageSize);
        page.setCondition(msResourceParams);
        resourceService.getResourceList(page);
        List<MsResourceMain> list = page.getDatas();
        List<MsResourceMain> temp =new ArrayList<MsResourceMain>();
        for(MsResourceMain main : list){
        	if(main.getTest_way().equals("web")){
        		MsWebsiteMetric websiteMetric=websiteMetricService.getLatest(main.getId());
        		main.setMsWebsiteMetric(websiteMetric);
        	}else {
				MsPingMetric pingMetric=pingMetricService.getLatest(main.getId());
				main.setMsPingMetric(pingMetric);
			}
        	temp.add(main);
        }
        page.setDatas(temp);
        result.setSuccess(true);
        result.setResult(page);
        return result;
    }

    @RequestMapping(value="/getList")
    @ResponseBody
    public Result<List<MsResourceMain>> getList(MsResourceMain msResourceMain) {
        Result<List<MsResourceMain>> result = new Result<List<MsResourceMain>>();
        try {
            String testWay = msResourceMain.getTest_way();
            List<MsResourceMain> list = resourceService.getResourceIdAndTestName(testWay);
            if(list == null || list.size() < 1){
                result.error500("未找到信息");
            }else{
                result.setSuccess(true);
                result.setResult(list);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        
        return result;
    }

    /**
     * 添加资源
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/add",method=RequestMethod.POST)
    @ResponseBody
    public Result<MsResourceMain> add( MsResourceMain resource) {
		Result<MsResourceMain> result = new Result<MsResourceMain>();
		try {
//			MsResourceMain resource = JSON.parseObject(jsonObject.toJSONString(), MsResourceMain.class);
			if("web".equals(resource.getTest_way())){
                resource.setResource_type("website");
            }else{
                resource.setResource_type(resource.getTest_way());
            }
            resource.setCreate_time(new Date());
            resource.setUpdate_time(new Date());
            int i = resourceService.addResource(resource);
            if(i>0){
                MsResourceMain fullResource = resourceService.getFullResource(resource);
                String s = JSONObject.toJSONString(fullResource);
                Integer probeId = fullResource.getProbe_id();
                MsProbe probe = probeService.getById(probeId);
                String url=probe.getProbe_ip()+":"+probe.getProbe_port();
                String method="/third/addjob";
                JSONObject httppost = HttpUtil.httppost(url, method, s);
                if(httppost.getInteger("code")==200){
                    result.success("添加成功！");
                    return result;
                }
                
                result.error500("任务添加失败");
                return result;
            }
            result.success("添加未成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

    /**
     * 编辑资源
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/edit")
    @ResponseBody
    //@RequiresPermissions("user:edit")
    public Result<MsResourceMain> edit( MsResourceMain resource) {
        Result<MsResourceMain> result = new Result<MsResourceMain>();
        try {
            MsResourceMain msResource = resourceService.getById(resource.getId());
            //sysBaseAPI.addLog("编辑资源，id： " +jsonObject.getString("id") , CommonConstant.LOG_TYPE_2, 2);
            if(msResource==null) {
                result.error500("未找到对应资源");
            }else {
//                MsResourceMain resource = JSON.parseObject(jsonObject.toJSONString(), MsResourceMain.class);
                resource.setUpdate_time(new Date());
                resourceService.editResource(resource);
                MsProbe probe = probeService.getById(resource.getProbe_id());
                String url=probe.getProbe_ip()+":"+probe.getProbe_port();
                String method="/third/editJob";
                String s = JSONObject.toJSONString(resource);
                JSONObject httppost = HttpUtil.httppost(url, method, s);
                if(httppost.getInteger("code")==200){
                    result.success("修改成功!");
                }else {
                    result.error500("任务修改失败");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 删除资源
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Result<?> delete(@RequestParam(required=true) String id) {
        MsResourceMain msResourceMain = resourceService.getById(id);
        MsProbe probe = probeService.getById(msResourceMain.getProbe_id());
        String url=probe.getProbe_ip()+":"+probe.getProbe_port();
        String method="/third/delJob";
        String s = JSONObject.toJSONString(msResourceMain);
        try {
            JSONObject httppost = HttpUtil.httppost(url, method, s);
            Integer code = httppost.getInteger("code");
            if(code==200){
                this.resourceService.deleteResource(id);
                return Result.ok("删除资源成功");
            }
            return Result.error("删除资源失败400");
        }
        catch (Exception e){
            e.printStackTrace();
            return Result.error("删除资源失败");
        }

    }

    /**
     * 批量删除资源
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch")
    @ResponseBody
    public Result<?> deleteBatch(@RequestParam(required=true) String ids) {
        resourceService.deleteBatchResource(ids);
        return Result.ok("批量删除资源成功");
    }

    /**
     * 导出excel
     * @param request
     */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(MsResourceMain msResource, HttpServletRequest request) {
//        // Step.1 组装查询条件
//        QueryWrapper<MsResourceMain> queryWrapper = QueryGenerator.initQueryWrapper(msResource, request.getParameterMap());
//        //Step.2 AutoPoi 导出Excel
//        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//        List<MsResourceMain> pageList = resourceService.list(queryWrapper);
//        //导出文件名称
//        mv.addObject(NormalExcelConstants.FILE_NAME,"资源列表");
//        mv.addObject(NormalExcelConstants.CLASS, MsResourceMain.class);
//        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("资源列表数据","导出人:"+user.getRealname(),"导出信息"));
//        mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
//        return mv;
//    }
   
    public static void main(String[] args) {
        String url="127.0.0.1:8080";
        String method="/third/addjob";
        //String method="/third/delAllJobs";
        String s="{\"testName\":\"888\",\"testWay\":\"ping\",\"resourceType\":\"ping\",\"testIp\":\"192.168.43.221\",\"repeatTime\":\"120\",\"profilelibId\":\"2\",\"probeId\":\"1\",\"id\":\"10\"}";
        JSONObject httppost = HttpUtil.httppost(url, method, s);
    }
}
