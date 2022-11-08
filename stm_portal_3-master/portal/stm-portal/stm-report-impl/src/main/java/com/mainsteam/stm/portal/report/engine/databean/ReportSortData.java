package com.mainsteam.stm.portal.report.engine.databean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ReportSortData implements Comparable<ReportSortData> {
	String value;
	Date time;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	public int compareTo(ReportSortData data) {
		return this.time.compareTo(data.getTime());
	}
	
	public static void main(String[] args) {
		ReportSortData d=new ReportSortData();
		d.setValue("AA,AA,AA");
		d.setTime(new Date("2014/10/02"));
		
		ReportSortData d2=new ReportSortData();
		d2.setValue("BB,b,c");
		d2.setTime(new Date("2014/10/01"));
		
		ReportSortData d3=new ReportSortData();
		d3.setValue("CC,b,c");
		d3.setTime(new Date("2014/10/03"));
		List<ReportSortData> list=new ArrayList<ReportSortData>();
		list.add(d);
		list.add(d2);
		list.add(d3);
	
		Collections.sort(list);  
		
		for(ReportSortData date:list){
			
			System.out.println(date.getValue());
		}
		
		
		
	}

}
