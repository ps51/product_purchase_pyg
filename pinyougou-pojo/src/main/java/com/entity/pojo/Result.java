package com.entity.pojo;

import java.io.Serializable;

/**
 * @program: pinyougou-parent
 * @description: 无数据的时候的返回结果
 * @author: Mr.Cherry
 * @create: 2019-11-06 11:27
 **/
public class Result implements Serializable {
    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
