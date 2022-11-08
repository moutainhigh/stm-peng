package com.mainsteam.stm.discovery.obj;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
/**
 * 发现资源实例返回给页面对象
 * @author shaw
 *
 */
public class DiscoverResourceIntanceResult {

	/**
	 * 资源实例重复标识
	 */
	public static final int RESOURCEINSTANCE_REPEAT  = 1;
	/**
	 * 操作资源实例异常
	 */
	public static final int OPERATE_ERROE = 2;
	
	/**
	 * 返回当前错误编码
	 */
	private int code;
	
	/**
	 * 发现是否成功
	 */
	private boolean isSuccess;
	
	
	/**
	 * 当前发现入库后资源实例对象
	 */
	private ResourceInstance resourceIntance;

	private List<Long> repeatIds;
	
	private BaseException baseException;
	
	private String errorMsg;
	
	private Map<InstanceLifeStateEnum,List<Long>> instanceLifeState;

	public boolean isSuccess() {
		return isSuccess;
	}

	public ResourceInstance getResourceIntance() {
		return resourceIntance;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public void setResourceIntance(ResourceInstance resourceIntance) {
		this.resourceIntance = resourceIntance;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the baseException
	 */
	public final BaseException getBaseException() {
		return baseException;
	}


	/**
	 * @param baseException the baseException to set
	 */
	public final void setBaseException(BaseException baseException) {
		this.baseException = baseException;
	}


	/**
	 * @return the errorMsg
	 */
	public final String getErrorMsg() {
		return errorMsg;
	}


	/**
	 * @param errorMsg the errorMsg to set
	 */
	public final void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	public List<Long> getRepeatIds() {
		return repeatIds;
	}


	public void setRepeatIds(List<Long> repeatIds) {
		this.repeatIds = repeatIds;
	}
	
	public void setInstanceLifeState(Map<InstanceLifeStateEnum, List<Long>> instanceLifeState) {
		this.instanceLifeState = instanceLifeState;
	}
	public Map<InstanceLifeStateEnum, List<Long>> getInstanceLifeState() {
		return instanceLifeState;
	}
}
