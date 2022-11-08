package com.mainsteam.stm.profile.fault.execute.obj;

import java.io.Serializable;

public class FaultScriptExecuteResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4027625747658339965L;

	/**
	 * 快照脚本执行结果文件内容
	 */
	private String snapshotFileContent;
	
	/**
	 * 恢复脚本执行结果文件内容
	 */
	private String recoveryFileContent;

	public String getSnapshotFileContent() {
		return snapshotFileContent;
	}

	public void setSnapshotFileContent(String snapshotFileContent) {
		this.snapshotFileContent = snapshotFileContent;
	}

	public String getRecoveryFileContent() {
		return recoveryFileContent;
	}

	public void setRecoveryFileContent(String recoveryFileContent) {
		this.recoveryFileContent = recoveryFileContent;
	}
	
	
}
