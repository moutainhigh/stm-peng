package com.mainsteam.stm.portal.netflow.bo;

public class DeviceGroupNetflowBo {

	private int id;
	private String name;
	private long inFlow = 0;
	private long outFlow = 0;
	private long inPackage = 0;
	private long outPackage = 0;
	private double inSpeed = 0.0;
	private double outSpeed = 0.0;
	private double accounting = 0.0;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInFlow() {
		return format(inFlow / (1024 * 2024.00)) + "MB";
	}

	public void setInFlow(long inFlow) {
		this.inFlow = inFlow;
	}

	public String getOutFlow() {
		return format(outFlow / (1024 * 2024.00)) + "MB";
	}

	public void setOutFlow(long outFlow) {
		this.outFlow = outFlow;
	}

	public String getTotalFlow() {
		return format(getTFlow() / (1024 * 2024.00)) + "MB";
	}

	public long getTFlow() {
		return this.inFlow + this.outFlow;
	}

	public String getInPackage() {
		return inPackage + "";
	}

	public void setInPackage(long inPackage) {
		this.inPackage = inPackage;
	}

	public String getOutPackage() {
		return outPackage + "";
	}

	public void setOutPackage(long outPackage) {
		this.outPackage = outPackage;
	}

	public String getTotalPackage() {
		return (inPackage + outPackage) + "";
	}

	public String getInSpeed() {
		return format(inSpeed / (1024 * 2024.00)) + "Mbps";
	}

	public void setInSpeed(double inSpeed) {
		this.inSpeed = inSpeed;
	}

	public String getOutSpeed() {
		return format(outSpeed / (1024 * 2024.00)) + "Mbps";
	}

	public void setOutSpeed(double outSpeed) {
		this.outSpeed = outSpeed;
	}

	public String getTotalSpeed() {
		return format((this.inSpeed + this.outSpeed) / (1024 * 2024.00))
				+ "Mbps";
	}

	public String getAccounting() {
		return format(accounting) + "%";
	}

	public void setAccounting(double accounting) {
		this.accounting = accounting;
	}

	private String format(double value) {
		return String.format("%.2f", value);
	}
}
