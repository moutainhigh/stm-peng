package com.mainsteam.stm.dsl.expression.antlr4.impl.exception;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class TypeConstraintException extends SemanticException {

    public TypeConstraintException() {
        super();
    }

    public TypeConstraintException(String message) {
        super(message);
    }

    public TypeConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeConstraintException(Throwable cause) {
        super(cause);
    }

    public static String getDetailMessage(ParseTree parent, ParseTreeProperty<Object> values, ParseTree... nodes) {
        StringBuilder sb = new StringBuilder();
        sb.append(parent.getText()).append("\t");
        for (ParseTree node : nodes) {
            Object value = values.get(node);
            sb.append(node == null ? null : node.getText()).append(":").append(value == null ? null : value.getClass()).append(":").append(value).append("\t");
        }
        return sb.toString();
    }
}
