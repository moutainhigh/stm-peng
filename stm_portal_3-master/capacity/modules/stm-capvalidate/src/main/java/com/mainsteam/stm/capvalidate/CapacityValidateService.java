package com.mainsteam.stm.capvalidate;

import java.util.List;


public class CapacityValidateService {
	
	/**
	 * 验证所有caplibsPath下面的所有xml文件
	 * @param caplibsPath
	 * @return
	 */
	public List<String> validateSchema(String caplibsPath) {
		CapacitySchemaValidate schemaValidate = new CapacitySchemaValidate();
		List<String> schemaError = schemaValidate.validate(caplibsPath);
		return schemaError;
		
	}
	
	/**
	 * 业务验证
	 * @param caplibsPath
	 * @return
	 */
	public List<String> businessValidate(String caplibsPath) {
		CapacityBusinessValidate businessValidate = new CapacityBusinessValidate();
		businessValidate.validate(caplibsPath);
		return businessValidate.getValidateError();
	}
	
}
