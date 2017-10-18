package com.xinlingyijiu.yanchat.core.service;

import com.xinlingyijiu.yanchat.core.Constant;
import com.xinlingyijiu.yanchat.core.Context;
import com.xinlingyijiu.yanchat.core.bean.ConnectMsg;
import com.xinlingyijiu.yanchat.core.net.Connect;
import com.xinlingyijiu.yanchat.core.user.User;
import com.xinlingyijiu.yanchat.util.ScheduledExecutorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class OnlineServiceImpl implements OnlineService {
    private Connect connect;
    private List<Consumer<User>> afterList;

    public OnlineServiceImpl() {
        afterList = new ArrayList<>();
    }

    @Override
    public void setConnect(Connect connect) {
        this.connect = connect;
    }

    @Override
    public Connect getConnet() {
        return this.connect;
    }

    @Override
    public void receive(User user) {
        Context.getInstance().getUserManager().online(user);
        executeAllAfter(user);
    }

    @Override
    public void after(Consumer<User> after) {
        this.afterList.add(after);
    }

    @Override
    public void executeAllAfter(User user) {
        if (this.afterList.isEmpty()) return;
        for (Consumer<User> after : afterList) {
            try {
                after.accept(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onlinePolling(long cycleTime) {
        Context context = Context.getInstance();
        User currentUser = context.getUserContext().getCurrentUser();
        ConnectMsg<User> connectMsg = new ConnectMsg<>(Constant.MSG_TYPE.ONLINE, currentUser);
        byte[] byteBroadcastMsg = context.getMsgHandleContext().getHandle(Constant.DATA_TYPE.TEXT).apply(connectMsg.toJSONString());
        Runnable runnable = () -> {
            try {
                context.getUdpConnect().send("localhost", byteBroadcastMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        ScheduledExecutorUtil.getScheduler().scheduleAtFixedRate(runnable,0,cycleTime, TimeUnit.SECONDS);
    }
}
