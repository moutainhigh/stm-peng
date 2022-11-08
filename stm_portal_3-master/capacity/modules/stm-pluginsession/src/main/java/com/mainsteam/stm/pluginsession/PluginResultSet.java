/**
 *
 */
package com.mainsteam.stm.pluginsession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * plugin执行的结果集<br>
 * 提供的方法，都是非线程安全的<br>
 *
 * @author ziw
 */
public class PluginResultSet implements Cloneable {

    private List<List<String>> rows = new ArrayList<>();

    /**
     * 存放session中和计算相关的扩展参数。不是结果，只是计算时需要根据前面的步骤动态设置。
     * <p>
     * 目前是SnmpPluginSession，会放置snmp的版本（32位oid或者64位oid），
     * 后续的processer会版本选择不同的oid处理方式。
     */
    private Map<Object, Object> extraReturnValue;

    private int column;

    public PluginResultSet() {
        column = 0;
    }

    protected PluginResultSet(PluginResultSet set) {
        if (null == set) {
            return;
        }
        if (set.rows == null) {
            this.rows = new ArrayList<>();
        } else {
            this.rows = set.rows;
        }
        this.column = set.column;
        if (set.extraReturnValue != null && !set.extraReturnValue.isEmpty())
            this.extraReturnValue = new HashMap<Object, Object>(set.extraReturnValue);

    }

    public int getColumnLength() {
        return this.column;
    }

    private void validateRow(int row) {
        if (row < 0 || row >= this.rows.size()) {
            throw new RuntimeException(row + " is out of range of the rows.i");
        }
    }

    public String getValue(int row, int column) {
        validateRow(row);
        List<String> rowList = rows.get(row);
        return rowList.size() > column ? rowList.get(column) : null;
    }

    public void putValue(int row, int column, Object value) {
        putValue(row, column, value == null ? null : value.toString());
    }

    public void putValue(int row, int column, String value) {

        if (this.rows.size() > row && this.rows.get(row).size() > column) {
            this.rows.get(row).set(column, value);
        } else {
            // 根据最大行和最大列进行填值
            // 当行数不足时，新增行
            while (rows.size() <= row) {
                this.addRow(new String[]{null});
            }
            // 取得最大列数
            int maxColumnCount = column + 1;
            for (int i = 0; i < this.rows.size(); i++) {
                try {
                    if (this.rows.get(i).size() > maxColumnCount) {
                        maxColumnCount = this.rows.get(i).size();
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            // 按照最大的行数和列数进行赋值。如果本身存在值。则需要赋值
            for (int i = 0; i < this.rows.size(); i++) {
                for (int j = 0; j < maxColumnCount; j++) {

                    try {
                        this.rows.get(i).get(j);
                    } catch (IndexOutOfBoundsException e) {
                        // 匹配到异常，表明该值还设置
                        this.rows.get(i).add(null);
                    }
                }
            }
            this.column = maxColumnCount;
            // 取得结果集后，最后设置值
            this.rows.get(row).set(column, value);

        }

    }

    public String[] getRow(int row) {
        validateRow(row);
        List<String> rowList = rows.get(row);
        return rowList.toArray(new String[rowList.size()]);
    }

    public String[] getColumn(int column) {
        List<String> columnData = new ArrayList<>(this.rows.size());
        for (List<String> row : rows) {
            if (row.size() > column) {
                columnData.add(row.get(column));
            } else {
                columnData.add(null);
            }
        }
        return columnData.toArray(new String[columnData.size()]);
    }

    public void addRow(String[] values) {
        addRow(-1, values);
    }
    
    public void addRow(int rowIndex,String[] values) {
    	List<String> row = new ArrayList<>(values.length);
        if (values != null && values.length > 0) {
            for (String v : values) {
                row.add(v);
            }
        }
        if (row.size() > column) {
            this.column = row.size();
        }
        if(rowIndex < 0 || rowIndex>=this.rows.size()) {
            this.rows.add(row);
        }else {
        	this.rows.add(rowIndex,row);
        }
    }

    public int getRowLength() {
        return this.rows.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (List<String> row : rows) {
            builder.append(row).append(".length=")
                    .append(row == null ? -1 : row.size()).append("\r\n");
        }
        builder.append(" extraReturnValue=").append(extraReturnValue);
        return builder.toString();
    }

    public void removeRow(int row) {
        validateRow(row);
        this.rows.remove(row);
        computeColumn();
    }
    
    public void replaceRow(int row,String[] values) {
    	 validateRow(row);
    	 List<String> rowList = new ArrayList<>(values.length);
         if (values != null && values.length > 0) {
             for (String v : values) {
            	 rowList.add(v);
             }
         }
    	 this.rows.set(row, rowList);
    	 computeColumn();
    }

    @Deprecated
    public void clearRows() {
        this.rows.clear();
        computeColumn();
    }

    public void clear() {
        rows.clear();
        column = 0;
    }

    private void computeColumn() {
        int columnCount = 0;
        for (List<String> row : rows) {
            if (row.size() > columnCount) {
                columnCount = row.size();
            }
        }
        this.column = columnCount;
    }

    public void removeColumn(int column) {
        boolean hasRemoved = false;
        for (List<String> row : rows) {
            if (row.size() > column) {
                row.remove(column);
                hasRemoved = true;
            }
        }
        if (hasRemoved) {
            this.column--;
        }
    }

    public boolean isEmpty() {
        return column == 0;
    }

    /**
     * 将数据行列互转。<br>
     * <p>
     * 第一行，转为第一列。<br>
     * 第二行，转为第二列<br>
     * 依此类推<br>
     */
    public void transfersRanks() {
        int column = -1;
        do {
            column++;
            List<String> newRow = null;
            for (List<String> row : this.rows) {
                if (row.size() > column) {
                    if (newRow == null) {
                        newRow = new ArrayList<>();
                    }
                    newRow.add(row.get(column));
                }
            }
        } while (column > 0);
        computeColumn();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() {
        PluginResultSet resultSet = new PluginResultSet();
        resultSet.column = this.column;
        if (this.rows == null) {
            resultSet.rows = null;
        } else {
            resultSet.rows = new ArrayList<>(this.rows.size());
            for (List<String> list : this.rows) {
                resultSet.rows.add(new ArrayList<>(list));
            }
        }
        if (this.extraReturnValue != null) {
            resultSet.extraReturnValue = new HashMap<>(extraReturnValue);
        }
        return resultSet;
    }

    public void setExtraValue(Object key, Object value) {
        if (key != null) {
            if (this.extraReturnValue == null) {
                this.extraReturnValue = new HashMap<>();
            }
            this.extraReturnValue.put(key, value);
        }
    }

    /**
     * @param key
     * @return 使用时注意，返回的对象没有经过克隆，其的属性不要修改，否则可能会影响其它线程的计算。
     */
    public Object getExtraValue(Object key) {
        if (key != null && this.extraReturnValue != null) {
            return this.extraReturnValue.get(key);
        }
        return null;
    }

    public static final class ExtraValueConstants {
        public final static String COLLECT_FAIL_EXCEPTION = "CollectFailException";
    }
}
