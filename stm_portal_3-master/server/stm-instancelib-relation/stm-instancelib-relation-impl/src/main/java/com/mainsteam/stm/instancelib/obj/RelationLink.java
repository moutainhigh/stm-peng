package com.mainsteam.stm.instancelib.obj;

public class RelationLink {

	public RelationLink(){}
	
	public RelationLink(long fromInstanceId,long toInstanceId){
		from = fromInstanceId;
		to = toInstanceId;
	}
	
	private long from;
	
	private long to;

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return to;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public void setTo(long to) {
		this.to = to;
	}
	
}
