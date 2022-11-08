package com.mainsteam.stm.simple.manager.workbench.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.simple.manager.workbench.dao.IWorkbenchDao;
import com.mainsteam.stm.simple.manager.workbench.report.api.IWorkbenchApi;
import com.mainsteam.stm.simple.manager.workbench.report.bo.ExpectBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.MsgUserBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.WorkbenchReportBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.ReportTypeBo;
import com.mainsteam.stm.util.DateUtil;

/**
 * <li>文件名称: WorkbenchImpl</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月18日 下午2:42:42
 * @author   俊峰
 */
@Service("managerWorkbenchApi")
public class WorkbenchImpl implements IWorkbenchApi {

	@Resource(name="managerWorkbenchDao")
	private IWorkbenchDao workbenchDao;

	private ISequence seq;
	@Autowired
	public WorkbenchImpl(SequenceFactory sequenceFactory){
		this.seq = sequenceFactory.getSeq(SEQ.SEQNAME_STM_SIMPLE_MANAGER_EXPECT);
	}
	
	@Override
	public List<ReportTypeBo> queryReportTemplate() {
		List<ReportTypeBo> types = new ArrayList<>();
		ReportTypeBo type = new ReportTypeBo();
		type.setId(1l);
		type.setName("业务运行报表（月）");
		types.add(type);
		return types;
	}

	@Override
	public List<WorkbenchReportBo> queryReportsByTemplate(Long templateId) {
		List<WorkbenchReportBo> reports = new ArrayList<WorkbenchReportBo>();
		return reports;
	}

	@Override
	public List<ExpectBo> getExpects(Long reportId) {
		List<ExpectBo> expects = workbenchDao.select(reportId);
		if(expects!=null && expects.size()>0){
			for (ExpectBo expect : expects) {
				if(expect!=null && expect.getCreateTime()!=null){
					expect.setCreateTimeStr(DateUtil.format(expect.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
				}
			}
		}
		return expects;
	}

	@Override
	public ExpectBo getDefaultExpect() {
		
		return workbenchDao.get(1l);
	}

	@Override
	public ExpectBo getExpectById(Long id){
		ExpectBo expectBo =  workbenchDao.get(id);
		if(expectBo!=null && expectBo.getCreateTime()!=null){
			expectBo.setCreateTimeStr(DateUtil.format(expectBo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		}
		return expectBo;
	}
	
	@Override
	public ExpectBo saveExpect(ExpectBo expect) {
		expect.setId(seq.next());
		expect.setCreateTime(Calendar.getInstance().getTime());
		expect.setAvailable(expect.getAvailable()==null?-1:expect.getAvailable());
		expect.setAlarmTimes(expect.getAlarmTimes()==null?-1:expect.getAlarmTimes());
		expect.setDownDuration(expect.getDownDuration()==null?-1:expect.getDownDuration());
		expect.setDownTimes(expect.getDownTimes()==null?-1:expect.getDownTimes());
		expect.setMtbf(expect.getMtbf()==null?-1:expect.getMtbf());
		expect.setMttr(expect.getMttr()==null?-1:expect.getMttr());
		expect.setUnrecoveryAlarmTimes(expect.getUnrecoveryAlarmTimes()==null?-1:expect.getUnrecoveryAlarmTimes());
		workbenchDao.insert(expect);
		return expect;
	}

	@Override
	public List<MsgUserBo> getMsgUsers(Long reportId) {
		
		return null;
	}

	@Override
	public boolean deleteExpect(Long expectId) {
		if(expectId!=null){
			return workbenchDao.delete(expectId)>0?true:false;
		}
		return false;
	}

	@Override
	public boolean updateExpect(Long id, int state) {
		if(id!=null){
			ExpectBo expect = this.getExpectById(id);
			if(expect!=null){
				expect.setState(state);
				expect.setNoticeDate(Calendar.getInstance().getTime());
				return workbenchDao.update(expect)>0?true:false;
			}
		}
		return false;
	}

}
