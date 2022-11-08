package com.mainsteam.stm.camera.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.camera.api.ICameraMonitorService;
import com.mainsteam.stm.camera.bo.CaremaMonitorBo;
import com.mainsteam.stm.camera.bo.JDBCVo;
import com.mainsteam.stm.camera.bo.TreeVo;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.report.MetricDataReportService;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.portal.report.bo.Chapter;
import com.mainsteam.stm.portal.report.bo.Chart;
import com.mainsteam.stm.portal.report.bo.ChartData;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ColumnsData;
import com.mainsteam.stm.portal.report.bo.ColumnsTitle;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.convert.BaseDataConvert;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;
import com.mainsteam.stm.system.um.login.api.ILoginApi;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.util.SpringBeanUtil;

public class CameraDataConvert extends BaseDataConvert {

	
	
	 private final Log logger = LogFactory.getLog(CameraDataConvert.class);
	 
	 List<Long> resourceIds = null;
	 
	public  ICameraMonitorService	cameraMonitorService; 
	
	 private ILoginApi loginApi;
	 
	 private IResourceDetailInfoApi resourceDetailInfoApi;
	 
	 private Map<String,String> scoreMap;
	 
	 
	 
	  public CameraDataConvert(ReportTemplate reportTemplate, ReportTemplateDirectory reportTemplateDirectory,List<Long> resourceIds)
		   {
		    this.reportTemplate = reportTemplate;
		    this.reportTemplateDirectory = reportTemplateDirectory;
		   this.metricDataReportService = ((MetricDataReportService)SpringBeanUtil.getObject("MetricDataReportService"));
		    this.metricDataService = ((MetricDataService)SpringBeanUtil.getObject("metricDataService"));
		    this.resourceIds=resourceIds;
		    init();
		   }
	
	@Override
	public ReportDirectory getReportDirectory() {
		 ReportDirectory reportDirectory = new ReportDirectory();
		 reportDirectory.setName("视频设备考核报表");
		reportDirectory.setType("1");
		 reportDirectory.setChapter(getChapters());
		 return reportDirectory;
	}
	
	
	
	public List<Chapter> getChapters() {
		 List<Chapter> chapters = new ArrayList<Chapter>();
		Chapter chapterSum = new Chapter();
		chapterSum.setName("");
		chapterSum.setSort("1");
		 List<Table> tables = new ArrayList<Table>();
		tables.add(getSumTable());
		List<Chart> charts = getSumCharts();
		chapterSum.setTable(tables);
		chapterSum.setChart(charts);
		chapters.add(chapterSum);
		
	
		 return chapters;
		 }
	
	
	private ColumnsTitle getColumnsTitle()
	 {
		ColumnsTitle title = new ColumnsTitle();
		List columns = new ArrayList();
		 columns.add(new Columns("序号"));
		columns.add(new Columns("地市名"));
		columns.add(new Columns("上联统计","考核指标数,上联数量,接入率"));
		columns.add(new Columns("在线统计","已诊断总数,在线数,在线率"));
		columns.add(new Columns("完好率统计","已诊断总数,完好数,图像完好率"));
		columns.add(new Columns("GIS采集率","GIS数,GIS率"));
		columns.add(new Columns("得分"));
		
		 title.setColumns(columns);
		return title;
		 }
	
	
	 public Table getSumTable()
	{
		 ColumnsTitle title = getColumnsTitle();
		 ColumnsData columnsData = getSumColumnsData();
		
		Table sumTable = new Table();
		sumTable.setName("");
		sumTable.setColumnsTitle(title);
		sumTable.setColumnsData(columnsData);
		
		return sumTable;
		 }
	 
	 
	 
	 public ColumnsData getSumColumnsData(){
		 ColumnsData columnsData = new ColumnsData();
		 columnsData.setTableData(getSumTableDatas());
		 return columnsData;
	 }
	 
	 
	 
