package com.mainsteam.stm.dsl.expression;

import com.mainsteam.stm.dsl.expression.exception.OCELException;

public interface OCELExpression {
    int MAX_LENGTH = 1 << 12;

    Object evaluate(OCELContext context) throws OCELException;

    Object evaluate(OCELContext context, OCELContext lastContext) throws OCELException;

    boolean checkChange();
}
