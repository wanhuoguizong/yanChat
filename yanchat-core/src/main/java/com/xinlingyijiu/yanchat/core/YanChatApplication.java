package com.xinlingyijiu.yanchat.core;

import com.xinlingyijiu.yanchat.core.bean.ConnectMsg;
import com.xinlingyijiu.yanchat.core.consumer.ConnectMsgConsumer;
import com.xinlingyijiu.yanchat.core.exception.YanChatRuntimeException;
import com.xinlingyijiu.yanchat.core.msg.MsgHandleContext;
import com.xinlingyijiu.yanchat.core.msg.StringMsgConverseHandle;
import com.xinlingyijiu.yanchat.core.msg.StringMsgHandle;
import com.xinlingyijiu.yanchat.core.net.broadcast.BroadcastImpl;
import com.xinlingyijiu.yanchat.core.net.socket.SimpleSocketManager;
import com.xinlingyijiu.yanchat.core.net.udp.UDPConnectImpl;
import com.xinlingyijiu.yanchat.core.queue.MsgProducerImpl;
import com.xinlingyijiu.yanchat.core.queue.QueueListennerImpl;
import com.xinlingyijiu.yanchat.core.queue.QueueManagerImpl;
import com.xinlingyijiu.yanchat.core.service.OnlineServiceImpl;
import com.xinlingyijiu.yanchat.core.user.User;
import com.xinlingyijiu.yanchat.core.user.UserManagerImpl;
import com.xinlingyijiu.yanchat.util.ScheduledExecutorUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by laotou on 2017/10/13.
 */
public class YanChatApplication {


    public static void start(Context context) {

        try {
            context.getBroadcast().listen();
            context.getUdpConnect().listen();

            context.getQueueListenner().listen();

//            ConnectMsg<User> connectMsg = new ConnectMsg<>(Constant.MSG_TYPE.ONLINE, context.getUserContext().getCurrentUser());
//            byte[] byteBroadcastMsg = context.getMsgHandleContext().getHandle(Constant.DATA_TYPE.TEXT).apply(connectMsg.toJSONString());
////            context.getBroadcast().cycle(byteBroadcastMsg, Constant.ONLINE_POLLING_CYCLE_TIME);
//
//            Runnable runnable = () -> {
//                try {
//                    context.getUdpConnect().send("localhost", byteBroadcastMsg);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            };
//            ScheduledExecutorUtil.getScheduler().scheduleAtFixedRate(runnable,0,10, TimeUnit.SECONDS);
            context.getOnlineService().onlinePolling(Constant.ONLINE_POLLING_CYCLE_TIME);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