	 public  List<TableData> getSumTableDatas()
	    {
		 
		 logger.info("-----------开始计算报表数据----------");
		 List tableDatas = new ArrayList();
	     List instanceMetricSummeryReportDatas = new ArrayList();
	     this.loginApi= ((ILoginApi)SpringBeanUtil.getObject("stm_system_login_api"));
	     this.cameraMonitorService = ((ICameraMonitorService)SpringBeanUtil.getObject("cameraMonitorService"));
	     this.resourceDetailInfoApi= ((IResourceDetailInfoApi)SpringBeanUtil.getObject("resourceDetailInfoApi"));
	     List<JDBCVo> jdbcList = this.cameraMonitorService .getDataBaseList("camera_config.xml");
	     
	     LoginUser user =this.loginApi.login("admin");
	     try {
			this.loginApi.setLoginUserRight(user);
		} catch (LicenseNotFoundException e) {
			e.printStackTrace();
		} catch (LicenseCheckException e) {
			e.printStackTrace();
		}
	     user.setUserType();
	     
	     try{
	     List<CaremaMonitorBo> cameraList= this.cameraMonitorService.
	    		 getCaremaBoReportList(user, 0, Long.MAX_VALUE, null, null, null, null, "CameraPlatform", null, null);
	     
	     Map<String,List<CaremaMonitorBo>> groupMap=new HashMap<String,List<CaremaMonitorBo>>();
	     
	     if(null!=cameraList&&cameraList.size()>0){
	    	 for(CaremaMonitorBo c:cameraList){
	    		 String groupId=c.getGroupId();
	    		 List<CaremaMonitorBo> list=null;
	    		 if(groupMap.containsKey(groupId)){
	    			 list=groupMap.get(groupId);
	    		 }
	    		 else{
	    			 list=new ArrayList<CaremaMonitorBo>();
	    		 }
	    		 list.add(c);
	    		 groupMap.put(groupId, list);
	    	 }
	     }
	     
	     int cnt = 1;
    	 long instancesId=this.resourceIds.get(0);
    	 logger.info("根节点信息是----------"+instancesId);
    	 //得到选中的节点和下级节点
    	 List<TreeVo> cityList=this.cameraMonitorService.getCameraListByParentId(instancesId, jdbcList,groupMap);
    	 List<Connection> connectList=this.cameraMonitorService.getConnection(jdbcList);
    	 if(null!=cityList&&cityList.size()>0){
    		 for(TreeVo treeVo:cityList){
    			 String voId=treeVo.getId();
				 List<TreeVo> treeList=new ArrayList<TreeVo>();
				//得到该节点下的所有摄像头
				 this.cameraMonitorService.loadChildCameraList(Long.parseLong(voId), treeList, connectList, groupMap);
				 //加上自己
				 treeList.add(treeVo);
				 int onlineNumber=0;
    			 int normalNumber=0;
    			 int gisNumber=0;
    			 int dignoseNumber=0;
				 if(null!=treeList&&treeList.size()>0){
					 for(TreeVo tree:treeList){
						 List<CaremaMonitorBo> caList=tree.getCameraList();
		    			 if(null!=caList&&caList.size()>0){
		    				 dignoseNumber+=caList.size();
		    				 for(CaremaMonitorBo c:caList){
		    					long instanceId= c.getInstanceId();
		    					//在线状态
		    					List<Map<String, Object>> availableList = this.resourceDetailInfoApi.getMetricByType(instanceId, "AvailabilityMetric",true);
		    					//诊断结果和诊断指标
		    					List<Map<String, Object>> performList = this.resourceDetailInfoApi.getMetricByType(instanceId, "PerformanceMetric",true);
		    					
		    					//得到完好数
		    					Map<String,String> normalMap= getMapFromList(performList,"status");
		    					if(normalMap.get("brightness").equals("NORMAL")&&
		    							normalMap.get("legibility").equals("NORMAL")&&
		    							normalMap.get("screenFreezed").equals("NORMAL")&&
		    							normalMap.get("colourCast").equals("NORMAL")&&
		    							normalMap.get("lostSignal").equals("NORMAL")&&
		    							normalMap.get("sightChange").equals("NORMAL")&&
		    							normalMap.get("PTZSpeed").equals("NORMAL")&&
		    							normalMap.get("keepOut").equals("NORMAL")&&
		    							normalMap.get("streakDisturb").equals("NORMAL")&&
		    							normalMap.get("PTZDegree").equals("NORMAL")&&
		    							normalMap.get("snowflakeDisturb").equals("NORMAL")){
		    							normalNumber++;
		        					}
		    					
		    					Map<String,String> StatusMap= getMapFromList(availableList,"status");
		    					String availability=StatusMap.get("availability");
		    					if(availability.equals("NORMAL")){
		    						onlineNumber++;
		    					}
		    					 if(!c.getGisx().equals("0.00000000")||!c.getGisy().equals("0.00000000")){
		    						 gisNumber++;
		        				 }
		    				 }
		    				
		    			 }
					 }
				 }
				 treeVo.setOnlineNumber(onlineNumber);
				 treeVo.setDignoseNumber(dignoseNumber);
				 treeVo.setGisNumber(gisNumber);
				 treeVo.setNormalNumber(normalNumber);
    	
    		 }
    	 }
    	 
    	 //从资源文件得到各个部分的得分权重
    	 Map<String,Double> rationMap=this.cameraMonitorService.getRationFromConfig("camera_config.xml");
    	 //上联统计的权重比
    	 double connectivityDouble=rationMap.get("connectivity");
    	 //在线统计的权重比
    	 double onlineDouble=rationMap.get("online");
    	 //完好率统计的权重比
    	 double completionDouble=rationMap.get("completion");
    	 //GIS采集率的权重比
    	 double xycompletionDouble=rationMap.get("xycompletion");
    	 //总计的考核指标数
    	 int totalPlanNum=0;
    	 //总计的上联数量,在线统计的已诊断总数和完好率统计的已诊断总数
    	 int totalUpperNum=0;
    	//总计的在线数
    	 int totalOnline=0;
    	 //总计的完好数
    	 int totalNormal=0;
    	 //总计的GIS数
    	 int totalGisNumber=0;
    	 
    	 scoreMap=new HashMap<String,String>();
    	 
	     for(TreeVo treeVo:cityList){
	    	 String id=treeVo.getId();
	    	 TableData tableData = new TableData();
	    	 StringBuilder value = new StringBuilder();
	    	//已诊断总数,上联数量
	    	 int dignoseNumber=treeVo.getDignoseNumber();
	    	//GIS数
	    	 int gisNumber=treeVo.getGisNumber();
	    	//在线数
	    	 int onlineNumber=treeVo.getOnlineNumber();
	    	 //完好数
	    	 int normalNumber=treeVo.getNormalNumber();
	    	 value.append(String.valueOf(cnt));
	    	  value.append("#--!!--#");
	    	  value.append(treeVo.getName());
	    	  value.append("#--!!--#");
	    	  //考核指标数
	    	  String planNumber=this.cameraMonitorService.getPlanNumber("camera_config.xml", "group", id, dignoseNumber);
	    	  //总计的考核指标数
	    	  totalPlanNum+=Integer.parseInt(planNumber);
	    	  value.append(planNumber);
	    	  value.append("#--!!--#");
	    	  //上联数量
	    	  value.append(String.valueOf(dignoseNumber));
	    	  totalUpperNum+=dignoseNumber;
	    	  value.append("#--!!--#");
	    	  //接入率	
	    	  int int4Plan=Integer.parseInt(planNumber);
	    	  String ratio="0%";
	    	  double score4Connectivity=0.00;
	    	  if(!planNumber.equals("0")){
	    		  if(dignoseNumber>0){
	    			  double f1 = new BigDecimal((float)dignoseNumber/int4Plan).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); 
		    		  score4Connectivity=f1*connectivityDouble;
		    		  f1=f1*100;
		    		  ratio=String.valueOf(f1)+"%";
	    		  }
	    		  
	    	  }
	    	  
	    	  value.append(ratio);
	    	  value.append("#--!!--#");
	    	  //已诊断总数
	    	  value.append(String.valueOf(dignoseNumber));
	    	  value.append("#--!!--#");
	    	  //在线数
	    	  totalOnline+=onlineNumber;
	    	  value.append(String.valueOf(onlineNumber));
	    	  value.append("#--!!--#");
	    	  //在线率
	    	  String onlineRatio="0%";
	    	  double score4Online=0.00;
	    	  if(dignoseNumber>0){
	    		  double f2 = new BigDecimal((float)onlineNumber/dignoseNumber).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	    		  score4Online=f2*onlineDouble;
	    		  f2=f2*100;
	    		  onlineRatio=String.valueOf(f2)+"%";
	    	  }
	    	  value.append(onlineRatio);
	    	  value.append("#--!!--#");
	    	//完好率统计的已诊断总数==在线数量
	    	  value.append(onlineNumber);
	    	  value.append("#--!!--#");
	    	  //完好数
	    	  totalNormal+=normalNumber;
	    	  value.append(normalNumber);
	    	  value.append("#--!!--#");
	    	  //图像完好率
	    	  String normalRatio="0%";
	    	  double score4Normal=0.00;
	    	  if(onlineNumber>0){
	    		  double f2 = new BigDecimal((float)normalNumber/onlineNumber).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
	    		  score4Normal=f2*completionDouble;
	    		  f2=f2*100;
	    		  normalRatio=String.valueOf(f2)+"%";
	    	  }
	    	  value.append(normalRatio);
	    	  value.append("#--!!--#");
	    	  //GIS数
	    	  value.append(String.valueOf(gisNumber));
	    	  totalGisNumber+=gisNumber;
	    	  value.append("#--!!--#");
	    	  //GIS率
	    	  String gisRatio="0%";
	    	  double score4GIS=0.00;
	    	  if(dignoseNumber>0){
	    		  double f2 = new BigDecimal((float)gisNumber/dignoseNumber).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	    		  score4GIS=f2*xycompletionDouble;
	    		  f2=f2*100;
	    		  gisRatio=String.valueOf(f2)+"%";
	    	  }
	    	  value.append(gisRatio);
	    	  value.append("#--!!--#");
	    	  //得分
	    	  double score=score4Connectivity+score4Online+score4Normal+score4GIS;
	    	  score=score*100;
	    	  BigDecimal big=new BigDecimal(score);
	    	  score=big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	    	 value.append(String.valueOf(score));
	    	  scoreMap.put(treeVo.getName(), String.valueOf(score*100));
	    	  tableData.setValue(checkSeparator(value));
	    	  tableDatas.add(tableData);
	    	  ++cnt;
	     }
	     //添加最后一行，统计的内容
	     TableData tableData4Total = new TableData();
	     StringBuilder value4Total = new StringBuilder();
	     value4Total.append(cnt);
	     value4Total.append("#--!!--#");
	     value4Total.append("总计");
	     value4Total.append("#--!!--#");
	     value4Total.append(String.valueOf(totalPlanNum));
	     value4Total.append("#--!!--#");
	     value4Total.append(String.valueOf(totalUpperNum));
	     value4Total.append("#--!!--#");
	     double totalUpperRation =0.00;
	     if(totalPlanNum>0){
	    	   totalUpperRation = new BigDecimal((float)totalUpperNum/totalPlanNum).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
	     }
	     double totalScore4Connectivity=totalUpperRation*connectivityDouble;
	     totalUpperRation=totalUpperRation*100;
	 	BigDecimal big1=new BigDecimal(totalUpperRation);
	 	totalUpperRation=big1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	     value4Total.append(totalUpperRation+"%");
	     value4Total.append("#--!!--#");
	     value4Total.append(String.valueOf(totalUpperNum));
	     value4Total.append("#--!!--#");
	     value4Total.append(String.valueOf(totalOnline));
	     value4Total.append("#--!!--#");
	     double totalOnlineRation =0.00;
	     if(totalUpperNum>0){
	    	 totalOnlineRation = new BigDecimal((float)totalOnline/totalUpperNum).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
	     }
	     double totalScore4Online=totalOnlineRation*onlineDouble;
	     totalOnlineRation=totalOnlineRation*100;
	     BigDecimal big2=new BigDecimal(totalOnlineRation);
	     totalOnlineRation=big2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	     value4Total.append(totalOnlineRation+"%");
	     value4Total.append("#--!!--#");
	     value4Total.append(String.valueOf(totalUpperNum));
	     value4Total.append("#--!!--#");
	     value4Total.append(String.valueOf(totalNormal));
	     value4Total.append("#--!!--#");
	     double totalNormalRation = 0.00;
	     if(totalUpperNum>0){
	    	 totalNormalRation = new BigDecimal((float)totalNormal/totalUpperNum).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
	     }
	     double totalScore4Normal=totalNormalRation*completionDouble;
	     totalNormalRation=totalNormalRation*100;
	     BigDecimal big3=new BigDecimal(totalNormalRation);
	     totalNormalRation=big3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	     value4Total.append(totalNormalRation+"%");
	     value4Total.append("#--!!--#");
	     value4Total.append(String.valueOf(totalGisNumber));
	     value4Total.append("#--!!--#");
	     double totalGisRation =0.00;
	     if(totalUpperNum>0){
	    	 totalGisRation = new BigDecimal((float)totalGisNumber/totalUpperNum).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	     }
	     double totalScore4Gis=totalGisRation*xycompletionDouble;
	     totalGisRation=totalGisRation*100;
	     BigDecimal big4=new BigDecimal(totalGisRation);
	     totalGisRation=big4.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	     value4Total.append(String.valueOf(totalGisRation));
	     value4Total.append("#--!!--#");
	     double totalScore=totalScore4Connectivity+totalScore4Online+totalScore4Normal+totalScore4Gis;
	     totalScore=totalScore*100;
	     BigDecimal big=new BigDecimal(totalScore);
	     totalScore=big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue(); 
	     value4Total.append(String.valueOf(totalScore));
	     tableData4Total.setValue(checkSeparator(value4Total));
	     tableDatas.add(tableData4Total);
	     }catch(Exception e){
	    	 e.printStackTrace();
	    	 logger.error("获取报表数据有误",e);
	     }
	     
