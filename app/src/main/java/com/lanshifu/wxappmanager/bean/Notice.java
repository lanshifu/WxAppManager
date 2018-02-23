package com.lanshifu.wxappmanager.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by lanxiaobin on 2018/2/23.
 */

public class Notice extends BmobObject {

    private String title;
    private String number;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
