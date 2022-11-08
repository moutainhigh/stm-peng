/**
 * 
 */
package com.mainsteam.stm.executor.generate.detail;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.ValueTypeEnum;
import com.mainsteam.stm.caplib.plugin.ParameterDef;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.pluginprocessor.ParameterValue;

/**
 * @author ziw
 * 
 */
public class ParameterValueGenerator {

	private static final Log logger = LogFactory
			.getLog(ParameterValueGenerator.class);

	private ModulePropService modulePropService;

	public void setModulePropService(ModulePropService modulePropService) {
		this.modulePropService = modulePropService;
	}

	/**
	 * 
	 */
	public ParameterValueGenerator() {
	}

	public ParameterValue generateParameterValue(long resourceInstId,
			ParameterDef def, Map<String, String> map)
			throws InstancelibException {
		ParameterValue value = new ParameterValue();
		value.setKey(def.getKey());
		value.setType(def.getType() == null ? null : def.getType().name());
		if (ValueTypeEnum.DiscoveryInfo == def.getType()) {
			/* 从发现信息里拿值 */
			value.setValue(map.get(def.getValue()));
		} else if (ValueTypeEnum.ResourceProperty == def.getType()) {
			// 从资源实例的模型属性里边拿值
			if (resourceInstId >= 0) {
				ModuleProp prop = modulePropService.getPropByInstanceAndKey(
						resourceInstId, def.getValue());
				if (prop != null) {
					String[] propValues = prop.getValues();
					if (propValues == null || propValues.length <= 0) {
						if (logger.isWarnEnabled()) {
							StringBuilder b = new StringBuilder();
							b.append("bindParameterValue prop no value.");
							b.append(" resourceInstId=").append(resourceInstId);
							b.append(" def.key=").append(def.getKey());
							b.append(" propKey=def.value=")
									.append(def.getKey());
							logger.warn(b.toString());
						}
					} else if (propValues.length == 1) {
						value.setValue(propValues[0]);
					} else {
						StringBuilder b = new StringBuilder();
						b.append(propValues[0]);
						int length = propValues.length;
						for (int i = 1; i < length; i++) {
							b.append(',').append(propValues[i]);
						}
						value.setValue(b.toString());
					}
				} else {
					if (logger.isErrorEnabled()) {
						StringBuilder b = new StringBuilder();
						b.append("bindParameterValue prop no exist.");
						b.append(" resourceInstId=").append(resourceInstId);
						b.append(" def.key=").append(def.getKey());
						b.append(" propKey=def.value=").append(def.getKey());
						logger.error(b.toString());
					}
				}
			}
		} else {
			value.setValue(def.getValue());
		}
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("bindParameterValue ");
			b.append(" resourceInstId=").append(resourceInstId);
			b.append(" def.key=").append(def.getKey());
			b.append(" def.value=").append(def.getKey());
			b.append(" resultKey=").append(value.getKey());
			/**
			 * 禁用日志，防止出现密码
			 */
			// b.append(" resultValue=").append(value.getValue());
			logger.debug(b.toString());
		}
		return value;

	}
}