	     return tableDatas;
	  
	    }
	 
	 
	 public List<Chart> getSumCharts()
	   {
		 List<Chart> charts = new ArrayList<Chart>();
		 Chart socreChart = new Chart();
		 socreChart.setName("考核得分");
		  socreChart.setType("1");
		  List<ChartData> chartDatas = new ArrayList<ChartData>();
		 if(null!=this.scoreMap){
			 for(Map.Entry<String, String> entry:this.scoreMap.entrySet()){
				  String cityName=entry.getKey();
				  String score=entry.getValue();
				  ChartData chartData = new ChartData();
				  chartData.setName(cityName);
				  chartData.setValue(score);
				  chartDatas.add(chartData);
			 }
		 }
		 socreChart.setChartData(chartDatas);
		 charts.add(socreChart);
		 
		 return charts;
	 }
	 
	 
	 private  Map<String, String> getMapFromList(List<Map<String, Object>> queryList,String valueName) {
			Map<String, String> map = new HashMap<String, String>();
			for (Map<String, Object> objectMap : queryList) {
				String key = objectMap.get("id").toString();
				String value = objectMap.get(valueName).toString();
				map.put(key, value);
			}
			return map;
		}
	 
	 
	 public static void main(String[] args){
		 int a=6;
		 int b=9;
		 double f1 = new BigDecimal((float)a/b).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); 
		 System.out.println(Double.parseDouble("0.25"));
	 }
	 
	 
	 private long getOnlineNumber(List<CaremaMonitorBo> boList){
		 long number=0;
		 if(null!=boList){
			 for(CaremaMonitorBo caremaMonitorBo:boList){
				 if(caremaMonitorBo.getAvailability().equals("NORMAL")){
					 number++;
					 continue;
				 }
			 }
		 }
		 return number;
	 }
	 
	 
	 private long geGisNumber(List<CaremaMonitorBo> boList){
		 long number=0;
		 if(null!=boList){
			 for(CaremaMonitorBo caremaMonitorBo:boList){
				 if(!caremaMonitorBo.getGisx().equals("0.00000000")||!caremaMonitorBo.getGisy().equals("0.00000000")){
					 number++;
					 continue;
				 }
			 }
		 }
		 return number;
	 }
	 
	 
	 private long getAbnormalNumber(List<CaremaMonitorBo> boList){
		 long number=0;
		 if(null!=boList){
			 for(CaremaMonitorBo caremaMonitorBo:boList){
				 if(caremaMonitorBo.getBrightness().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getPTZDegree().equals("SERIOUS")){
					 number++;
					 continue;
				 } if(caremaMonitorBo.getColourCast().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getSightChange().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getKeepOut().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getLegibility().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getLostSignal().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getPtzSpeed().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getScreenFreezed().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getSnowflakeDisturb().equals("SERIOUS")){
					 number++;
					 continue;
				 }
				 if(caremaMonitorBo.getStreakDisturb().equals("SERIOUS")){
					 number++;
					 continue;
				 }
			 }
		 }

		 return number;
	 }
	

}
