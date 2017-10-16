package com.xinlingyijiu.yanchat.core;

import com.xinlingyijiu.yanchat.core.bean.BroadcastMsg;
import com.xinlingyijiu.yanchat.core.net.broadcast.Broadcast;
import com.xinlingyijiu.yanchat.core.net.broadcast.BroadcastImpl;
import com.xinlingyijiu.yanchat.core.consumer.BroadcastMsgConsumer;
import com.xinlingyijiu.yanchat.core.exception.YanChatRuntimeException;
import com.xinlingyijiu.yanchat.core.msg.MsgHandleContext;
import com.xinlingyijiu.yanchat.core.msg.StringMsgConverseHandle;
import com.xinlingyijiu.yanchat.core.msg.StringMsgHandle;
import com.xinlingyijiu.yanchat.core.queue.*;
import com.xinlingyijiu.yanchat.core.user.User;
import com.xinlingyijiu.yanchat.core.user.UserContext;
import com.xinlingyijiu.yanchat.core.user.UserManager;
import com.xinlingyijiu.yanchat.core.user.UserManagerImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by laotou on 2017/10/12.
 */
public class Context {
    private Broadcast broadcast;
    private UserContext userContext;
    private UserManager userManager;
    private QueueListenner queueListenner;
    private MsgHandleContext msgHandleContext;

    private String broadcastHost ;
    private int broadcastPort;
    private int tcpPort;
    private int udpPort;


    private static Context context;
    private Context(){
        this.broadcastHost = Constant.BROADCAST_DEFAULT_HOST;
        this.broadcastPort = Constant.DEFAULT_PORT.BROADCAST;
        this.tcpPort = Constant.DEFAULT_PORT.TCP;
        this.udpPort = Constant.DEFAULT_PORT.UDP;
    }

    public String getBroadcastHost() {
        return broadcastHost;
    }

    public void setBroadcastHost(String broadcastHost) {
        Objects.requireNonNull(broadcastHost,"broadcastHost must be not null!");
        this.broadcastHost = broadcastHost;
    }

    public int getBroadcastPort() {
        return broadcastPort;
    }

    public void setBroadcastPort(int broadcastPort) {
        this.broadcastPort = broadcastPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public static Context getInstance() {
        return context == null ?(context = new Context()) : context;
    }

    public void setUserContext(UserContext userContext) {
        Objects.requireNonNull(userContext,"userContext must be not null!");
        this.userContext = userContext;
    }

    public QueueListenner getQueueListenner() {
        return queueListenner;
    }

    public void setQueueListenner(QueueListenner queueListenner) {
        Objects.requireNonNull(queueListenner,"queueListenner must be not null!");
        this.queueListenner = queueListenner;
    }

    public MsgHandleContext getMsgHandleContext() {
        return msgHandleContext;
    }

    public void setMsgHandleContext(MsgHandleContext msgHandleContext) {
        Objects.requireNonNull(msgHandleContext,"msgHandleContext must be not null!");
        this.msgHandleContext = msgHandleContext;
    }

    public Broadcast getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(Broadcast broadcast) {
        Objects.requireNonNull(broadcast,"broadcast must be not null!");
        this.broadcast = broadcast;
    }

    public UserContext getUserContext() {
        return userContext;
    }

//    public void setUserContext(UserContext userContext) {
//        this.userContext = userContext;
//    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        Objects.requireNonNull(userManager,"userManager must be not null!");
        this.userContext = userManager;
        this.userManager = userManager;
    }

    /**
     * 使用默认的实现
     */
    public void userDefaultContext(){

        //当前用户
        User user = new User();
        try {
            user.setHost(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            throw new YanChatRuntimeException("无法获取到本地ip地址");
        }
        user.setOnline(true);
        user.setNickName(user.getHost());
        user.setBroadcastPort(getBroadcastPort());
        user.setTcpPort(getTcpPort());
        user.setUdpPort(getUdpPort());
        //User
        UserManagerImpl userManager = new UserManagerImpl();
        userManager.setCurrentUser(user);
        this.setUserContext(userManager);
        this.setUserManager(userManager);


        //消息消费者
        BroadcastMsgConsumer consumer = new BroadcastMsgConsumer();//广播消息处理
        //队列
        BlockingQueue<BroadcastMsg> broadcastMsgsQueue = new ArrayBlockingQueue<>(Constant.DEFAULT_QUEUE_CAPACITY);
        //队列管理者
        QueueManagerImpl queueManager = QueueManagerImpl.getInstance();
        queueManager.putQueue(Constant.QUEUE_KEY.BROADCAST,broadcastMsgsQueue);//添加队列

        //队列监听者
        QueueListennerImpl queueListenner = QueueListennerImpl.getInstance();
        queueListenner.setQueueManager(queueManager);
        queueListenner.bindConsumer(Constant.QUEUE_KEY.BROADCAST,consumer);
        this.setQueueListenner(queueListenner);

        //消息转换处理
        StringMsgHandle stringMsgHandle = new StringMsgHandle();//字符串
        //消息逆向处理
        StringMsgConverseHandle stringMsgConverseHandle = new StringMsgConverseHandle();//字符串
        //消息出列
        MsgHandleContext msgHandleContext = MsgHandleContext.getInstance();
        msgHandleContext.putConverseHandle(Constant.MSG_TYPE.TEXT,stringMsgConverseHandle);//文本逆向转换
        msgHandleContext.putMsgHandle(Constant.MSG_TYPE.TEXT, stringMsgHandle);//文本转换
        this.setMsgHandleContext(msgHandleContext);

        //消息生产者
        MsgProducerImpl msgProducer = new MsgProducerImpl();
        msgProducer.setManager(queueManager);
        //广播socket
//        MulticastSocketManager multicastSocketManager = new MulticastSocketManager(getBroadcastHost(),getBroadcastPort());
        //广播
        BroadcastImpl broadcast = new BroadcastImpl();
        broadcast.setMsgProducer(msgProducer);
//        broadcast.setSocketManager(multicastSocketManager);
        this.setBroadcast(broadcast);

    }
}
