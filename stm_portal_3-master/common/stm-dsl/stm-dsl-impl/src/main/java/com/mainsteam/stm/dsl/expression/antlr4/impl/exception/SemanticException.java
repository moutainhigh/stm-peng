package com.mainsteam.stm.dsl.expression.antlr4.impl.exception;

public class SemanticException extends RuntimeException {
    public SemanticException() {
        super();
    }

    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }

    public SemanticException(Throwable cause) {
        super(cause);
    }
}
