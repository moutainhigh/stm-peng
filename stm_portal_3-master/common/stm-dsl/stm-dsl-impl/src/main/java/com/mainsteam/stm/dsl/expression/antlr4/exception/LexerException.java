package com.mainsteam.stm.dsl.expression.antlr4.exception;

import com.mainsteam.stm.dsl.expression.exception.OCELException;

public class LexerException extends OCELException {
    public LexerException() {
        super();
    }

    public LexerException(String message) {
        super(message);
    }

    public LexerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LexerException(Throwable cause) {
        super(cause);
    }

    public LexerException(String expression, String cause) {
        this("expression:" + expression + ".\t" + "cause:" + cause);
    }

}
