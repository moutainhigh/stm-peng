package com.mainsteam.stm.fileTransfer.exception;

import java.io.IOException;

public class FileTransferException extends IOException {
	private static final long serialVersionUID = 1L;

	public FileTransferException(String message) {
        super(message);
    }
}
