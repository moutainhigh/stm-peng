package com.mainsteam.stm.ct.web.action;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.IMetricTemplatesService;
import com.mainsteam.stm.ct.api.IProfilelibService;
import com.mainsteam.stm.ct.api.IResourceService;
import com.mainsteam.stm.ct.bo.MsMetricTemplates;
import com.mainsteam.stm.ct.bo.MsProfilelibMain;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping({ "/portal/ct/profilelib" })
public class ProfilelibController extends BaseAction{

	Logger log=Logger.getLogger(ProfilelibController.class);
    @Resource
    private IProfilelibService profilelibService;

    @Resource
    private IResourceService resourceService;

    @Resource
    private IMetricTemplatesService metricTemplatesService;

    /**
     * 获取策略信息页面
     * @param pageNo
     * @param pageSize
     * @param msProfilelibParams
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Page<MsProfilelibMain,MsProfilelibMain>> userRoleList(@RequestParam(defaultValue="1") Integer pageNo,
                                                        @RequestParam(defaultValue="10") Integer pageSize, MsProfilelibMain msProfilelibParams) {
    	log.error(msProfilelibParams.getProfilelib_code());
    	Result<Page<MsProfilelibMain,MsProfilelibMain>> result = new Result<Page<MsProfilelibMain,MsProfilelibMain>>();
        Page<MsProfilelibMain,MsProfilelibMain> page = new Page<MsProfilelibMain,MsProfilelibMain>((pageNo-1)*pageSize, pageSize);
        page.setCondition(msProfilelibParams);
        profilelibService.getProfilelibList(page);
        result.setSuccess(true);
        result.setResult(page);
        return result;
    }

    /**
     * 添加策略
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/add",method=RequestMethod.POST)
    @ResponseBody
    public Result<MsProfilelibMain> add( MsProfilelibMain profilelib) {
		Result<MsProfilelibMain> result = new Result<MsProfilelibMain>();
		try {
//			MsProfilelibMain profilelib = JSON.parseObject(jsonObject.toJSONString(), MsProfilelibMain.class);
            profilelib.setCreate_time(new Date());
            profilelib.setUpdate_time(new Date());
            String uuid = UUID.randomUUID().toString();
            profilelib.setId(uuid);
            int i=profilelibService.addProfilelib(profilelib);
            if(i>0){
	            Page<MsProfilelibMain, MsProfilelibMain> page=new Page<>();
	            page.setCondition(profilelib);
	            profilelibService.getProfilelibList(page);
	            List<MsProfilelibMain> list = page.getDatas();
	            result.success("添加成功！");
				result.setResult(list.get(0));
            }
//            int status = profilelib.getStatus();
//            String type = "";
//            if(profilelib.getCompar_type() == 1){
//                type = ">=";
//            }else{
//                type = "<";
//            }
//            String msg = "${name}过高";
//            String testValue = profilelib.getTest_value();
//            Map jsonMap = JSONObject.parseObject(testValue);
//            Iterator<Map.Entry> entries = jsonMap.entrySet().iterator();
//            MsMetricTemplates msMetricTemplates = null;
//            while(entries.hasNext()){
//                Map.Entry<String, Integer> entry = entries.next();
//                msMetricTemplates = new MsMetricTemplates();
//                msMetricTemplates.setMetric_name(entry.getKey());
//                msMetricTemplates.setEnable(status);
//                msMetricTemplates.setAttention_value(entry.getValue());
//                msMetricTemplates.setAlarm_value(entry.getValue());
//                msMetricTemplates.setProfilelib_id(uuid);
//                msMetricTemplates.setCompare_type(type);
//                msMetricTemplates.setAlarm_message(msg);
//                metricTemplatesService.insertTemplates(msMetricTemplates);
//            }
//            if("ping".equals(profilelib.getTest_way())){
//                msMetricTemplates = new MsMetricTemplates();
//                msMetricTemplates.setMetric_name("state");
//                msMetricTemplates.setEnable(status);
//                msMetricTemplates.setAttention_value(1);
//                msMetricTemplates.setAlarm_value(1);
//                msMetricTemplates.setProfilelib_id(uuid);
//                msMetricTemplates.setCompare_type(type);
//                msMetricTemplates.setAlarm_message("可用性异常");
//                metricTemplatesService.insertTemplates(msMetricTemplates);
//            }

			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

    /**
     * 编辑策略
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/edit")
    @ResponseBody
    public Result<MsProfilelibMain> edit( MsProfilelibMain profilelib) {
        Result<MsProfilelibMain> result = new Result<MsProfilelibMain>();
        try {
            MsProfilelibMain msProfilelib = profilelibService.getById(profilelib.getId().toString());
            if(msProfilelib==null) {
                result.error500("未找到对应策略");
            }else {
//                MsProfilelibMain profilelib = JSON.parseObject(jsonObject.toJSONString(), MsProfilelibMain.class);
                profilelib.setUpdate_time(new Date());
                profilelibService.editProfilelib(profilelib);
                result.success("修改成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 删除策略
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Result<?> delete(@RequestParam(required=true) String id) {
        log.error("删除策略，id： " +id );
        this.profilelibService.deleteProfilelib(id);
        return Result.ok("删除策略成功");
    }

    /**
     * 批量删除策略
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch")
    @ResponseBody
    public Result<?> deleteBatch(@RequestParam(required=true) String ids) {
        log.error("批量删除策略， ids： " +ids );
        profilelibService.deleteBatchProfilelib(ids);
        return Result.ok("批量删除策略成功");
    }

    @RequestMapping(value = "/queryProfilelibs", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MsProfilelibMain>> queryProfilelibs() {
        Result<List<MsProfilelibMain>> result = new Result<>();
        Page<MsProfilelibMain, MsProfilelibMain> page = new Page<MsProfilelibMain,MsProfilelibMain>();
        MsProfilelibMain msProfilelibMain=new MsProfilelibMain();
        page.setCondition(msProfilelibMain);
        profilelibService.getProfilelibList(page);
        List<MsProfilelibMain> list = page.getDatas();
        if (list == null || list.size() <= 0) {
            result.error500("暂无策略可用");
        } else {
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }

    /**
     * 导出excel
     * @param request
     */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(MsProfilelibMain msProfilelibMain, HttpServletRequest request) {
//        // Step.1 组装查询条件
//        QueryWrapper<MsProfilelibMain> queryWrapper = QueryGenerator.initQueryWrapper(msProfilelibMain, request.getParameterMap());
//        //Step.2 AutoPoi 导出Excel
//        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//        List<MsProfilelibMain> pageList = profilelibService.list(queryWrapper);
//        //导出文件名称
//        mv.addObject(NormalExcelConstants.FILE_NAME,"策略列表");
//        mv.addObject(NormalExcelConstants.CLASS, MsProfilelibMain.class);
//        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("策略列表数据","导出人:"+user.getRealname(),"导出信息"));
//        mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
//        return mv;
//    }
}
