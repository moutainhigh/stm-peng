package com.mainsteam.stm.dsl.expression.antlr4.exception;

import com.mainsteam.stm.dsl.expression.exception.OCELException;

public class EvaluatorException extends OCELException {
    public EvaluatorException() {
        super();
    }

    public EvaluatorException(String message) {
        super(message);
    }

    public EvaluatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public EvaluatorException(Throwable cause) {
        super(cause);
    }
}
