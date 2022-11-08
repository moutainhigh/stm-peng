package com.mainsteam.stm.portal.resource.bo;

import java.io.Serializable;

public class SessionBo implements Serializable {

	private static final long serialVersionUID = -5852184084083169171L;

	private String sid;
	private String seq;
	private String event;
	private String waitTime;
	private String p1;
	private String p2;
	private String p3;
	private String p1text;
	private String p2text;
	private String p3text;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(String waitTime) {
		this.waitTime = waitTime;
	}

	public String getP1() {
		return p1;
	}

	public void setP1(String p1) {
		this.p1 = p1;
	}

	public String getP2() {
		return p2;
	}

	public void setP2(String p2) {
		this.p2 = p2;
	}

	public String getP3() {
		return p3;
	}

	public void setP3(String p3) {
		this.p3 = p3;
	}

	public String getP1text() {
		return p1text;
	}

	public void setP1text(String p1text) {
		this.p1text = p1text;
	}

	public String getP2text() {
		return p2text;
	}

	public void setP2text(String p2text) {
		this.p2text = p2text;
	}

	public String getP3text() {
		return p3text;
	}

	public void setP3text(String p3text) {
		this.p3text = p3text;
	}

}
