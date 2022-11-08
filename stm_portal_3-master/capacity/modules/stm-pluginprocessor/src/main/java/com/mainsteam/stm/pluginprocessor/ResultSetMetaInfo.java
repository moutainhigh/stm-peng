/**
 *
 */
package com.mainsteam.stm.pluginprocessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugin执行结果的头信息
 *
 * @author ziw
 */
public class ResultSetMetaInfo implements Cloneable {

    private List<String> columnNames;

    public ResultSetMetaInfo() {
        columnNames = new ArrayList<>(6);
    }

    public ResultSetMetaInfo(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public static void main(String[] argv) {
        List<String> cns = new ArrayList<>();
        cns.add("A");
        cns.add("B");
        cns.add("C");
        ResultSetMetaInfo mi = new ResultSetMetaInfo(cns);
        System.out.println(mi.toString());
    }

    /**
     * 获取列的长度
     *
     * @return int
     */
    public int getColumnLength() {
        return columnNames.size();
    }

    public boolean isEmpty() {
        return columnNames.isEmpty();
    }

    /**
     * 获取列的名称
     *
     * @param index 列索引
     * @return String 列名称
     */
    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    /**
     * 获取列的名称列表
     *
     * @return String[] 列名称列表
     */
    public String[] getColumnNames() {
        return columnNames.toArray(new String[columnNames.size()]);
    }

    /**
     * 返回列名称对应的索引
     *
     * @param columnName
     * @return 索引
     */
    public int indexOfColumnName(String columnName) {
        return columnNames.indexOf(columnName);
    }

    /**
     * 添加列
     *
     * @param columnName
     */
    public void addColumnName(String columnName) {
        columnNames.add(columnName);
    }

    /**
     * 修改列
     *
     * @param index
     * @param columnName
     */
    public void setColumnName(int index, String columnName) {
        columnNames.set(index, columnName);
    }

    /**
     * 删除列
     *
     * @param index 列索引
     */
    public void removeColumnName(int index) {
        columnNames.remove(index);
    }

    /**
     * 删除列
     *
     * @param columnName 列名称
     */
    public void removeColumnName(String columnName) {
        columnNames.remove(columnName);
    }

    /**
     * 删除所有的columnName
     */
    public void removeAllColumnNames() {
        columnNames.clear();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < columnNames.size(); i++) {
            if (i > 0) {
                b.append(",");
            }
            b.append(columnNames.get(i));
        }
        return b.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() {
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.columnNames = new ArrayList<>(this.columnNames);
        return metaInfo;
    }
}
