package com.mainsteam.stm.portal.inspect.web.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.DownUtil;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.inspect.api.IInspectReportApi;
import com.mainsteam.stm.portal.inspect.api.InspectReportFileApi;
import com.mainsteam.stm.portal.inspect.api.InspectXMLHandlerApi;
import com.mainsteam.stm.portal.inspect.bo.InspectFrontReportOrPlanBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportConditionBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportData;
import com.mainsteam.stm.portal.inspect.bo.InspectReportFileBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;
import com.mainsteam.stm.portal.inspect.web.utils.InspectionReportHelper;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Body;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Conclusion;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Head;
import com.mainsteam.stm.portal.inspect.web.utils.entity.InspectionReport;
import com.mainsteam.stm.portal.inspect.web.utils.entity.Item;
import com.mainsteam.stm.util.BooleanUtil;
import com.mainsteam.stm.util.StringUtil;
import com.itextpdf.text.DocumentException;

@Controller
@RequestMapping("inspect/report")
public class InspectReportAction {
	
	private static final Log logger = LogFactory.getLog(InspectReportAction.class);
	
	@Autowired
	private IInspectReportApi inspectReportApi;
	
	@Resource(name="stm_inspect_inspectXMLHandlerApi")
	private InspectXMLHandlerApi inspectXMLHandlerApi;
	
	@Resource(name="stm_inspect_inspectReportFileApi")
	private InspectReportFileApi inspectReportFileApi;

	@RequestMapping("list")
	public JSONObject list(
			long page,
			long rows,
			long startRow,
			long rowCount,
			@RequestParam(value = "condition.inspectReportProduceStartTime", required = false) String inspectReportProduceStartTime,
			@RequestParam(value = "condition.inspectReportProduceEndTime", required = false) String inspectReportProduceEndTime,
			@RequestParam(value = "condition.inspectReportInspector", required = false) String inspectReportInspector,
			@RequestParam(value = "condition.inspectReportTaskCreator", required = false) String inspectReportCreator,
			@RequestParam(value = "condition.inspectReportDomain", required = false) String[] inspectReportDomain,
			@RequestParam(value = "condition.inspectReportName", required = false) String inspectReportName,
			@RequestParam(value = "condition.inspectReportPlanName", required = false) String inspectReportPlanName,
			@RequestParam(value = "condition.inspectReportStatus", required = false) Integer[] status) {
		Page<InspectReportBasicBo, InspectReportConditionBo> p = new Page<InspectReportBasicBo, InspectReportConditionBo>();
		p.setRowCount(rowCount);
		p.setStartRow(startRow);
		InspectReportConditionBo query = new InspectReportConditionBo();
		p.setCondition(query);
		query.setInspectReportProduceStartTime(inspectReportProduceStartTime);
		query.setInspectReportProduceEndTime(inspectReportProduceEndTime);

		query.setStatus(status);
		query.setInspectReportInspector(inspectReportInspector);
		query.setReportName(inspectReportName);
		query.setInspectReportCreator(inspectReportCreator);
		query.setInspectReportDomain(inspectReportDomain);

		query.setInspectReportPlanName(inspectReportPlanName);
		ILoginUser user = BaseAction.getLoginUser();
		Long userid = user.getId();
		query.setInspectorId(userid);
		if (!user.isSystemUser()) {
			query.setAuthorityUserId(userid + "");
			if (user.isDomainUser()) {
				if (user.getDomainManageDomains() != null
						&& user.getDomainManageDomains().size() > 0) {
					String[] ds = new String[user.getDomainManageDomains()
							.size()];
					int index = 0;
					for (IDomain d : user.getDomainManageDomains()) {
						ds[index++] = d.getName();
					}
					query.setAuthorityDomainIds(ds);
				}
			}
		}
		
		this.inspectReportApi.getReportList(p);
		List<InspectReportBasicBo> result = p.getDatas();
		for (InspectReportBasicBo bo : result) {
			Long creatorid = bo.getCreatorId();
			Long inspectorid = bo.getInspectorId();
			if (userid.longValue() == creatorid.longValue()) {
				bo.setUserType("creator");
			} else if (userid.longValue() == inspectorid.longValue()) {
				bo.setUserType("inspector");
			} else {
				bo.setUserType("inspector");
			}
		}
		return BaseAction.toSuccess(p);
	}

