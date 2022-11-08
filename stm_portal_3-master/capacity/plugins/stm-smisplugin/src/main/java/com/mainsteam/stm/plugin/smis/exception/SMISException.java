package com.mainsteam.stm.plugin.smis.exception;

public class SMISException extends Exception {

    private static final long serialVersionUID = 4060784233874291302L;

    public SMISException() {
        super();
    }

    public SMISException(String message, Throwable cause) {
        super(message, cause);
    }

    public SMISException(String message) {
        super(message);
    }

    public SMISException(Throwable cause) {
        super(cause);
    }

}
