package com.mainsteam.stm.portal.inspect.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.inspect.api.IInspectReportApi;
import com.mainsteam.stm.portal.inspect.bo.InspectFrontReportOrPlanBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportConditionBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;
import com.mainsteam.stm.portal.inspect.dao.IInspectReportDaoApi;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;

public class InspectReportImpl implements IInspectReportApi {

	private IInspectReportDaoApi inspectReportDao;

	private ISequence sequence;

	private ISearchApi searchApi;

	public void setSearchApi(ISearchApi searchApi) {
		this.searchApi = searchApi;
	}

	public void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	public IInspectReportDaoApi getInspectReportDao() {
		return inspectReportDao;
	}

	public void setInspectReportDao(IInspectReportDaoApi inspectReportDao) {
		this.inspectReportDao = inspectReportDao;
	}

	public List<InspectReportBasicBo> getReportList(
			Page<InspectReportBasicBo, InspectReportConditionBo> page) {
		return inspectReportDao.getReportList(page);

	}

	@Override
	public int updateStatus(String id) {

		return this.inspectReportDao.updateStatus(id);
	}

	@Override
	public InspectReportBasicBo loadBasic(long id) {
		return this.inspectReportDao.loadBasic(id);

	}

	@Override
	public List<InspectReportSelfItemBo> loadRoutine(long id) {
		return this.inspectReportDao.loadRoutine(id);
	}

	@Override
	public List<InspectReportResultsSettingBo> getConclusionsByBasicId(long id) {

		return this.inspectReportDao.getConclusionsByBasicId(id);
	}

	@Override
	public List<InspectReportContentBo> loadInspectionItems(long id) {

		return this.inspectReportDao.loadInspectionItems(id);
	}

	@Override
	public List<InspectReportContentBo> loadItem(long catalogId) {

		return this.inspectReportDao.loadItem(catalogId);
	}

	@Override
	public int saveBasic(InspectReportBasicBo data) {
		data.setId(sequence.next());
		return this.inspectReportDao.saveBasic(data);
	}

	@Override
	public int saveSelfItem(List<InspectReportSelfItemBo> reportSelfs) {
		if (reportSelfs != null) {
			for (InspectReportSelfItemBo b : reportSelfs) {
				b.setId(sequence.next());
			}
		}
		return this.inspectReportDao.saveSelfItem(reportSelfs);
	}

	@Override
	public int saveResults(List<InspectReportResultsSettingBo> results) {
		if (results != null) {
			for (InspectReportResultsSettingBo b : results) {
				b.setId(sequence.next());
			}
		}
		return this.inspectReportDao.saveResults(results);
	}

	@Override
	public int saveContents(List<InspectReportContentBo> contents) {
		List<InspectReportContentBo> data = new ArrayList<>();
		List<Long> resourceIds = new ArrayList<>();
		this.f(null, contents, data, resourceIds);
		int u = 0;
		try {
			if (data != null && data.size() > 0) {
				u = this.inspectReportDao.saveContents(data);
			//	u = this.inspectReportDao.addContents(data);
			if (u > 0) {
					ResourceBizRel r = new ResourceBizRel();
					r.setBizId(data.get(0).getInspectReportid());
					r.setNav("自动生成.");
					r.setResourceIds(resourceIds);
					this.searchApi.saveSearchInspect(r);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return u;
	}

	private void f(Long parentId, List<InspectReportContentBo> children,
			List<InspectReportContentBo> data, List<Long> resourceIds) {
		if (children != null && children.size() > 0) {
			for (InspectReportContentBo b : children) {
				data.add(b);
				b.setId(sequence.next());
				b.setInspectReportParentId(parentId);
				if (b.getResourceId() != null
						&& !resourceIds.contains(b.getResourceId())) {
					resourceIds.add(b.getResourceId());
				}
				this.f(b.getId(), b.getChildren(), data, resourceIds);
			}
		}
	}

	@Override
	public int updateBasic(InspectReportBasicBo bo) {
		int u = this.inspectReportDao.updateBasic(bo);
		this.inspectReportDao.updateEditDate(bo.getId());
		return u;
	}

	@Override
	public int updateNormal(InspectFrontReportOrPlanBo requestBo) {
		int num = 0;
		if(requestBo != null){
			List<InspectReportSelfItemBo> list = requestBo.getReportSelfItemsList();
			if(list != null && !list.isEmpty()){
				for(InspectReportSelfItemBo bo : list){
					num = num + this.inspectReportDao.updateReportSelfItems(bo);
				}
			}
		}
		return num;
	}

	@Override
	public int updateResult(InspectFrontReportOrPlanBo requestBo) {
		int num = 0;
		if(requestBo != null){
			List<InspectReportResultsSettingBo> list = requestBo.getReportResultsList();
			if(list != null && !list.isEmpty()){
				for(InspectReportResultsSettingBo bo : list){
					if(bo.getId() != null){
						num = this.inspectReportDao.updateResult(bo) + num;
					}
				}
			} 
		}
		
		return num;
	}

	@Override
	public int updateItems(String items) {
		if (null != items && 0 != items.length()) {
			JSONArray array = JSONArray.parseArray(items);
			if (null != array && 0 != array.size()) {
				for (int i = 0, len = array.size(); i < len; i++) {
					this.updateItemSingle(array.getJSONObject(i));
				}
			}
		}
		return 1;
	}

	/**
	 * 逐个保存
	 *
	 * @param obj
	 */
	private void updateItemSingle(JSONObject obj) {
		long id = obj.getLong("id");
		String value = obj.getString("inspectReportItemValue");
		String describle = obj.getString("reportItemConditionDescrible");
		String result = obj.getString("inspectReportItemResult");
		InspectReportContentBo bo = new InspectReportContentBo();
		bo.setId(id);
		bo.setInspectReportItemValue(value);
		bo.setReportItemConditionDescrible(describle);
		bo.setInspectReportItemResult((result == null || "".equals(result) ? false
				: Boolean.parseBoolean(result)));
		this.inspectReportDao.updateItemSingle(bo);
		if (obj.containsKey("children")) {
			JSONArray array = obj.getJSONArray("children");
			if (null != array && 0 != array.size()) {
				for (int i = 0, len = array.size(); i < len; i++) {
					this.updateItemSingle(array.getJSONObject(i));
				}
			}
		}
	}

	@Override
	public int updateEditDate(long id) {
		return this.inspectReportDao.updateEditDate(id);
	}

	@Override
	public List<InspectReportContentBo> loadItemTwo(long catalogId) {
		return this.inspectReportDao.loadItemTwo(catalogId);
	}
}
