package com.mainsteam.stm.ct.web.action;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ct.api.IProbeService;
import com.mainsteam.stm.ct.bo.MsProbe;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.ct.util.HttpUtil;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;

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
@RequestMapping({ "/portal/ct/probe" })
public class ProbeController extends BaseAction{
	Logger log=Logger.getLogger(ProbeController.class);

    @Resource
    private IProbeService probeService;

    /**
     * 获取探针信息页面
     * @param pageNo
     * @param pageSize
     * @param msProbeParams
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result<Page<MsProbe,MsProbe>> userRoleList(@RequestParam(defaultValue="1") Integer pageNo,
                                                    @RequestParam(defaultValue="10") Integer pageSize, MsProbe msProbeParams) {
        log.error("ip+"+msProbeParams.getProbe_ip());
    	Result<Page<MsProbe,MsProbe>> result = new Result<Page<MsProbe,MsProbe>>();
        Page<MsProbe,MsProbe> page = new Page<MsProbe,MsProbe>((pageNo-1)*pageSize, pageSize);
        page.setCondition(msProbeParams);
        try {
        	probeService.getProbeList(page);
            result.setSuccess(true);
            result.setResult(page);
            return result;
		} catch (Exception e) {
			// TODO: handle exception
			log.error("erroe+"+e.fillInStackTrace());
			log.error(e.getMessage());
			log.error(e.getStackTrace());
			return result.error500("操作失败");
		}
        
        
    }

    /**
     * 添加探针
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/add",method=RequestMethod.POST)
    @ResponseBody
    public Result<MsProbe> add(MsProbe msProbe) {
		Result<MsProbe> result = new Result<MsProbe>();
        int i;
        try {
            i = License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelCt);

            Page<MsProbe,MsProbe> page = new Page<MsProbe,MsProbe>(0, 999);
            MsProbe probe=new MsProbe();
            page.setCondition(probe);
            probeService.getProbeList(page);
            long totalRecord = page.getTotalRecord();
            log.error("num "+i+"//////"+totalRecord);
            if(i>totalRecord){

            }else {
                result.error500("授权数量上限");
                return result;
            }
        } catch (Exception e) {
            log.error("license error",e);
        }


        try {
//			MsProbe probe = JSON.parseObject(jsonObject.toJSONString(), MsProbe.class);
			msProbe.setCreate_time(new Date());
			msProbe.setUpdate_time(new Date());
            probeService.addProbe(msProbe);

            // TODO: 给探针发送一个消息，看是否连通
            msProbe.setProbe_status(1);
            msProbe.setLast_time(new Date());
            probeService.editProbe(msProbe);

			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

    /**
     * 编辑探针
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/edit")
    @ResponseBody
    public Result<MsProbe> edit(MsProbe msProbe) {
        Result<MsProbe> result = new Result<MsProbe>();
        try {
            MsProbe probe = probeService.getById(Integer.parseInt(msProbe.getId().toString()));
            if(probe==null) {
                result.error500("未找到对应探针");
            }else {
//                MsProbe probe = JSON.parseObject(jsonObject.toJSONString(), MsProbe.class);
            	msProbe.setUpdate_time(new Date());
                probeService.editProbe(msProbe);
                result.success("修改成功!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 删除探针
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public Result<?> delete(@RequestParam(required=true) String id) {
        MsProbe probe = probeService.getById(Integer.parseInt(id));
        String url=probe.getProbe_ip()+":"+probe.getProbe_port();
        String method="/third/delAllJobs";
        String s = "{}";
        JSONObject object = HttpUtil.httppost(url, method, s);
        Integer code = object.getInteger("code");
        if(code==200){
            this.probeService.deleteProbe(id);
            return Result.ok("删除探针成功");
        }else {
            return Result.error("指针任务删除失败");
        }

    }

    /**
     * 批量删除探针
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch")
    @ResponseBody
    public Result<?> deleteBatch(@RequestParam(required=true) String ids) {
    	log.error("删除探针id:"+ids);
        this.probeService.deleteBatchProbe(ids);
        return Result.ok("批量删除探针成功");
    }

    @RequestMapping(value = "/queryProbes", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MsProbe>> queryProbes() {
        Result<List<MsProbe>> result = new Result<>();
        Page<MsProbe, MsProbe> page=new Page<MsProbe, MsProbe>();
        MsProbe msProbe=new MsProbe();
        msProbe.setProbe_status(1);
        page.setCondition(msProbe);
        probeService.getProbeList(page);
        List<MsProbe> list = page.getDatas();
        if (list == null || list.size() <= 0) {
            result.error500("暂无可用探针");
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
//    public ModelAndView exportXls(MsProbe msProbe, HttpServletRequest request) {
//        // Step.1 组装查询条件
//        QueryWrapper<MsProbe> queryWrapper = QueryGenerator.initQueryWrapper(msProbe, request.getParameterMap());
//        //Step.2 AutoPoi 导出Excel
//        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//        List<MsProbe> pageList = probeService.list(queryWrapper);
//        //导出文件名称
//        mv.addObject(NormalExcelConstants.FILE_NAME,"探针列表");
//        mv.addObject(NormalExcelConstants.CLASS, MsProbe.class);
//        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        mv.addObject(NormalExcelConstants.PARAMS,new ExportParams("探针列表数据","导出人:"+user.getRealname(),"导出信息"));
//        mv.addObject(NormalExcelConstants.DATA_LIST,pageList);
//        return mv;
//    }
}
