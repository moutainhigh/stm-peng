package com.mainsteam.stm.dcimmanage.vo;

import com.mainsteam.stm.dcimmanage.vo.DcimResourceVo;
import com.mainsteam.stm.platform.web.vo.BasePageVo;

import java.util.Collection;

public class DcimResourcePageVo implements BasePageVo {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    private long startRow;
    private long rowCount;
    private long totalRecord;
    private DcimResourceVo condition;
    private String sort;
    private String order;


    public long getStartRow() {
        return startRow;
    }

    public long getRowCount() {
        return rowCount;
    }

    public long getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public void setStartRow(long startRow) {
        this.startRow = startRow;
    }

    @Override
    public void setRowCount(long rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public long getTotal() {
        return this.totalRecord;
    }

    @Override
    public Collection<? extends Object> getRows() {
        return null;
    }

    public DcimResourceVo getCondition() {
        return condition;
    }

    public void setCondition(DcimResourceVo condition) {
        this.condition = condition;
    }
}
