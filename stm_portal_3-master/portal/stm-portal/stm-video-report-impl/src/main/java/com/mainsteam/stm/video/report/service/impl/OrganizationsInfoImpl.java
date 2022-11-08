package com.mainsteam.stm.video.report.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.dom4j.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Value;

import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2;
import com.mainsteam.stm.alarm.query.AlarmEventQueryDetail;
import com.mainsteam.stm.alarm.query.AlarmEventQuery2.OrderField;
import com.mainsteam.stm.camera.api.ICameraMonitorService;
import com.mainsteam.stm.camera.bo.CaremaMonitorBo;
import com.mainsteam.stm.camera.bo.JDBCVo;
import com.mainsteam.stm.camera.bo.TreeVo;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.convert.BaseDataConvert;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi; 
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.util.XmlUtil;
import com.mainsteam.stm.video.report.api.OrganizationsInfoApi;
import com.mainsteam.stm.video.report.bo.Organizayions;
import com.mainsteam.stm.video.report.bo.VedioOnlineRate;
import com.mainsteam.stm.video.report.bo.reportTop;
import com.mainsteam.stm.video.report.bo.zTreeVo;
import com.mainsteam.stm.video.report.dao.OrganizationsInfoDao;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class OrganizationsInfoImpl extends BaseDataConvert implements OrganizationsInfoApi{
	private final Log logger = LogFactory.getLog(OrganizationsInfoImpl.class);

	@Resource
	private OrganizationsInfoDao organizationsInfoDao;
	@Resource(name = "resourceInstanceService")
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private AlarmEventService resourceEventService;
	@Resource
	private MetricDataService metricDataService;
	
	 @Resource
	  private InstanceStateService instanceStateService;
	
	@Resource
	private MetricStateService metricStateService;
	private static final String DOWLOAD_PATH = "common" + File.separator
			+ "classes" + File.separator + "config";
	private static final String DOWLOAD_FILENAME = "camera_config.xml";
	private static final String CATALINA_HOME = "catalina.home";
	
	 @Resource
	 private  ICameraMonitorService	cameraMonitorService; 
	
	 @Resource
	 private IResourceDetailInfoApi resourceDetailInfoApi;

    @Resource
    private DiscoverPropService discoverPropService;
	 
	 private Map<String,String> scoreMap;
	
	
	
	@Override
	public List<Organizayions> getAllOrg() throws InstancelibException {
        return organizationsInfoDao.getAllOrg();
	}

    @Override
    public List<ResourceInstance> queryAllOrg() throws InstancelibException, MetricExecutorException {
        List<ResourceInstance> eastWitPlatform = resourceInstanceService.getResourceInstanceByResourceId("EastWitPlatform");
        return eastWitPlatform;
    }

    @Override
	public List<TableData> getInspectionReport(zTreeVo zTree,zTreeVo rootZtree, String startTime, String endTime) {
        logger.info("-----------开始计算报表数据----------");
        List<TableData> tableDatas = new ArrayList<TableData>();
        ILoginUser user = BaseAction.getLoginUser();

        try{
            int cnt = 1;
            List<TreeVo> cityList = new ArrayList<TreeVo>(); //获得子节点
            List<zTreeVo> children = zTree.getChildren();
            TreeVo tv = null;
            List<String> orgList = null;
            Map<String,List<String>> orgMap = new HashMap<>(); //根组织对应的子组织ID集合，包含根组织ID
            if(children != null && children.size() > 0){
                for(zTreeVo zv : children){
                    orgList = new ArrayList<>();
                    tv = new TreeVo();
                    tv.setId(zv.getId());
                    tv.setName(zv.getName());
                    cityList.add(tv);
                    this.bulidOrgId(orgList,zv);
                    orgMap.put(zv.getId(),orgList);
                }
            }else{
                orgList = new ArrayList<>();
                tv = new TreeVo();
                tv.setId(zTree.getId());
                tv.setName(zTree.getName());
                cityList.add(tv);
                orgList.add(zTree.getId());
                orgMap.put(zTree.getId(),orgList);
            }
            //得到选中的节点和下级节点
            //List<TreeVo> cityList=this.cameraMonitorService.getCameraListByParentId(instancesId, jdbcList,groupMap);
            List<ResourceInstance> eastWitCamera = resourceInstanceService.getResourceInstanceByResourceId("EastWitCamera");
            //按照根组织过滤摄像头
            DiscoverProp ip = discoverPropService.getPropByInstanceAndKey(Long.parseLong(rootZtree.getId()), "IP");
            String s = ip.getValues()[0];
            List<ResourceInstance> tempCameras = new ArrayList<>(eastWitCamera.size() / 2);
            for(ResourceInstance ri : eastWitCamera){
                DiscoverProp ip1 = discoverPropService.getPropByInstanceAndKey(ri.getId(), "IP");
                String s1 = ip1.getValues()[0];
                if(s.equals(s1)){
                    tempCameras.add(ri);
                }
            }
            eastWitCamera = tempCameras;

             List<Long> insS = new ArrayList<Long>(eastWitCamera.size());
             List<MetricData> metricInfoDatas = new ArrayList<MetricData>();
             for(int i = 0 ; i < eastWitCamera.size(); ++i){
                 insS.add(eastWitCamera.get(i).getId());
             }
             //分段查询，分解成每次900个参数查询
             int[] resolve = this.resolve(eastWitCamera);
             String[] metS = {"groupID"};
             int start = 0;
             for(int i = 0; i < resolve.length; ++i){
                int preInt = start;
                int nextInt = start + resolve[i];
                List<Long> longs = insS.subList(preInt, nextInt);
                 long[] tempIds = new long[longs.size()];
                 for(int j = 0; j < longs.size(); ++j){
                     tempIds[j] = longs.get(j);
                 }
                 if(tempIds.length > 0){
                     List<MetricData> tempList = metricDataService.getMetricInfoDatas(tempIds, metS);
                     metricInfoDatas.addAll(tempList);
                 }
                 start = nextInt;
             }

             Map<String, List<Long>> insMap = new HashMap<>();//对应根组织所属摄像头实例ID集合
             for(MetricData md : metricInfoDatas){
                 String groupId = md.getData()[0];
                 long resourceInstanceId = md.getResourceInstanceId();
                 for (Map.Entry<String, List<String>> entry : orgMap.entrySet()) {
                     String key = entry.getKey();
                     List<Long> longs = insMap.get(key);
                     if(longs == null){
                         longs = new ArrayList<>();
                         insMap.put(key,longs);
                     }
                     List<String> value = entry.getValue();
                     if(value.contains(groupId)){
                         longs.add(resourceInstanceId);
                     }
                 }
             }

             //List<Connection> connectList=this.cameraMonitorService.getConnection(jdbcList);
             if(null!=cityList&&cityList.size()>0){
                 for(TreeVo treeVo:cityList){
                     String voId=treeVo.getId();
                     //List<TreeVo> treeList=new ArrayList<TreeVo>();
                    //得到该节点下的所有摄像头
                    // this.cameraMonitorService.loadChildCameraList(Long.parseLong(voId), treeList, connectList, groupMap);
                     List<Long> longs = insMap.get(voId);   //摄像头实例ID集合
                     //加上自己
                     //treeList.add(treeVo);
                     int gisNumber=0;
                     int dignoseNumber=0;
                     if(longs != null && longs.size() > 0){
                         dignoseNumber += longs.size(); //总数
                         //获取gisX、gisY指标值
                         String[] metGisXAndGisY = {"gisX","gisY"};
                         for(long id : longs){
                             List<MetricData> metricInfoDatas1 = metricDataService.getMetricInfoDatas(id, metGisXAndGisY);
                             MetricData m1 = metricInfoDatas1.get(0);
                             MetricData m2 = metricInfoDatas1.get(1);
                             String gisX = "";
                             String gisY = "";
                             if("gisX".equals(m1.getMetricId())){
                                 gisX = m1.getData()[0];
                                 gisY = m2.getData()[0];
                             }else{
                                 gisX = m2.getData()[0];
                                 gisY = m1.getData()[0];
                             }
                             if(!"0.0".equals(gisX) && !("0.0").equals(gisY)){
                                 gisNumber++;
                             }
                         }
                     }
                    /* if(null != treeList&&treeList.size()>0){
                         for(TreeVo tree:treeList){
                             List<CaremaMonitorBo> caList=tree.getCameraList();
                             if(null!=caList&&caList.size()>0){
                                 dignoseNumber+=caList.size();

                                 for(CaremaMonitorBo c:caList){
                                     if(!c.getGisx().equals("0.0") && !c.getGisy().equals("0.0")){
                                         gisNumber++;
                                     }
                                 }
                             }
                         }
                     }*/
                     treeVo.setDignoseNumber(dignoseNumber);
                     treeVo.setGisNumber(gisNumber);
                 }
                 this.getRate(zTree, startTime, endTime, cityList, insMap, orgMap, eastWitCamera);
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
                  String ratio="0.0%";
                  double score4Connectivity=0.00;
                  if(!planNumber.equals("0")){
                      if(dignoseNumber>0){
                          double f1 = new BigDecimal((float)dignoseNumber/int4Plan).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                          score4Connectivity=f1*connectivityDouble;
                          f1=f1*100;
                          String temp = String.format("%.1f", f1);
                          ratio=temp+"%";
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
                  String onlineRatio="0.0%";
                  double score4Online=0.00;
                  if(dignoseNumber>0){
                      double f2 = new BigDecimal((float)onlineNumber/dignoseNumber).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                      score4Online=f2*onlineDouble;
                      f2=f2*100;
                      String temp = String.format("%.1f", f2);
                      onlineRatio=temp+"%";
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
                  String normalRatio="0.0%";
                  double score4Normal=0.00;
                  if(onlineNumber>0){
                      double f2 = new BigDecimal((float)normalNumber/onlineNumber).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                      score4Normal=f2*completionDouble;
                      f2=f2*100;
                      String temp = String.format("%.1f", f2);
                      normalRatio=temp+"%";
                  }
                  value.append(normalRatio);
                  value.append("#--!!--#");
                  //GIS数
                  value.append(String.valueOf(gisNumber));
                  totalGisNumber+=gisNumber;
                  value.append("#--!!--#");
                  //GIS率
                  String gisRatio="0.0%";
                  double score4GIS=0.00;
                  if(dignoseNumber>0){
                      double f2 = new BigDecimal((float)gisNumber/dignoseNumber).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                      score4GIS=f2*xycompletionDouble;
                      f2=f2*100;
                      String temp = String.format("%.1f", f2);
                      gisRatio=temp+"%";
                  }
                  value.append(gisRatio);
                  value.append("#--!!--#");
                  //得分
                  double score=score4Connectivity+score4Online+score4Normal+score4GIS;
                  score=score*100;
                  BigDecimal big=new BigDecimal(score);
                  score=big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                 value.append(String.format("%.1f", score));
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
            value4Total.append(String.format("%.1f", totalUpperRation)+"%");
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
            value4Total.append(String.format("%.1f", totalOnlineRation)+"%");
            value4Total.append("#--!!--#");
            value4Total.append(String.valueOf(totalOnline));
            value4Total.append("#--!!--#");
            value4Total.append(String.valueOf(totalNormal));
            value4Total.append("#--!!--#");
            double totalNormalRation = 0.00;
            if(totalOnline>0){
             totalNormalRation = new BigDecimal((float)totalNormal/totalOnline).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            double totalScore4Normal=totalNormalRation*completionDouble;
            totalNormalRation=totalNormalRation*100;
            BigDecimal big3=new BigDecimal(totalNormalRation);
            totalNormalRation=big3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            value4Total.append(String.format("%.1f", totalNormalRation)+"%");
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
            value4Total.append(String.format("%.1f", totalGisRation));
            value4Total.append("#--!!--#");
            double totalScore=totalScore4Connectivity+totalScore4Online+totalScore4Normal+totalScore4Gis;
            totalScore=totalScore*100;
            BigDecimal big=new BigDecimal(totalScore);
            totalScore=big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            value4Total.append(String.format("%.1f", totalScore));
            tableData4Total.setValue(checkSeparator(value4Total));
            tableDatas.add(tableData4Total);
        }catch(Exception e){
            logger.error("获取报表数据有误",e);
        }

        return tableDatas;

	}

	private void bulidOrgId( List<String> orgList, zTreeVo zTreeVo){
        orgList.add(zTreeVo.getId());
        List<zTreeVo> children = zTreeVo.getChildren();
        if(children != null && children.size() > 0){
           for(zTreeVo zv : children){
               this.bulidOrgId(orgList,zv);
           }
        }
    }

	@SuppressWarnings("deprecation")
	@Override
	public List<VedioOnlineRate> getVedioOnlineRate(zTreeVo tree,zTreeVo rootZtree, String startTime, String endTime) {
        List<TreeVo> cityList = new ArrayList<TreeVo>(); //获得子节点
        List<zTreeVo> childrens = tree.getChildren();
        TreeVo tv = null;
        List<String> orgList = null;
        Map<String,List<String>> orgMaps = new HashMap<>(); //根组织对应的子组织ID集合，包含根组织ID
        if(childrens != null && childrens.size() > 0){
            for(zTreeVo zv : childrens){
                orgList = new ArrayList<>();
                tv = new TreeVo();
                tv.setId(zv.getId());
                tv.setName(zv.getName());
                cityList.add(tv);
                this.bulidOrgId(orgList,zv);
                orgMaps.put(zv.getId(),orgList);
            }
        }else{
            orgList = new ArrayList<>();
            tv = new TreeVo();
            tv.setId(tree.getId());
            tv.setName(tree.getName());
            cityList.add(tv);
            orgList.add(tree.getId());
            orgMaps.put(tree.getId(),orgList);
        }
        //得到选中的节点和下级节点
        List<ResourceInstance> eastWitCamera = null;
        try {
            eastWitCamera = resourceInstanceService.getResourceInstanceByResourceId("EastWitCamera");

            //按照根组织过滤摄像头
            DiscoverProp ip = discoverPropService.getPropByInstanceAndKey(Long.parseLong(rootZtree.getId()), "IP");
            String s = ip.getValues()[0];
            List<ResourceInstance> tempCameras = new ArrayList<>(eastWitCamera.size() / 2);
            for(ResourceInstance ri : eastWitCamera){
                DiscoverProp ip1 = discoverPropService.getPropByInstanceAndKey(ri.getId(), "IP");
                String s1 = ip1.getValues()[0];
                if(s.equals(s1)){
                    tempCameras.add(ri);
                }
            }
            eastWitCamera = tempCameras;
        } catch (InstancelibException e) {
            logger.error("this is error##",e);
        }


        List<Long> insS = new ArrayList<Long>(eastWitCamera.size());
        List<MetricData> metricInfoDatas = new ArrayList<MetricData>();
        for(int i = 0 ; i < eastWitCamera.size(); ++i){
            insS.add(eastWitCamera.get(i).getId());
        }
        //分段查询，分解成每次900个参数查询
        int[] resolve = this.resolve(eastWitCamera);
        String[] metS = {"groupID"};
        int start = 0;
        for(int i = 0; i < resolve.length; ++i){
            int preInt = start;
            int nextInt = start + resolve[i];
            List<Long> longs = insS.subList(preInt, nextInt);
            long[] tempIds = new long[longs.size()];
            for(int j = 0; j < longs.size(); ++j){
                tempIds[j] = longs.get(j);
            }
            List<MetricData> tempList = metricDataService.getMetricInfoDatas(tempIds, metS);
            metricInfoDatas.addAll(tempList);
            start = nextInt;
        }

        Map<String, List<Long>> insMaps = new HashMap<>();//对应根组织所属摄像头实例ID集合
        for(MetricData md : metricInfoDatas){
            String groupId = md.getData()[0];
            long resourceInstanceId = md.getResourceInstanceId();
            for (Map.Entry<String, List<String>> entry : orgMaps.entrySet()) {
                String key = entry.getKey();
                List<Long> longs = insMaps.get(key);
                if(longs == null){
                    longs = new ArrayList<>();
                    insMaps.put(key,longs);
                }
                List<String> value = entry.getValue();
                if(value.contains(groupId)){
                    longs.add(resourceInstanceId);
                }
            }
        }

		List<VedioOnlineRate> voList = new ArrayList<VedioOnlineRate>();
		List<ResourceInstance> parentInstanceByCategoryId = eastWitCamera;

		try {
			//查询所有摄像头
			//parentInstanceByCategoryId = resourceInstanceService.getParentInstanceByCategoryId("CameraPlatform");

			//查询摄像头所属组织
			List<zTreeVo> children = tree.getChildren();
			if(children != null && children.size() > 0){
				for(zTreeVo zt : children){
					VedioOnlineRate vl = new VedioOnlineRate();
					vl.setId(zt.getId());
					vl.setName(zt.getName());
					/*vl.getOrgList().add(zt.getId());
					if(zt.getChildren() != null && zt.getChildren().size() > 0){
						this.getChildOrg(zt,vl);
					}*/
                    List<String> strings = orgMaps.get(zt.getId());
                    vl.getOrgList().addAll(strings);
                    List<Long> longs = insMaps.get(zt.getId());
                    List<String> insList = new ArrayList<>(longs.size());
                    for(Long insId : longs){
                        insList.add(String.valueOf(insId));
                    }
                    vl.getInsList().addAll(insList);
                    vl.setVedioSum(longs.size());
					voList.add(vl);

				}

				/*if(parentInstanceByCategoryId != null && parentInstanceByCategoryId.size() > 0){
					for(ResourceInstance re : parentInstanceByCategoryId){
						String[] discoverPropBykey = re.getDiscoverPropBykey("groupId");
						flag:for(VedioOnlineRate v : voList){
								List<String> orgList = v.getOrgList();
								for(String id : orgList){
									if(discoverPropBykey[0].equals(id)){
										int vedioSum = v.getVedioSum();
										v.setVedioSum(vedioSum + 1);
										v.getInsList().add(String.valueOf(re.getId()));
										break flag;
									}
								}
							}
					}
				}*/
			}else{
				//当前组织没有子组织
				VedioOnlineRate vl = new VedioOnlineRate();
				vl.setId(tree.getId());
				vl.setName(tree.getName());
                List<Long> longs = insMaps.get(tree.getId());
                List<String> insList = new ArrayList<>(longs.size());
                for(Long insId : longs){
                    insList.add(String.valueOf(insId));
                }
                vl.getInsList().addAll(insList);
                vl.setVedioSum(longs.size());
				voList.add(vl);

				/*if(parentInstanceByCategoryId != null && parentInstanceByCategoryId.size() > 0){
					for(ResourceInstance re : parentInstanceByCategoryId){
						String[] discoverPropBykey = re.getDiscoverPropBykey("groupId");
						if(discoverPropBykey[0].equals(vl.getId())){
							int vedioSum = vl.getVedioSum();
							vl.setVedioSum(vedioSum + 1);
							vl.getInsList().add(String.valueOf(re.getId()));
						}
					}
				}*/
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> insMap = null;
			//通过告警查询对应组织下摄像头状态
			for(VedioOnlineRate vl : voList){
				List<String> insList = vl.getInsList();
				if(vl.getVedioSum() == 0){
					continue;
				}
				List<AlarmEventQueryDetail> filters = new ArrayList<AlarmEventQueryDetail>();
				AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
				try {
					Date endDate = format.parse(endTime);
					endDate.setHours(23);
					endDate.setMinutes(59);
					endDate.setSeconds(59);
					detail.setEnd(endDate);
				} catch (ParseException e) {
					logger.error("this is error##",e);
				}
				detail.setSysID(SysModuleEnum.MONITOR);
				detail.setSourceIDes(insList);
				detail.setRecovered(false);
				filters.add(detail);

				AlarmEventQuery2 paramAlarmEventQuery2 = new AlarmEventQuery2();
				paramAlarmEventQuery2.setOrderASC(false);
				OrderField[] orderFieldes = {AlarmEventQuery2.OrderField.COLLECTION_TIME};
				paramAlarmEventQuery2.setOrderFieldes(orderFieldes);
				paramAlarmEventQuery2.setFilters(filters);
				Page<AlarmEvent, AlarmEventQuery2> page = resourceEventService.queryAlarmEvent(paramAlarmEventQuery2, 0, Integer.MAX_VALUE);


				insMap = new HashMap<String, Object>();
				Map<String, Boolean> onMap = new HashMap<String, Boolean>();
				for(String id : insList){
					insMap.put(id, null);
					onMap.put(id, true);
				}

				int badSum = 0;
				for(String insId : insList){
					if (null != page.getRows() && page.getRows().size() > 0) {
						for (AlarmEvent re : page.getRows()) {
							if(insMap.get(re.getSourceID()) == null){
								insMap.put(re.getSourceID(), re.getCollectionTime());
							}

							if(insId.equals(re.getSourceID()) && re.getCollectionTime().compareTo((Date)insMap.get(re.getSourceID())) == 0){
								if(re.getLevel() == InstanceStateEnum.CRITICAL){
									onMap.put(insId, false);
									break;
								}
							}
						}
					}else{
						break;
					}
				}
				for(Map.Entry<String, Boolean> entry : onMap.entrySet()){
					if(entry.getValue() == false){
						++ badSum;
					}
				}
				Integer vedioSum = vl.getVedioSum();		//总数
				vl.setOnlineQuality(vedioSum - badSum);
				Integer onlineQuality = vl.getOnlineQuality();	//在线数量
				double rate = onlineQuality.doubleValue() / vedioSum.doubleValue() * 100;
				String onlineRate=String.format("%.1f", rate);		//在线率
				vl.setOnlineRate(onlineRate + "%");
				vl.setScore(onlineRate);
			}
		} catch (Exception e) {
			logger.error("this is error##",e);
			return null;
		}
		return voList;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<VedioOnlineRate> getIntactRate(zTreeVo tree,zTreeVo rootZtree, String startTime,
			String endTime) {
        List<TreeVo> cityList = new ArrayList<TreeVo>(); //获得子节点
        List<zTreeVo> childrens = tree.getChildren();
        TreeVo tv = null;
        List<String> orgList = null;
        Map<String,List<String>> orgMaps = new HashMap<>(); //根组织对应的子组织ID集合，包含根组织ID
        if(childrens != null && childrens.size() > 0){
            for(zTreeVo zv : childrens){
                orgList = new ArrayList<>();
                tv = new TreeVo();
                tv.setId(zv.getId());
                tv.setName(zv.getName());
                cityList.add(tv);
                this.bulidOrgId(orgList,zv);
                orgMaps.put(zv.getId(),orgList);
            }
        }else{
            orgList = new ArrayList<>();
            tv = new TreeVo();
            tv.setId(tree.getId());
            tv.setName(tree.getName());
            cityList.add(tv);
            orgList.add(tree.getId());
            orgMaps.put(tree.getId(),orgList);
        }
        //得到选中的节点和下级节点
        List<ResourceInstance> eastWitCamera = null;
        try {
            eastWitCamera = resourceInstanceService.getResourceInstanceByResourceId("EastWitCamera");
            //按照根组织过滤摄像头
            DiscoverProp ip = discoverPropService.getPropByInstanceAndKey(Long.parseLong(rootZtree.getId()), "IP");
            String s = ip.getValues()[0];
            List<ResourceInstance> tempCameras = new ArrayList<>(eastWitCamera.size() / 2);
            for(ResourceInstance ri : eastWitCamera){
                DiscoverProp ip1 = discoverPropService.getPropByInstanceAndKey(ri.getId(), "IP");
                String s1 = ip1.getValues()[0];
                if(s.equals(s1)){
                    tempCameras.add(ri);
                }
            }
            eastWitCamera = tempCameras;
        } catch (InstancelibException e) {
            logger.error("this is error##",e);
        }
        List<Long> insS = new ArrayList<Long>(eastWitCamera.size());
        List<MetricData> metricInfoDatas = new ArrayList<MetricData>();
        for(int i = 0 ; i < eastWitCamera.size(); ++i){
            insS.add(eastWitCamera.get(i).getId());
        }
        //分段查询，分解成每次900个参数查询
        int[] resolve = this.resolve(eastWitCamera);
        String[] metS = {"groupID"};
        int start = 0;
        for(int i = 0; i < resolve.length; ++i){
            int preInt = start;
            int nextInt = start + resolve[i];
            List<Long> longs = insS.subList(preInt, nextInt);
            long[] tempIds = new long[longs.size()];
            for(int j = 0; j < longs.size(); ++j){
                tempIds[j] = longs.get(j);
            }
            List<MetricData> tempList = metricDataService.getMetricInfoDatas(tempIds, metS);
            metricInfoDatas.addAll(tempList);
            start = nextInt;
        }

        Map<String, List<Long>> insMaps = new HashMap<>();//对应根组织所属摄像头实例ID集合
        for(MetricData md : metricInfoDatas){
            String groupId = md.getData()[0];
            long resourceInstanceId = md.getResourceInstanceId();
            for (Map.Entry<String, List<String>> entry : orgMaps.entrySet()) {
                String key = entry.getKey();
                List<Long> longs = insMaps.get(key);
                if(longs == null){
                    longs = new ArrayList<>();
                    insMaps.put(key,longs);
                }
                List<String> value = entry.getValue();
                if(value.contains(groupId)){
                    longs.add(resourceInstanceId);
                }
            }
        }



        List<VedioOnlineRate> voList = new ArrayList<VedioOnlineRate>();
		List<ResourceInstance> parentInstanceByCategoryId = eastWitCamera;

        //查询摄像头所属组织
        List<zTreeVo> children = tree.getChildren();
        if(children != null && children.size() > 0){
            for(zTreeVo zt : children){
                VedioOnlineRate vl = new VedioOnlineRate();
                vl.setId(zt.getId());
                vl.setName(zt.getName());
                List<String> strings = orgMaps.get(zt.getId());
                vl.getOrgList().addAll(strings);
                List<Long> longs = insMaps.get(zt.getId());
                List<String> insList = new ArrayList<>(longs.size());
                for(Long insId : longs){
                    insList.add(String.valueOf(insId));
                }
                vl.getInsList().addAll(insList);
                vl.setVedioSum(longs.size());
                voList.add(vl);
            }
        }else{
            //当前组织没有子组织
            VedioOnlineRate vl = new VedioOnlineRate();
            vl.setId(tree.getId());
            vl.setName(tree.getName());
            List<Long> longs = insMaps.get(tree.getId());
            List<String> insList = new ArrayList<>(longs.size());
            for(Long insId : longs){
                insList.add(String.valueOf(insId));
            }
            vl.getInsList().addAll(insList);
            vl.setVedioSum(longs.size());
            voList.add(vl);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> insMap = null;
        //通过告警查询对应组织下摄像头状态
        for(VedioOnlineRate vl : voList){
            List<String> insList = vl.getInsList();
            if(vl.getVedioSum() == 0){
                continue;
            }
            List<AlarmEventQueryDetail> filters = new ArrayList<AlarmEventQueryDetail>();
            AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
            try {
                Date endDate = format.parse(endTime);
                endDate.setHours(23);
                endDate.setMinutes(59);
                endDate.setSeconds(59);
                detail.setEnd(endDate);
            } catch (ParseException e) {
                logger.error("this is error##",e);
            }
            detail.setSysID(SysModuleEnum.MONITOR);
            detail.setSourceIDes(insList);
            detail.setRecovered(false);
            filters.add(detail);

            AlarmEventQuery2 paramAlarmEventQuery2 = new AlarmEventQuery2();
            paramAlarmEventQuery2.setOrderASC(false);
            OrderField[] orderFieldes = {AlarmEventQuery2.OrderField.COLLECTION_TIME};
            paramAlarmEventQuery2.setOrderFieldes(orderFieldes);
            paramAlarmEventQuery2.setFilters(filters);
            Page<AlarmEvent, AlarmEventQuery2> page = resourceEventService.queryAlarmEvent(paramAlarmEventQuery2, 0, Integer.MAX_VALUE);

            insMap = new HashMap<String, Object>();
            Map<String, Boolean> badMap = new HashMap<String, Boolean>();
            Map<String, Boolean> onMap = new HashMap<String, Boolean>();
            for(String id : insList){
                insMap.put(id, null);
                badMap.put(id, false);
                onMap.put(id, true);
            }

            int badSum = 0;
            int badIntactSum = 0;
            for(String insId : insList){
                if (null != page.getRows() && page.getRows().size() > 0) {
                    for (AlarmEvent re : page.getRows()) {
                        if(insMap.get(re.getSourceID()) == null){
                            insMap.put(re.getSourceID(), re.getCollectionTime());
                        }

                        if(insId.equals(re.getSourceID()) && re.getCollectionTime().compareTo((Date)insMap.get(re.getSourceID())) == 0){
                            if(re.getLevel() == InstanceStateEnum.CRITICAL){
                                onMap.put(insId, false);
                            }
                            badMap.put(insId, true);
                        }
                    }
                }else{
                    break;
                }
            }
            for(Map.Entry<String, Boolean> entry : onMap.entrySet()){
                if(entry.getValue() == false){
                    ++ badSum;
                }else if(badMap.get(entry.getKey()) == true){
                    ++ badIntactSum;
                }
            }

            Integer vedioSum = vl.getVedioSum();		//总数

            vl.setOnlineQuality(vedioSum - badSum);
            Integer onlineQuality = vl.getOnlineQuality();	//在线数量
            Integer intactQuality = onlineQuality - badIntactSum;	//完好数量
            double rate = 0.0;
            if(vedioSum != 0){
                rate = onlineQuality.doubleValue() / vedioSum.doubleValue() * 100;
            }
            String onlineRate=String.format("%.1f", rate);		//在线率
            double inRate = 0.0;
            if(onlineQuality != 0){
                inRate = intactQuality.doubleValue() / onlineQuality.doubleValue() * 100;
            }
            String intactRate = String.format("%.1f", inRate);	//完好率
            if(inRate == 0.0d){
                intactRate = "0.0";
            }
            vl.setOnlineRate(onlineRate + "%");
            vl.setIntactRate(intactRate + "%");
            vl.setScore(intactRate);
            vl.setIntactQuality(intactQuality);
            vl.setOnlineQuality(onlineQuality);
        }
		return voList;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<VedioOnlineRate> getFaultReport(zTreeVo tree,zTreeVo rootZtree, String startTime,
			String endTime) {
        List<TreeVo> cityList = new ArrayList<TreeVo>(); //获得子节点
        List<zTreeVo> childrens = tree.getChildren();
        TreeVo tv = null;
        List<String> orgList = null;
        Map<String,List<String>> orgMaps = new HashMap<>(); //根组织对应的子组织ID集合，包含根组织ID
        if(childrens != null && childrens.size() > 0){
            for(zTreeVo zv : childrens){
                orgList = new ArrayList<>();
                tv = new TreeVo();
                tv.setId(zv.getId());
                tv.setName(zv.getName());
                cityList.add(tv);
                this.bulidOrgId(orgList,zv);
                orgMaps.put(zv.getId(),orgList);
            }
        }else{
            orgList = new ArrayList<>();
            tv = new TreeVo();
            tv.setId(tree.getId());
            tv.setName(tree.getName());
            cityList.add(tv);
            orgList.add(tree.getId());
            orgMaps.put(tree.getId(),orgList);
        }
        //得到选中的节点和下级节点
        List<ResourceInstance> eastWitCamera = null;
        try {
            eastWitCamera = resourceInstanceService.getResourceInstanceByResourceId("EastWitCamera");
            //按照根组织过滤摄像头
            DiscoverProp ip = discoverPropService.getPropByInstanceAndKey(Long.parseLong(rootZtree.getId()), "IP");
            String s = ip.getValues()[0];
            List<ResourceInstance> tempCameras = new ArrayList<>(eastWitCamera.size() / 2);
            for(ResourceInstance ri : eastWitCamera){
                DiscoverProp ip1 = discoverPropService.getPropByInstanceAndKey(ri.getId(), "IP");
                String s1 = ip1.getValues()[0];
                if(s.equals(s1)){
                    tempCameras.add(ri);
                }
            }
            eastWitCamera = tempCameras;
        } catch (InstancelibException e) {
            logger.error("this is error##",e);
        }
        List<Long> insS = new ArrayList<Long>(eastWitCamera.size());
        List<MetricData> metricInfoDatas = new ArrayList<MetricData>();
        for(int i = 0 ; i < eastWitCamera.size(); ++i){
            insS.add(eastWitCamera.get(i).getId());
        }
        //分段查询，分解成每次900个参数查询
        int[] resolve = this.resolve(eastWitCamera);
        String[] metS = {"groupID"};
        int start = 0;
        for(int i = 0; i < resolve.length; ++i){
            int preInt = start;
            int nextInt = start + resolve[i];
            List<Long> longs = insS.subList(preInt, nextInt);
            long[] tempIds = new long[longs.size()];
            for(int j = 0; j < longs.size(); ++j){
                tempIds[j] = longs.get(j);
            }
            List<MetricData> tempList = metricDataService.getMetricInfoDatas(tempIds, metS);
            metricInfoDatas.addAll(tempList);
            start = nextInt;
        }

        Map<String, List<Long>> insMaps = new HashMap<>();//对应根组织所属摄像头实例ID集合
        for(MetricData md : metricInfoDatas){
            String groupId = md.getData()[0];
            long resourceInstanceId = md.getResourceInstanceId();
            for (Map.Entry<String, List<String>> entry : orgMaps.entrySet()) {
                String key = entry.getKey();
                List<Long> longs = insMaps.get(key);
                if(longs == null){
                    longs = new ArrayList<>();
                    insMaps.put(key,longs);
                }
                List<String> value = entry.getValue();
                if(value.contains(groupId)){
                    longs.add(resourceInstanceId);
                }
            }
        }


		List<VedioOnlineRate> voList = new ArrayList<VedioOnlineRate>();
		List<ResourceInstance> parentInstanceByCategoryId = eastWitCamera;

        //查询摄像头所属组织
        List<zTreeVo> children = tree.getChildren();
        if(children != null && children.size() > 0){
            for(zTreeVo zt : children){
                VedioOnlineRate vl = new VedioOnlineRate();
                vl.setId(zt.getId());
                vl.setName(zt.getName());
                List<String> strings = orgMaps.get(zt.getId());
                vl.getOrgList().addAll(strings);
                List<Long> longs = insMaps.get(zt.getId());
                List<String> insList = new ArrayList<>(longs.size());
                for(Long insId : longs){
                    insList.add(String.valueOf(insId));
                }
                vl.getInsList().addAll(insList);
                vl.setVedioSum(longs.size());
                voList.add(vl);
            }
        }else{
            //当前组织没有子组织
            VedioOnlineRate vl = new VedioOnlineRate();
            vl.setId(tree.getId());
            vl.setName(tree.getName());
            List<Long> longs = insMaps.get(tree.getId());
            List<String> insList = new ArrayList<>(longs.size());
            for(Long insId : longs){
                insList.add(String.valueOf(insId));
            }
            vl.getInsList().addAll(insList);
            vl.setVedioSum(longs.size());
            voList.add(vl);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //通过告警查询对应组织下摄像头状态
        for(VedioOnlineRate vl : voList){
            List<String> insList = vl.getInsList();
            if(vl.getVedioSum() == 0){
                continue;
            }
            AlarmEventQuery2 query = new AlarmEventQuery2();
            List<AlarmEventQueryDetail> filters= new ArrayList<AlarmEventQueryDetail>();
            AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
            try {
                Date endDate = format.parse(endTime);
                endDate.setHours(23);
                endDate.setMinutes(59);
                endDate.setSeconds(59);
                detail.setEnd(endDate);
            } catch (ParseException e) {
                logger.error("this is error##",e);
            }
            detail.setRecovered(false);//未恢复
            detail.setSourceIDes(insList);
            detail.setSysID(SysModuleEnum.MONITOR);
            filters.add(detail);
            query.setFilters(filters);
            Page<AlarmEvent, AlarmEventQuery2> page = resourceEventService.queryAlarmEvent(query, 0, Integer.MAX_VALUE);

            HashSet<String> hashSet = new HashSet<String>();
            int brightness = 0,legibility = 0,screenFreezed = 0,colourCast = 0,lostSignal = 0,sightChange = 0,PTZSpeed = 0,keepOut = 0,streakDisturb = 0,cloudPlatFormInvalid = 0,snowflakeDisturb = 0;
            if (null != page.getRows() && page.getRows().size() > 0) {
                for (AlarmEvent re : page.getRows()) {
                    if(re.getExt3().equals("brightness")){
                        brightness += 1;
                    }else if(re.getExt3().equals("legibility")){
                        legibility += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("screenFrozen")){
                        screenFreezed += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("colourCast")){
                         colourCast += 1;
                         hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("lostSignal")){
                        lostSignal += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("sightChange")){
                        sightChange += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("PTZSpeed")){
                        PTZSpeed += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("keepOut")){
                        keepOut += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("streakDisturb")){
                        streakDisturb += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("PTZDegree")){
                        cloudPlatFormInvalid += 1;
                        hashSet.add(re.getSourceID());
                    }else if(re.getExt3().equals("snowflakeDisturb")){
                        snowflakeDisturb += 1;
                        hashSet.add(re.getSourceID());
                    }
                }
            }
            vl.setLegibility(legibility);
            vl.setLostSignal(lostSignal);
            vl.setBrightness(brightness);
            vl.setStreakDisturb(streakDisturb);
            vl.setSnowflakeDisturb(snowflakeDisturb);
            vl.setColourCast(colourCast);
            vl.setPTZSpeed(PTZSpeed);
            vl.setCloudPlatFormInvalid(cloudPlatFormInvalid);
            vl.setScreenFreezed(screenFreezed);
            vl.setSightChange(sightChange);
            vl.setKeepOut(keepOut);
            vl.setDamagedEquipment(hashSet.size());
        }
		return voList;
	}

	@Override
	public void exportReport(String reportType, String exportType,String svgCode, List<VedioOnlineRate> list,
			String filePath,HttpServletResponse response, String orgName, String sTime, String eTime) {
		String pngPath = null;
		filePath = this.getRootDirPath();
		try {
			if("x".equals(exportType)){
				pngPath =  filePath + "excel.png";
			}else if("w".equals(exportType)){
				pngPath =  filePath + "word.png";
			}else if("p".equals(exportType)){
				pngPath =  filePath + "pdf.png";
			}else{
				;
			}
			this.convertToPng(svgCode, pngPath);
			if("x".equals(exportType)){
				this.writeExcelToIo(response, pngPath, list, reportType,orgName,sTime,eTime);
			}else if("w".equals(exportType)){
				this.writeWordToIo(response, pngPath, list, reportType,orgName,sTime,eTime);
			}else if("p".equals(exportType)){
				this.writePDFToIo(response, pngPath, list, reportType,orgName,sTime,eTime);
			}else{
				;
			}
		} catch (IOException e) {
			logger.error("文件读写错误",e);
		} catch (TranscoderException e) {
			logger.error("svg转换错误",e);
		}

	}


	private void getChildOrg(zTreeVo zt,VedioOnlineRate vl){
		List<zTreeVo> children = zt.getChildren();
		if(children != null && children.size() > 0){
			for(zTreeVo z : children){
				vl.getOrgList().add(z.getId());
				if(z.getChildren() != null && z.getChildren().size() > 0){
					getChildOrg(z, vl);
				}
			}
		}
	}

	/**
	 * 将svg字符串转换为png
	 *
	 * @param svgCode svg代码
	 * @param pngFilePath 保存的路径
	 * @throws TranscoderException svg代码异常
	 * @throws IOException io错误
	 */
	private  void convertToPng(String svgCode, String pngFilePath) throws IOException,
	        TranscoderException {

	    File file = new File(pngFilePath);

	    FileOutputStream outputStream = null;
	    try {
	        file.createNewFile();
	        outputStream = new FileOutputStream(file);
	        this.convertToPng(svgCode, outputStream);
	    } finally {
	        if (outputStream != null) {
	            try {
	                outputStream.close();
	            } catch (IOException e) {
	                logger.error("this is error##",e);
	            }
	        }
	    }
	}

	/**
	 * 将svgCode转换成png文件，直接输出到流中
	 *
	 * @param svgCode svg代码
	 * @param outputStream 输出流
	 * @throws TranscoderException 异常
	 * @throws IOException io异常
	 */
	@SuppressWarnings("deprecation")
	private void convertToPng(String svgCode, OutputStream outputStream)
	        throws TranscoderException, IOException {
	    try {
	        byte[] bytes = svgCode.getBytes("ISO_8859_1");
	        PNGTranscoder t = new PNGTranscoder();
	        TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
	        TranscoderOutput output = new TranscoderOutput(outputStream);
	        t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(800));
	        t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(600));
	        t.addTranscodingHint(PNGTranscoder.KEY_PIXEL_TO_MM, new Float(1000));
	        t.transcode(input, output);
	        outputStream.flush();
	    } finally {
	        if (outputStream != null) {
	            try {
	                outputStream.close();
	            } catch (IOException e) {
	                logger.error("this is error##",e);
	            }
	        }
	    }
	}

	private void writePDFToIo(HttpServletResponse response,String pngPath,List<VedioOnlineRate> list,String reportType
			, String orgName, String sTime, String eTime) {
		Document document = null;
		if("kh".equals(reportType) || "gz".equals(reportType)){
			document = new Document(new RectangleReadOnly(842F,595F));
		}else{
			document = new Document();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, baos);
			document.open();
			BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			Font font = new Font(bfChinese, 12, Font.NORMAL);
			Font titleFont = new Font(bfChinese, 20, Font.NORMAL);
			Font stFont = new Font(bfChinese, 13, Font.NORMAL);
			if("zx".equals(reportType)){
				Paragraph paragraph1=new Paragraph("视频诊断分析报表—在线率\r",titleFont);
				Chunk chunk1 = new Chunk("单位名称: " + new String(orgName.getBytes("ISO_8859_1"),"utf-8")+" \r",stFont);
				Chunk chunk2 = new Chunk("起止时间: "+ sTime + " / " + eTime +" \r\r",stFont);
				paragraph1.add(chunk1);
		        paragraph1.add(chunk2);
		        paragraph1.setAlignment(1);
		        document.add(paragraph1);

				PdfPCell cell = null;
				PdfPTable table = new PdfPTable(5);
				for(int i = 0; i < 3; ++i){
					if(i == 0){
						cell = new PdfPCell(new Paragraph("设备在线率表", font));
						cell.setColspan(5);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}else if(i == 1){
						cell = new PdfPCell(new Paragraph("单位名称", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("在线数量", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("总数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("在线率", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("得分", font));
						table.addCell(cell);
					}else {
						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							cell = new PdfPCell(new Paragraph(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getOnlineQuality()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getVedioSum()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getOnlineRate()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getScore()), font));
							table.addCell(cell);
						}
					}
				}
				document.add(table);

				// 插入一个图片
				Image image = Image.getInstance(pngPath);
				image.setAlignment(Image.MIDDLE);
				image.scaleAbsolute(500,300);
				document.add(image);


				document.close();

				response.reset();
				String contentType = "application/octet-stream";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—在线率.pdf", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				response.setHeader("Content-Length", String.valueOf(baos.size()));

				ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
				out.flush();
				out.close();
			}else if("gz".equals(reportType)){
				Paragraph paragraph1=new Paragraph("视频诊断分析报表—故障分析\r",titleFont);
				Chunk chunk1 = new Chunk("单位名称: " + new String(orgName.getBytes("ISO_8859_1"),"utf-8")+" \r",stFont);
				Chunk chunk2 = new Chunk("起止时间: "+ sTime + " / " + eTime +" \r\r",stFont);
				paragraph1.add(chunk1);
		        paragraph1.add(chunk2);
		        paragraph1.setAlignment(1);
		        document.add(paragraph1);

				PdfPCell cell = null;
				PdfPTable table = new PdfPTable(13);
				for(int i = 0; i < 3; ++i){
					if(i == 0){
						cell = new PdfPCell(new Paragraph("故障分析表", font));
						cell.setColspan(13);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}else if(i == 1){
						cell = new PdfPCell(new Paragraph("单位名称", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("设备总数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("清晰度故障", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("信号丢失", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("亮度故障", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("条纹干扰", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("雪花干扰", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("画面偏色", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("云镜速度", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("云镜角度", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("画面冻结", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("场景变化", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("人为遮挡", font));
						table.addCell(cell);
					}else {
						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							cell = new PdfPCell(new Paragraph(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getVedioSum()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getLegibility()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getLostSignal()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getBrightness()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getStreakDisturb()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getSnowflakeDisturb()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getColourCast()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getPTZSpeed()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getCloudPlatFormInvalid()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getScreenFreezed()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getSightChange()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getKeepOut()), font));
							table.addCell(cell);
						}
					}
				}
				document.add(table);

				// 插入一个图片
				Image image = Image.getInstance(pngPath);
				image.setAlignment(Image.MIDDLE);
				image.scaleAbsolute(500,300);
				document.add(image);

				document.close();

				response.reset();
				String contentType = "application/octet-stream";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—故障分析.pdf", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				response.setHeader("Content-Length", String.valueOf(baos.size()));

				ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
				out.flush();
				out.close();
			}else if("ls".equals(reportType)){
				Paragraph paragraph1=new Paragraph("视频诊断分析报表—完好率\r",titleFont);
				Chunk chunk1 = new Chunk("单位名称: " + new String(orgName.getBytes("ISO_8859_1"),"utf-8")+" \r",stFont);
				Chunk chunk2 = new Chunk("起止时间: "+ sTime + " / " + eTime +" \r\r",stFont);
				paragraph1.add(chunk1);
		        paragraph1.add(chunk2);
		        paragraph1.setAlignment(1);
		        document.add(paragraph1);

				PdfPCell cell = null;
				PdfPTable table = new PdfPTable(5);
				for(int i = 0; i < 3; ++i){
					if(i == 0){
						cell = new PdfPCell(new Paragraph("历史完好率表", font));
						cell.setColspan(5);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}else if(i == 1){
						cell = new PdfPCell(new Paragraph("单位名称", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("完好数量", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("总数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("完好率", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("得分", font));
						table.addCell(cell);
					}else {
						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							cell = new PdfPCell(new Paragraph(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getIntactQuality()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getVedioSum()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getIntactRate()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getScore()), font));
							table.addCell(cell);
						}
					}
				}
				document.add(table);

				// 插入一个图片
				Image image = Image.getInstance(pngPath);
				image.setAlignment(Image.MIDDLE);
				image.scaleAbsolute(500,300);
				document.add(image);

				document.close();

				response.reset();
				String contentType = "application/octet-stream";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—完好率.pdf", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				response.setHeader("Content-Length", String.valueOf(baos.size()));

				ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
				out.flush();
				out.close();
			}else if("kh".equals(reportType)){
				Paragraph paragraph1=new Paragraph("视频诊断分析报表—考核报表\r",titleFont);
				Chunk chunk1 = new Chunk("单位名称: " + new String(orgName.getBytes("ISO_8859_1"),"utf-8")+" \r",stFont);
				Chunk chunk2 = new Chunk("起止时间: "+ sTime + " / " + eTime +" \r\r",stFont);
				paragraph1.add(chunk1);
		        paragraph1.add(chunk2);
		        paragraph1.setAlignment(1);
		        document.add(paragraph1);

				PdfPCell cell = null;
				PdfPTable table = new PdfPTable(14);
				for(int i = 0; i < 3; ++i){
					if(i == 0){
						cell = new PdfPCell(new Paragraph("视频设备考核报表", font));
						cell.setColspan(14);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}else if(i == 1){
						cell = new PdfPCell(new Paragraph("序号", font));
						cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setPaddingBottom(10);
						cell.setRowspan(2);
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("地市名", font));
						cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setPaddingBottom(10);
						cell.setRowspan(2);
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("上联统计", font));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setColspan(3);
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("在线统计", font));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setColspan(3);
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("完好率统计", font));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setColspan(3);
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("GIS采集", font));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setColspan(2);
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("得分", font));
						cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setPaddingBottom(10);
						cell.setRowspan(2);
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("考核指标数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("上联数量", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("接入率", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("上联数量", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("在线数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("在线率", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("在线数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("完好数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("图像完好率", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("GIS数", font));
						table.addCell(cell);
						cell = new PdfPCell(new Paragraph("GIS率", font));
						table.addCell(cell);
					}else {
						int n = 1;
						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							cell = new PdfPCell(new Paragraph(String.valueOf(n), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(vl.getName(), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getIndicatorNumber()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getVedioSum()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getAccessRate()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getVedioSum()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getOnlineQuality()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getOnlineRate()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getOnlineQuality()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getIntactQuality()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getIntactRate()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getGisnum()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getGisRate()), font));
							table.addCell(cell);
							cell = new PdfPCell(new Paragraph(String.valueOf(vl.getScore()), font));
							table.addCell(cell);
							++n;
						}
					}
				}
				document.add(table);

				// 插入一个图片
				Image image = Image.getInstance(pngPath);
				image.setAlignment(Image.MIDDLE);
				image.scaleAbsolute(500,300);
				document.add(image);


				document.close();

				response.reset();
				String contentType = "application/octet-stream";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—考核报表.pdf", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				response.setHeader("Content-Length", String.valueOf(baos.size()));

				ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.error("this is error##",e);
		}
	}

	private void writeWordToIo(HttpServletResponse response,String pngPath,List<VedioOnlineRate> list,String reportType
			, String orgName, String sTime, String eTime) {
		String path = this.getRootDirPath();
		if("kh".equals(reportType)){
			path = path + "word1.docx";
		}else if("zx".equals(reportType)){
			path = path + "word2.docx";
		}else if("gz".equals(reportType)){
			path = path + "word3.docx";
		}else if("ls".equals(reportType)){
			path = path + "word4.docx";
		}
		CustomXWPFDocument document = null;
		try {
			document = new CustomXWPFDocument(new FileInputStream(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			if("zx".equals(reportType)){
				//处理段落
				Map<String, Object> param = new HashMap<String, Object>();
		        param.put("{name}", new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
		        param.put("{time}", sTime + "/" + eTime);
				List<XWPFParagraph> paragraphList = document.getParagraphs();
		        try {
					this.processParagraphs(paragraphList, param, document);
				} catch (InvalidFormatException e1) {
					e1.printStackTrace();
				}


				//处理表格
		        Iterator<XWPFTable> it = document.getTablesIterator();
		        XWPFTableRow row = null;
		        while (it.hasNext()) {
		            XWPFTable table = it.next();
		            for(VedioOnlineRate vl : list){
		            	 row = table.createRow();
		            	 row.getCell(0).setText(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"));
		            	 row.createCell().setText(String.valueOf(vl.getOnlineQuality()));
		            	 row.createCell().setText(String.valueOf(vl.getVedioSum()));
		            	 row.createCell().setText(String.valueOf(vl.getOnlineRate()));
		            	 row.createCell().setText(String.valueOf(vl.getScore()));
		            }
		        }

				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				is.close();
				XWPFParagraph paragraph = document.createParagraph();
				document.addPictureData(bytes, XWPFDocument.PICTURE_TYPE_PNG);
				document.createPicture(paragraph, document.getAllPictures().size()-1, 1000, 600, "");
				paragraph.setAlignment(ParagraphAlignment.CENTER);

				response.reset();
				String contentType = "application/x-msdownload";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—在线率.docx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				document.write(out);
				out.flush();
				out.close();
			}else if("gz".equals(reportType)){
				//处理段落
				Map<String, Object> param = new HashMap<String, Object>();
		        param.put("{name}", new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
		        param.put("{time}", sTime + "/" + eTime);
				List<XWPFParagraph> paragraphList = document.getParagraphs();
		        try {
					this.processParagraphs(paragraphList, param, document);
				} catch (InvalidFormatException e1) {
					e1.printStackTrace();
				}


				//处理表格
		        Iterator<XWPFTable> it = document.getTablesIterator();
		        XWPFTableRow row = null;
		        while (it.hasNext()) {
		            XWPFTable table = it.next();
		            for(VedioOnlineRate vl : list){
		            	 row = table.createRow();
		            	 row.getCell(0).setText(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"));
		            	 row.createCell().setText(String.valueOf(vl.getVedioSum()));
		            	 row.createCell().setText(String.valueOf(vl.getLegibility()));
		            	 row.createCell().setText(String.valueOf(vl.getLostSignal()));
		            	 row.createCell().setText(String.valueOf(vl.getBrightness()));
		            	 row.createCell().setText(String.valueOf(vl.getStreakDisturb()));
		            	 row.createCell().setText(String.valueOf(vl.getSnowflakeDisturb()));
		            	 row.createCell().setText(String.valueOf(vl.getColourCast()));
		            	 row.createCell().setText(String.valueOf(vl.getPTZSpeed()));
		            	 row.createCell().setText(String.valueOf(vl.getCloudPlatFormInvalid()));
		            	 row.createCell().setText(String.valueOf(vl.getScreenFreezed()));
		            	 row.createCell().setText(String.valueOf(vl.getSightChange()));
		            	 row.createCell().setText(String.valueOf(vl.getKeepOut()));
		            }
		        }
				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				is.close();
				XWPFParagraph paragraph = document.createParagraph();
				document.addPictureData(bytes, XWPFDocument.PICTURE_TYPE_PNG);
				document.createPicture(paragraph, document.getAllPictures().size()-1, 900, 600, "");
				paragraph.setAlignment(ParagraphAlignment.CENTER);

				response.reset();
				String contentType = "application/x-msdownload";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—故障分析.docx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				document.write(out);
				out.flush();
				out.close();
			}else if("ls".equals(reportType)){
				//处理段落
				Map<String, Object> param = new HashMap<String, Object>();
		        param.put("{name}", new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
		        param.put("{time}", sTime + "/" + eTime);
				List<XWPFParagraph> paragraphList = document.getParagraphs();
		        try {
					this.processParagraphs(paragraphList, param, document);
				} catch (InvalidFormatException e1) {
					e1.printStackTrace();
				}


				//处理表格
		        Iterator<XWPFTable> it = document.getTablesIterator();
		        XWPFTableRow row = null;
		        while (it.hasNext()) {
		            XWPFTable table = it.next();
		            for(VedioOnlineRate vl : list){
		            	 row = table.createRow();
		            	 row.getCell(0).setText(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"));
		            	 row.createCell().setText(String.valueOf(vl.getIntactQuality()));
		            	 row.createCell().setText(String.valueOf(vl.getOnlineQuality()));
		            	 row.createCell().setText(String.valueOf(vl.getVedioSum()));
		            	 row.createCell().setText(String.valueOf(vl.getIntactRate()));
		            	 row.createCell().setText(String.valueOf(vl.getScore()));
		            }
		        }

				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				is.close();
				XWPFParagraph paragraph = document.createParagraph();
				document.addPictureData(bytes, XWPFDocument.PICTURE_TYPE_PNG);
				document.createPicture(paragraph, document.getAllPictures().size()-1, 1000, 600, "");
				paragraph.setAlignment(ParagraphAlignment.CENTER);

				response.reset();
				String contentType = "application/x-msdownload";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—完好率.docx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				document.write(out);
				out.flush();
				out.close();
			}else if("kh".equals(reportType)){
				//处理段落
				Map<String, Object> param = new HashMap<String, Object>();
		        param.put("{name}", new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
		        param.put("{time}", sTime + "/" + eTime);
				List<XWPFParagraph> paragraphList = document.getParagraphs();
		        try {
					this.processParagraphs(paragraphList, param, document);
				} catch (InvalidFormatException e1) {
					e1.printStackTrace();
				}
				//处理表格
		        Iterator<XWPFTable> it = document.getTablesIterator();
		        XWPFTableRow row = null;
		        while (it.hasNext()) {
		            XWPFTable table = it.next();
		            int n = 1;
		            for(VedioOnlineRate vl : list){
		            	 row = table.createRow();
		            	 row.getCell(0).setText(String.valueOf(n));
		            	 row.createCell().setText(vl.getName());
		            	 row.createCell().setText(String.valueOf(vl.getIndicatorNumber()));
		            	 row.createCell().setText(String.valueOf(vl.getVedioSum()));
		            	 row.createCell().setText(String.valueOf(vl.getAccessRate()));
		            	 row.createCell().setText(String.valueOf(vl.getVedioSum()));
		            	 row.createCell().setText(String.valueOf(vl.getOnlineQuality()));
		            	 row.createCell().setText(String.valueOf(vl.getOnlineRate()));
		            	 row.createCell().setText(String.valueOf(vl.getOnlineQuality()));
		            	 row.createCell().setText(String.valueOf(vl.getIntactQuality()));
		            	 row.createCell().setText(String.valueOf(vl.getIntactRate()));
		            	 row.createCell().setText(String.valueOf(vl.getGisnum()));
		            	 row.createCell().setText(String.valueOf(vl.getGisRate()));
		            	 row.createCell().setText(String.valueOf(vl.getScore()));
							++n;
		            }
		        }


				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				is.close();
				XWPFParagraph paragraph = document.createParagraph();
				document.addPictureData(bytes, XWPFDocument.PICTURE_TYPE_PNG);
				document.createPicture(paragraph, document.getAllPictures().size()-1, 1000, 600, "");
				paragraph.setAlignment(ParagraphAlignment.CENTER);

				response.reset();
				String contentType = "application/x-msdownload";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—考核报表.docx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				document.write(out);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.error("this is error##",e);
		}

	}


	private void writeExcelToIo(HttpServletResponse response,String pngPath,List<VedioOnlineRate> list,String reportType
			, String orgName, String sTime, String eTime) {
		XSSFWorkbook xwb = new XSSFWorkbook();
		XSSFCellStyle style=xwb.createCellStyle();
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		XSSFCellStyle style2=xwb.createCellStyle();
		style2.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
		style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);//下边框
		style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
		style2.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		XSSFFont font = xwb.createFont();
		font.setFontName("华文宋体");//设置字体名称
		font.setFontHeightInPoints((short)24);//设置字号
		XSSFCellStyle style3=xwb.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style3.setFont(font);

		XSSFFont font2 = xwb.createFont();
		font2.setFontName("华文宋体");//设置字体名称
		font2.setFontHeightInPoints((short)18);//设置字号
		XSSFCellStyle style4=xwb.createCellStyle();
		style4.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style4.setFont(font2);
		try {
			if("zx".equals(reportType)){
				XSSFSheet xsheet = xwb.createSheet("在线率报表");
				xsheet.setColumnWidth(0, 35 * 256);
				xsheet.setDisplayGridlines(false);
				CellRangeAddress cra=new CellRangeAddress(4, 4, 0, 4);
				xsheet.addMergedRegion(cra);
				xsheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
				xsheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
				xsheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 4));
				//创建标题
				XSSFRow row1 = xsheet.createRow(0);
				XSSFCell ce = row1.createCell(0);
				ce.setCellStyle(style3);
				ce.setCellValue("视频诊断分析报表—在线率");
				XSSFRow row2 = xsheet.createRow(1);
				ce = row2.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("单位名称:"+new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
				XSSFRow row3 = xsheet.createRow(2);
				ce = row3.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("起止时间:" + sTime + "/" + eTime);

				XSSFRow row = null;
				for(int i = 4; i < 7; ++i){
					if(i == 4){
						row = xsheet.createRow(i);
						XSSFCell xcell = row.createCell(0);
						xcell.setCellStyle(style2);
						xcell.setCellValue("设备在线率表");
						xcell = row.createCell(1);
						xcell.setCellStyle(style2);
						xcell = row.createCell(2);
						xcell.setCellStyle(style2);
						xcell = row.createCell(3);
						xcell.setCellStyle(style2);
						xcell = row.createCell(4);
						xcell.setCellStyle(style2);
					}else if(i == 5){
						row = xsheet.createRow(i);
						XSSFCell cell=row.createCell(0);
						cell.setCellStyle(style);
						cell.setCellValue("单位名称");
						cell=row.createCell(1);
						cell.setCellStyle(style);
						cell.setCellValue("在线数量");
						cell=row.createCell(2);
						cell.setCellStyle(style);
						cell.setCellValue("总数");
						cell=row.createCell(3);
						cell.setCellStyle(style);
						cell.setCellValue("在线率");
						cell=row.createCell(4);
						cell.setCellStyle(style);
						cell.setCellValue("得分");
					}else{

						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							row = xsheet.createRow(i + j);
							XSSFCell cell=row.createCell(0);
							cell.setCellStyle(style);
							cell.setCellValue(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"));
							cell=row.createCell(1);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getOnlineQuality());
							cell=row.createCell(2);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getVedioSum());
							cell=row.createCell(3);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getOnlineRate());
							cell=row.createCell(4);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getScore());
						}
					}
				}
				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				int pictureIdx = xwb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
				is.close();
				CreationHelper helper = xwb.getCreationHelper();
				Drawing drawing = xsheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setRow1(list.size() + 7);

				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.resize();

				response.reset();
				String contentType = "application/vnd.ms-excel";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—在线率.xlsx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				xwb.write(out);
				out.flush();
				out.close();
			}else if("gz".equals(reportType)){
				XSSFSheet xsheet = xwb.createSheet("故障分析报表");
				xsheet.setColumnWidth(0, 35 * 256);
				CellRangeAddress cra=new CellRangeAddress(4, 4, 0, 12);
				xsheet.addMergedRegion(cra);
				xsheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));
				xsheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));
				xsheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));
				xsheet.setDisplayGridlines(false);
				//创建标题
				XSSFRow row1 = xsheet.createRow(0);
				XSSFCell ce = row1.createCell(0);
				ce.setCellStyle(style3);
				ce.setCellValue("视频诊断分析报表—故障分析");
				XSSFRow row2 = xsheet.createRow(1);
				ce = row2.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("单位名称:"+new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
				XSSFRow row3 = xsheet.createRow(2);
				ce = row3.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("起止时间:" + sTime + "/" + eTime);

				XSSFRow row = null;
				for(int i = 4; i < 7; ++i){
					row = xsheet.createRow(i);
					if(i == 4){
						XSSFCell xcell = row.createCell(0);
						xcell.setCellStyle(style2);
						xcell.setCellValue("故障分析表");
						xcell = row.createCell(1);
						xcell.setCellStyle(style2);
						xcell = row.createCell(2);
						xcell.setCellStyle(style2);
						xcell = row.createCell(3);
						xcell.setCellStyle(style2);
						xcell = row.createCell(4);
						xcell.setCellStyle(style2);
						xcell = row.createCell(5);
						xcell.setCellStyle(style2);
						xcell = row.createCell(6);
						xcell.setCellStyle(style2);
						xcell = row.createCell(7);
						xcell.setCellStyle(style2);
						xcell = row.createCell(8);
						xcell.setCellStyle(style2);
						xcell = row.createCell(9);
						xcell.setCellStyle(style2);
						xcell = row.createCell(10);
						xcell.setCellStyle(style2);
						xcell = row.createCell(11);
						xcell.setCellStyle(style2);
						xcell = row.createCell(12);
						xcell.setCellStyle(style2);
					}else if(i == 5){
						row = xsheet.createRow(i);
						XSSFCell cell=row.createCell(0);
						cell.setCellStyle(style);
						cell.setCellValue("单位名称");
						cell=row.createCell(1);
						cell.setCellStyle(style);
						cell.setCellValue("设备总数");
						cell=row.createCell(2);
						cell.setCellStyle(style);
						cell.setCellValue("清晰度故障");
						cell=row.createCell(3);
						cell.setCellStyle(style);
						cell.setCellValue("信号丢失");
						cell=row.createCell(4);
						cell.setCellStyle(style);
						cell.setCellValue("亮度故障");
						cell=row.createCell(5);
						cell.setCellStyle(style);
						cell.setCellValue("条纹干扰");
						cell=row.createCell(6);
						cell.setCellStyle(style);
						cell.setCellValue("雪花干扰");
						cell=row.createCell(7);
						cell.setCellStyle(style);
						cell.setCellValue("画面偏色");
						cell=row.createCell(8);
						cell.setCellStyle(style);
						cell.setCellValue("云镜速度");
						cell=row.createCell(9);
						cell.setCellStyle(style);
						cell.setCellValue("云镜角度");
						cell=row.createCell(10);
						cell.setCellStyle(style);
						cell.setCellValue("画面冻结");
						cell=row.createCell(11);
						cell.setCellStyle(style);
						cell.setCellValue("场景变化");
						cell=row.createCell(12);
						cell.setCellStyle(style);
						cell.setCellValue("人为遮挡");
					}else{
						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							row = xsheet.createRow(i + j);
							XSSFCell cell=row.createCell(0);
							cell.setCellStyle(style);
							cell.setCellValue(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"));
							cell=row.createCell(1);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getVedioSum());
							cell=row.createCell(2);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getLegibility());
							cell=row.createCell(3);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getLostSignal());
							cell=row.createCell(4);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getBrightness());
							cell=row.createCell(5);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getStreakDisturb());
							cell=row.createCell(6);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getSnowflakeDisturb());
							cell=row.createCell(7);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getColourCast());
							cell=row.createCell(8);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getPTZSpeed());
							cell=row.createCell(9);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getCloudPlatFormInvalid());
							cell=row.createCell(10);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getScreenFreezed());
							cell=row.createCell(11);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getSightChange());
							cell=row.createCell(12);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getKeepOut());
						}
					}
				}
				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				int pictureIdx = xwb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
				is.close();
				CreationHelper helper = xwb.getCreationHelper();
				Drawing drawing = xsheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setRow1(list.size() + 7);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.resize();

				response.reset();
				String contentType = "application/vnd.ms-excel";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—故障分析.xlsx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				xwb.write(out);
				out.flush();
				out.close();
			}else if("ls".equals(reportType)){
				XSSFSheet xsheet = xwb.createSheet("历史完好率表");
				xsheet.setColumnWidth(0, 35 * 256);
				CellRangeAddress cra=new CellRangeAddress(4, 4, 0, 5);
				xsheet.addMergedRegion(cra);
				xsheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
				xsheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5));
				xsheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
				xsheet.setDisplayGridlines(false);
				//创建标题
				XSSFRow row1 = xsheet.createRow(0);
				XSSFCell ce = row1.createCell(0);
				ce.setCellStyle(style3);
				ce.setCellValue("视频诊断分析报表—完好率");
				XSSFRow row2 = xsheet.createRow(1);
				ce = row2.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("单位名称:"+new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
				XSSFRow row3 = xsheet.createRow(2);
				ce = row3.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("起止时间:" + sTime + "/" + eTime);

				XSSFRow row = null;
				for(int i = 4; i < 7; ++i){
					row = xsheet.createRow(i);
					if(i == 4){
						XSSFCell xcell = row.createCell(0);
						xcell.setCellStyle(style2);
						xcell.setCellValue("历史完好率表");
						xcell = row.createCell(1);
						xcell.setCellStyle(style2);
						xcell = row.createCell(2);
						xcell.setCellStyle(style2);
						xcell = row.createCell(3);
						xcell.setCellStyle(style2);
						xcell = row.createCell(4);
						xcell.setCellStyle(style2);
						xcell = row.createCell(5);
						xcell.setCellStyle(style2);
					}else if(i == 5){
						row = xsheet.createRow(i);
						XSSFCell cell=row.createCell(0);
						cell.setCellStyle(style);
						cell.setCellValue("单位名称");
						cell=row.createCell(1);
						cell.setCellStyle(style);
						cell.setCellValue("完好数量");
						cell=row.createCell(2);
						cell.setCellStyle(style);
						cell.setCellValue("在线数量");
						cell=row.createCell(3);
						cell.setCellStyle(style);
						cell.setCellValue("总数");
						cell=row.createCell(4);
						cell.setCellStyle(style);
						cell.setCellValue("完好率");
						cell=row.createCell(5);
						cell.setCellStyle(style);
						cell.setCellValue("得分");
					}else{

						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							row = xsheet.createRow(i + j);
							XSSFCell cell=row.createCell(0);
							cell.setCellStyle(style);
							cell.setCellValue(new String(vl.getName().getBytes("ISO_8859_1"),"utf-8"));
							cell=row.createCell(1);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getIntactQuality());
							cell=row.createCell(2);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getOnlineQuality());
							cell=row.createCell(3);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getVedioSum());
							cell=row.createCell(4);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getIntactRate());
							cell=row.createCell(5);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getScore());
						}
					}
				}
				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				int pictureIdx = xwb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
				is.close();
				CreationHelper helper = xwb.getCreationHelper();
				Drawing drawing = xsheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setRow1(list.size() + 7);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.resize();

				response.reset();
				String contentType = "application/vnd.ms-excel";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—完好率.xlsx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				xwb.write(out);
				out.flush();
				out.close();
			}else if("kh".equals(reportType)){
				XSSFSheet xsheet = xwb.createSheet("视频设备考核报表");
				xsheet.setColumnWidth(0, 10 * 256);
				xsheet.setColumnWidth(1, 30 * 256);
				CellRangeAddress cra=new CellRangeAddress(4, 4, 0, 13);
				xsheet.addMergedRegion(cra);
				xsheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
				xsheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));
				xsheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 13));

				xsheet.addMergedRegion(new CellRangeAddress(5, 5, 2, 4));
				xsheet.addMergedRegion(new CellRangeAddress(5, 5, 5, 7));
				xsheet.addMergedRegion(new CellRangeAddress(5, 5, 8, 10));
				xsheet.addMergedRegion(new CellRangeAddress(5, 5, 11, 12));
				xsheet.addMergedRegion(new CellRangeAddress(5, 6, 0, 0));
				xsheet.addMergedRegion(new CellRangeAddress(5, 6, 1, 1));
				xsheet.addMergedRegion(new CellRangeAddress(5, 6, 13, 13));
				xsheet.setDisplayGridlines(false);
				//创建标题
				XSSFRow row1 = xsheet.createRow(0);
				XSSFCell ce = row1.createCell(0);
				ce.setCellStyle(style3);
				ce.setCellValue("视频诊断分析报表—考核报表");
				XSSFRow row2 = xsheet.createRow(1);
				ce = row2.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("单位名称:"+new String(orgName.getBytes("ISO_8859_1"),"utf-8"));
				XSSFRow row3 = xsheet.createRow(2);
				ce = row3.createCell(0);
				ce.setCellStyle(style4);
				ce.setCellValue("起止时间:" + sTime + "/" + eTime);

				XSSFRow row = null;
				for(int i = 4; i < 7; ++i){
					if(i == 4){
						row = xsheet.createRow(i);
						XSSFCell xcell = row.createCell(0);
						xcell.setCellStyle(style2);
						xcell.setCellValue("视频设备考核报表");
						xcell = row.createCell(1);
						xcell.setCellStyle(style2);
						xcell = row.createCell(2);
						xcell.setCellStyle(style2);
						xcell = row.createCell(3);
						xcell.setCellStyle(style2);
						xcell = row.createCell(4);
						xcell.setCellStyle(style2);
						xcell = row.createCell(5);
						xcell.setCellStyle(style2);
						xcell = row.createCell(6);
						xcell.setCellStyle(style2);
						xcell = row.createCell(7);
						xcell.setCellStyle(style2);
						xcell = row.createCell(8);
						xcell.setCellStyle(style2);
						xcell = row.createCell(9);
						xcell.setCellStyle(style2);
						xcell = row.createCell(10);
						xcell.setCellStyle(style2);
						xcell = row.createCell(11);
						xcell.setCellStyle(style2);
						xcell = row.createCell(12);
						xcell.setCellStyle(style2);
						xcell = row.createCell(13);
						xcell.setCellStyle(style2);
					}else if(i == 5){
						row = xsheet.createRow(5);
						XSSFCell cell=row.createCell(0);
						cell.setCellStyle(style2);
						cell.setCellValue("序号");
						cell=row.createCell(1);
						cell.setCellStyle(style2);
						cell.setCellValue("地市名");
						cell=row.createCell(2);
						cell.setCellStyle(style2);
						cell.setCellValue("上联数量");
						cell=row.createCell(3);
						cell.setCellStyle(style);
						cell=row.createCell(4);
						cell.setCellStyle(style);
						cell=row.createCell(5);
						cell.setCellStyle(style2);
						cell.setCellValue("在线统计");
						cell=row.createCell(6);
						cell.setCellStyle(style);
						cell=row.createCell(7);
						cell.setCellStyle(style);
						cell=row.createCell(8);
						cell.setCellStyle(style2);
						cell.setCellValue("完好率统计");
						cell=row.createCell(9);
						cell.setCellStyle(style);
						cell=row.createCell(10);
						cell.setCellStyle(style);
						cell=row.createCell(11);
						cell.setCellStyle(style2);
						cell.setCellValue("GIS采集率");
						cell=row.createCell(12);
						cell.setCellStyle(style);
						cell=row.createCell(13);
						cell.setCellStyle(style2);
						cell.setCellValue("得分");

						row = xsheet.createRow(6);
						cell=row.createCell(0);
						cell.setCellStyle(style);
						cell=row.createCell(1);
						cell.setCellStyle(style);
						cell=row.createCell(2);
						cell.setCellStyle(style);
						cell.setCellValue("考核指标数");
						cell=row.createCell(3);
						cell.setCellStyle(style);
						cell.setCellValue("上联数量");
						cell=row.createCell(4);
						cell.setCellStyle(style);
						cell.setCellValue("接入率");
						cell=row.createCell(5);
						cell.setCellStyle(style);
						cell.setCellValue("上联数量");
						cell=row.createCell(6);
						cell.setCellStyle(style);
						cell.setCellValue("在线数");
						cell=row.createCell(7);
						cell.setCellStyle(style);
						cell.setCellValue("在线率");
						cell=row.createCell(8);
						cell.setCellStyle(style);
						cell.setCellValue("在线数");
						cell=row.createCell(9);
						cell.setCellStyle(style);
						cell.setCellValue("完好数");
						cell=row.createCell(10);
						cell.setCellStyle(style);
						cell.setCellValue("图像完好率");
						cell=row.createCell(11);
						cell.setCellStyle(style);
						cell.setCellValue("GIS数");
						cell=row.createCell(12);
						cell.setCellStyle(style);
						cell.setCellValue("GIS率");
						cell=row.createCell(13);
						cell.setCellStyle(style);
					}else{
						int n = 1;
						for(int j = 0; j < list.size(); ++j){
							VedioOnlineRate vl = list.get(j);
							row = xsheet.createRow(7 + j);
							XSSFCell cell=row.createCell(0);
							cell.setCellStyle(style);
							cell.setCellValue(String.valueOf(n));
							cell=row.createCell(1);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getName());
							cell=row.createCell(2);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getIndicatorNumber());
							cell=row.createCell(3);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getVedioSum());
							cell=row.createCell(4);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getAccessRate());
							cell=row.createCell(5);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getVedioSum());
							cell=row.createCell(6);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getOnlineQuality());
							cell=row.createCell(7);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getOnlineRate());
							cell=row.createCell(8);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getOnlineQuality());
							cell=row.createCell(9);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getIntactQuality());
							cell=row.createCell(10);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getIntactRate());
							cell=row.createCell(11);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getGisnum());
							cell=row.createCell(12);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getGisRate());
							cell=row.createCell(13);
							cell.setCellStyle(style);
							cell.setCellValue(vl.getScore());
							++n;
						}
					}
				}
				//插入图片
				InputStream is = new FileInputStream(pngPath);
				byte[] bytes = IOUtils.toByteArray(is);
				int pictureIdx = xwb.addPicture(bytes, XSSFWorkbook.PICTURE_TYPE_PNG);
				is.close();
				CreationHelper helper = xwb.getCreationHelper();
				Drawing drawing = xsheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setRow1(list.size() + 7);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.resize();

				response.reset();
				String contentType = "application/vnd.ms-excel";
				response.setContentType(contentType);
				String realName = URLEncoder.encode("视频诊断分析报表—考核报表.xlsx", "UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + realName);
				ServletOutputStream out = response.getOutputStream();
				xwb.write(out);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.error("this is error##",e);
		}

	}

	public  void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            if ( rowIndex == fromRow ) {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

	@Override
	public List<Organizayions> getAllOrgByLevel(Integer level) {
		int id=0;
		return organizationsInfoDao.getAllOrgByLevel(id,level);
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Map<String, Object>> getReportBySelLevel(String id, String level) {
	List<Map<String, Object>> maps=new ArrayList<Map<String,Object>>();
	Map<String, Object> finalresult = new HashMap<String, Object>();

		int cameratoral=0;//摄像头总数
		DecimalFormat df=new DecimalFormat(".##");
		 int currentLevel=Integer.parseInt(level)+1;
		/***
	 * 1.查询组织下的资源集合
	 * 2.获取2级组织下各自的上报数
	 * 在线：availability
	 */
/*		 instances=this.getcameratotal(id);//获取组织下摄像头
		 cameratoral= instances.size();*/
		 List<Organizayions> organizayions=null;
		 if(id.equals("0")){
			 organizayions=  organizationsInfoDao.getAllOrgByLevel(0,2);
		 }else{//所选组织的下级

			 organizayions=  organizationsInfoDao.getAllOrgByLevel(Integer.parseInt(id),Integer.parseInt(level)+1);
		 }
		 if(organizayions==null || organizayions.size()<=0){
			 organizayions= organizationsInfoDao.getAllOrgByid(Integer.parseInt(id));
		 }
		 List<reportTop> tops= new ArrayList<reportTop>();
			if(organizayions!=null){
				try {
					for (Organizayions org : organizayions) {
						List<String> ids= new ArrayList<String>();
						int sbnum=0;
						int online=0;
						int allNoraml=0;int gis=0; int badSum=0; int notwhnum=0;
						int total=0;
						Map<String, Object> orgmap= new TreeMap<String, Object>();
						orgmap.put("name", org.getOrgName());//组织名称
						finalresult.put("name", org.getOrgName());
						List<Organizayions> newOrg =new ArrayList<Organizayions>();
						List<Organizayions> allOrg=organizationsInfoDao.getAllOrg();
//					System.out.println(org.getId());
						List<Organizayions> childOrganizayions=	this.getChildOrg(newOrg, allOrg, org);
//						System.out.println(childOrganizayions.size());
						List<Map<String, Object>> sbNumMaps	=	this.findRootEl();
						//查询所有的摄像头
						List<ResourceInstance>		parentInstanceByCategoryId = resourceInstanceService.getParentInstanceByCategoryId("CameraPlatform");//查询到所有摄像头资源集合
						for (int i = 0; i < childOrganizayions.size(); i++) {
						for (int k = 0; k < sbNumMaps.size(); k++) {
							Map<String, Object> map= sbNumMaps.get(k);
							int number= Integer.parseInt(map.get("number").toString());
							int orgid=	Integer.parseInt(map.get("id").toString());

							if(Integer.parseInt(childOrganizayions.get(i).getId())==orgid){
								sbnum=sbnum+number;
							}
							}

						for (int j = 0; j < parentInstanceByCategoryId.size(); j++) {

							String[] groupIds=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("groupId");
							String[] gisx=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("gisx");
							String[] gisy=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("gisy");
							String groupId=groupIds[0];
						Double gis_x=Double.valueOf(gisx[0]);
						Double gis_y=Double.valueOf(gisy[0]);
							if(childOrganizayions.get(i).getId().equals(groupId)){//组织内资源
						//		System.out.println("groupid: "+groupId +"--"+parentInstanceByCategoryId.get(j).getName());
								total+=1;
								ids.add(String.valueOf(parentInstanceByCategoryId.get(j).getId()));
				/*			MetricStateData stateData=	metricStateService.getMetricState(parentInstanceByCategoryId.get(j).getId(), "availability");
						if(stateData!=null){
							if(stateData.getState().name().equals("NORMAL")){//可用性显示可用，表示在线

								online=online+1;
							}
						}*/
						if(gis_x!=null && gis_y!=null && gis_x!=0.0 && gis_y!=0.0){//不为空不为0，说明坐标完整
							gis++;
						}
							}
						}
						}

						if(!ids.isEmpty()){

							List<AlarmEventQueryDetail> filters = new ArrayList<AlarmEventQueryDetail>();
							AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
							Date endDate= new Date();

							endDate.setHours(23);
							endDate.setMinutes(59);
							endDate.setSeconds(59);
							detail.setEnd(endDate);

							detail.setSysID(SysModuleEnum.MONITOR);
							detail.setSourceIDes(ids);
							detail.setRecovered(false);
//							detail.setExt3("availability");
							filters.add(detail);

							AlarmEventQuery2 paramAlarmEventQuery2 = new AlarmEventQuery2();
							paramAlarmEventQuery2.setOrderASC(false);
							OrderField[] orderFieldes = {AlarmEventQuery2.OrderField.COLLECTION_TIME};
							paramAlarmEventQuery2.setOrderFieldes(orderFieldes);
							paramAlarmEventQuery2.setFilters(filters);
							Page<AlarmEvent, AlarmEventQuery2> page = resourceEventService.queryAlarmEvent(paramAlarmEventQuery2, 0, Integer.MAX_VALUE);
							Map<String, Object>	insMap=null;
						insMap = new HashMap<String, Object>();

						Map<String, Boolean> badMap = new HashMap<String, Boolean>();
						Map<String, Boolean> onMap = new HashMap<String, Boolean>();
							for(String resid : ids){
								insMap.put(resid, null);
								badMap.put(resid, false);
								onMap.put(resid, true);
							}
							for(String insId : ids){

								if (null != page.getRows() && page.getRows().size() > 0) {

									for (AlarmEvent re : page.getRows()) {
										if(insMap.get(re.getSourceID()) == null){
											insMap.put(re.getSourceID(), re.getCollectionTime());
										}


										if(insId.equals(re.getSourceID())  && re.getCollectionTime().compareTo((Date)insMap.get(re.getSourceID())) == 0){
											if(re.getLevel() == InstanceStateEnum.CRITICAL){
												onMap.put(insId, false);
											}
											badMap.put(insId, true);
//											break;
										}


									}
								}
							}


							for(Map.Entry<String, Boolean> entry : onMap.entrySet()){
								if(entry.getValue() == false){
									++ badSum;
								}else if(badMap.get(entry.getKey()) == true){
									++ notwhnum;
								}
							}
						}

						reportTop top= new reportTop();
					top.setOnline(online);
					top.setId(org.getId());
					tops.add(top);


					cameratoral=total;
					online=(cameratoral-badSum);
					allNoraml=(online-notwhnum);
					double jrRate=(sbnum==0)?100:(float)cameratoral / sbnum * 100;
					double onlineRate=( cameratoral==0)?0: (float)online / cameratoral * 100;
					double normalRate=(online==0) ?0:(float)allNoraml / online * 100;
					double gisRate=(cameratoral==0) ?0:(float)gis / cameratoral * 100;
						orgmap.put("jrRate",Double.parseDouble(df.format(jrRate)));//接入率
						orgmap.put("onlineRate",Double.parseDouble(df.format(onlineRate)));//在线率
						orgmap.put("normalRate",Double.parseDouble(df.format(normalRate)));//完好率
						orgmap.put("gisRate",Double.parseDouble(df.format(gisRate)));//gis率
						orgmap.put("id", org.getId());//id
						orgmap.put("cameratoral",cameratoral);//id
						//视频接入率（总的摄像头数/计划上报normalRate
						maps.add(orgmap);

						//finalresult.put("data", orgmap);
					}
				} catch (Exception e) {
					logger.error("this is error##",e);
				}
			}
			Collections.sort(tops, new Comparator<reportTop>() {//排序在线率top5
				@Override
				public int compare(reportTop o1, reportTop o2) {
					if (o1.getOnline() < o2
							.getOnline()) {
						return 1;
					} else if (o1.getOnline() > o2
							.getOnline()) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			List<Map<String, Object>> newmaps=new ArrayList<Map<String,Object>>();
			int size=tops.size();
			if(size>4){
				size=5;
			}
			for (int i = 0; i < size; i++) {
			for(int j=0;j<maps.size();j++){
					Map<String, Object> map=maps.get(j);
//					System.out.println(map);
				if(map.get("id").equals(tops.get(i).getId())){
					newmaps.add(map);
					List<Map<String, Object>> maps2=		this.getMetricTop(map.get("id").toString(), String.valueOf(currentLevel+1), Integer.parseInt(String.valueOf(map.get("cameratoral"))));
					map.put("data", maps2);
				}else{

				}

					}
				}

		return newmaps;
	}

	@Override
	public Map<String, Object> getAnalysisReportBy(String id, String level) {
		 Map<String, Object> map= new HashMap<String, Object>();

			//查询所有摄像头
			List<ResourceInstance> parentInstanceByCategoryId = null;
			String[] metricsname=null;
			List<String> ids= new ArrayList<String>();
			List<Organizayions> organizayions=null;
			int brightness=0; int legibility=0; int screenFreezed=0; int colourCast=0;
			int lostSignal=0; int sightChange=0; int keepOut=0; int PTZSpeed=0; int streakDisturb=0; int cloudPlatFormInvalid=0;  int snowflakeDisturb=0;

		organizayions=	getResourceByOrg(Integer.parseInt(id),false);
			try {
				parentInstanceByCategoryId = resourceInstanceService.getParentInstanceByCategoryId("CameraPlatform");//查询到所有摄像头资源集合

				for (int i = 0; i < parentInstanceByCategoryId.size(); i++) {
				String[] groupIds=	parentInstanceByCategoryId.get(i).getDiscoverPropBykey("groupId");
					String groupId=groupIds[0];
				for (int j = 0; j < organizayions.size(); j++) {
						if(groupId.equals(organizayions.get(j).getId())){//如果再查询的组织范围内，加入ids集合
							ids.add(String.valueOf(parentInstanceByCategoryId.get(i).getId()));

						}
					}
				}
				//查询故障信息
				AlarmEventQuery2 query = new AlarmEventQuery2();
				List<AlarmEventQueryDetail> filters= new ArrayList<AlarmEventQueryDetail>();
				AlarmEventQueryDetail detail=new AlarmEventQueryDetail();
				detail.setRecovered(false);//未恢复
				detail.setSourceIDes(ids);
				detail.setSysID(SysModuleEnum.MONITOR);
				filters.add(detail);
				query.setFilters(filters);
				Page<AlarmEvent, AlarmEventQuery2> page=	resourceEventService.queryAlarmEvent(query, 0, Integer.MAX_VALUE);
				if (null != page.getRows() && page.getRows().size() > 0) {
					for (AlarmEvent re : page.getRows()) {

						if(re.getExt3().equals("brightness")){//亮度
							brightness=brightness+1;
						}else if(re.getExt3().equals("legibility")){//清晰度
							legibility=legibility+1;
						}else if(re.getExt3().equals("screenFreezed")){//画面冻结
							screenFreezed=screenFreezed+1;
						}else if(re.getExt3().equals("colourCast")){//画面偏色
							 colourCast= colourCast+1;
						}else if(re.getExt3().equals("lostSignal")){//信号缺失
							lostSignal=lostSignal+1;
						}else if(re.getExt3().equals("sightChange")){//场景变换
							sightChange=sightChange+1;
						}else if(re.getExt3().equals("PTZSpeed")){//PTZ速度
							PTZSpeed=PTZSpeed+1;
						}else if(re.getExt3().equals("keepOut")){//人为遮挡
							keepOut=keepOut+1;
						}else if(re.getExt3().equals("streakDisturb")){//条纹干扰

							streakDisturb=streakDisturb+1;
						}else if(re.getExt3().equals("PTZDegree")){//云台控制失效
							cloudPlatFormInvalid=cloudPlatFormInvalid+1;
						}else if(re.getExt3().equals("snowflakeDisturb")){//云台控制失效
							snowflakeDisturb=snowflakeDisturb+1;
						}
					//	System.out.println(re.getCollectionTime());

					}

				}


				 metricsname= new String[]{"亮度","清晰度","画面冻结","偏色","信号丢失","场景变换","PTZ速度","人为遮挡", "条纹干扰","PTZ角度","雪花干扰"};
				long[] num= new long[]{brightness,legibility,screenFreezed,colourCast,lostSignal,sightChange,PTZSpeed,keepOut,streakDisturb,cloudPlatFormInvalid,snowflakeDisturb};
				map.put("categories", metricsname);
				map.put("data",num);

			} catch (Exception e) {
			    logger.error("this is error##",e);
				map.put("categories", metricsname);
				map.put("data",new long[]{});
			}

			return map;

		}

	@Override
	public Map<String, Object> getVedioReportBy4Metric(zTreeVo tree, String id) {
		Map<String, Object> map= new HashMap<String, Object>();
		return map;
	}
	public List<ResourceInstance> getcameratotal(String id){//获取摄像头总数
		List<ResourceInstance> instances= new ArrayList<ResourceInstance>();
		List<ResourceInstance> parentInstanceByCategoryId = null;
		 List<Organizayions> 	organizayions=	getResourceByOrg(Integer.parseInt(id),true);//过滤后的组织集合
			try {
				parentInstanceByCategoryId = resourceInstanceService.getParentInstanceByCategoryId("CameraPlatform");//查询到所有摄像头资源集合
				for (int i = 0; i < parentInstanceByCategoryId.size(); i++) {
				String[] groupIds=	parentInstanceByCategoryId.get(i).getDiscoverPropBykey("groupId");
					String groupId=groupIds[0];
				for (int j = 0; j < organizayions.size(); j++) {
						if(groupId.equals(organizayions.get(j).getId())){//如果再查询的组织范围内，加入ids集合
							instances.add(parentInstanceByCategoryId.get(i));

						}
					}
				}
			} catch (Exception e) {
				logger.error("this is error##",e);
			}
		return instances;
	}

	/**
	 * getResourceByOrg
	 * @param id
	 * @return
	 */
	public List<Organizayions> getResourceByOrg(int id,boolean isLevel){
		List<Organizayions> organizayions=null;
		List<Organizayions> allOrg= new ArrayList<Organizayions>();
		if(id==0){
			if(isLevel==false){//故障分析
		organizayions=organizationsInfoDao.getAllOrg();
			}else{
				List<Organizayions> organizayions2=	organizationsInfoDao.getAllOrg();
				for (int i = 0; i < organizayions2.size(); i++) {
				if(organizayions2.get(i).getLevel()<=1){
					continue;
				}else{
					allOrg.add(organizayions2.get(i));
					organizayions= getChildOrg(allOrg,organizayions2,organizayions2.get(i));
				}
			}

			}
		}else{
			organizayions=organizationsInfoDao.getAllOrgByid(id);
		//	List<Organizayions> childOrg= organizationsInfoDao.getAllOrgByPid(Integer.valueOf(organizayions.get(0).getId()));
			List<Organizayions>  allorg=organizationsInfoDao.getAllOrg();
			allOrg.add(organizayions.get(0));
			organizayions= getChildOrg(allOrg,allorg,organizayions.get(0));
		}





		return organizayions;
	}
	/**
	 *
	 * @param organizayions 组织下id集合
	 * @param id 当前组织id
	 * @param level 当前组织级别
	 * @param cameraTotal  摄像头总数
	 *  @param currentMrteic  当前统计的指标
	 * @param currentMap 当前map集合，存放数据
	 * {
					            id: '100004_jrRate',
					            name: '100004_jrRate',
					            data: [
					                ['East', 4],
					                ['West', 2],
					                ['North', 1],
					                ['South', 4]
					            ]
					        }
	 * @return
	 */
@SuppressWarnings("static-access")
public  List<Map<String, Object>> getMetricTop(String id,String level,int cameraTotal){
	String[] currentMrteic=new String[] {"jrRate","onlineRate","normalRate","gisRate"};
	List<Organizayions> organizayions=  organizationsInfoDao.getAllOrgByLevel(Integer.parseInt(id),Integer.parseInt(level));
	List<Map<String, Object>> finalMap= new  ArrayList<Map<String,Object>>();
	DecimalFormat df=new DecimalFormat(".##");
	 List<Long> ids= new ArrayList<Long>();


for(int num=0;num<currentMrteic.length;num++){//封装四个指标
	List<reportTop> tops= new ArrayList<reportTop>();
	 List<Map<String, Object>> maps = new ArrayList<Map<String,Object>>();//返回结果
	 Map<String, Object> results = new TreeMap<String, Object>();
	results.put("id", currentMrteic[num]);

	finalMap.add(results);
	if(organizayions!=null){
		try {
			for (int k=0;k<organizayions.size();k++) {
				int sbnum=0;
				int online=0;
				int allNoraml=0;int gis=0;
				Map<String, Object> orgmaps= new TreeMap<String, Object>();
				orgmaps.put("id", id);//组织名称
				orgmaps.put("name",organizayions.get(k).getOrgName());
				/*	currentMap.put("data",datas);*/

				List<Organizayions> newOrg =new ArrayList<Organizayions>();
				List<Organizayions> allOrg=organizationsInfoDao.getAllOrg();
				List<Organizayions> childOrganizayions=	this.getChildOrg(newOrg, allOrg, organizayions.get(k));
				List<Map<String, Object>> sbNumMaps	=	this.findRootEl();
				//查询所有的摄像头
				List<ResourceInstance>		parentInstanceByCategoryId = resourceInstanceService.getParentInstanceByCategoryId("CameraPlatform");//查询到所有摄像头资源集合
				for (int i = 0; i < childOrganizayions.size(); i++) {
				for (int j = 0; j < sbNumMaps.size(); j++) {
					Map<String, Object> map= sbNumMaps.get(j);
					int number= Integer.parseInt(map.get("number").toString());
					int orgid=	Integer.parseInt(map.get("id").toString());

					if(Integer.parseInt(childOrganizayions.get(i).getId())==orgid){
						sbnum=sbnum+number;
					}
					}

				for (int j = 0; j < parentInstanceByCategoryId.size(); j++) {

					String[] groupIds=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("groupId");
					String[] gisx=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("gisx");
					String[] gisy=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("gisy");
					String groupId=groupIds[0];
				Double gis_x=Double.valueOf(gisx[0]);
				Double gis_y=Double.valueOf(gisy[0]);
					if(childOrganizayions.get(i).getId().equals(groupId)){//组织内资源
						ids.add(parentInstanceByCategoryId.get(j).getId());
					MetricStateData stateData=	metricStateService.getMetricState(parentInstanceByCategoryId.get(j).getId(), "availability");
				if(stateData!=null){
					if(stateData.getState().name().equals("NORMAL")){//可用性显示可用，表示在线
						online=online+1;
					}
				}
				if(gis_x!=null && gis_y!=null && gis_x!=0.0 && gis_y!=0.0){//不为空不为0，说明坐标完整
					gis++;
				}
					}
				}
				}
		if(!ids.isEmpty()){
			List<InstanceStateData> stateDatas=	instanceStateService.findStates(ids);
			if(stateDatas!=null && stateDatas.size()>0){
				@SuppressWarnings("unused")
				Map<Long, Object> instanceStateDataMap = new TreeMap<Long, Object>();
				for (int i = 0; i < stateDatas.size(); i++) {
					 InstanceStateData isd = (InstanceStateData)stateDatas.get(i);
				String state=	 this.getInstanceStateColor(isd.getState());
				    if(state!=null && state!=""){
				    	if(state.equals("res_normal_nothing")){//可用无告警
				    		allNoraml++;
				    	}
				    }
				}
			}
		}


			double jrRate=(sbnum==0 || cameraTotal==0) ?100:(float)cameraTotal / sbnum * 100;
			double onlineRate=(online==0 || cameraTotal==0) ?0: (float)online / cameraTotal * 100;
			double normalRate=(allNoraml==0 || online==0)?0:(float)allNoraml / online * 100;
			double gisRate=(gis==0 || cameraTotal==0) ?0:(float)  gis / cameraTotal * 100;
			orgmaps.put("jrRate", Double.parseDouble(df.format(jrRate)) );//接入率
			orgmaps.put("onlineRate",Double.parseDouble(df.format(onlineRate)));//在线率
			orgmaps.put("normalRate",Double.parseDouble(df.format(normalRate)));//完好率
			orgmaps.put("gisRate",Double.parseDouble(df.format(gisRate)));//gis率
			orgmaps.put("id", organizayions.get(k).getId());//id
			reportTop top= new reportTop();
			if(currentMrteic[num].equals("jrRate")){
				top.setOnline((float)jrRate);
			}else if(currentMrteic[num].equals("onlineRate")){
				top.setOnline((float)onlineRate);
			}else if(currentMrteic[num].equals("normalRate")){
				top.setOnline((float)allNoraml / online * 100);
			}else if(currentMrteic[num].equals("gisRate")){
				top.setOnline((float)  gis / cameraTotal * 100);
			}

			top.setId(organizayions.get(k).getId());
			tops.add(top);
				//视频接入率（总的摄像头数/计划上报normalRate
				maps.add(orgmaps);

				//finalresult.put("data", orgmap);
			}

		//排序
			Collections.sort(tops, new Comparator<reportTop>() {//排序在线率top5
				@Override
				public int compare(reportTop o1, reportTop o2) {
					if (o1.getOnline() < o2
							.getOnline()) {
						return 1;
					} else if (o1.getOnline() > o2
							.getOnline()) {
						return -1;
					} else {
						return 0;
					}
				}
			});

			List<Map<String, Object>> newmaps=new ArrayList<Map<String,Object>>();
			int size=tops.size();
			if(size>4){
				size=5;
			}
			for (int i = 0; i < size; i++) {
			for(int j=0;j<maps.size();j++){
					Map<String, Object> map=maps.get(j);
				if(map.get("id").equals(tops.get(i).getId())){
					newmaps.add(map);

				}else{

				}

					}
				}
			results.put("data", newmaps);
		} catch (Exception e) {
			logger.error("this is error##",e);
			results.put("data", null);
		}
	}



}
return finalMap;


}
	/**
	 * 获取xml文件中的rootElement
	 *
	 * @return
	 */
	private List<Map<String, Object>> findRootEl() {
		List<Map<String, Object> > result=new ArrayList<Map<String,Object>>();
		String filePath = System.getProperty(CATALINA_HOME) + File.separator
				+ DOWLOAD_PATH + File.separator + DOWLOAD_FILENAME;
		org.dom4j.Element root = XmlUtil.getRootElement(filePath);

		QName qName = new QName("groups");
		@SuppressWarnings("rawtypes")
		Iterator iter = root.elementIterator(qName);
		while (iter.hasNext()) {
			org.dom4j.Element el = (org.dom4j.Element) iter.next();
			QName gName = new QName("group");
			@SuppressWarnings("rawtypes")
			Iterator iters = el.elementIterator(gName);
			while (iters.hasNext()) {
				Map<String, Object> rootmap= new HashMap<String, Object>();
				org.dom4j.Element elc = (org.dom4j.Element) iters.next();
				rootmap.put("id", elc.elementText("id"));
				rootmap.put("number", elc.elementText("number"));
				result.add(rootmap);
			}

		}
		return result;
	}

	public List<Organizayions> 	getChildOrg(List<Organizayions> newOrg,List<Organizayions>  allOrg,Organizayions org){
		newOrg.add(org);
		for (int i = 0; i <allOrg.size(); i++) {
		//	newOrg.add(org);
			if(allOrg.get(i).getParentOrgId().equals(org.getId())){
			//	newOrg.add(allOrg.get(i));
				getChildOrg(newOrg,allOrg,allOrg.get(i));
			}
		}

		return newOrg;

		}
	private static String getInstanceStateColor(InstanceStateEnum stateEnum) {
		String ise = null;
		switch (stateEnum) {
		case CRITICAL:
			ise = "res_critical";
			break;
		case CRITICAL_NOTHING:
			ise = "res_critical_nothing";
			break;
		case SERIOUS:
			ise = "res_serious";
			break;
		case WARN:
			ise = "res_warn";
			break;
		case NORMAL:
		case NORMAL_NOTHING:
			ise = "res_normal_nothing";
			break;
		case NORMAL_CRITICAL:
			ise = "res_normal_critical";
			break;
		case NORMAL_UNKNOWN:
			ise = "res_normal_unknown";
			break;
		// case UNKNOWN_NOTHING:
		// ise = "res_unknown_nothing";
		// break;
		// case UNKOWN:
		// ise = "res_unkown";
		// break;
		default:
			ise = "res_normal_nothing";
			break;
		}
		return ise;
	}

	@Override
	public List<Map<String, Object>> getHomeReportByTime(zTreeVo tree) {

		List<Map<String, Object>> results= new ArrayList<Map<String,Object>>();
		List<String> category = new ArrayList<String>();

		List<Date> dates = new ArrayList<Date>();
		try {
			SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
			Calendar date = Calendar.getInstance();
			Date beginDate = new Date();

			String stratTime=dft.format(beginDate.getTime());
			dates.add(beginDate);
			category.add(stratTime);
			for(int i=1;i<=7;i++){

				date.setTime(beginDate);
				date.set(Calendar.DATE, date.get(Calendar.DATE) - i);
				String endTime=dft.format(date.getTime());
				Date endDate = dft.parse(dft.format(date.getTime()));
				category.add(endTime);
				dates.add(endDate);

			}
//===========================================================================================//

			String id="0";String level="0";
			if(tree==null){

			}else{
				id=tree.getId();
			 level=tree.getLevel();
			}
			if(level==null){
				level="0";
			}

			List<String> ids= new ArrayList<String>();
			int cameratoral=0;//摄像头总数

//			DecimalFormat d=new DecimalFormat(".##");
			/***
		 * 1.查询组织下的资源集合
		 * 2.获取2级组织下各自的上报数
		 * 在线：availability
		 */
			 List<Organizayions> organizayions=null;
			 if(id.equals("0")){
				 organizayions=  organizationsInfoDao.getAllOrgByid(Integer.parseInt(id));//.getAllOrgByLevel(0,2);
			 }else{//所选组织的下级

				 organizayions=  organizationsInfoDao.getAllOrgByid(Integer.parseInt(id));//.getAllOrgByLevel(Integer.parseInt(id),Integer.parseInt(level)+1);
			 }
				int sbnum=0; int gis=0;  int total=0;
				if(organizayions!=null){
					try {
						for (Organizayions org : organizayions) {

							List<Organizayions> newOrg =new ArrayList<Organizayions>();
							List<Organizayions> allOrg=organizationsInfoDao.getAllOrg();
//						System.out.println(org.getId());
							List<Organizayions> childOrganizayions=	this.getChildOrg(newOrg, allOrg, org);
							/*System.out.println("org: "+org.getId()+" =="+org.getOrgName());
							for(int kk=0;kk<childOrganizayions.size();kk++){
								System.out.println(childOrganizayions.get(kk).getId()+"==="+childOrganizayions.get(kk).getOrgName());
							}*/
							List<Map<String, Object>> sbNumMaps	=	this.findRootEl();
							//查询所有的摄像头
							List<ResourceInstance>		parentInstanceByCategoryId = resourceInstanceService.getParentInstanceByCategoryId("CameraPlatform");//查询到所有摄像头资源集合
							for (int i = 0; i < childOrganizayions.size(); i++) {
							for (int k = 0; k < sbNumMaps.size(); k++) {
								Map<String, Object> map= sbNumMaps.get(k);
								int number= Integer.parseInt(map.get("number").toString());
								int orgid=	Integer.parseInt(map.get("id").toString());

								if(Integer.parseInt(childOrganizayions.get(i).getId())==orgid){
									sbnum=sbnum+number;
								}
								}

							for (int j = 0; j < parentInstanceByCategoryId.size(); j++) {

								String[] groupIds=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("groupId");
								String[] gisx=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("gisx");
								String[] gisy=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("gisy");
								String groupId=groupIds[0];
							Double gis_x=Double.valueOf(gisx[0]);
							Double gis_y=Double.valueOf(gisy[0]);
								if(childOrganizayions.get(i).getId().equals(groupId)){//组织内资源
									total+=1;
									ids.add(String.valueOf(parentInstanceByCategoryId.get(j).getId()));

							if(gis_x!=null && gis_y!=null && gis_x!=0.0 && gis_y!=0.0){//不为空不为0，说明坐标完整
				/*				System.out.println("parentInstanceByCategoryId: "+parentInstanceByCategoryId.get(j).getId());
								System.out.println("gis_x: "+gis_x);
								System.out.println("gis_y: "+gis_y);*/
								gis+=1;
							}
								}
							}
							}
						}
					} catch (Exception e) {
						logger.error("this is error##",e);
					}
				}

//=============================================================================================//
/*			System.out.println("final gis: "+gis);
			System.out.println("final total: "+total);*/
	int count=0;
	for (int i = 0; i < dates.size(); i++) {
		if(count<7){
			count=i;
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date startD=dates.get(count);
			Date endD=dates.get(count+1);
			String strat_Time=df.format(startD);
			String end_Time=df.format(endD);
		count++;
		cameratoral=total;
		List<Map<String, Object>> maps=	this.getHomeReportByTimes(tree, strat_Time, end_Time, startD, endD,sbnum,gis,cameratoral);
	Map<String, Object> temp=new TreeMap<String, Object>();
	double gisRate=0; 	double jrRate=0; double normalRate=0; double onlineRate=0;
		if(maps.size()>0 && maps!=null){
		for (int j = 0; j < maps.size(); j++) {
			gisRate=gisRate+(double)maps.get(j).get("gisRate");
			jrRate=jrRate+(double)maps.get(j).get("jrRate");
			normalRate=normalRate+(double)maps.get(j).get("normalRate");
			onlineRate=onlineRate+(double)maps.get(j).get("onlineRate");
		}
	}

		temp.put("jrRate", jrRate);
		temp.put("onlineRate",onlineRate);
		temp.put("normalRate", normalRate);
		temp.put("gisRate", gisRate);
		temp.put("category", category);

		results.add(temp);
		}

	//
	}

		} catch (Exception e) {
			logger.error("this is error##",e);
			return null;
		}
		return results;

}


	@SuppressWarnings("deprecation")
	public 	List<Map<String, Object>> getHomeReportByTimes(zTreeVo tree,String stratTime,String endTime,Date beginDate,Date endDate,int sbnum,int gis,int cameratoral ){
		List<Map<String, Object>>  maps= new ArrayList<Map<String,Object>>();
		String id="0";String level="0";
		if(tree==null){

		}else{
			id=tree.getId();
		 level=tree.getLevel();
		}
		if(level==null){
			level="0";
		}

	Map<String, Object> finalresult = new HashMap<String, Object>();



		/***
	 * 1.查询组织下的资源集合
	 * 2.获取2级组织下各自的上报数
	 * 在线：availability
	 */
		 List<Organizayions> organizayions=null;
		 if(id.equals("0")){
			 organizayions=  organizationsInfoDao.getAllOrgByid(Integer.parseInt(id));//.getAllOrgByLevel(0,2);
		 }else{//所选组织的下级

			 organizayions=  organizationsInfoDao.getAllOrgByid(Integer.parseInt(id));//.getAllOrgByLevel(Integer.parseInt(id),Integer.parseInt(level)+1);
		 }
			if(organizayions!=null){
				try {
					for (Organizayions org : organizayions) {
						List<String> ids= new ArrayList<String>();

						int online=0;

						int whnum=0;
						int badSum=0;
						int notwhnum=0;
						Map<String, Object> orgmap= new TreeMap<String, Object>();
						orgmap.put("name", org.getOrgName());//组织名称
						finalresult.put("name", org.getOrgName());
						List<Organizayions> newOrg =new ArrayList<Organizayions>();
						List<Organizayions> allOrg=organizationsInfoDao.getAllOrg();
//					System.out.println(org.getId());
						List<Organizayions> childOrganizayions=	this.getChildOrg(newOrg, allOrg, org);
						//查询所有的摄像头
						List<ResourceInstance>		parentInstanceByCategoryId = resourceInstanceService.getParentInstanceByCategoryId("CameraPlatform");//查询到所有摄像头资源集合
						for (int i = 0; i < childOrganizayions.size(); i++) {

						for (int j = 0; j < parentInstanceByCategoryId.size(); j++) {
							String[] groupIds=	parentInstanceByCategoryId.get(j).getDiscoverPropBykey("groupId");

							String groupId=groupIds[0];

							if(childOrganizayions.get(i).getId().equals(groupId)){//组织内资源
								ids.add(String.valueOf(parentInstanceByCategoryId.get(j).getId()));

							}
						}
						}
						if(!ids.isEmpty()){

							List<AlarmEventQueryDetail> filters = new ArrayList<AlarmEventQueryDetail>();
							AlarmEventQueryDetail detail = new AlarmEventQueryDetail();


							endDate.setHours(23);
							endDate.setMinutes(59);
							endDate.setSeconds(59);
							detail.setEnd(beginDate);
				/*		System.out.println(beginDate);
						System.out.println(endDate);*/
							detail.setSysID(SysModuleEnum.MONITOR);
							detail.setSourceIDes(ids);
							detail.setRecovered(false);
//							detail.setExt3("availability");
							filters.add(detail);

							AlarmEventQuery2 paramAlarmEventQuery2 = new AlarmEventQuery2();
							paramAlarmEventQuery2.setOrderASC(false);
							OrderField[] orderFieldes = {AlarmEventQuery2.OrderField.COLLECTION_TIME};
							paramAlarmEventQuery2.setOrderFieldes(orderFieldes);
							paramAlarmEventQuery2.setFilters(filters);
							Page<AlarmEvent, AlarmEventQuery2> page = resourceEventService.queryAlarmEvent(paramAlarmEventQuery2, 0, Integer.MAX_VALUE);
							Map<String, Object>	insMap=null;
						insMap = new HashMap<String, Object>();
						Map<String, Boolean> badMap = new HashMap<String, Boolean>();
						Map<String, Boolean> onMap = new HashMap<String, Boolean>();
							for(String resid : ids){
								insMap.put(resid, null);
								badMap.put(resid, false);
								onMap.put(resid, true);
							}
							for(String insId : ids){

								if (null != page.getRows() && page.getRows().size() > 0) {

									for (AlarmEvent re : page.getRows()) {
										if(insMap.get(re.getSourceID()) == null){
											insMap.put(re.getSourceID(), re.getCollectionTime());
										}


										if(insId.equals(re.getSourceID())  && re.getCollectionTime().compareTo((Date)insMap.get(re.getSourceID())) == 0){
											if(re.getLevel() == InstanceStateEnum.CRITICAL){
												onMap.put(insId, false);
											}
											badMap.put(insId, true);
//											break;
										}


									}
								}else{
									break;
								}
							}


							for(Map.Entry<String, Boolean> entry : onMap.entrySet()){
								if(entry.getValue() == false){
									++ badSum;
								}else if(badMap.get(entry.getKey()) == true){
									++ notwhnum;
								}
							}
						}





					online=(cameratoral-badSum);
					whnum=(online-notwhnum);


					double jrRate=(sbnum==0)?100:(float)cameratoral / sbnum * 100;
					double onlineRate=( cameratoral==0)?0: (float)online / cameratoral * 100;
					double normalRate=(online==0) ?0:(float)whnum / online * 100;
					double gisRate=(cameratoral==0) ?0:(float)gis / cameratoral * 100;


					/*		System.out.println("===========start=====");
				System.out.println("cameratoral: "+cameratoral +"=="+"gis: "+gis);
					System.out.println(jrRate);
					System.out.println(onlineRate);
					System.out.println(normalRate);
					System.out.println(gisRate);
					System.out.println("========end=================");*/
					orgmap.put("jrRate", jrRate);//接入率
						orgmap.put("onlineRate",onlineRate);//在线率
						orgmap.put("normalRate",normalRate);//完好率
						orgmap.put("gisRate",gisRate);//gis率
						orgmap.put("id", org.getId());//id
						//视频接入率（总的摄像头数/计划上报normalRate
						maps.add(orgmap);

						//finalresult.put("data", orgmap);
					}
				} catch (Exception e) {
					logger.error("this is error##",e);
					return null;
				}
			}


		return maps;

	}
	@Value("${stm.file.islocal}")
	private String islocal;
	@Value("${stm.file.server_ip}")
	private String serverIP;
	@Value("${stm.file.server_dir}")
	private String serverDir;
	@Value("${stm.file.root_path}")
	private String rootPath;
	private static final String LOCAL_FILE_SERVER = System.getProperties().getProperty(CATALINA_HOME) + File.separatorChar + DOWLOAD_PATH + File.separatorChar + "STM_VIDEO_REPORT" + File.separatorChar;
	private static final String OS = System.getProperty("os.name");

	private String getRootDirPath() {
		String root = null;
		if ("on".equals(islocal.toLowerCase())) {
			root = LOCAL_FILE_SERVER;
		} else {
			if ((OS != null) && (OS.toLowerCase().indexOf("windows") != -1)) {
				root = "\\\\" + serverIP + File.separator + serverDir;
			}
			if ((OS != null) && (OS.toLowerCase().indexOf("linux") != -1)) {
				root = rootPath;
			}
		}
		if (!root.endsWith(File.separator)) {
			root = root + File.separator;
		}
		return root;
	}

	@Override
	public ReportDirectory getReportDirectory() {
		return null;
	}
	@SuppressWarnings("deprecation")
	private void getRate(zTreeVo tree, String startTime,String endTime,List<TreeVo> cityList, Map<String, List<Long>> insMaps, Map<String,List<String>> orgMaps,List<ResourceInstance> eastWitCamera) {
		List<VedioOnlineRate> voList = new ArrayList<VedioOnlineRate>();
		List<ResourceInstance> parentInstanceByCategoryId = eastWitCamera;

		try {
			//查询摄像头所属组织
			List<zTreeVo> children = tree.getChildren();
			if(children != null && children.size() > 0){
				for(zTreeVo zt : children){
					VedioOnlineRate vl = new VedioOnlineRate();
					vl.setId(zt.getId());
					vl.setName(zt.getName());
                    List<String> strings = orgMaps.get(zt.getId());
                    vl.getOrgList().addAll(strings);
                    List<Long> longs = insMaps.get(zt.getId());
                    if(longs == null || longs.size() == 0){
                        continue;
                    }
                    List<String> insList = new ArrayList<>(longs.size());
                    for(Long insId : longs){
                        insList.add(String.valueOf(insId));
                    }
                    vl.getInsList().addAll(insList);
                    vl.setVedioSum(longs.size());
					voList.add(vl);
				}
			}else{
				//当前组织没有子组织
				VedioOnlineRate vl = new VedioOnlineRate();
				vl.setId(tree.getId());
				vl.setName(tree.getName());
                List<Long> longs = insMaps.get(tree.getId());
                List<String> insList = new ArrayList<>(longs.size());
                for(Long insId : longs){
                    insList.add(String.valueOf(insId));
                }
                vl.getInsList().addAll(insList);
                vl.setVedioSum(longs.size());
                voList.add(vl);
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object> insMap = null;
			//通过告警查询对应组织下摄像头状态
			for(VedioOnlineRate vl : voList){
				List<String> insList = vl.getInsList();
				if(vl.getVedioSum() == 0){
					continue;
				}
				List<AlarmEventQueryDetail> filters = new ArrayList<AlarmEventQueryDetail>();
				AlarmEventQueryDetail detail = new AlarmEventQueryDetail();
				try {
					Date endDate = format.parse(endTime);
					endDate.setHours(23);
					endDate.setMinutes(59);
					endDate.setSeconds(59);
					detail.setEnd(endDate);
//					detail.setStart(format.parse(startTime));
				} catch (ParseException e) {
					logger.error("this is error##",e);
				}
				detail.setSysID(SysModuleEnum.MONITOR);
				detail.setSourceIDes(insList);
				detail.setRecovered(false);
				filters.add(detail);

				AlarmEventQuery2 paramAlarmEventQuery2 = new AlarmEventQuery2();
				paramAlarmEventQuery2.setOrderASC(false);
				OrderField[] orderFieldes = {AlarmEventQuery2.OrderField.COLLECTION_TIME};
				paramAlarmEventQuery2.setOrderFieldes(orderFieldes);
				paramAlarmEventQuery2.setFilters(filters);
				Page<AlarmEvent, AlarmEventQuery2> page = resourceEventService.queryAlarmEvent(paramAlarmEventQuery2, 0, Integer.MAX_VALUE);

				insMap = new HashMap<String, Object>();
				Map<String, Boolean> badMap = new HashMap<String, Boolean>();
				Map<String, Boolean> onMap = new HashMap<String, Boolean>();
				for(String id : insList){
					insMap.put(id, null);
					badMap.put(id, false);
					onMap.put(id, true);
				}

				int badSum = 0;
				int badIntactSum = 0;
				for(String insId : insList){
					if (null != page.getRows() && page.getRows().size() > 0) {
						for (AlarmEvent re : page.getRows()) {
							if(insMap.get(re.getSourceID()) == null){
								insMap.put(re.getSourceID(), re.getCollectionTime());
							}

							if(insId.equals(re.getSourceID()) && re.getCollectionTime().compareTo((Date)insMap.get(re.getSourceID())) == 0){
								if(re.getLevel() == InstanceStateEnum.CRITICAL){
									onMap.put(insId, false);
								}
								badMap.put(insId, true);
							}
						}
					}else{
						break;
					}
				}
				for(Map.Entry<String, Boolean> entry : onMap.entrySet()){
					if(entry.getValue() == false){
						++ badSum;
					}else if(badMap.get(entry.getKey()) == true){
						++ badIntactSum;
					}
				}

				Integer vedioSum = vl.getVedioSum();		//总数

				vl.setOnlineQuality(vedioSum - badSum);
				Integer onlineQuality = vl.getOnlineQuality();	//在线数量
				Integer intactQuality = onlineQuality - badIntactSum;	//完好数量
				for(TreeVo z : cityList){
					if(z.getId().equals(vl.getId())){
						 z.setOnlineNumber(onlineQuality);
						 z.setNormalNumber(intactQuality);
						 break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("this is error##",e);
		}
	}
	
	/** 
     * 处理段落 
     * @param paragraphList 
     * @throws FileNotFoundException  
     * @throws InvalidFormatException  
     */  
    public void processParagraphs(List<XWPFParagraph> paragraphList,Map<String, Object> param,XWPFDocument doc) throws InvalidFormatException, FileNotFoundException{  
        if(paragraphList != null && paragraphList.size() > 0){  
            for(XWPFParagraph paragraph:paragraphList){  
                List<XWPFRun> runs = paragraph.getRuns();  
                for (XWPFRun run : runs) {  
                    String text = run.getText(0);  
                    if(text != null){  
                        boolean isSetText = false;  
                        for (Entry<String, Object> entry : param.entrySet()) {  
                            String key = entry.getKey();  
                            if(text.indexOf(key) != -1){  
                                isSetText = true;  
                                Object value = entry.getValue();  
                                if (value instanceof String) {//文本替换  
                                    text = text.replace(key, value.toString());  
                                } 
                            }  
                        }  
                        if(isSetText){  
                            run.setText(text,0);  
                        }  
                    }  
                }  
            }  
        }  
    }


    private int[] resolve(List<?> objects){
        int flag = 900;
        int size = objects.size();
        if(size <= flag){
            return new int[]{size};
        }
        int count = size / flag;        //循环次 数
        int complementation = size % flag;  //求余
        int[] temp = null;
        if(complementation == 0){
            temp = new int[count];
            for(int i = 0; i < count; ++i){
                temp[i] = 900;
            }
        }else{
            temp = new int[count + 1];
            for(int i = 0; i < count; ++i){
                temp[i] = 900;
            }
            temp[temp.length - 1] = complementation;
        }
        return temp;
    }
}
