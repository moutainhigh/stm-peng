package com.mainsteam.stm.ct.web.action;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.ICtAlarmService;
import com.mainsteam.stm.ct.api.IResourceService;
import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.ct.bo.MsResourceMain;
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
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping({ "/portal/ct/ctAlarm" })
public class CtAlarmAction extends BaseAction{
	Logger log=Logger.getLogger(CtAlarmAction.class);

    @Resource
    private ICtAlarmService ctAlarmService;

    @Resource
    private IResourceService resourceService;
    /**
     * 获取信息页面
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Page<MsCtAlarm,MsCtAlarm>> userRoleList(MsCtAlarm alarm, @RequestParam(defaultValue="1") Integer pageNo,
                                                    @RequestParam(defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<Page<MsCtAlarm,MsCtAlarm>> result = new Result<Page<MsCtAlarm,MsCtAlarm>>();
        Page<MsCtAlarm,MsCtAlarm> page = new Page<MsCtAlarm,MsCtAlarm>((pageNo-1)*pageSize, pageSize);
        page.setCondition(alarm);
        log.error("confirmed:"+alarm.getConfirmed());
        ctAlarmService.getAlarmPage(page);
        result.setSuccess(true);
        result.setResult(page);
        return result;
    }

    /**
     * 添加
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/add",method=RequestMethod.POST)
    @ResponseBody
    public Result<MsCtAlarm> add( MsCtAlarm msCtAlarm) {
		Result<MsCtAlarm> result = new Result<MsCtAlarm>();
		try {
//			MsCtAlarm alarm = JSON.parseObject(jsonObject.toJSONString(), MsCtAlarm.class);
			msCtAlarm.setCreate_time(new Date());
			ctAlarmService.insertAlarm(msCtAlarm);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

    /**
     * 编辑
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/edit")
    @ResponseBody
    public Result<MsCtAlarm> edit( MsCtAlarm msCtAlarm) {
        Result<MsCtAlarm> result = new Result<MsCtAlarm>();
        try {
            MsCtAlarm msProbe = ctAlarmService.getById(msCtAlarm.getId().toString());
            
            if(msProbe==null) {
                result.error500("未找到对应告警");
            }else {
//                MsCtAlarm alarm = JSON.parseObject(jsonObject.toJSONString(), MsCtAlarm.class);
                ctAlarmService.editAlarm(msCtAlarm);
                result.success("修改成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 确认告警
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/recover")
    @ResponseBody
    public Result<MsProbe> recover(String id) {
//        int id = Integer.valueOf(jsonObject.getString("id"));
    	Result<MsProbe> result = new Result<MsProbe>();
    	log.error("id:"+id);
    	try{
    		MsCtAlarm alarm = ctAlarmService.getById(id);
    		log.error("alarmd:"+alarm);
            alarm.setConfirmed(1);
            int n = ctAlarmService.editAlarm(alarm);
            log.error("n:"+n);
            if(n > 0){
            	log.error("成功:"+n);
            	String resource_id = alarm.getResource_id();
            	Page<MsCtAlarm, MsCtAlarm> page=new Page<>();
            	MsCtAlarm a =new MsCtAlarm();
            	a.setResource_id(resource_id);
            	a.setConfirmed(0);
            	page.setCondition(a);
				ctAlarmService.getAlarmPage(page);
				List<MsCtAlarm> datas = page.getDatas();
				if(datas==null||datas.size()==0){
					MsResourceMain resourceMain = resourceService.getById(resource_id);
					resourceMain.setStatus(0);
					resourceService.editResource(resourceMain);
				}
               result.ok("确认告警成功");
            }else{
            	log.error("失败:"+n);
                result.error("确认告警失败");
            }
    	}catch(Exception e){
    		log.error(e.getMessage());
    		result.error500("修改数据库出错");
    	}
    	return result;
    }

    @RequestMapping(value = "/batchRecover")
    @ResponseBody
    public Result<?> batchRecover(String ids) {
//        String ids = jsonObject.getString("ids");
        String [] str = ids.split(",");
        int i = 0;
        int j = 0;
        for (String id : str){
            i++;
            MsCtAlarm alarm = ctAlarmService.getById(id);
            if(alarm != null){
                alarm.setConfirmed(1);
                int n = ctAlarmService.editAlarm(alarm);
                String resource_id = alarm.getResource_id();
            	Page<MsCtAlarm, MsCtAlarm> page=new Page<>();
            	MsCtAlarm a =new MsCtAlarm();
            	a.setResource_id(resource_id);
            	a.setConfirmed(0);
            	page.setCondition(a);
				ctAlarmService.getAlarmPage(page);
				List<MsCtAlarm> datas = page.getDatas();
				if(datas==null||datas.size()==0){
					MsResourceMain resourceMain = resourceService.getById(resource_id);
					resourceMain.setStatus(0);
					resourceService.editResource(resourceMain);
				}
                if(n > 0){
                    j++;
                }
            }
        }
        return Result.ok("共" + i + "条告警，确认成功" + j + "条");
    }
    /**
     * 导出excel
     * @param request
     */
//    @RequestMapping(value = "/exportXls")
//    public ModelAndView exportXls(MsCtAlarm msProbe, HttpServletRequest request) {
//        // Step.1 组装查询条件
//        QueryWrapper<MsCtAlarm> queryWrapper = QueryGenerator.initQueryWrapper(msProbe, request.getParameterMap());
//        //Step.2 AutoPoi 导出Excel
//        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//        List<MsCtAlarm> pageList = alarmService.list(queryWrapper);
//        //导出文件名称
//        mv.addObject(NormalExcelConstants.FILE_NAME,"告警列表");
//        mv.addObject(NormalExcelConstants.CLASS, MsCtAlarm.class);
//        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("告警列表数据","导出人:"+user.getRealname(),"导出信息"));
//        mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
//        return mv;
//    }
}
