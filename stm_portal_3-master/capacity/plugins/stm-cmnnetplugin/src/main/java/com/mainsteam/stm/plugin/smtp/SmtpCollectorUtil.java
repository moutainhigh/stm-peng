package com.mainsteam.stm.plugin.smtp;

public class SmtpCollectorUtil {
	private SmtpCollector smtpCollector;
	public SmtpCollectorUtil() {
		smtpCollector=new SmtpCollector();
	}
	public String ip(SmtpBo smtpBo){
		return smtpBo.getIp();
	}
	public String port(SmtpBo smtpBo){
		return smtpBo.getPort();
	}
	public String availability(SmtpBo smtpBo){
		return smtpCollector.availability(smtpBo);
	}
	/**
	 * 响应时间
	 * @param smtpBo
	 * @return
	 */
	public String responseTime(SmtpBo smtpBo){
		return smtpCollector.ResponseTime();
	}
	/**
	 * 给plunginsession的isalive调用
	 * @param smtpBo
	 * @return
	 */
	public boolean init(SmtpBo smtpBo){
		return smtpCollector.connect(smtpBo);
	}
}
