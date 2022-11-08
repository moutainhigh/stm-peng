package com.mainsteam.stm.alarm;

public class AlarmFile {
	private Long snapshotFileId;
	private String snapshotFileName;
	private Long recoverFileId;
	private String recoverFileName;
	
	public Long getSnapshotFileId() {
		return snapshotFileId;
	}
	public void setSnapshotFileId(Long snapshotFileId) {
		this.snapshotFileId = snapshotFileId;
	}
	public String getSnapshotFileName() {
		return snapshotFileName;
	}
	public void setSnapshotFileName(String snapshotFileName) {
		this.snapshotFileName = snapshotFileName;
	}
	public Long getRecoverFileId() {
		return recoverFileId;
	}
	public void setRecoverFileId(Long recoverFileId) {
		this.recoverFileId = recoverFileId;
	}
	public String getRecoverFileName() {
		return recoverFileName;
	}
	public void setRecoverFileName(String recoverFileName) {
		this.recoverFileName = recoverFileName;
	}
	
}
