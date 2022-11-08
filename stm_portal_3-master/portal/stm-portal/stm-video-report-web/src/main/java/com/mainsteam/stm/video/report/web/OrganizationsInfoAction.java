package com.mainsteam.stm.video.report.web;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.util.MetricDataUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.video.report.api.OrganizationsInfoApi;
import com.mainsteam.stm.video.report.bo.Organizayions;
import com.mainsteam.stm.video.report.bo.VedioOnlineRate;
import com.mainsteam.stm.video.report.bo.zTreeVo;

@Controller
@RequestMapping("/portal/video/report")
public class OrganizationsInfoAction extends BaseAction{
    private static final Logger logger = Logger.getLogger(OrganizationsInfoAction.class);

	@Resource
	private OrganizationsInfoApi organizationsInfoApi;

    @Resource
    private MetricDataService metricDataService;

    @Resource
    private ModulePropService modulePropService;

	@RequestMapping("/getAllOrg")
	public JSONObject getAllOrg(){

        List<zTreeVo> treeList = null;
        List<zTreeVo> treeListRoot = null;
        try {
            //treeList = this.getTreeList(organizationsInfoApi.getAllOrg());

            List<ResourceInstance> resourceInstances = organizationsInfoApi.queryAllOrg();
            //JSONArray objects = JSONObject.parseArray("[\"id,name,parentID\",\"100000,组织信息,0\",\"100001,玛纳斯县非重点清真寺,100000\",\"100002,玛纳斯县重点清真寺,100000\",\"100003,外域,100000\",\"100004,昌吉市公安局,100003\",\"100005,3农区派出所,100004\",\"100006,滨湖派出所,100005\",\"100007,3重点单位,100006\",\"100008,硫磺沟派出所,100005\",\"100009,4民用爆炸物品从业重点单位,100008\",\"100010,宝平煤矿,100009\",\"100011,三屯河水库民爆库房,100009\",\"100012,屯宝煤矿地面库,100009\",\"100013,g便民警务站和检查站根目录,100004\",\"100014,g区厅统建视频监控,100004\",\"100015,g治安监控二期根目录,100004\",\"100016,g治安监控三期根目录,100004\",\"100017,g治安监控一期根目录,100004\",\"100018,g宗教场所,100004\",\"100019,昌吉市本地寺,100018\",\"100020,昌吉市大西渠镇小西渠清真寺,100018\",\"100021,昌吉市大西渠镇玉堂村头工清真寺,100018\",\"100022,昌吉市喀什寺,100018\",\"100023,昌吉市六工镇沙梁子清真寺,100018\",\"100024,昌吉市宁边路街道兰州寺,100018\",\"100025,昌吉市宁边路街道西河寺,100018\",\"100026,昌吉市陕西寺,100018\",\"100027,昌吉市吐鲁番寺,100018\",\"100028,昌吉市西五工寺,100018\",\"100029,昌吉市新区清真寺,100018\",\"100030,昌吉市延南寺,100018\",\"100031,昌吉市中沟寺,100018\",\"100032,昌吉市中山路街道苗圃寺,100018\",\"100033,硫磺沟镇楼庄子清真寺,100018\",\"100034,便民警务站,100004\",\"100035,0便民警务站内部监控统计,100034\",\"100036,12个警银亭,100035\",\"100037,21号小区便民警务站,100035\",\"100038,6个岗亭,100035\",\"100039,昌吉商场便民警务站,100038\",\"100040,汇嘉时代便民警务站,100038\",\"100041,亚中车站便民警务站,100038\",\"100042,影剧院便民警务站,100038\",\"100043,中亚市场便民警务站,100038\",\"100044,州客运站便民警务站,100038\",\"100045,昌吉学院便民警务站,100035\",\"100046,富民市场便民警务站,100035\",\"100047,海棠大道便民警务站,100035\",\"100048,回民小学便民警务站,100035\",\"100049,吉祥花园便民警务站,100035\",\"100050,建材城便民警务站,100035\",\"100051,建材城便民警务站,100050\",\"100052,江南小镇便民警务站,100035\",\"100053,教育局便民警务站,100035\",\"100054,聚龙城便民警务站,100035\",\"100055,君悦海棠便民警务站,100035\",\"100056,科技广场便民警务站,100035\",\"100057,科技广场便民警务站,100056\",\"100058,联汇市场便民警务站,100035\",\"100059,明峰国际便民警务站,100035\",\"100060,农职院便民警务站,100035\",\"100061,青岛花园便民警务站,100035\",\"100062,人社局便民警务站,100035\",\"100063,森林大第便民警务站,100035\",\"100064,石油大厦便民警务站,100035\",\"100065,市法院便民警务站,100035\",\"100066,市三小便民警务站,100035\",\"100067,市五中便民警务站,100035\",\"100068,塔城路带状公园便民警务站,100035\",\"100069,桃源新城便民警务站,100035\",\"100070,西域便民警务站,100035\",\"100071,新疆大剧院便民警务站,100035\",\"100072,新客站便民警务站,100035\",\"100073,亚心广场便民警务站,100035\",\"100074,亚中商城便民警务站,100035\",\"100075,延安南路惠民市场便民警务站,100035\",\"100076,友好商场便民警务站,100035\",\"100077,御景家园便民警务站,100035\",\"100078,园林便民警务站,100035\",\"100079,中央公园便民警务站,100035\",\"100080,州四中便民警务站,100035\",\"100081,州政府便民警务站,100035\",\"100082,阜康市公安局,100003\",\"100083,阜康市便民警务服务站,100082\",\"100084,博峰街派出所,100083\",\"100085,东方花园便民警务站,100084\",\"100086,市一小便民警务站,100084\",\"100087,有色苑便民警务站,100084\",\"100088,中医院便民警务站,100084\",\"100089,准电便民警务站,100084\",\"100090,城关派出所,100083\",\"100091,碧琳城便民警务站,100090\",\"100092,滨河公园便民警务站,100090\",\"100093,城北市场便民警务站,100090\",\"100094,崇文便民警务站,100090\",\"100095,阜华景源便民警务站,100090\",\"100096,华星便民警务站,100090\",\"100097,康宁便民警务站,100090\",\"100098,市一中便民警务站,100090\",\"100099,文博便民警务站,100090\",\"100100,瑶池园编民警务站,100090\",\"100101,准东路南警务服务站,100090\",\"100102,阜新街派出所,100083\",\"100103,金座便民警务站,100102\",\"100104,阳光花园便民警务站,100102\",\"100105,迎宾路便民警务站,100102\",\"100106,甘河子派出所,100083\",\"100107,绿洲公园便民警务站,100106\",\"100108,时代商贸城便民警务站,100106\",\"100109,九运街派出所,100083\",\"100110,九运街便民警务站,100109\",\"100111,滋泥泉子派出所,100083\",\"100112,滋泥泉子便民警务站,100111\",\"100113,阜康市公安局巡逻防控大队,100082\",\"100114,阜康市公安局巡逻防控大队,100113\",\"100115,阜康市准东路口公安检查站,100082\",\"100116,阜康市公安局准东路口检查站,100115\",\"100117,阜康市宗教局,100082\",\"100118,阜康市宗教局,100117\",\"100119,阜康四期社会治安,100082\",\"100120,博峰派出所,100119\",\"100121,城关派出所,100119\",\"100122,阜新派出所,100119\",\"100123,甘河子派出所,100119\",\"100124,九运街派出所,100119\",\"100125,三工派出所,100119\",\"100126,上户沟派出所,100119\",\"100127,滋泥泉子派出所,100119\",\"100128,公安局内部监控,100082\",\"100129,内部监控NVR01,100128\",\"100130,内部监控NVR02,100128\",\"100131,内部监控NVR03,100128\",\"100132,卡口,100082\",\"100133,全市清真寺,100132\",\"100134,全市执法窗口监控,100082\",\"100135,城关派出所,100134\",\"100136,阜康市城关镇派出所,100135\",\"100137,阜新街派出所,100134\",\"100138,阜康市阜新派出所,100137\",\"100139,甘河子派出所,100134\",\"100140,九运街派出所,100134\",\"100141,上户沟派出所,100134\",\"100142,天池派出所,100134\",\"100143,天池派出所01,100142\",\"100144,天池派出所02,100142\",\"100145,天池派出所审讯系统,100142\",\"100146,滋泥泉子派出所,100134\",\"100147,全市重点单位,100082\",\"100148,加气站,100147\",\"100149,城关派出所,100148\",\"100150,阜康服务区北加气站,100149\",\"100151,阜康服务区南加气站,100149\",\"100152,天池景区加气站,100149\",\"100153,甘河子派出所,100148\",\"100154,大黄山新捷加气站北,100153\",\"100155,大黄山新捷加气站南,100153\",\"100156,广汇能源,100153\",\"100157,通源能源,100153\",\"100158,同德能源加气站,100153\",\"100159,九运街派出所,100148\",\"100160,九运街加气站,100159\",\"100161,上户沟派出所,100148\",\"100162,阜东一区加气站,100161\",\"100163,晋商工业园加气站,100161\",\"100164,滋泥泉子派出所,100148\",\"100165,广汇能源滋泥泉子站,100164\",\"100166,加油站,100147\",\"100167,商信委01,100166\",\"100168,商信委02,100166\",\"100169,巡逻防控大队屯兵点,100082\",\"100170,州局推送,100082\",\"100171,阜康市市清真寺,100170\",\"100172,阜康市准东公安检查站,100170\",\"100173,天池管委会,100170\",\"100174,呼图壁县公安局,100003\",\"100175,呼图壁县视频资源上传共享,100174\",\"100176,呼图壁全县清真寺,100175\",\"100177,吉木萨尔县公安局,100003\",\"100178,01城镇派出所,100177\",\"100179,02北庭辖派出所,100177\",\"100180,03大有镇派出所,100177\",\"100181,04二工镇派出所,100177\",\"100182,05老台乡派出所,100177\",\"100183,06庆阳湖乡派出所,100177\",\"100184,07泉子街派出所,100177\",\"100185,08三台镇派出所,100177\",\"100186,09新地派出所,100177\",\"100187,10看守所,100177\",\"100188,高空瞭望,100177\",\"100189,宗教场所汇总,100177\",\"100190,总汇二期高清,100177\",\"100191,总汇吉木萨尔县电警卡口,100177\",\"100192,总汇三期高清,100177\",\"100193,总汇四期高清,100177\",\"100194,总汇一期标清,100177\",\"100195,总汇重点单位,100177\",\"100196,玛纳斯县公安局,100003\",\"100197,_玛纳斯平安城市共享,100196\",\"100198,高空云盘摄像机,100197\",\"100199,玛纳斯县清真寺,100197\",\"100200,平安城市,100197\",\"100201,玛纳斯全县平安城市,100196\",\"100202,宇视卡口,100196\",\"100203,宇视卡口外域,100196\",\"100204,东外环01,100203\",\"100205,恒盛加气站01,100203\",\"100206,恒盛加气站02,100203\",\"100207,老交警队01,100203\",\"100208,老交警队02,100203\",\"100209,玛清路,100203\",\"100210,塔西河,100203\",\"100211,太阳庙01,100203\",\"100212,太阳庙02,100203\",\"100213,西海公园01,100203\",\"100214,西海公园02,100203\",\"100215,重点区域20路图像,100196\",\"100216,木垒县公安局,100003\",\"100217,便民警务站,100216\",\"100218,博斯坦乡便民警务站,100217\",\"100219,博斯坦乡便民警务站,100218\",\"100220,大浪沙公安检查站,100216\",\"100221,大浪沙公安检查站,100220\",\"100222,大浪沙检查站NVR02,100220\",\"100223,高清卡口图片数据,100216\",\"100224,木垒县社会治安监控,100216\",\"100225,2016平安城市改造,100224\",\"100226,2016平安城市新建,100224\",\"100227,高空云台,100224\",\"100228,数字高清摄像机,100224\",\"100229,全县重点清真寺监控,100216\",\"100230,总清真寺根目录,100229\",\"100231,阿拉苏活动点,100230\",\"100232,白杨河西村维族寺NVR,100230\",\"100233,白杨河乡牧民新村清真寺哈NVR,100230\",\"100234,白杨河乡一碗泉村清真寺,100230\",\"100235,北桥清真寺NVR,100230\",\"100236,博斯坦村五组维族寺NVR,100230\",\"100237,博斯坦三个泉子回族寺NVR,100230\",\"100238,博斯坦乡合然托别克村寺哈,100230\",\"100239,博斯坦乡依然哈巴克清真寺NVR,100230\",\"100240,大南沟阿克哈巴克清真寺NVR,100230\",\"100241,大南沟东沟清真寺,100230\",\"100242,大石头拜格卓勒清真寺,100230\",\"100243,大石头牧民新村清真寺哈,100230\",\"100244,大石头铁尔沙克哈族清真寺,100230\",\"100245,东城沈家沟维族寺NVR,100230\",\"100246,东城镇鸡心梁村库克别列斯清真寺,100230\",\"100247,东梁回族寺NVR,100230\",\"100248,花园哈族清真寺,100230\",\"100249,老大石头村哈族寺NVR,100230\",\"100250,雀仁五棵树哈族寺NVR,100230\",\"100251,雀仁正格勒得哈族清真寺,100230\",\"100252,水磨沟维族寺NVR,100230\",\"100253,新沟三组维族寺NVR,100230\",\"100254,照壁山乡霍斯阔拉清真寺,100230\",\"100255,周家塘回族寺NVR,100230\",\"100256,新中新对接卡口,100216\",\"100257,总汇州局共享组织,100216\",\"100258,木垒县清真寺,100257\",\"100259,奇台县公安局,100003\",\"100260,高空瞭望,100259\",\"100261,奇台边境线乌拉斯台口岸,100259\",\"100262,乌拉斯台海康平台,100261\",\"100263,乌拉斯台口孔道1,100261\",\"100264,乌拉斯台口孔道2,100261\",\"100265,乌拉斯台口孔道3,100261\",\"100266,乌拉斯台口联检大厅,100261\",\"100267,奇台县卡口,100259\",\"100268,全县平安城市三期,100259\",\"100269,全县平安城市四期,100259\",\"100270,全县清真寺,100259\",\"100271,全县社会面高清监控,100259\",\"100272,准噶尔公安局,100003\"]");
            //List<String> ls = new ArrayList<>();
            //for(int i = 0; i < objects.size(); ++i){
            //    String str = objects.getString(i);
            //    ls.add(str);
            //}
           if(resourceInstances.size() > 0){
                treeListRoot = new ArrayList<>(resourceInstances.size());
           }
            zTreeVo rootZt = null;
            for(ResourceInstance ri : resourceInstances){
                rootZt = new zTreeVo();
                treeListRoot.add(rootZt);
                rootZt.setId(String.valueOf(ri.getId()));
                rootZt.setName(ri.getShowName());
                rootZt.setPId("-1");
                MetricData metricData = metricDataService.catchRealtimeMetricData(ri.getId(),"orgInfo");
                String[] data = metricData.getData();
                if(data == null || data.length == 0){
                    treeListRoot.remove(rootZt);
                    continue;
                }
                List<Map<String, String>> orgMaps = MetricDataUtil.parseTableResultSet(data);
                if(data != null && orgMaps.size() > 0){
                    List<Organizayions> orgList = new ArrayList<>(data.length);
                    Organizayions org = null;
                    for(int i = 0; i < orgMaps.size(); ++i){
                        Map<String, String> infoMap = orgMaps.get(i);
                        org = new Organizayions();
                        org.setId(infoMap.get("id"));
                        org.setOrgName(infoMap.get("name"));
                        org.setParentOrgId(infoMap.get("parentID"));
                        orgList.add(org);
                    }
                    if(orgList.size() > 0){
                        treeList = this.getNewTreeList(orgList);
                        rootZt.setChildren(treeList);
                    }
                }
            }
        } catch (InstancelibException e) {
            logger.error("treeList is empty!!");
            treeListRoot = new ArrayList<>();
        } catch (MetricExecutorException e) {
            e.printStackTrace();
        }

        return toSuccess(JSONObject.toJSON(treeListRoot));
	}
	
