package com.mainsteam.stm.dsl.expression.dict;

import java.util.*;

/**
 * A part of terminal symbols for frond end usage, e.g. display and translation.
 * These are almost operators that don't contain any blank.
 *
 * @author lich
 * @version 7.3.1
 * @since 7.3.1
 */
public class TerminalSymbolConst {
    public static final String EQUALS = "==";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_OR_EQUAL_TO = "<=";
    public static final String GREATER_THAN = ">";
    public static final String GREATER_THAN_OR_EQUAL_TO = ">=";
    public static final String NOT_EQUAL_TO = "!=";
    //    public static final String BETWEEN = "BETWEEN";
    public static final String IN = "IN";
    public static final String LIKE = "LIKE";
    public static final String CONTAINS = "CONTAINS";
    public static final String NOT_CONTAINS = "NOT_CONTAINS";
    public static final String IS_NULL = "ISNULL";
    public static final String IS_NOT_NULL = "IS_NOT_NULL";
    public static final String HAS_CHANGED = "HAS_CHANGED";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";

    private static final Map<String, String> ALIAS_MAP;
    private static final Map<String, String> DESCRIPTION_MAP;
    private static final Set<String> LOGICAL_OPERATORS = new LinkedHashSet<>(Arrays.asList(AND, OR));
    private static final Set<String> UNARY_COMPARISON_OPERATORS = new LinkedHashSet<>(Arrays.asList(IS_NULL, HAS_CHANGED));
    private static final Set<String> BINARY_COMPARISON_OPERATORS = new LinkedHashSet<>(Arrays.asList(EQUALS, LESS_THAN, LESS_THAN_OR_EQUAL_TO, GREATER_THAN, GREATER_THAN_OR_EQUAL_TO, NOT_EQUAL_TO, CONTAINS, NOT_CONTAINS));

    static {
        HashMap<String, String> tAliasMap = new HashMap<>();
        tAliasMap.put(EQUALS, EQUALS);
        tAliasMap.put(LESS_THAN, LESS_THAN);
        tAliasMap.put(LESS_THAN_OR_EQUAL_TO, LESS_THAN_OR_EQUAL_TO);
        tAliasMap.put(GREATER_THAN, GREATER_THAN);
        tAliasMap.put(GREATER_THAN_OR_EQUAL_TO, GREATER_THAN_OR_EQUAL_TO);
        tAliasMap.put(NOT_EQUAL_TO, NOT_EQUAL_TO);
        tAliasMap.put(IN, IN);
        tAliasMap.put(LIKE, LIKE);
        tAliasMap.put(CONTAINS, CONTAINS);
        tAliasMap.put(NOT_CONTAINS, NOT_CONTAINS);
        tAliasMap.put(IS_NULL, IS_NULL);
        tAliasMap.put(IS_NOT_NULL, IS_NOT_NULL);
        tAliasMap.put(HAS_CHANGED, HAS_CHANGED);
        tAliasMap.put(AND, AND);
        tAliasMap.put(OR, OR);
        tAliasMap.put(NOT, NOT);

        tAliasMap.put("<>", NOT_EQUAL_TO);
        tAliasMap.put("IS NULL", IS_NULL);
        tAliasMap.put("IS NOT NULL", IS_NOT_NULL);
        tAliasMap.put("HAS CHANGED", HAS_CHANGED);
        tAliasMap.put("NOT CONTAINS", NOT_CONTAINS);
        ALIAS_MAP = Collections.unmodifiableMap(tAliasMap);
    }

    static {
        HashMap<String, String> tDescriptionMap = new HashMap<>();
        tDescriptionMap.put(EQUALS, "=");
        tDescriptionMap.put(LESS_THAN, "<");
        tDescriptionMap.put(LESS_THAN_OR_EQUAL_TO, "≤");
        tDescriptionMap.put(GREATER_THAN, ">");
        tDescriptionMap.put(GREATER_THAN_OR_EQUAL_TO, "≥");
        tDescriptionMap.put(NOT_EQUAL_TO, "≠");
        tDescriptionMap.put(IN, "包含于");
        tDescriptionMap.put(LIKE, "似于");
        tDescriptionMap.put(CONTAINS, "包含");
        tDescriptionMap.put(NOT_CONTAINS, "不包含");
        tDescriptionMap.put(IS_NULL, "为空");
        tDescriptionMap.put(IS_NOT_NULL, "不为空");
        tDescriptionMap.put(HAS_CHANGED, "发生变化");
        tDescriptionMap.put(AND, "与");
        tDescriptionMap.put(OR, "或");
        tDescriptionMap.put(NOT, "非");
        DESCRIPTION_MAP = Collections.unmodifiableMap(tDescriptionMap);
    }

    /**
     * Get the aliases of a symbol.
     *
     * @param symbol a symbol string.
     * @return the alias list of the symbol, including the formal one. If the symbol doesn't match any of the pre-defined ones, return an empty list.
     */
    public List<String> getAlias(String symbol) {
        List<String> result = new ArrayList<>();
        String formal = getFormal(symbol);
        if (formal != null) {
            for (Map.Entry<String, String> entry : ALIAS_MAP.entrySet()) {
                if (entry.getValue().equals(formal))
                    result.add(entry.getKey());
            }
        }
        return result;
    }

    /**
     * Get the pre-defined formal notation of a symbol.
     *
     * @param symbol a symbol string.
     * @return the formal symbol of the input one. If the input symbol doesn't match any of the pre-defined ones, return null.
     */
    public String getFormal(String symbol) {
        return ALIAS_MAP.get(symbol);
    }

    /**
     * Get the description of a symbol.
     *
     * @param symbol a symbol string.
     * @return the description the symbol. If the input symbol doesn't match any of the pre-defined ones, return null.
     */
    public String getDescription(String symbol) {
        String formal = getFormal(symbol);
        if (formal != null) {
            return DESCRIPTION_MAP.get(formal);
        }
        return null;
    }

    /**
     * Get the description and formal-notation map.
     *
     * @return the map whose key set is the formal notation and the value set is the description.
     */
    public Map<String, String> getDescriptionMap() {
        return DESCRIPTION_MAP;
    }

    public Set<String> getLogicalOperators() {
        return LOGICAL_OPERATORS;
    }

    public Set<String> getUnaryComparisonOperators() {
        return UNARY_COMPARISON_OPERATORS;
    }

    public Set<String> getBinaryComparisonOperators() {
        return BINARY_COMPARISON_OPERATORS;
    }
}


