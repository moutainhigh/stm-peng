package com.mainsteam.stm.portal.inspect.dao.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.inspect.bo.BasicInfoBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanClob;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanResultSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanSelfItemBo;
import com.mainsteam.stm.portal.inspect.bo.Routine;
import com.mainsteam.stm.portal.inspect.dao.IInspectPlanDaoApi;


@SuppressWarnings("rawtypes")
public class InspectPlanDaoImpl extends BaseDao implements IInspectPlanDaoApi {
	private static Log logger = LogFactory.getLog(InspectPlanDaoImpl.class);
	
	@Autowired
	private SqlSessionFactory myBatisSqlSessionFactory;
	public InspectPlanDaoImpl(SqlSessionTemplate session) {
		super(session, IInspectPlanDaoApi.class.getName());
	}

	@Override
	public Page<InspectPlanBasicBo, InspectPlanBasicBo> list(
			Page<InspectPlanBasicBo, InspectPlanBasicBo> page) {
		
		logger.info("page参数。。。。"+page.getCondition().getOrderUserId());
		List<InspectPlanBasicBo> data = super.getSession()
				.<InspectPlanBasicBo> selectList(
						getNamespace() + "inspectPlanByList", page);
		for (InspectPlanBasicBo b : data) {
			if ("0000-00-00 00:00:00".equals(b.getInspectPlanLastExecTime())) {
				b.setInspectPlanLastExecTime(null);
			}
		}
		page.setDatas(data);
		return page;
	}

