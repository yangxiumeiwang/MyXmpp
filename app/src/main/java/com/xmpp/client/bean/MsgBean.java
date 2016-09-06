package com.xmpp.client.bean;

/**
 * Created by yxm on 2016/9/1.
 */
public class MsgBean {
    String userid;
    String msg;
    String date;
    Type type;

    public enum Type {
        INCOMING, OUTCOMING
    }

    public MsgBean(String userid, String msg, String date, Type type) {
        this.userid = userid;
        this.msg = msg;
        this.date = date;
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
