package com.mainsteam.stm.plugin.sunjes.executors;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.amx.DottedNames;
import com.mainsteam.stm.plugin.sunjes.util.CommonUtils;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;

/**
 * JvmExecutor<br>
 */
public class JvmExecutor extends BaseExecutor {

	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接信息
	 */
	public JvmExecutor(final SunJESConnInfo connInfo) {
		this.m_connInfo = connInfo;
		this.m_dottedNames = DottedNames.getInstance(m_connInfo
				.getInstanceName());
	}

	/**
	 * JVM运行时已分配的堆大小
	 * 
	 * @param manager
	 *            AMXManager
	 * @return String
	 */
	public String getJvmCommittedHeapSize(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getJvmCommittedHeapSize());
		double t_MBValue = CommonUtils.convertByteToMByte(Double
				.parseDouble(t_value.toString()));
		return String.valueOf(t_MBValue);
	}

	/**
	 * JVM允许使用的最大堆大小
	 * 
	 * @param manager
	 *            AMXManager
	 * @return String
	 */
	public String getJvmUpperboundHeapSize(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getJvmUpperboundHeapSize());
		double t_MBValue = CommonUtils.convertByteToMByte(Double
				.parseDouble(t_value.toString()));
		return String.valueOf(t_MBValue);
	}

	/**
	 * JVM内存利用率
	 * 
	 * @param manager
	 *            AMXManager
	 * @return String
	 */
	public String getJvmMEMRate(final AMXManager manager) {
		Object t_max = manager.getMonitoringDottedNameValue(m_dottedNames
				.getJvmUpperboundHeapSize());
		Object t_usered = manager.getMonitoringDottedNameValue(m_dottedNames
				.getJvmUsedHeapSize());
		double t_maxSize = Double.parseDouble(t_max.toString());
		double t_usedSize = Double.parseDouble(t_usered.toString());

		double t_rate = CommonUtils.getTwoDecimalResult(t_usedSize * 100
				/ t_maxSize);

		return String.valueOf(t_rate);
	}

	/**
	 * JVM运行时已使用堆大小
	 * 
	 * @param manager
	 *            AMXManager
	 * @return String
	 */
	public String getJvmUsedHeapSize(final AMXManager manager) {
		Object t_value = manager.getMonitoringDottedNameValue(m_dottedNames
				.getJvmUsedHeapSize());
		double t_MBValue = CommonUtils.convertByteToMByte(Double
				.parseDouble(t_value.toString()));
		return String.valueOf(t_MBValue);
	}

}