	@Override
	public int saveBasic(long id, String name, int type, String format,
			String domain, long inspector, String description,
			boolean reportChange, long creator) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		map.put("type", type);
		map.put("format", format);
		map.put("domain", domain);
		map.put("inspector", inspector);
		map.put("description", description);
		map.put("reportChange", reportChange);
		map.put("creator", creator);
		return super.getSession().insert("inspect_plan_saveBasic", map);
	}

	@Override
	public int updateState(long id, boolean state) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("state", state);
		return super.getSession().update("inspect_plan_updateState", map);
	}

	@Override
	public int updateBasic(long id, String name, int type, String format,
			String domain, long inspector, String description,
			boolean reportChange) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		map.put("type", type);
		map.put("format", format);
		map.put("domain", domain);
		map.put("inspector", inspector);
		map.put("description", description);
		map.put("reportChange", reportChange);
		return super.getSession().update("inspect_plan_updateBasic", map);
	}

	@Override
	public int updateRoutine(long id, boolean reportProduceTimeShow,
			boolean reportModifyTimeShow, boolean reportModifiorShow,
			boolean resourceShow, boolean businessShow, String resourceName,
			String businessName, String inspectReportResourceType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("reportProduceTimeShow", reportProduceTimeShow);
		map.put("reportModifyTimeShow", reportModifyTimeShow);
		map.put("reportModifiorShow", reportModifiorShow);
		map.put("resourceShow", resourceShow);
		map.put("businessShow", businessShow);
		map.put("resourceName", resourceName);
		map.put("businessName", businessName);
		map.put("resourceType", inspectReportResourceType);
		return super.getSession().update("inspect_plan_updateRoutine", map);
	}

	@Override
	public int addSelfItem(InspectPlanSelfItemBo bo) {
		List<InspectPlanSelfItemBo> data = new ArrayList<>();
		data.add(bo);
		return this.addSelfItems(data);
	}

	@Override
	public int delSelfItems(long basicId) {
		return this.delSelf(new long[] { basicId });
	}

	@Override
	public int addSelfItems(List<InspectPlanSelfItemBo> data) {
		return super.getSession().insert("inspect_plan_addSelfItems", data);
	}

	@Override
	public int addInspectionItems(List<InspectPlanContentBo> data) {
		return super.getSession().insert("inspect_plan_addInspectionItems",
				data);
	}

	@Override
	public int updateInspectionItem(InspectPlanContentBo data) {
		return super.getSession().update("inspect_plan_updateInspectionItem",
				data);
	}

	@Override
	public int delInspectionItem(Long[] inspectionItemids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", inspectionItemids);
		if(inspectionItemids.length!=0){
			
			try(
					SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
				for (int i = 0; i < inspectionItemids.length; i++) {
					long id=inspectionItemids[i];
					super.getSession().delete("inspect_plan_delInspectionItem",id);
				}
					batchSession.commit();
				}
			
		
		}
		return 1;
	}

	@Override
	public List<Long> queryInspectionItemByThreeId(Long[] inspectionItemId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", inspectionItemId);
		return super.getSession().selectList(
				"inspect_plan_queryInspectionItemByThreeId", map);
	}

	@Override
	public List<InspectPlanContentBo> loadInspectionItems(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return super.getSession().selectList(
				"inspect_plan_loadInspectionItems", map);
	}

	@Override
	public BasicInfoBo loadBasic(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		return getSession().selectOne("loadBasic", paramMap);
	}

	@Override
	public Routine getRoutine(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		List<Map<String, Object>> data = super.getSession().<Map<String, Object>> selectList("inspect_plan_getRoutine",
						map);
		if (data != null && data.size() > 0) {
			
			Routine routine = new Routine();
			Map<String, Object> mr = data.get(0);
			boolean bl = false;//是否是非mysql数据库
	
			if(mr.get("PID") == null){//mysql
				bl = true;
			}
			
			if(bl){
				routine.setInspector(this.convertLong(mr.get("inspector")));
			}else{
				if(mr.get("INSPECTOR") != null){
					routine.setInspector(Long.parseLong(String.valueOf(mr.get("INSPECTOR"))));
				}
			}
			routine.setReportName(this.convertString(bl?mr.get("reportName"):mr.get("REPORTNAME")));
			routine.setInspectPlanReportModifiorShow(this.convertString(bl?mr
					.get("inspectPlanReportModifiorShow"):mr.get("INSPECTPLANREPORTMODIFIORSHOW")));
			routine.setReportModifyTimeShow(this.convertString(bl?mr
					.get("reportModifyTimeShow"):mr.get("REPORTMODIFYTIMESHOW")));
			routine.setReportProduceTimeShow(this.convertString(bl?mr
					.get("reportProduceTimeShow"):mr.get("REPORTPRODUCETIMESHOW")));
			routine.setInspectReportBusinessName(this.convertString(bl?mr
					.get("inspectReportBusinessName"):mr.get("INSPECTREPORTBUSINESSNAME")));
			routine.setInspectReportBusinessNameShow(this.convertString(bl?mr
					.get("inspectReportBusinessNameShow"):mr.get("INSPECTREPORTBUSINESSNAMESHOW")));
			routine.setInspectReportResourceName(this.convertString(bl?mr
					.get("inspectReportResourceName"):mr.get("INSPECTREPORTRESOURCENAME")));
			routine.setInspectReportResourceNameShow(this.convertString(bl?mr
					.get("inspectReportResourceNameShow"):mr.get("INSPECTREPORTRESOURCENAMESHOW")));
			routine.setSelfItems(new ArrayList<InspectPlanSelfItemBo>());
			routine.setInspectReportResourceType(this.convertString(bl?mr
					.get("inspectReportResourceType"):mr.get("INSPECTREPORTRESOURCTYPE")));
			//添加报告生成时间和最后修改时间
			if(bl){
				if(mr.get("reportModifyTime") == null || "".equals(mr.get("reportModifyTime").toString())){
					routine.setReportModifyTime("");
				}else{
					Timestamp mt = (Timestamp)mr.get("reportModifyTime");
					routine.setReportModifyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mt.getTime())));
				}
				if(mr.get("reportProduceTime") == null || "".equals(mr.get("reportProduceTime").toString())){
					routine.setReportProduceTime("");
				}else{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				    Date date;
					try {
						date = sdf.parse(String.valueOf(mr.get("reportProduceTime")));
						routine.setReportProduceTime(sdf.format(date));
					} catch (ParseException e) {
						if(logger.isErrorEnabled()){
                           logger.error(this.getClass().getName()+".getRoutine error:", e);
						}
						routine.setReportProduceTime("");
					}  
			
				}
					
			}else{
				if(mr.get("REPORTMODIFYTIME") == null || "".equals(mr.get("REPORTMODIFYTIME").toString())){
					routine.setReportModifyTime("");
				}else{
					try{
					Timestamp mt =getOracleTimestamp(mr.get("REPORTMODIFYTIME")); // (Timestamp)mr.get("REPORTMODIFYTIME");
					routine.setReportModifyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mt.getTime())));
					}
					catch(Exception e){
						logger.info(e.toString());
						
					}
				}
				if(mr.get("REPORTPRODUCETIME") == null || "".equals(mr.get("REPORTPRODUCETIME").toString())){
					routine.setReportProduceTime("");
				}else{
					try{
					Timestamp pt = getOracleTimestamp(mr.get("REPORTMODIFYTIME"));//(Timestamp)mr.get("REPORTPRODUCETIME");
					routine.setReportProduceTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(pt.getTime())));
					}
					catch(Exception e){
						logger.info(e.toString());
					}
				}
				
			}
			
			for (Map m : data) {
				Long cid = null; 
				if(bl){
					cid = this.convertLong(m.get("cid"));
				}else{ 
					if(mr.get("CID") != null){
						cid = Long.parseLong(String.valueOf(mr.get("CID")));	
					} 
				}
				if (cid != null) {
					InspectPlanSelfItemBo item = new InspectPlanSelfItemBo();
					routine.getSelfItems().add(item);
					item.setId(cid);
					item.setInspectPlanId(id);
					item.setInspectPlanSelfItemName(this.convertString(bl ? m.get("inspectReportSelfItemName") : m.get("INSPECTREPORTSELFITEMNAME")));
					if(bl){
						item.setInspectPlanSelfItemType(this.convertInteger(m.get("inspectReportSelfItemType")));
					}else{
						if(m.get("INSPECTREPORTSELFITEMTYPE") != null){
							item.setInspectPlanSelfItemType(Integer.parseInt(String.valueOf(m.get("INSPECTREPORTSELFITEMTYPE"))));
						}
					}
				if(bl){
					item.setInspectPlanItemContent(this.convertString(m.get("inspectReportItemContent")));	
				}else{
					try {
						if(m.get("INSPECTREPORTITEMCONTENT") != null){
							item.setInspectPlanItemContent(ClobToString((Clob) m.get("INSPECTREPORTITEMCONTENT")));	
						}
					} catch (Exception e) {
						if(logger.isErrorEnabled()){
	                         logger.error(this.getClass().getName()+".getRoutine error:",e);
					     }
					}
					
				}
					
				}
			}
			return routine;
		}
		return null;
	}

	public String ClobToString(Clob clob) throws SQLException, IOException {

		String reString = "";
		Reader is = clob.getCharacterStream();// 得到流
		BufferedReader br = new BufferedReader(is);
		String s = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
		sb.append(s);
		s = br.readLine();
		}
		reString = sb.toString();
		return reString;
		}
	private Timestamp getOracleTimestamp(Object value) { 
		try { 
		Class clz = value.getClass(); 
		Method m = clz.getMethod("timestampValue", null); 
		                       //m = clz.getMethod("timeValue", null); 时间类型 
		                       //m = clz.getMethod("dateValue", null); 日期类型 
		return (Timestamp) m.invoke(value, null); 

		} catch (Exception e) { 
		return null; 
		} 
		}
	private String convertString(Object obj) {
		if (obj != null) {
			return (String) obj;
		}
		return null;
	}

	private Long convertLong(Object obj) {
		if (obj != null) {
			return Long.parseLong(obj.toString());
		}
		return null;
	}

	private Integer convertInteger(Object obj) {
		if (obj != null) {
			return (Integer) obj;
		}
		return null;
	}

	@Override
	public int saveConclusions(List<InspectPlanResultSettingBo> data) {
		return super.getSession().insert("inspect_plan_saveConclusions", data);
	}

	@Override
	public int delConclusionsByBasicId(long basicId) {
		return this.delResult(new long[] { basicId });
	}

	@Override
	public List<InspectPlanResultSettingBo> getConclusionsByBasicId(long basicId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("basicId", basicId);
		return super.getSession().selectList(
				"inspect_plan_getConclusionsByBasicId", map);
	}

	@Override
	public List<InspectPlanContentBo> loadItem(long catalogId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("catalogId", catalogId);
		return super.getSession().selectList("inspect_plan_loadItem", map);
	}

	public List<InspectPlanContentBo> loadItemHierarchyTwo(long catalogId) {
		final List<InspectPlanContentBo> data = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
//		System.out.println(Integer.parseInt(String.valueOf(catalogId))); 
		map.put("catalogId", Long.parseLong(String.valueOf(catalogId)));
		logger.info("map的catalogid是！！！！！！"+map.get("catalogId"));
		super.getSession().select("inspect_plan_loadItem_hierarchy_two", map,
				new ResultHandler() {
					InspectPlanContentBo c = null;
					long oldId = -1;

					@Override
					public void handleResult(ResultContext rc) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map) rc.getResultObject();
//						long newId = (long) map.get("ID");
						Object idObj = map.get("id");
						if (idObj !=  null) {
							logger.info("idobj是~~~~"+idObj);
							long newId = Long.valueOf(idObj.toString());  /* (long)idObj;*/
							if (newId != oldId) {
								c = new InspectPlanContentBo();
								data.add(c);
								c.setId(newId);
								oldId = newId;
								try{
								c.setInspectPlanId((Long)map.get("inspectPlanId")/*Long.parseLong(map.get("inspectPlanId").toString())*/);
								c.setInspectPlanParentId(/*(Long) map
										.get("inspectPlanParentId")*/Long.parseLong(map
										.get("inspectPlanParentId").toString()));
								c.setInspectPlanItemName(map
										.get("inspectPlanItemName").toString());
								c.setInspectPlanItemDescrible(map
										.get("inspectPlanItemDescrible").toString());
								c.setInspectPlanItemValue(map
										.get("inspectPlanItemValue").toString());
								c.setInspectPlanItemReferencePrefix(map
										.get("inspectPlanItemReferencePrefix").toString());
								c.setInspectPlanItemReferenceSubfix( map
										.get("inspectPlanItemReferenceSubfix").toString());
								c.setInspectPlanItemUnit(map
										.get("inspectPlanItemUnit").toString());
								c.setItemConditionDescrible(map
										.get("itemConditionDescrible").toString());
								c.setSort(Integer.parseInt(map.get("sort").toString()));
								c.setEdit(/*(boolean) map.get("edit")*/Boolean.parseBoolean(map.get("edit").toString()));
								c.setIndicatorAsItem(Boolean.parseBoolean(map
										.get("indicatorAsItem").toString()));
								c.setResourceId(/*(Long) map.get("resourceId")*/Long.parseLong(map.get("resourceId").toString()));
								c.setIndexModelId( map.get("indexModelId").toString());
								c.setModelId(map.get("modelId").toString());
								c.setType(map.get("TYPE")!=null?Integer.parseInt(String.valueOf(map.get("TYPE"))):null);
								c.setChildren(new ArrayList<InspectPlanContentBo>());
								}catch(Exception e){
									logger.info(e.toString());
								}
								
							}
						
						} else {
							long newId = Long.parseLong(map.get("ID").toString());
							if (newId != oldId) {
								c = new InspectPlanContentBo();
								data.add(c);
								c.setId(newId);
								oldId = newId;
								if (map.get("INSPECTPLANID") != null) {
									c.setInspectPlanId(Long.parseLong(map.get("INSPECTPLANID").toString()));
								}
								if (map.get("INSPECTPLANPARENTID") != null) {
									c.setInspectPlanParentId(Long.parseLong(map.get("INSPECTPLANPARENTID").toString()));
								}
								if (map.get("INSPECTPLANITEMNAME") != null) {
									c.setInspectPlanItemName(map
											.get("INSPECTPLANITEMNAME").toString());
								}
								if (map.get("INSPECTPLANITEMDESCRIBLE") != null) {
									c.setInspectPlanItemDescrible(map
											.get("INSPECTPLANITEMDESCRIBLE").toString());
								}
								if (map.get("INSPECTPLANITEMVALUE") != null) {
									Clob itemValueClob = (Clob)map.get("INSPECTPLANITEMVALUE");
									try {
										c.setInspectPlanItemValue(itemValueClob.
												getSubString(1, (int)itemValueClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (map.get("INSPECTPLANITEMREFERENCEPREFIX") != null) {
									Clob prefixClob = (Clob)map.get("INSPECTPLANITEMREFERENCEPREFIX");
									try {
										c.setInspectPlanItemReferencePrefix(prefixClob
												.getSubString(1, (int)prefixClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (map.get("INSPECTPLANITEMREFERENCESUBFIX") != null) {
									Clob subfixClob = (Clob)map.get("INSPECTPLANITEMREFERENCESUBFIX");
									try {
										c.setInspectPlanItemReferenceSubfix(subfixClob.getSubString(1, (int)subfixClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (map.get("INSPECTPLANITEMUNIT") != null) {
									c.setInspectPlanItemUnit(map
											.get("INSPECTPLANITEMUNIT").toString());
								}
								if (map.get("ITEMCONDITIONDESCRIBLE") != null) {
									Clob  desClob = (Clob)map.get("ITEMCONDITIONDESCRIBLE");
									try {
										c.setItemConditionDescrible(desClob.getSubString(1, (int)desClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								c.setSort(Integer.parseInt(map.get("SORT").toString()));
								c.setEdit(Integer.parseInt(map.get("EDIT").toString()) == 0 ? false : true);
								
								c.setType(map.get("TYPE")!=null?Integer.parseInt(String.valueOf(map.get("TYPE"))):null);
								c.setIndicatorAsItem(Integer.parseInt(map
										.get("INDICATORASITEM").toString()) == 0 ? false : true);
								if (map.get("RESOURCEID") != null) {
									c.setResourceId(Long.parseLong(map.get("RESOURCEID").toString()));
								}
								if (map.get("INDEXMODELID") != null) {
									c.setIndexModelId(map.get("INDEXMODELID").toString());
								}
								if (map.get("MODELID") != null) {
									c.setModelId(map.get("MODELID").toString());
								}
								c.setChildren(new ArrayList<InspectPlanContentBo>());
							}
						}
						Object id2Obj = map.get("id2");
						Object id2UpperObj = map.get("ID2");
						if (id2Obj != null) {
							Long id2 = (Long)id2Obj;
							InspectPlanContentBo c2 = new InspectPlanContentBo();
							c.getChildren().add(c2);
							c2.setId(id2);
							c2.setInspectPlanId((Long) map
									.get("inspectPlanId2"));
							c2.setInspectPlanParentId((Long) map
									.get("inspectPlanParentId2"));
							c2.setInspectPlanItemName((String) map
									.get("inspectPlanItemName2"));
							c2.setInspectPlanItemDescrible((String) map
									.get("inspectPlanItemDescrible2"));
							c2.setInspectPlanItemValue((String) map
									.get("inspectPlanItemValue2"));
							c2.setInspectPlanItemReferencePrefix((String) map
									.get("itemReferencePrefix2"));
							c2.setInspectPlanItemReferenceSubfix((String) map
									.get("itemReferenceSubfix2"));
							c2.setInspectPlanItemUnit((String) map
									.get("inspectPlanItemUnit2"));
							c2.setItemConditionDescrible((String) map
									.get("itemConditionDescrible2"));
							c2.setSort((int) map.get("sort2"));
							c2.setEdit((boolean) map.get("edit2"));
							c2.setIndicatorAsItem((boolean) map
									.get("indicatorAsItem2"));
							c2.setResourceId((Long) map.get("resourceId2"));
							c2.setIndexModelId((String) map
									.get("indexModelId2"));
							c2.setModelId((String) map.get("modelId2"));
							c2.setType(map.get("TYPE")!=null?Integer.parseInt(String.valueOf(map.get("TYPE"))):null);
						}
						if (null == id2Obj && id2UpperObj != null) {
							Long idUpper = Long.parseLong(id2UpperObj.toString());
							InspectPlanContentBo c2 = new InspectPlanContentBo();
							c.getChildren().add(c2);
							c2.setId(idUpper);
							if (map.get("INSPECTPLANID2") != null) {
								c2.setInspectPlanId(Long.parseLong(map.get("INSPECTPLANID2").toString()));
							}
							if (map.get("INSPECTPLANPARENTID2") != null) {
								c2.setInspectPlanParentId(Long.parseLong(map.get("INSPECTPLANPARENTID2").toString()));
							}
							if (map.get("INSPECTPLANITEMNAME2") != null) {
								c2.setInspectPlanItemName(map
										.get("INSPECTPLANITEMNAME2").toString());
							}
							if ( map.get("INSPECTPLANITEMDESCRIBLE2") != null) {
								c2.setInspectPlanItemDescrible(map
										.get("INSPECTPLANITEMDESCRIBLE2").toString());
							}
							if (map.get("INSPECTPLANITEMVALUE2") != null) {
								Clob itemValueClob2 = (Clob)map.get("INSPECTPLANITEMVALUE2");
								try {
									c2.setInspectPlanItemValue(itemValueClob2.
											getSubString(1, (int)itemValueClob2.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							if (map.get("ITEMREFERENCEPREFIX2") != null) {
								Clob prefixClob2 = (Clob)map.get("ITEMREFERENCEPREFIX2");
								try {
									c2.setInspectPlanItemReferencePrefix(prefixClob2
											.getSubString(1, (int)prefixClob2.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							if (map.get("ITEMREFERENCESUBFIX2") != null) {
								Clob subfixClob2 = (Clob)map.get("ITEMREFERENCESUBFIX2");
								try {
									c2.setInspectPlanItemReferenceSubfix(subfixClob2.getSubString(1, (int)subfixClob2.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							if (map.get("INSPECTPLANITEMUNIT2") != null) {
								c2.setInspectPlanItemUnit(map
										.get("INSPECTPLANITEMUNIT2").toString());
							}
							if (map.get("ITEMCONDITIONDESCRIBLE2") != null) {
								Clob desClob2 = (Clob)map.get("ITEMCONDITIONDESCRIBLE2");
								try {
									c.setItemConditionDescrible(desClob2.getSubString(1, (int)desClob2.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							
							c2.setSort(Integer.parseInt(map.get("SORT2").toString()));
							c2.setEdit(Integer.parseInt(map.get("EDIT2").toString()) == 0 ? false : true);
							c2.setType(map.get("TYPE")!=null?Integer.parseInt(String.valueOf(map.get("TYPE"))):null);
							c2.setIndicatorAsItem(Integer.parseInt(map
									.get("INDICATORASITEM2").toString()) == 0 ? false : true);
							if (map.get("RESOURCEID2") != null) {
								c2.setResourceId(Long.parseLong(map.get("RESOURCEID2").toString()));
							}
							if (map.get("INDEXMODELID2") != null) {
								c2.setIndexModelId(map
										.get("INDEXMODELID2").toString());
							}
							if (map.get("MODELID2") != null) {
								c2.setModelId(map.get("MODELID2").toString());
							}
						}
					}
				});
		return data;
	}

	@Override
	public int addItems(List<InspectPlanContentBo> data) {
		
		try(
				SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
				for (InspectPlanContentBo inspectPlanContentBo : data) {
					super.getSession().insert("inspect_plan_addItems", inspectPlanContentBo);

				}
				batchSession.commit();
			}

		return 1;

	}

	@Override
	public int updateItem(InspectPlanContentBo data) {
		return super.getSession().update("inspect_plan_updateItem", data);
	}

	@Override
	public int updateItemBasic(long id, String prefix, String subfix,
			String unit, String value, String describle) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("prefix", prefix);
		map.put("subfix", subfix);
		map.put("unit", unit);
		map.put("value", value);
		map.put("describle", describle);
		return super.getSession().update("inspect_plan_updateItemBasic", map);
	}

	@Override
	public InspectPlanClob loadJobInfo(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return super.getSession().selectOne("inspect_plan_loadJobInfo", map);
	}

	@Override
	public int updateExecTime(long id, Date date) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("date", date);
		return super.getSession().update("inspect_plan_updateExecTime", map);
	}

	@Override
	public Map<String, Object> getPlan(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return super.getSession().selectOne("inspect_plan_getPlan", map);
	}

	@Override
	public List<InspectPlanSelfItemBo> getSelfItem(long planId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planId", planId);
		return super.getSession().selectList("inspect_plan_getSelfItem", map);
	}

	@Override
	public boolean getState(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return super.getSession().selectOne("inspect_plan_getState", map);
	}

	@Override
	public int del(long[] planIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planIds", planIds);
		return super.getSession().delete("inspect_plan_delPlan", map);
	}

	@Override
	public int delSelf(long[] planIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planIds", planIds);
		return super.getSession().delete("inspect_plan_delSelfItems", map);
	}

	@Override
	public int delResult(long[] planIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planIds", planIds);
		return super.getSession().delete(
				"inspect_plan_delConclusionsByBasicId", map);
	}

	@Override
	public Long[] getThreeItems(long[] planIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planIds", planIds);
		List<Long> ids = super.getSession().selectList(
				"inspect_plan_comtentByDel", map);
		Long[] idsArray = null;
		if (ids != null) {
			idsArray = new Long[ids.size()];
			ids.toArray(idsArray);
		}
		return idsArray;
	}

	@Override
	public Long[] getThreeItemsByItemIds(long[] itemIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("itemIds", itemIds);
		List<Long> ids = super.getSession().selectList(
				"inspect_plan_comtentByDel", map);
		Long[] idsArray = null;
		if (ids != null) {
			idsArray = new Long[ids.size()];
			ids.toArray(idsArray);
		}
		return idsArray;
	}

	@Override
	public int copyPlan(long planId, long newId, long userId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planId", planId);
		map.put("newId", newId);
		map.put("userId", userId);
		return super.getSession().insert("inspect_plan_copyPlan", map);
	}

	@Override
	public int updateReportName(long planId, String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planId", planId);
		map.put("name", name);
		return super.getSession().update("inspect_plan_updateReportName", map);
	}
	
	@Override
	public Long selectReportByName(String name) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		return super.getSession().selectOne("inspect_plan_selectReportByName", map);
	}

	@Override
	public Long[] getTwoItems(long[] parentIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentIds", parentIds);
		List<Long> ids = super.getSession().selectList(
				"inspect_plan_getTwoItems", map);
		Long[] idsArray = null;
		if (ids != null) {
			idsArray = new Long[ids.size()];
			ids.toArray(idsArray);
		}
		return idsArray;
	}

	@Override
	public int updateEditDate(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return super.getSession().update("inspect_plan_updateEditDate", map);
	}

	@Override
	public List<Long> getContentByResourceId(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return this.getSession().selectList(
				"inspect_plan_getContentByResourceId", map);
	}

	@Override
	public int delContent(List<Long> ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", ids);
		return super.getSession().delete("inspect_plan_delContent", map);
	}

	@Override
	public int saveDomainBasic(Long id, long inspectid , long domainid) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("inspectid", inspectid);
		map.put("domainid", domainid);
		return super.getSession().insert("inspect_planDomain_save", map);
	}

	
	@Override
	public int del(long id,List<Long> ids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("ids", ids);
		return super.getSession().delete("inspect_planDomain_del", map);
	}



	@Override
	public List<InspectPlanBasicBo> inspectPlanloadAll(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("inspectid", id);
		return getSession().selectOne("inspect_planDomain_loadAll", paramMap);
	}



	@Override
	public InspectPlanBasicBo get(long inspectid, long domainid) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("inspectid", inspectid);
		paramMap.put("domainid", domainid);
		return getSession().selectOne("inspect_planDomain_loadById", paramMap);
	}

	@Override
	public int delInspectionItemParent(Long[] inspectionItemids) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", inspectionItemids);
		return super.getSession().delete("inspect_plan_delInspectionItemParent", map);
		//
	}


	@Override
	public int delAll(long[] planIds) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("planIds", planIds);
		return super.getSession().delete("inspect_planDomain_delAll", map);
	}

}
