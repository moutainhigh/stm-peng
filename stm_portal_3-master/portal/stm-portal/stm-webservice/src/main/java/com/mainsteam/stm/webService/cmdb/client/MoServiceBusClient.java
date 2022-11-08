
package com.mainsteam.stm.webService.cmdb.client;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.system.cmdb.api.ICmdbApi;
import com.mainsteam.stm.system.cmdb.bo.CmdbWebServiceBo;
import com.mainsteam.stm.system.itsm.api.IItsmApi;
import com.mainsteam.stm.system.itsm.bo.ItsmWebServiceBo;
import com.mainsteam.stm.webService.cmdb.client.bo.AttributeValue;
import com.mainsteam.stm.webService.cmdb.client.bo.MoValue;
import com.mainsteam.stm.webService.cmdb.server.Result;
import com.mainsteam.stm.webService.cmdb.server.ResultCode;

/**
 * This class was generated by Apache CXF 2.7.14
 * 2015-05-29T13:42:00.365+08:00
 * Generated source version: 2.7.14
 * 
 */
@Service
public final class MoServiceBusClient implements InstancelibListener{
	@Resource
	private CapacityService capacityService;
	
	@Autowired
	private ICmdbApi cmdbApi;
	
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	@Autowired
	private IItsmApi itsmApi;
	private final Logger logger = LoggerFactory.getLogger(MoServiceBusClient.class);
	
    private static final QName SERVICE_NAME = new QName("http://impl.server.webservice.ms.com/", "ConfigInfoMOserviceBusImplService");

    private MoServiceBusClient() {
    }

	@Override
	public void listen(InstancelibEvent instancelibEvent){
		CmdbWebServiceBo cmdbWebServiceBo = null;
		ItsmWebServiceBo webServiceBo = null;
		try {
			cmdbWebServiceBo = JSONObject.parseObject(cmdbApi.getWebService(), CmdbWebServiceBo.class);
			webServiceBo = JSONObject.parseObject(itsmApi.getWebService(), ItsmWebServiceBo.class);
		} catch (Exception e) {
			logger.error("查找URL地址失败",e);
		}
		
		String reource = webServiceBo.getCODE();
		//判断是否打开同步
		if (cmdbWebServiceBo != null && cmdbWebServiceBo.isOpen()) {
			URL wsdlURL = null;
			try {
				wsdlURL = new URL(cmdbWebServiceBo.getURL());
			} catch (MalformedURLException e) {
				logger.error("URL转换失败", e);
			}
			try{
		        ConfigInfoMOserviceBusImplService ss = new ConfigInfoMOserviceBusImplService(wsdlURL, SERVICE_NAME);
		        MoServiceBus port = ss.getConfigInfoMOserviceBusImplPort();
		        
		        if (instancelibEvent.getEventType() == EventEnum.INSTANCE_ADD_EVENT) {
					logger.info("开始资源同步");
					List<ResourceInstance> resourceInstances = (List<ResourceInstance>)instancelibEvent.getSource();
					
					if(resourceInstances == null || resourceInstances.size() <= 0){
						return;
					}
					
					for(ResourceInstance resourceInstance : resourceInstances){
						
						List<MoValue> listMoValues = new ArrayList<MoValue>();
						//如果发现IP不为空
						if (!"".equals(resourceInstance.getShowIP()) && resourceInstance.getShowIP() != null) {
							MoValue moValue = new MoValue();
							moValue.setMoId(String.valueOf(resourceInstance.getId()));
							moValue.setMoTypeId(resourceInstance.getParentCategoryId());
							moValue.setSource(reource);//唯一标识
							
							//设备值list
							List<AttributeValue> listValues = new ArrayList<AttributeValue>();
							
							// 信息指标List
							List<String> InfoMetricList = new ArrayList<String>();
							// 查询信息指标  查询信息指标需要过滤
//							List<MetricData> infoMetrics = metricDataService.getMetricInfoDatas(resourceInstance.getId(), InfoMetricList.toArray(new String[InfoMetricList.size()]));
							List<MetricData> infoMetrics = infoMetricQueryAdaptService.getMetricInfoDatas(resourceInstance.getId(), InfoMetricList.toArray(new String[InfoMetricList.size()]));

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
							
							Result<List<MoValue>> result = new Result<List<MoValue>>();
							result.setData(listMoValues);
							result.setResultCode(ResultCode.SUCCESS);
							logger.info("Invoking sendMoAttributeValue...");
					        String sendValue = JSONObject.toJSONString(result);
					        logger.info("send values is..." + sendValue);
					        try {
					            String resultStr = port.sendMos(sendValue);
					            logger.info("resultStr---"+resultStr);
					        } catch (WSException e) {
					        	logger.error("Expected exception: WSException has occurred.", e);
					        }
						}
						
					}

				}
			}catch(Exception ex) {
				logger.error("同步资源失败,错误的URL:" + cmdbWebServiceBo.getURL());
			}
		}
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