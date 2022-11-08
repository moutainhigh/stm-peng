package com.mainsteam.stm.dsl.expression;

import com.mainsteam.stm.dsl.expression.exception.OCELException;

/**
 *
 */
public interface OCELEngine {

    void init();

    OCELExpression createExpression(String expression) throws OCELException;

    OCELBooleanExpression createBooleanExpression(String booleanExpression) throws OCELException;
}