	@RequestMapping("updateState")
	public JSONObject updateStatus(final String id) {
		int result = this.inspectReportApi.updateStatus(id);
		if(result > 0){
			//
			new Thread(new Runnable() {
				@Override
				public void run() {
					long start = System.currentTimeMillis();
					//查询数据-比较耗时，不放到保存文件的事务里 dfw
					InspectReportData inspectReportData = new InspectReportData();
					inspectReportData.setData(inspectReportApi.loadBasic(Long.valueOf(id)));
					inspectReportData.setReportSelfs(inspectReportApi.loadRoutine(Long.valueOf(id)));
					List<InspectReportContentBo> list = inspectReportApi.loadInspectionItems(Long.valueOf(id));
					if(list != null && list.size() > 0){
						for(InspectReportContentBo item : list){
                            List<InspectReportContentBo> inspectReportContentBos = inspectReportApi.loadItemTwo(item.getId());
                            item.setChildren(inspectReportContentBos);
                            if(inspectReportContentBos != null && inspectReportContentBos.size() > 0){
                                for(InspectReportContentBo citem : inspectReportContentBos){
                                    List<InspectReportContentBo> children = citem.getChildren();
                                    if(children != null && children.size() > 0){
                                        for(InspectReportContentBo c : children){
                                            List<InspectReportContentBo> ccList = inspectReportApi.loadItemTwo(c.getId());
                                            if(ccList != null && ccList.size() > 0){
                                                c.setChildren(ccList);
                                            }
                                        }
                                    }
                                }
                            }
						}
					}
					inspectReportData.setReportContents(list);
					inspectReportData.setReportResults(inspectReportApi.getConclusionsByBasicId(Long.valueOf(id)));
					inspectReportFileApi.saveFile(inspectReportData);
					logger.error("generate inspect report file(millis): " + (System.currentTimeMillis() - start));
				}
			}).start();
		}
		return BaseAction.toSuccess(result);
	}

	@RequestMapping("loadBasic")
	public JSONObject loadBasic(long id) {
		return BaseAction.toSuccess(this.inspectReportApi.loadBasic(id));
	}

	@RequestMapping("loadRoutine")
	public JSONObject loadRoutine(long id) {
		return BaseAction.toSuccess(this.inspectReportApi.loadRoutine(id));
	}

	@RequestMapping("loadConclusions")
	public JSONObject loadConclusions(long id) {
		return BaseAction.toSuccess(this.inspectReportApi
				.getConclusionsByBasicId(id));
	}

	@RequestMapping("loadInspectionItems")
	public JSONObject loadInspectionItems(long id) {
		return BaseAction.toSuccess(this.inspectReportApi
				.loadInspectionItems(id));
	}

	@RequestMapping("loadItem")
	public JSONObject loadItem(long catalogId) {
		List<InspectReportContentBo> loadItemTwo = this.inspectReportApi.loadItemTwo(catalogId);
		for(InspectReportContentBo irBo : loadItemTwo){
			List<InspectReportContentBo> children = irBo.getChildren();
			for(InspectReportContentBo cBo : children){
				List<InspectReportContentBo> childLoadItemTwo = this.inspectReportApi.loadItemTwo(cBo.getId());
				cBo.setChildren(childLoadItemTwo);
			}
		}
		return BaseAction.toSuccess(loadItemTwo);
	}

	/**
	 * 更新巡检报告基本信息
	 *
	 * @param bo
	 * @return
	 */
	@RequestMapping("updateBasic")
	public JSONObject updateBasic(InspectReportBasicBo bo) {
		return BaseAction.toSuccess(this.inspectReportApi.updateBasic(bo));
	}

