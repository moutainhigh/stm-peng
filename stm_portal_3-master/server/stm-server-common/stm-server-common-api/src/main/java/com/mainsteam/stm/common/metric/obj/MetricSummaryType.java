package com.mainsteam.stm.common.metric.obj;

import java.util.Calendar;
import java.util.Date;

/**指标汇总分类
 * @author heshengchao
 *
 */
public enum MetricSummaryType {

	/**一小时汇总:HOUR*/
	H(60),
	/**六小时汇总:SIX_HOUR*/
	SH(60*6),
	/**24小时汇总:DAY*/
	D(60*24),
	/**半小时:HALF_HOUR*/
	HH(30);
	
	private int duaration;
	
	MetricSummaryType(int duaration){
		this.duaration=duaration;
	}
	
	public Date getPrePeriodStart(Date time){
		return getPrePeroid(time,1);
	}

	private Date getPrePeroid(Date time,int pre) {
		Calendar cal=Calendar.getInstance();
		cal.setTime(time);
		int min=cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
		int p=min/duaration;
		
		cal.set(Calendar.HOUR,(p-pre)*duaration/60);
		cal.set(Calendar.MINUTE,(p-pre)*duaration%60);
		return cal.getTime();
	}
	public Date getPrePeriodEnd(Date time){
		return getPrePeroid(time,0);
	}
	
	public MetricSummaryType next(){
		switch(this){
			case D:
				return SH;
			case SH:
				return H;
			case H:
				return HH;
			default:
				return null;
		}
	}
}
