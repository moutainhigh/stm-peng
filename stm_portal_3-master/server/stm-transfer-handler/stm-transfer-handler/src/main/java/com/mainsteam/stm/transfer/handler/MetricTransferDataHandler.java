/**
 * 
 */
package com.mainsteam.stm.transfer.handler;

import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessEngine;
import com.mainsteam.stm.transfer.TransferDataHandler;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * @author ziw
 * 
 */
public class MetricTransferDataHandler implements TransferDataHandler {

	private MetricDataProcessEngine metricDataProcessEngine;

	/**
	 * 
	 */
	public MetricTransferDataHandler() {
	}

	/**
	 * @param metricDataProcessEngine
	 *            the metricDataProcessEngine to set
	 */
	public final void setMetricDataProcessEngine(
			MetricDataProcessEngine metricDataProcessEngine) {
		this.metricDataProcessEngine = metricDataProcessEngine;
	}

	public void start() {
		// do noting.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.transfer.TransferDataHandler#getDataTransferType()
	 */
	@Override
	public TransferDataType getDataTransferType() {
		return TransferDataType.MetricData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.transfer.TransferDataHandler#handle(com.mainsteam
	 * .oc.transfer.obj.TransferData)
	 */
	@Override
	public void handle(TransferData data) {
		metricDataProcessEngine.handle((MetricCalculateData) data.getData());
	}
}
