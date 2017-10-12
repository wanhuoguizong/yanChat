package com.xinlingyijiu.yanchat.core;

/**
 * Created by laotou on 2017/10/12.
 */
public interface Constant {
    //缺省广播ip
    String BROADCAST_DEFAULT_HOST = "230.250.250.250";


    //默认端口
    interface DEFAULT_PORT{
        int BROADCAST = 9250;
        int TCP = 9350;
        int UDP = 9450;
    }
    //默认广播接收byte[]长度
    int BROADCAST_LISTEN_LEN = 1024;

    /**
     * 消息类型
     */
    interface MSG_TYPE{
        String TEST = "TEXT";//文本
    }
}
