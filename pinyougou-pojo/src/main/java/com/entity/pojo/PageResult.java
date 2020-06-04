package com.entity.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @program: pinyougou-parent
 * @description: 分页结果类
 * @author: Mr.Cherry
 * @create: 2019-11-06 10:47
 **/
public class PageResult implements Serializable {

    //总记录数
    private long total;
    //当前页记录
    private List<?> rows;

    public PageResult(long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
