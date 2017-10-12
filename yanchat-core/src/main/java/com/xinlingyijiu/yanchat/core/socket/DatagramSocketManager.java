package com.xinlingyijiu.yanchat.core.socket;

import com.xinlingyijiu.yanchat.util.IOUtil;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DatagramSocketManager implements SocketManager{
    private DatagramSocket socket;



    private int port;

    @Override
    public DatagramSocket getSocket() throws IOException {
        if (this.socket == null) {
            this.socket = new MulticastSocket(this.port);
        }
        return this.socket;
    }

    /**
     * @param port        监听端口
     */
    public DatagramSocketManager( int port) {

        this.port = port;
    }



//    public void setBroadcastIp(String broadcastIp) {
//        this.broadcastIp = broadcastIp;
//    }

    public int getPort() {
        return port;
    }
    /**
     * 关闭所有资源
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        IOUtil.close(this.socket);
        this.port = 0;
    }

//    public void setPort(int port) {
//        this.port = port;
//    }
}