	@RequestMapping("/getOrg")
	public JSONObject getOrg(){

	//	List<zTreeVo> treeList = this.getTreeList(organizationsInfoApi.getAllOrg());
        List<Organizayions> organizayions= null;
        try {
            organizayions = organizationsInfoApi.getAllOrg();
        } catch (InstancelibException e) {
            e.printStackTrace();
        }
        Organizayions org=organizayions.get(0);
		return toSuccess(org);
	}
	
	@RequestMapping("/getReportBySelLevel")
	public JSONObject getReportBySelLevel(String id,String level){
	/*	zTreeVo tree = new zTreeVo();
		if(id.equals("0")){
	
			List<zTreeVo> treeList = this.getTreeList(organizationsInfoApi.getAllOrg());
			for(int i=0;i<treeList.size();i++){
				if(treeList.get(i).getLevel().equals("2")){
					
				}else{
					
				}
			}
		//	 tree=treeList.get(0);
		}else{
		JSONObject jsonObject = JSONObject.parseObject(id);
		 tree = (zTreeVo)JSONObject.toJavaObject(jsonObject, zTreeVo.class);	
		}
		organizationsInfoApi.getVedioReportBy4Metric(tree, id);*/
		return toSuccess(organizationsInfoApi.getReportBySelLevel(id, level));
		
	}
	
