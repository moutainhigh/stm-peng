package com.mainsteam.stm.system.bigdata.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.bigdata.api.IBigdataApi;
import com.mainsteam.stm.system.bigdata.bo.BigdataBo;

/**
 * <li>文件名称: BigdataAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月26日
 * @author   ziwenwen
 */
@Controller
@RequestMapping("/system/bigdata/")
public class BigdataAction extends BaseAction {
	
	@Autowired
	private IBigdataApi bigdataApi;
	
	/**
	 * <pre>
	 * 获取配置信息
	 * </pre>
	 * @return
	 */
	@RequestMapping("get")
	public JSONObject get(){
		return toSuccess(bigdataApi.get());
	}
	
	/**
	 * <pre>
	 * 修改配置信息
	 * </pre>
	 * @return
	 */
	@RequestMapping("save")
	public JSONObject save(String bigdata){
		return toSuccess(bigdataApi.save(JSONObject.parseObject(bigdata, BigdataBo.class)));
	}
}