	/**
	 * 更新巡检报告常规信息
	 *
	 * @param id
	 * @param inspectReportSelfItemName
	 * @param inspectReportSelfItemType
	 * @param inspectReportItemContent
	 * @return
	 */
	@RequestMapping("updateNormal")
	public JSONObject updateNormal(InspectFrontReportOrPlanBo requestBo) {
		return BaseAction.toSuccess(this.inspectReportApi.updateNormal(requestBo));
	}

	/**
	 * 更新巡检报告结论
	 *
	 * @param id
	 * @param inspectReportSumeriseDescrible
	 * @return
	 */
	@RequestMapping("updateResult")
	public JSONObject updateResult(InspectFrontReportOrPlanBo requestBo) {
		return BaseAction.toSuccess(this.inspectReportApi.updateResult(requestBo));
	}

	/**
	 * 更新巡检报告巡检项
	 *
	 * @param items
	 * @return
	 */
	@RequestMapping("updateItems")
	public JSONObject updateItems(String items) {
		return BaseAction.toSuccess(this.inspectReportApi.updateItems(items));
	}

	@RequestMapping("excel")
	public void excle(long id, HttpServletResponse response) throws IOException {
		InspectionReport report = get(id);
		DownUtil.down(response, "report.xlsx",
				new InspectionReportHelper().excel(report));
	}

	@RequestMapping("word")
	public void word(long id, HttpServletResponse response) throws IOException {
		InspectionReport report = get(id);
		DownUtil.down(response, "report.docx",
				new InspectionReportHelper().word(report));
	}

	@RequestMapping("pdf")
	public void pdf(long id, HttpServletResponse response) throws IOException,
			DocumentException {
		InspectionReport report = get(id);
		DownUtil.down(response, "report.pdf",
				new InspectionReportHelper().pdf(report));
	}

