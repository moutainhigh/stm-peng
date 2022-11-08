/**
 *
 */
package com.mainsteam.stm.pluginprocessor;

import com.mainsteam.stm.pluginsession.PluginResultSet;

/**
 * 结果集，用来对plugin的返回值做数据加工<br>
 * 针对结果集里的列范围，不做强类型的验证。允许其某一行数据的列索引超出metainfo的规定的范围之外或达不到metainfo.<br>
 * 如果达不到metainfo的规定范围，而且该范围被访问时，返回null.<br>
 *
 * @author ziw
 */
public class ResultSet extends PluginResultSet implements Cloneable {

    private ResultSetMetaInfo metaInfo;

    public ResultSet(PluginResultSet resultSet, ResultSetMetaInfo metaInfo) {
        super(resultSet);
        this.metaInfo = metaInfo;
    }

    public ResultSetMetaInfo getResultMetaInfo() {
        return this.metaInfo;
    }

    private int getColumnIndexByName(String columnName) {
        int column = this.metaInfo.indexOfColumnName(columnName);
        if (column < 0) {
            throw new RuntimeException(columnName + " is not exist.");
        }
        return column;
    }

    public void putValue(int row, String columnName, String value) {
        int column = getColumnIndexByName(columnName);
        this.putValue(row, column, value);
    }

    public String[] getColumn(String columnName) {
        int column = getColumnIndexByName(columnName);
        return getColumn(column);
    }

    @Override
    public int getColumnLength() {
        if (this.metaInfo != null && !metaInfo.isEmpty()) {
            return this.metaInfo.getColumnLength();
        } else {
            return super.getColumnLength();
        }
    }

    public void removeColumnByTitle(String columnTitle) {
        int index = this.metaInfo.indexOfColumnName(columnTitle);
        this.removeColumn(index);
    }

    @Override
    public void removeColumn(int column) {
        super.removeColumn(column);
        if (this.metaInfo != null && this.metaInfo.getColumnLength() > column) {
            this.metaInfo.removeColumnName(column);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Headers:").append("\r\n");
        if (this.metaInfo != null) {
            builder.append(this.metaInfo.toString()).append("\r\n");
        }
        builder.append("datas:").append("\r\n");
        builder.append(super.toString());
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        return new ResultSet(
                (PluginResultSet) super.clone(),
                (ResultSetMetaInfo) (metaInfo == null ? null : metaInfo.clone()));
    }
}
