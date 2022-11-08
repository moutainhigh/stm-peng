package com.mainsteam.stm.dsl.expression;

import com.mainsteam.stm.dsl.expression.exception.OCELException;

public interface OCELBooleanExpression extends OCELExpression {
    OCELBooleanExpression getCause();

    @Override
    Boolean evaluate(OCELContext context) throws OCELException;

    @Override
    Boolean evaluate(OCELContext context, OCELContext lastContext) throws OCELException;
}
