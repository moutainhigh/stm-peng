package com.mainsteam.stm.dsl.expression.antlr4.impl;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class RecordErrorListener extends BaseErrorListener {

    private StringBuilder sb = new StringBuilder();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        sb.append("line ").append(line).append(":").append(charPositionInLine).append(" at ").append(offendingSymbol).append(": ").append(msg).append("\n");
    }

    public void reset() {
        sb.setLength(0);
    }

    @Override
    public String toString() {
        String ret = sb.toString();
        if (!ret.isEmpty())
            return ret.substring(0, ret.length() - 1);
        return ret;
    }
}
