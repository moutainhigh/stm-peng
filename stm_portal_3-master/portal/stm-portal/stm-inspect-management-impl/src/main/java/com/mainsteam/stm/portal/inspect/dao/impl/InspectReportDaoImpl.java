package com.mainsteam.stm.portal.inspect.dao.impl;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.inspect.bo.InspectPlanContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportConditionBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;
import com.mainsteam.stm.portal.inspect.dao.IInspectReportDaoApi;


@SuppressWarnings("rawtypes")
public class InspectReportDaoImpl extends BaseDao implements
		IInspectReportDaoApi {
	@Autowired
	private SqlSessionFactory myBatisSqlSessionFactory;
	public InspectReportDaoImpl(SqlSessionTemplate session) {
		super(session, IInspectReportDaoApi.class.getName());
	}

	@Override
	public List<InspectReportBasicBo> getReportList(
			Page<InspectReportBasicBo, InspectReportConditionBo> page) {
		List<InspectReportBasicBo> reportList = super.getSession()
				.<InspectReportBasicBo> selectList(
						getNamespace() + "getAllReportList", page);
		return reportList;
	}

	@Override
	public int updateStatus(String id) {

		return super.getSession().update(getNamespace() + "updateReportStatus",
				id);
	}

	@Override
	public InspectReportBasicBo loadBasic(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		return getSession().selectOne(getNamespace() + "loadReportBasic",
				paramMap);
	}

	@Override
	public List<InspectReportSelfItemBo> loadRoutine(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		return super.getSession().selectList(
				getNamespace() + "loadReportRoutine", paramMap);
	}

	@Override
	public List<InspectReportResultsSettingBo> getConclusionsByBasicId(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		return super.getSession().<InspectReportResultsSettingBo> selectList(
				getNamespace() + "loadReportConclusions", paramMap);
	}

	@Override
	public List<InspectReportContentBo> loadInspectionItems(long id) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", id);
		return super.getSession().selectList(
				getNamespace() + "loadInspectionItems", paramMap);
	}

	@Override
	public List<InspectReportContentBo> loadItem(long catalogId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", catalogId);
		return super.getSession().selectList(getNamespace() + "loadItem",
				paramMap);
	}

	@Override
	public int saveBasic(InspectReportBasicBo data) {
		return super.getSession().insert("inspect_report_saveBasic", data);
	}

	@Override
	public int saveSelfItem(List<InspectReportSelfItemBo> reportSelfs) {
		return super.getSession().insert("inspect_report_saveSelfItem",
				reportSelfs);
	}

	@Override
	public int saveResults(List<InspectReportResultsSettingBo> results) {
		return super.getSession().insert("inspect_report_saveResults", results);
	}

	@Override
	public int saveContents(List<InspectReportContentBo> contents) {
		for (InspectReportContentBo t : contents) {
			if (null==t.getInspectReportItemValue()) {
				t.setInspectReportItemValue("");
			}
		}
		try(
				SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
				for (InspectReportContentBo ContentBo : contents) {
					super.getSession().insert("inspect_report_saveContents", ContentBo);

				}
				batchSession.commit();
			}
		return 1;
	}

	@Override
	public int updateBasic(InspectReportBasicBo bo) {
		return super.getSession().update("update_basic_info", bo);
	}

	@Override
	public int deleteNormalByReportId(Long id) {
		return super.getSession().delete("delete_normal_by_reportid", id);
	}

	@Override
	public int updateReportSelfItems(InspectReportSelfItemBo bo) {
		return super.getSession().update("update_report_self_items", bo);
	}

	@Override
	public int updateResult(InspectReportResultsSettingBo bo) {
		return super.getSession().update("update_report_result", bo);
	}

	@Override
	public int updateItemSingle(InspectReportContentBo bo) {
		return super.getSession().update("update_item_single", bo);
	}

	@Override
	public int updateEditDate(long id) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		return super.getSession().update("update_inspect_report_EditDate", map);
	}

	@Override
	public List<InspectReportContentBo> loadItemTwo(long catalogId) {
		final List<InspectReportContentBo> data = new ArrayList<>();
	final	List<String> ids= new ArrayList<String>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", catalogId);
//		System.out.println("id= "+map.get("id"));
//		System.out.println("id= "+map.get("ID"));
	List<InspectReportContentBo> bos=	super.getSession().selectList("inspect_report_loadItem_hierarchy_twos", catalogId);
/*	System.out.println(bos.size());
	if(bos.isEmpty()){
		System.out.println("123");
		
	}
	for (InspectReportContentBo inspectReportContentBo : bos) {
		System.out.println(inspectReportContentBo.getInspectReportItemName()+"=="+inspectReportContentBo.getType());
	}*/
		super.getSession().select("inspect_report_loadItem_hierarchy_two", map,
				new ResultHandler() {
					InspectReportContentBo c = null;
					long oldId = -1;
					
					@Override
					public void handleResult(ResultContext rc) {
						@SuppressWarnings("unchecked")
						Map<String, Object> map = (Map) rc.getResultObject();
						Object newIdObj = map.get("id");
//						Object newIdObj = map.get("ID");
						if (newIdObj != null) {
				
							long 	 newId = (long) newIdObj;
							if (newId != oldId) {
								c = new InspectReportContentBo();
								data.add(c);
								c.setId(newId);
								oldId = newId;
								c.setInspectReportid((Long) map
										.get("inspectReportid"));
								c.setInspectReportParentId((Long) map
										.get("inspectReportParentId"));
								c.setInspectReportItemName((String) map
										.get("inspectReportItemName"));
								c.setInspectReportItemDescrible((String) map
										.get("inspectReportItemDescrible"));
								c.setInspectReportItemValue((String) map
										.get("inspectReportItemValue"));
								c.setReportItemReferencePrefix((String) map
										.get("reportItemReferencePrefix"));
								c.setReportItemReferenceSubfix((String) map
										.get("reportItemReferenceSubfix"));
								c.setInspectReportItemUnit((String) map
										.get("inspectReportItemUnit"));
								c.setReportItemConditionDescrible((String) map
										.get("reportItemConditionDescrible"));
								c.setInspectReportItemResult((Boolean) map
										.get("inspectReportItemResult"));
								c.setEdit((boolean) map.get("edit"));
								c.setType((int) map.get("type"));
								c.setIndicatorAsItem((boolean) map
										.get("indicatorAsItem"));
								c.setChildren(new ArrayList<InspectReportContentBo>());
							}
						}else {
							long newUpperId = Long.parseLong(map.get("ID").toString());

							if (newUpperId != oldId) {
								c = new InspectReportContentBo();
						 
								data.add(c);
								c.setId(newUpperId);
								oldId = newUpperId;
								if (map.get("INSPECTREPORTID") != null) {
									c.setInspectReportid(Long.parseLong(map.get("INSPECTREPORTID").toString()));
								}
								if (map.get("INSPECTREPORTPARENTID") != null) {
									c.setInspectReportParentId(Long.parseLong(map.get("INSPECTREPORTPARENTID").toString()));
								}
								c.setInspectReportItemName((String) map
										.get("INSPECTREPORTITEMNAME"));
								
								if (map.get("INSPECTREPORTITEMDESCRIBLE") != null) {
									Clob itemDesClob = (Clob)map.get("INSPECTREPORTITEMDESCRIBLE");
									try {
										c.setInspectReportItemDescrible(itemDesClob.
												getSubString(1, (int)itemDesClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (map.get("INSPECTREPORTITEMVALUE") != null) {
									Clob itemValueClob = (Clob)map.get("INSPECTREPORTITEMVALUE");
									try {
										c.setInspectReportItemValue(itemValueClob.
												getSubString(1, (int)itemValueClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (map.get("REPORTITEMREFERENCEPREFIX") != null) {
									Clob itemPrefixClob = (Clob)map.get("REPORTITEMREFERENCEPREFIX");
									try {
										c.setReportItemReferencePrefix(itemPrefixClob.
												getSubString(1, (int)itemPrefixClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								if (map.get("REPORTITEMREFERENCESUBFIX") != null) {
									Clob itemSubfixClob = (Clob)map.get("REPORTITEMREFERENCESUBFIX");
									try {
										c.setReportItemReferenceSubfix(itemSubfixClob.
												getSubString(1, (int)itemSubfixClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								c.setInspectReportItemUnit((String) map
										.get("INSPECTREPORTITEMUNIT"));
								
								if (map.get("REPORTITEMCONDITIONDESCRIBLE") != null) {
									Clob conditonDesClob = (Clob)map.get("REPORTITEMCONDITIONDESCRIBLE");
									try {
										c.setReportItemConditionDescrible(conditonDesClob.
												getSubString(1, (int)conditonDesClob.length()));
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								c.setInspectReportItemResult(Integer.parseInt(map
										.get("INSPECTREPORTITEMRESULT").toString()) == 0 ? false : true);
								c.setEdit(Integer.parseInt(map.get("EDIT").toString()) == 0 ? false : true);
								if (map.get("TYPE") != null) {
									c.setType(Integer.parseInt(map.get("TYPE").toString()));
								}
								c.setIndicatorAsItem(Integer.parseInt(map
										.get("INDICATORASITEM").toString()) == 0 ? false : true);
							
								c.setChildren(new ArrayList<InspectReportContentBo>());
							}
						}
						Object id2Obj = map.get("id2");
						Object id2UpperObj = map.get("ID2");
						if (id2Obj != null) {
							Long id2 = (Long) id2Obj;
							InspectReportContentBo c2 = new InspectReportContentBo();
							c.getChildren().add(c2);
							c2.setId(id2);
							c2.setInspectReportid((Long) map
									.get("inspectReportid2"));
							c2.setInspectReportParentId((Long) map
									.get("inspectReportParentId2"));
							c2.setInspectReportItemName((String) map
									.get("inspectReportItemName2"));
							c2.setInspectReportItemDescrible((String) map
									.get("inspectReportItemDescrible2"));
							c2.setInspectReportItemValue((String) map
									.get("inspectReportItemValue2"));
							c2.setReportItemReferencePrefix((String) map
									.get("reportItemReferencePrefix2"));
							c2.setReportItemReferenceSubfix((String) map
									.get("reportItemReferenceSubfix2"));
							c2.setInspectReportItemUnit((String) map
									.get("inspectReportItemUnit2"));
							c2.setReportItemConditionDescrible((String) map
									.get("reportItemConditionDescrible2"));
							c2.setInspectReportItemResult((Boolean) map
									.get("inspectReportItemResult2"));
							c2.setEdit((boolean) map.get("edit2"));
							c2.setType((int) map.get("type2"));
							c2.setIndicatorAsItem((boolean) map
									.get("indicatorAsItem2"));
						}
						if (id2Obj == null && id2UpperObj != null) {
							Long id2Upper = Long.parseLong(id2UpperObj.toString());
							InspectReportContentBo c2 = new InspectReportContentBo();
							c.getChildren().add(c2);
							c2.setId(id2Upper);
							if (map.get("INSPECTREPORTID2") != null) {
								c2.setInspectReportid(Long.parseLong(map
										.get("INSPECTREPORTID2").toString()));
							}
							if (map.get("INSPECTREPORTPARENTID2") != null) {
								c2.setInspectReportParentId(Long.parseLong(map
										.get("INSPECTREPORTPARENTID2").toString()));
							}
							c2.setInspectReportItemName((String) map
									.get("INSPECTREPORTITEMNAME2"));
							if (map.get("INSPECTREPORTITEMDESCRIBLE2") != null) {
								Clob itemDesClob = (Clob)map.get("INSPECTREPORTITEMDESCRIBLE2");
								try {
									c2.setInspectReportItemDescrible(itemDesClob.
											getSubString(1, (int)itemDesClob.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							if (map.get("INSPECTREPORTITEMVALUE2") != null) {
								Clob itemValueClob = (Clob)map.get("INSPECTREPORTITEMVALUE2");
								try {
									c2.setInspectReportItemValue(itemValueClob.
											getSubString(1, (int)itemValueClob.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							if (map.get("REPORTITEMREFERENCEPREFIX2") != null) {
								Clob itemPrefixClob = (Clob)map.get("REPORTITEMREFERENCEPREFIX2");
								try {
									c2.setReportItemReferencePrefix(itemPrefixClob.
											getSubString(1, (int)itemPrefixClob.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							if (map.get("REPORTITEMREFERENCESUBFIX2") != null) {
								Clob itemSubfixClob = (Clob)map.get("REPORTITEMREFERENCESUBFIX2");
								try {
									c2.setReportItemReferenceSubfix(itemSubfixClob.
											getSubString(1, (int)itemSubfixClob.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							c2.setInspectReportItemUnit((String) map
									.get("INSPECTREPORTITEMUNIT2"));
							if (map.get("REPORTITEMCONDITIONDESCRIBLE2") != null) {
								Clob conditonDesClob = (Clob)map.get("REPORTITEMCONDITIONDESCRIBLE2");
								try {
									c2.setReportItemConditionDescrible(conditonDesClob.
											getSubString(1, (int)conditonDesClob.length()));
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							c2.setInspectReportItemResult(Integer.parseInt(map
									.get("INSPECTREPORTITEMRESULT2").toString()) == 0 ? false : true);
							c2.setEdit(Integer.parseInt(map.get("EDIT2").toString()) == 0 ? false : true);
							if (map.get("TYPE2") != null) {
								c2.setType(Integer.parseInt(map.get("TYPE2").toString()));
							}
							
							c2.setIndicatorAsItem(Integer.parseInt(map
									.get("INDICATORASITEM2").toString()) == 0 ? false : true);
						
						}
					}
				});
		 List<InspectReportContentBo> newDatas=	getData(data);
//		 System.out.println(newDatas);
		return newDatas;
	}
	public List<InspectReportContentBo> getData(List<InspectReportContentBo> data){
		List<InspectReportContentBo> datas= new ArrayList<InspectReportContentBo>();
		List<String> ids= new ArrayList<String>();
		if(data.size()!=0){
			for (int i = 0; i < data.size(); i++) {
				if(ids.contains(data.get(i).getId().toString())){
					List<InspectReportContentBo> childs= new ArrayList<InspectReportContentBo>();
					List<InspectReportContentBo> oldchilds=data.get(i).getChildren();
					
						for(int j=0;j<oldchilds.size();j++){
							for(int k=0;k<datas.size();k++){
								if(oldchilds.get(j).getInspectReportParentId().equals(datas.get(k).getId())){//属于子
								datas.get(k).getChildren().add(oldchilds.get(j));
							}
						}
					}
					
				}else{
					ids.add(data.get(i).getId().toString());
					datas.add(data.get(i));
				}
			}	
		}
	
		return datas;
	}
	
	@Override
	public int addContents(List<InspectReportContentBo> contents) {
		for (InspectReportContentBo t : contents) {
			if (null==t.getInspectReportItemValue()) {
				t.setInspectReportItemValue("");
			}
		}
		
		int k=0;
		List<InspectReportContentBo> contentBos= new ArrayList<InspectReportContentBo>();
		for (int i = k; i < contents.size(); i++) {
		
			contentBos.add(contents.get(i));
			k++;
			if((i>0 && i % 5 == 0)|| i == contents.size() - 1){
			super.getSession().insert("inspect_report_saveContentss", contentBos);
				contentBos= new ArrayList<InspectReportContentBo>();
				continue;
			}
			
		}
	
		return 1;
	
	}
}
