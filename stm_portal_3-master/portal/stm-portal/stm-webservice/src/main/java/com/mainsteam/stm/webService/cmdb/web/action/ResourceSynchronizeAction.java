package com.mainsteam.stm.webService.cmdb.web.action;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.system.cmdb.api.ICmdbApi;
import com.mainsteam.stm.system.cmdb.bo.CmdbWebServiceBo;
import com.mainsteam.stm.system.itsm.api.IItsmApi;
import com.mainsteam.stm.system.itsm.bo.ItsmWebServiceBo;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.webService.cmdb.client.ConfigInfoMOserviceBusImplService;
import com.mainsteam.stm.webService.cmdb.client.MoServiceBus;
import com.mainsteam.stm.webService.cmdb.client.WSException;
import com.mainsteam.stm.webService.cmdb.client.bo.AttributeValue;
import com.mainsteam.stm.webService.cmdb.client.bo.MoValue;
import com.mainsteam.stm.webService.cmdb.server.MOType;
import com.mainsteam.stm.webService.cmdb.server.Result;
import com.mainsteam.stm.webService.cmdb.server.ResultCode;
import com.mainsteam.stm.webService.util.URLAvailability;
import com.mainsteam.stm.instancelib.dao.pojo.ResourceInstanceDO;
import com.mainsteam.stm.instancelib.dao.impl.ResourceInstanceDAOImpl;

@Controller
@RequestMapping("cmdbWebservice/synchronize")
public class ResourceSynchronizeAction extends BaseAction {

	@Resource
	private CapacityService capacityService;

	@Resource
	private ResourceInstanceService resourceInstanceService;

	@Resource
	private MetricDataService metricDataService;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	@Resource
	private ResourceInstanceDAOImpl resourceInstanceDAOImpl;
	@Autowired
	private IResourceApi resourceApi;
	@Autowired
	private ICmdbApi cmdbApi;
	@Autowired
	private IItsmApi itsmApi;
	private static final QName SERVICE_NAME = new QName("http://impl.server.webservice.ms.com/", "ConfigInfoMOserviceBusImplService");

	private final Logger logger = LoggerFactory.getLogger(ResourceSynchronizeAction.class);
	
	//记录虚拟机类型，用于motypeid比对
	private static CategoryDef[] categoryDefArr;
	private static final String vmId = "VM";
	
	private ResourceSynchronizeAction() {
	}

	@RequestMapping("/synchronizeAllResource")
	public JSONObject synchronizeAllResource() {
		CmdbWebServiceBo cmdbWebServiceBo = JSONObject.parseObject(cmdbApi.getWebService(), CmdbWebServiceBo.class);
		ItsmWebServiceBo webServiceBo = JSONObject.parseObject(itsmApi.getWebService(), ItsmWebServiceBo.class);
		
		// 判断是否打开同步
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(cmdbWebServiceBo.getURL());
		} catch (MalformedURLException e) {
			logger.error("URL转换失败", e);
		}
		ConfigInfoMOserviceBusImplService ss = null;
		try {
			boolean avalible = URLAvailability.isConnect(wsdlURL);
			if(avalible) {
				ss = new ConfigInfoMOserviceBusImplService(wsdlURL, SERVICE_NAME);
			} else {
				logger.error("无效的URL地址");
				return toSuccess("同步失败,URL地址无效");
			}
		} catch (Exception e) {
			logger.error("创建WSDL连接失败", e);
			return toSuccess("同步失败，URL连接无效");
		}
		
		MoServiceBus port = ss.getConfigInfoMOserviceBusImplPort();

		

		List<MoValue> listMoValues = new ArrayList<MoValue>();
		List<ResourceInstanceBo> listResBo = resourceApi.getResources(getLoginUser());

