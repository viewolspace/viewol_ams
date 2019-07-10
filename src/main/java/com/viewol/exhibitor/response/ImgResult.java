package com.viewol.exhibitor.response;

import java.util.Map;

/**
 * describe:
 *
 * @author: shi_lei@suixingpay.com
 * @date: 2019/07/10 22:33:22:33
 * @version: V1.0
 * @review:
 */
public class ImgResult {
    private int code;
    private String msg;
    private Map<String, String> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
