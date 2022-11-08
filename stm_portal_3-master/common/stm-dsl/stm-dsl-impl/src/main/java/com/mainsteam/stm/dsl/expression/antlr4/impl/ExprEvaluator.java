package com.mainsteam.stm.dsl.expression.antlr4.impl;

import com.mainsteam.stm.dsl.expression.antlr4.gen.ExprParser;
import com.mainsteam.stm.dsl.expression.antlr4.gen.ExprParserBaseListener;
import com.mainsteam.stm.dsl.expression.antlr4.impl.exception.TypeConstraintException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;

public class ExprEvaluator extends ExprParserBaseListener {

    private Map<String, Object> variables;
    private Map<String, Object> lastVariables;
    private ParseTreeProperty<Object> values = new ParseTreeProperty<>();

    public ExprEvaluator(Map<String, Object> variables) {
        this.variables = variables;
        normalizeVariables(variables);
    }

    public ExprEvaluator(Map<String, Object> variables, Map<String, Object> lastVariables) {
        this.variables = variables;
        this.lastVariables = lastVariables;
        normalizeVariables(variables);
        normalizeVariables(lastVariables);
    }

    private void normalizeVariables(Map<String, Object> variables) {
        for (Map.Entry<String, Object> variable : variables.entrySet()) {
            String key = variable.getKey();
            Object value = variable.getValue();
            if (value instanceof Float) {
                variables.put(key, new BigDecimal(((Float) value)));
            } else if (value instanceof Double) {
                variables.put(key, new BigDecimal(((Double) value)));
            } else if (value instanceof Byte) {
                variables.put(key, new BigDecimal(((Byte) value)));
            } else if (value instanceof Short) {
                variables.put(key, new BigDecimal(((Short) value)));
            } else if (value instanceof Integer) {
                variables.put(key, new BigDecimal(((Integer) value)));
            } else if (value instanceof Long) {
                variables.put(key, new BigDecimal(((Long) value)));
            } else if (value instanceof BigInteger) {
                variables.put(key, new BigDecimal(((BigInteger) value)));
            }
        }
    }

    private void saveValue(ParseTree node, Object value) {
        values.put(node, value);
    }

    public Object loadValue(ParseTree node) {
        return values.get(node);
    }

    @Override
    public void exitSingleExpression(ExprParser.SingleExpressionContext ctx) {
        saveValue(ctx, loadValue(ctx.expression()));
    }

    @Override
    public void exitExpression(ExprParser.ExpressionContext ctx) {
        if (ctx.predicate() != null) {
            Object value = loadValue(ctx.predicate());
            if (ctx.NOT() != null) {
                if (value instanceof Boolean) {
                    saveValue(ctx, !(Boolean) value);
                } else {
                    throw new TypeConstraintException(TypeConstraintException.getDetailMessage(ctx, values, ctx.predicate()));
                }
            } else {
                saveValue(ctx, value);
            }
        } else {
            Object left = loadValue(ctx.left);
            Object right = loadValue(ctx.right);
            if (left instanceof Boolean && right instanceof Boolean) {
                switch (ctx.op.getType()) {
                    case ExprParser.AND:
                        saveValue(ctx, (Boolean) left && (Boolean) right);
                        break;
                    case ExprParser.OR:
                        saveValue(ctx, (Boolean) left || (Boolean) right);
                        break;
                    case ExprParser.XOR:
                        saveValue(ctx, (Boolean) left ^ (Boolean) right);
                        break;
                }
            } else {
                throw new TypeConstraintException(TypeConstraintException.getDetailMessage(ctx, values, ctx.left, ctx.right));
            }
        }
    }

    private void attemptConvertToNumber(List<? extends ParseTree> nodes) {
        for (ParseTree node : nodes) {
            Object value = loadValue(node);
            if (value == null || value instanceof BigDecimal)
                continue;
            if (value instanceof String) {
                try {
                    new BigDecimal((String) value);
                    continue;
                } catch (NumberFormatException ignored) {
                }
            }
            return;
        }
        for (ParseTree node : nodes) {
            Object value = loadValue(node);
            if (value == null || value instanceof BigDecimal)
                continue;
            if (value instanceof String) {
                saveValue(node, new BigDecimal((String) value));
            }
        }
    }

    @Override
    public void exitComparisonPredicate(ExprParser.ComparisonPredicateContext ctx) {
        attemptConvertToNumber(ctx.compoundExpression());
        Object left = loadValue(ctx.left);
        Object right = loadValue(ctx.right);
        if (left != null && right != null) {
            if (left.getClass() != right.getClass() || !(left instanceof Comparable)) {
                left = left.toString();
                right = right.toString();
            }
            int comparison = ((Comparable) left).compareTo(right);
            switch (ctx.comparisonOperator().getText()) {
                case "==":
                case "=":
                    saveValue(ctx, comparison == 0);
                    break;
                case "<>":
                case "!=":
                    saveValue(ctx, comparison != 0);
                    break;
                case ">=":
                    saveValue(ctx, comparison >= 0);
                    break;
                case ">":
                    saveValue(ctx, comparison > 0);
                    break;
                case "<=":
                    saveValue(ctx, comparison <= 0);
                    break;
                case "<":
                    saveValue(ctx, comparison < 0);
                    break;
            }
        } else {
            throw new TypeConstraintException(TypeConstraintException.getDetailMessage(ctx, values, ctx.left, ctx.right));
        }
    }

