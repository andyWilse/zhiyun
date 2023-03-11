package com.religion.zhiyun.utils.response;

import com.religion.zhiyun.sys.menus.entity.MenuList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse {
    private long code;
    private String message;
    private Object result;

    public PageResponse(){
        super();
    }

    public PageResponse(long code, String message, Object result){
        this.code=code;
        this.message=message;
        this.result=result;
    }

}
