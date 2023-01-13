package com.religion.zhiyun.utils;

import java.util.List;

public class RespPageBean {

    private Long total;
    private List<?> data;
    private Object[] datas;

    public Long getTotal()
    {
        return total;
    }

    public void setTotal(Long total)
    {
        this.total = total;
    }

    public List<?> getData()
    {
        return data;
    }

    public void setData(List<?> data)
    {
        this.data = data;
    }

    public Object[] getDatas() {
        return datas;
    }

    public void setDatas(Object[] datas) {
        this.datas = datas;
    }
}