		for (ResourceInstanceBo resBo : listResBo) {
			CategoryDef categoryDef = capacityService.getCategoryById(resBo.getCategoryId());
			if (categoryDef == null) {
				continue;
			}
			ResourceInstance resInstance = resBo.src();
			MoValue moValue = new MoValue();
			moValue.setMoId(String.valueOf(resBo.getId()));
			moValue.setMoTypeId(resInstance.getParentCategoryId());
			moValue.setSource(webServiceBo.getCODE());
			
//			if ("".equals(resBo.getDiscoverIP()) || resBo.getDiscoverIP() == null) {
//				continue;
//			}
			//设备值list
			List<AttributeValue> listValues = new ArrayList<AttributeValue>();
			
			// 信息指标List
			List<String> InfoMetricList = new ArrayList<String>();
			// 查询信息指标   查询信息指标需要过滤
//			List<MetricData> infoMetrics = metricDataService.getMetricInfoDatas(resBo.getId(), InfoMetricList.toArray(new String[InfoMetricList.size()]));
			List<MetricData> infoMetrics = infoMetricQueryAdaptService.getMetricInfoDatas(resBo.getId(), InfoMetricList.toArray(new String[InfoMetricList.size()]));

			// malachi update start
			if("Hardware".equals(resInstance.getParentCategoryId())){
				ResourceInstanceDO rido = resourceInstanceDAOImpl.getResourceInstanceById(resBo.getId());
				AttributeValue attr1 = new AttributeValue();
				attr1.setInstanceId("ip");
				attr1.setValue(rido.getShowIP());
				listValues.add(attr1);
			}
			// malachi update end

			for (MetricData metricData : infoMetrics) {

				// 当前值
				String val = emptyFirstLastChar(metricData.getData());
				AttributeValue attr = new AttributeValue();
				attr.setInstanceId(metricData.getMetricId());
				attr.setValue(val);
				listValues.add(attr);
			}
			AttributeValue[] attrValue = new AttributeValue[listValues.size()];
			moValue.setAttributes(listValues.toArray(attrValue));
			listMoValues.add(moValue);
		}
		/*list的大小*/
		int listSize = listMoValues.size();
		/*每段list 50个数据*/
		int pageSize = 50;
		/*页数，即循环的次数*/
		int page = (listSize + (pageSize - 1))/pageSize;
		
		for (int i = 0; i < page; i++) {//按照循环次数遍历
			List<MoValue> subList = new ArrayList<MoValue>();
			for(int j = 0; j < listSize; j++) {//遍历待分割的list
				//当前记录的页码，（第几页）
				int pageIndex = ((j + 1) + (pageSize - 1)) / pageSize;
				if(pageIndex == (i + 1)) {//当前记录的页码等于要放入的页码时
					subList.add(listMoValues.get(j));
				}
				
				if((j + 1) == ((i + 1) * pageSize)) {//当放满一页时退出当前循环
					break;
				}
			}
			//分段同步结果
			Result<List<MoValue>> result = new Result<List<MoValue>>();
			result.setData(subList);
			result.setResultCode(ResultCode.SUCCESS);
			
			String sendValue = JSONObject.toJSONString(result);
			logger.info("send values is..." + sendValue);
			
			try {
				String resultValue = port.sendMos(sendValue);
				//如果有該標識碼，代表第三方系統沒有配置沒有配置
				if(resultValue.contains(ResultCode.NO_CONFIG)) {
					return toSuccess("请在STSM中配置ms的接口信息！");
				}
				logger.info("手动同步返回值:-" + resultValue);
			} catch (WSException e) {
				logger.error("Expected exception: WSException has occurred.", e);
				return toSuccess("资源同步失败");
			}
		}
		return toSuccess("资源同步成功");
	}

	//虚拟机类型层级多一层，特殊处理,都归纳为VM
	private String getMoTypeId(ResourceInstanceBo resBo){
		String typeId = resBo.src().getParentCategoryId();
		if(null==typeId||"".equals(typeId)){
			typeId = resBo.getCategoryId();
		}
		if(null==categoryDefArr){
			// 获取根
			CategoryDef categoryDef = capacityService.getRootCategory();
			// 一级菜单
			CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();
			
			for (CategoryDef cdf:baseCategoryDef) {
				if(vmId.equals(cdf.getId())){
					categoryDefArr = cdf.getChildCategorys();
					break;
				}
			}
		}

		for(CategoryDef cdf:categoryDefArr){
			if(typeId.equals(cdf.getId())){
				typeId = vmId;
				break;
			}
		}
		
		return typeId;
	}
	
	/**
	 * 把字符串数组转换成数符串
	 * 
	 * @param data
	 * @return
	 */
	private String emptyFirstLastChar(String[] data) {
		if (data == null || data.length == 0) {
			return "";
		} else {
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				if (data[i] != null && !"".equals(data[i].trim())) {
					str.append(data[i].trim());
					if (i < data.length - 1) {
						str.append(" , ");
					}
				}
			}
			return str.toString();
		}
	}
}