    @Override
    public void exitHasChangedPredicate(ExprParser.HasChangedPredicateContext ctx) {
        String id = ctx.id().getText();
        boolean changed = false;
        if (lastVariables != null && lastVariables.get(id) != null)
            changed = !lastVariables.get(id).equals(variables.get(id));
        if (ctx.NOT() != null)
            changed = !changed;
        saveValue(ctx, changed);
    }

    @Override
    public void exitIsNullPredicate(ExprParser.IsNullPredicateContext ctx) {
        boolean isNull = loadValue(ctx.compoundExpression()) == null;
        if (ctx.NOT() != null)
            isNull = !isNull;
        saveValue(ctx, isNull);
    }

    @Override
    public void exitContainsPredicate(ExprParser.ContainsPredicateContext ctx) {
        Object left = loadValue(ctx.left);
        Object right = loadValue(ctx.right);
        if (left != null && right != null) {
            boolean contains = left.toString().contains(right.toString());
            if (ctx.NOT() != null || ctx.NOT_CONTAINS() != null)
                contains = !contains;
            saveValue(ctx, contains);
        } else {
            throw new TypeConstraintException(TypeConstraintException.getDetailMessage(ctx, values, ctx.left, ctx.right));
        }
    }

    @Override
    public void exitCompoundPredicate(ExprParser.CompoundPredicateContext ctx) {
        saveValue(ctx, loadValue(ctx.compoundExpression()));
    }

    @Override
    public void exitCompoundExpression(ExprParser.CompoundExpressionContext ctx) {
        if (ctx.atom() != null) {
            Object value = loadValue(ctx.atom());
            if (ctx.op != null && ctx.op.getType() == ExprParser.MINUS) {
                if (value instanceof BigDecimal) {
                    saveValue(ctx, ((BigDecimal) value).negate());
                } else {
                    throw new TypeConstraintException(TypeConstraintException.getDetailMessage(ctx, values, ctx.atom()));
                }
            } else {
                saveValue(ctx, loadValue(ctx.atom()));
            }
        } else {
            Object left = loadValue(ctx.left);
            Object right = loadValue(ctx.right);
            if (left instanceof BigDecimal && right instanceof BigDecimal) {
                switch (ctx.op.getType()) {
                    case ExprParser.PLUS:
                        saveValue(ctx, ((BigDecimal) left).add((BigDecimal) right));
                        break;
                    case ExprParser.MINUS:
                        saveValue(ctx, ((BigDecimal) left).subtract((BigDecimal) right));
                        break;
                    case ExprParser.STAR:
                        saveValue(ctx, ((BigDecimal) left).multiply((BigDecimal) right));
                        break;
                    case ExprParser.DIVIDE:
                        saveValue(ctx, ((BigDecimal) left).divide((BigDecimal) right, MathContext.DECIMAL32));
                        break;
                    case ExprParser.MODULE:
                        saveValue(ctx, ((BigDecimal) left).divideAndRemainder((BigDecimal) right)[1]);
                        break;
                }
            } else if (left instanceof String && right instanceof String && ctx.op.getType() == ExprParser.AND) {
                saveValue(ctx, (String) left + right);
            } else {
                throw new TypeConstraintException(TypeConstraintException.getDetailMessage(ctx, values, ctx.compoundExpression(0), ctx.compoundExpression(1)));
            }
        }
    }

    @Override
    public void exitAtom(ExprParser.AtomContext ctx) {
        if (ctx.id() != null) {
            saveValue(ctx, variables.get(ctx.id().getText()));
        } else if (ctx.constant() != null) {
            saveValue(ctx, loadValue(ctx.constant()));
        } else if (ctx.expression() != null) {
            saveValue(ctx, loadValue(ctx.expression()));
        }
    }

    @Override
    public void exitConstant(ExprParser.ConstantContext ctx) {
        if (ctx.TRUE() != null) {
            saveValue(ctx, true);
        } else if (ctx.FALSE() != null) {
            saveValue(ctx, false);
        } else if (ctx.STRING() != null) {
            saveValue(ctx, formatString(ctx.STRING().getText()));
        } else if (ctx.INTEGER() != null) {
            saveValue(ctx, new BigDecimal(ctx.INTEGER().getText()));
        } else if (ctx.REAL() != null) {
            saveValue(ctx, new BigDecimal(ctx.REAL().getText()));
        }
    }

    private String formatString(String text) {
        text = text.substring(1, text.length() - 1);
        return text.replaceAll("\\\\\"", "\"");
    }

//    @Override
//    public void exitEveryRule(ParserRuleContext ctx) {
//        if (ctx.exception != null) {
//            for (ParseTree node : ctx.children)
//                System.out.println(node.getClass());
//        }
//    }
}