	private InspectionReport get(long id) {
		long min = System.currentTimeMillis();
		InspectReportFileBo inspectReportFileBo = inspectReportFileApi.getByReportId(id);
		InspectReportBasicBo basic;
		InspectReportData inspectReportData = null;
		if(inspectReportFileBo == null){
			basic = this.inspectReportApi.loadBasic(id);
		} else{
			inspectReportData = inspectXMLHandlerApi.createInspectReportData(inspectReportFileBo.getFileId());
			if(inspectReportData == null){
				logger.error("没有获取到巡检报告文件，fileID：" + inspectReportFileBo.getFileId());
				return null;
			}
			logger.error("get inspect report file(millis)：" + (System.currentTimeMillis() - min));
			basic = inspectReportData.getData();
		}
//		InspectReportBasicBo basic = this.inspectReportApi.loadBasic(id);
		InspectionReport report = new InspectionReport();
		report.setName(basic.getInspectReportName());
		report.setHeads(new ArrayList<Head>());
		if (!StringUtil.isNull(basic.getInspectReportInspector())) {
			Head h = new Head();
			report.getHeads().add(h);
			h.setName("巡检人:");
			h.setValue(basic.getInspectReportInspector());
		}
		if (!StringUtil.isNull(basic.getInspectReportDomain())) {
			Head h = new Head();
			report.getHeads().add(h);
			h.setName("域名称:");
			h.setValue(basic.getInspectReportDomain());
		}
		if (!StringUtil.isNull(basic.getInspectReportResourceName())) {
			Head h = new Head();
			report.getHeads().add(h);
			h.setName("资源类型名称:");
			h.setValue(basic.getInspectReportResourceName());
		}
		if (!StringUtil.isNull(basic.getInspectReportBusinessName())) {
			Head h = new Head();
			report.getHeads().add(h);
			h.setName("资源类型名称:");
			h.setValue(basic.getInspectReportBusinessName());
		}
		if (BooleanUtil.is(basic.getInspectReportModifiorShow())) {
			Head h = new Head();
			report.getHeads().add(h);
			h.setName("最后编辑人:");
			h.setValue(basic.getEditUserName());
		}
		if (BooleanUtil.is(basic.getInspectReportProduceTimeShow())) {
			Head h = new Head();
			report.getHeads().add(h);
			h.setName("报告生成时间:");
			h.setValue(basic.getInspectReportProduceTime());
		}
		if (BooleanUtil.is(basic.getInspectReportModifyTimeShow())) {
			Head h = new Head();
			report.getHeads().add(h);
			h.setName("报告最后编辑时间:");
			h.setValue(basic.getEditTime());
		}
		List<InspectReportSelfItemBo> selfs = inspectReportData == null ? this.inspectReportApi.loadRoutine(id) : inspectReportData.getReportSelfs();
//		List<InspectReportSelfItemBo> selfs = this.inspectReportApi
//				.loadRoutine(id);
		if (selfs != null) {
			for (InspectReportSelfItemBo s : selfs) {
				Head h = new Head();
				report.getHeads().add(h);
				h.setName(s.getInspectReportSelfItemName() + ":");
				h.setValue(s.getInspectReportItemContent());
			}
		}
		Head h = new Head();
		report.getHeads().add(h);
		h.setName("导出时间:");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		h.setValue(sdf.format(new Date()));
		logger.error("1:" + (System.currentTimeMillis() - min));
		List<InspectReportContentBo> contents = inspectReportData == null ? this.inspectReportApi.loadInspectionItems(id) : inspectReportData.getReportContents();
//		List<InspectReportContentBo> contents = this.inspectReportApi
//				.loadInspectionItems(id);
		logger.error("2:" + (System.currentTimeMillis() - min));
		if (contents != null) {
			report.setBodys(new ArrayList<Body>());
			for (InspectReportContentBo c : contents) {
				Body b = new Body();
				report.getBodys().add(b);
				b.setName(c.getInspectReportItemName() + ":"
						+ c.getInspectReportItemDescrible());
				
				List<InspectReportContentBo> items = inspectReportData == null ? this.inspectReportApi.loadItemTwo(id) : c.getChildren();
				b.setItems(getItem(items));
			}
		}
		logger.error("3:" + (System.currentTimeMillis() - min));
		List<InspectReportResultsSettingBo> conclusions = inspectReportData == null ? this.inspectReportApi.getConclusionsByBasicId(id) : inspectReportData.getReportResults();
//		List<InspectReportResultsSettingBo> conclusions = this.inspectReportApi
//				.getConclusionsByBasicId(id);
		if (conclusions != null && conclusions.size() > 0) {
			report.setConclusions(new ArrayList<Conclusion>());
			for (InspectReportResultsSettingBo co : conclusions) {
				Conclusion c = new Conclusion();
				c.setName(co.getInspectReportSummeriseName());
				c.setValue(co.getInspectReportSumeriseDescrible());
				report.getConclusions().add(c);
			}
		}
		logger.error("4:" + (System.currentTimeMillis() - min));
		return report;
	}

