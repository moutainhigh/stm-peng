package com.mainsteam.stm.dsl.expression.antlr4.exception;

import com.mainsteam.stm.dsl.expression.exception.OCELException;

public class ParserException extends OCELException {
    public ParserException() {
        super();
    }

    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String expression, String cause) {
        this("expression:" + expression + ".\t" + "cause:" + cause);
    }
}
