package com.mainsteam.stm.ct.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.mainsteam.stm.ct.api.IMetricTemplatesService;
import com.mainsteam.stm.ct.bo.MsMetricTemplates;
import com.mainsteam.stm.ct.bo.ProfileMetricVo;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.ct.dao.MetricTemplatesMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


public class MetricTemplatesServiceImpl implements IMetricTemplatesService {

    @Autowired
    private MetricTemplatesMapper metricTemplatesMapper;
    

    @Override
    public Result updateOrInsertMetricTemplates(List<MsMetricTemplates> list) {
        Result<MsMetricTemplates> result=new Result<>();
        if(list==null||list.size()==0){
            result.error500("指标数为0");
            return result;
        }
        Page<MsMetricTemplates,MsMetricTemplates> page=new Page<MsMetricTemplates,MsMetricTemplates>(1,20);
        MsMetricTemplates msMetricTemplates=new MsMetricTemplates();
        msMetricTemplates.setProfilelib_id(list.get(0).getProfilelib_id());
        page.setCondition(msMetricTemplates);
        metricTemplatesMapper.getMetricTemplates(page);
        page.getDatas();
        if(page.getTotalRecord()==0){
            try {
                for (MsMetricTemplates metricTemplate : list) {
                    
                    metricTemplatesMapper.insert(metricTemplate);
                }
                result.success("添加成功");
                return result;
            }
            catch (Exception e){
                result.error500(e.getMessage());
                return result;
            }
        }else {
            try {
                for (MsMetricTemplates metricTemplate : list) {
                    metricTemplatesMapper.updateById(metricTemplate);
                }
                result.success("修改成功");
                return result;
            }
            catch (Exception e){
                result.error500(e.getMessage());
                return result;
            }
        }

    }

    @Override
    public int deleteTemplates(String profilelibId) {

        return metricTemplatesMapper.deleteTemplatesByProfilelibId(profilelibId);
    }

    @Override
    public int insertTemplates(MsMetricTemplates msMetricTemplates) {
        return metricTemplatesMapper.insert(msMetricTemplates);
    }

	@Override
	public void getMetricTemplates(Page<MsMetricTemplates, MsMetricTemplates> page) {
		// TODO Auto-generated method stub
		metricTemplatesMapper.getMetricTemplates(page);
	}


	
}