	private List<Item> getItem(List<InspectReportContentBo> childerns) {
//		List<InspectReportContentBo> childerns = this.inspectReportApi
//				.loadItemTwo(id);
		if (childerns != null && childerns.size() > 0) {
			List<Item> items = new ArrayList<Item>();
			for (InspectReportContentBo ch : childerns) {
				Item item = new Item();
				items.add(item);
				item.setName(ch.getInspectReportItemName());
				item.setDescription(ch.getInspectReportItemDescrible());
				String unit = (ch.getInspectReportItemUnit() != null ? ch
						.getInspectReportItemUnit() : "");
				if (!StringUtil.isNull(ch.getReportItemReferencePrefix())
						|| !StringUtil
								.isNull(ch.getReportItemReferenceSubfix())) {
					item.setReferenceValue((ch.getReportItemReferencePrefix() == null ? ""
							: ch.getReportItemReferencePrefix() + unit)
							+ "-"
							+ (ch.getReportItemReferenceSubfix() == null ? ""
									: ch.getReportItemReferenceSubfix() + unit));
				}
				if (ch.getInspectReportItemValue() != null) {
					item.setValue(ch.getInspectReportItemValue() + unit);
				}
				item.setSummary(ch.getReportItemConditionDescrible());
				item.setStatus(ch.getInspectReportItemResult() ? "正常" : "异常");
				if (ch.getType() == 2) {
					if (ch.isIndicatorAsItem() != true) {
						item.setCheckingType("系统自检");
					}
				} else {
					item.setCheckingType("人工手检");
				}
				if (ch.getChildren() != null && ch.getChildren().size() > 0) {
					item.setItems(new ArrayList<Item>());
					for (InspectReportContentBo ch2 : ch.getChildren()) {
						Item item2 = new Item();
						item.getItems().add(item2);
						item2.setName(ch2.getInspectReportItemName());
						item2.setDescription(ch2
								.getInspectReportItemDescrible());
						unit = (ch2.getInspectReportItemUnit() != null ? ch2
								.getInspectReportItemUnit() : "");
						if (!StringUtil.isNull(ch2.getReportItemReferencePrefix())
								|| !StringUtil.isNull(ch2.getReportItemReferenceSubfix())) {
							item2.setReferenceValue((StringUtil.isNull(ch2.getReportItemReferencePrefix()) ? ""
									: ch2.getReportItemReferencePrefix() + unit)
									+ "~"
									+ (StringUtil.isNull(ch2.getReportItemReferenceSubfix()) ? ""
											: ch2.getReportItemReferenceSubfix() + unit));
						}
						if (ch2.getInspectReportItemValue() != null) {
							item2.setValue(ch2.getInspectReportItemValue()
									+ unit);
						}
						item2.setSummary(ch2.getReportItemConditionDescrible());
						item2.setStatus(ch2.getInspectReportItemResult() ? "正常" : "异常");
						if (ch2.getType() == 2) {
							if (ch2.isIndicatorAsItem() != true) {
								item2.setCheckingType("系统自检");
							}
						} else {
							item2.setCheckingType("人工手检");
						}
                        if (ch2.getChildren() != null && ch2.getChildren().size() > 0) {
                            item2.setItems(new ArrayList<Item>());
                            for (InspectReportContentBo ch3 : ch2.getChildren()) {
                                Item item3 = new Item();
                                item2.getItems().add(item3);
                                item3.setName(ch3.getInspectReportItemName());
                                item3.setDescription(ch3
                                        .getInspectReportItemDescrible());
                                unit = (ch3.getInspectReportItemUnit() != null ? ch3
                                        .getInspectReportItemUnit() : "");
                                if (!StringUtil.isNull(ch3.getReportItemReferencePrefix())
                                        || !StringUtil.isNull(ch3.getReportItemReferenceSubfix())) {
                                    item3.setReferenceValue((StringUtil.isNull(ch3.getReportItemReferencePrefix()) ? ""
                                            : ch3.getReportItemReferencePrefix() + unit)
                                            + "~"
                                            + (StringUtil.isNull(ch3.getReportItemReferenceSubfix()) ? ""
                                            : ch3.getReportItemReferenceSubfix() + unit));
                                }
                                if (ch3.getInspectReportItemValue() != null) {
                                    item3.setValue(ch3.getInspectReportItemValue()
                                            + unit);
                                }
                                item3.setSummary(ch3.getReportItemConditionDescrible());
                                item3.setStatus(ch3.getInspectReportItemResult() ? "正常" : "异常");
                                if (ch3.getType() == 2) {
                                    if (ch3.isIndicatorAsItem() != true) {
                                        item3.setCheckingType("系统自检");
                                    }
                                } else {
                                    item3.setCheckingType("人工手检");
                                }
                            }
                        }
					}
				}
			}
			return items;
		}
		return null;
	}

	@RequestMapping("updateEditDate")
	public JSONObject updateEditDate(long id) {
		return BaseAction.toSuccess(this.inspectReportApi.updateEditDate(id));
	}
}