	/***
	 * 故障分析
	 * @param id
	 * @param level
	 * @return
	 */
	@RequestMapping("/getAnalysisReportBy")
	public JSONObject getAnalysisReportBy(String id,String level){
	//	organizationsInfoApi.getAnalysisReportBy(id, level);
		return toSuccess(organizationsInfoApi.getAnalysisReportBy(id, level));
		
	}
	@RequestMapping("/getReportInfoByOrg")
	public JSONObject getReportInfoByOrg(String node, String rootNode, String type, String startTime, String endTime){
		JSONObject jsonObject = JSONObject.parseObject(node);
		zTreeVo tree = (zTreeVo)JSONObject.toJavaObject(jsonObject, zTreeVo.class);

        JSONObject rootObject = JSONObject.parseObject(rootNode);
        zTreeVo rootZtree = (zTreeVo)JSONObject.toJavaObject(rootObject, zTreeVo.class);
		
		if("zx".equals(type)){
			List<VedioOnlineRate> vedioOnlineRate = organizationsInfoApi.getVedioOnlineRate(tree,rootZtree, startTime, endTime);
			return toSuccess(vedioOnlineRate);
		}else if("gz".equals(type)){
			List<VedioOnlineRate> faultReport = organizationsInfoApi.getFaultReport(tree,rootZtree, startTime, endTime);
			return toSuccess(faultReport);
		}else if("ls".equals(type)){
			List<VedioOnlineRate> intactRate = organizationsInfoApi.getIntactRate(tree,rootZtree, startTime, endTime);
			return toSuccess(intactRate);
		}else{
			List<TableData> inspectionReport = organizationsInfoApi.getInspectionReport(tree,rootZtree, startTime, endTime);
			return toSuccess(inspectionReport);
		}
	}
	@RequestMapping("/getHomeReportByTime")	
public JSONObject getHomeReportByTime(String id,String level){
		zTreeVo tree = new zTreeVo();
	/*	if(node.equals("")){
			List<zTreeVo> treeList = this.getTreeList(organizationsInfoApi.getAllOrg());
			if(treeList!=null ){
				tree=treeList.get(0);
			}else{
				tree=null;
			}
			
		}else{
			JSONObject jsonObject = JSONObject.parseObject(node);
			 tree = (zTreeVo)JSONObject.toJavaObject(jsonObject, zTreeVo.class);	
		}*/
			tree.setId(id);
			tree.setLevel(level);
		
	return toSuccess(organizationsInfoApi.getHomeReportByTime(tree));
	
}	

	
	@RequestMapping("/exportReportByexportType")
	public void exportReportByexportType(HttpServletRequest request,HttpServletResponse response, String reportType,
			String exportType, String svgCode, String tableData, String orgName, String sTime, String eTime){
		if("kh".equals(reportType)){
			try {
				tableData = new String(tableData.getBytes("ISO8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		}
		JSONArray jsonArray = JSONArray.parseArray(tableData);
		List<VedioOnlineRate> list = new ArrayList<VedioOnlineRate>();
		for(int i = 0; i < jsonArray.size(); ++i){
			VedioOnlineRate vl = JSONObject.toJavaObject(jsonArray.getJSONObject(i), VedioOnlineRate.class);		
			list.add(vl);
		}
		String filePath = request.getSession().getServletContext().getRealPath("/");
		organizationsInfoApi.exportReport(reportType,exportType,svgCode,list,filePath,response,orgName,sTime,eTime);
	}

    private List<zTreeVo> getNewTreeList(List<Organizayions> allOrg) {
        List<zTreeVo> zTreeVos = new ArrayList<zTreeVo>();
        zTreeVo zTreeVo = null;
        String[] paIds = new String[allOrg.size()];
        String[] ids = new String[allOrg.size()];
        for(int i = 0; i < allOrg.size(); ++i){
            Organizayions or = allOrg.get(i);
            zTreeVo = new zTreeVo();
            zTreeVo.setId(or.getId());
            zTreeVo.setPId(or.getParentOrgId());
            zTreeVo.setName(or.getOrgName());
            zTreeVos.add(zTreeVo);
            paIds[i] = or.getParentOrgId();
            ids[i] = or.getId();
        }
        Arrays.sort(ids);
        List<zTreeVo> zteeList = new ArrayList<zTreeVo>();
        orgFlag : for(int i = 0; i < paIds.length; ++i){
            String paId = paIds[i];
            int i1 = Arrays.binarySearch(ids, paId);
            if(i1 <= 0){
                //获取root
                for(zTreeVo zt : zTreeVos){
                    if(paId.equals(zt.getPId())){
                        zt.setOpen(true);
                        zteeList.add(zt);
                        this.buildZtree(zt ,zTreeVos ,zteeList);
                        break orgFlag;
                    }
                }
            }
        }




        return zteeList;
    }
	

	private List<zTreeVo> getTreeList(List<Organizayions> allOrg) {
		List<zTreeVo> zTreeVos = new ArrayList<zTreeVo>();
		zTreeVo zTreeVo = null;
		for(Organizayions or : allOrg){
			zTreeVo = new zTreeVo();
			zTreeVo.setId(or.getId());
			zTreeVo.setPId(or.getParentOrgId());
			zTreeVo.setName(or.getOrgName());
			zTreeVos.add(zTreeVo);
		}

		List<zTreeVo> zteeList = new ArrayList<zTreeVo>();
		for(zTreeVo zt : zTreeVos){
			if("0".equals(zt.getPId())){
				zt.setOpen(true);
				zteeList.add(zt);
				this.buildZtree(zt ,zTreeVos ,zteeList);
				break;
			}
		}
		
		return zteeList;
	}
	
	private List<zTreeVo> getTreeListExceptTop(List<Organizayions> allOrg) {
		List<zTreeVo> zTreeVos = new ArrayList<zTreeVo>();
		zTreeVo zTreeVo = null;
		List<zTreeVo> zteeList = new ArrayList<zTreeVo>();
		for(Organizayions or : allOrg){
			if(or.getLevel()>=2){
				zTreeVo = new zTreeVo();
				zTreeVo.setId(or.getId());
				zTreeVo.setPId(or.getParentOrgId());
				zTreeVo.setName(or.getOrgName());
				zTreeVos.add(zTreeVo);
			
				for(zTreeVo zt : zTreeVos){
					if("0".equals(zt.getPId())){
						zt.setOpen(true);
						zteeList.add(zt);
						this.buildZtree(zt ,zTreeVos ,zteeList);
						break;
					}
				}
			}
		
		}
	
		
		return zteeList;
	}
	
	private void buildZtree(zTreeVo zTreeVo ,List<zTreeVo> zTreeVos,List<zTreeVo> zteeList){
		List<zTreeVo> cList = new ArrayList<zTreeVo>();
		for(zTreeVo zt : zTreeVos){
			if(zTreeVo.getId().equals(zt.getPId())){
				cList.add(zt);
			}
		}
		if(cList.size() > 0){
			zTreeVo.setIsParent(true);
			zTreeVo.setChildren(cList);
			for(zTreeVo zt : cList){
				buildZtree(zt, zTreeVos, zteeList);
			}
		}
	}
	
}
