package com.sourcecode.web.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer {
    public static final int PORT = 30000;
    // 定义每个数据报的最大大小为4K
    private static final int DATA_LEN = 4096;
    // 定义该服务器使用的DatagramSocket
    private DatagramSocket socket = null;
    // 定义接收网络数据的字节数组
    byte[] inBuff = new byte[DATA_LEN];
    // 以指定字节数组创建准备接受数据的DatagramPacket对象
    private final DatagramPacket inPacket = new DatagramPacket(inBuff, inBuff.length);
    // 定义一个用于发送的DatagramPacket对象
    private DatagramPacket outPacket;
    // 定义一个字符串数组，服务器发送该数组的的元素
    String[] books = new String[]{"轻量级J2EE企业应用实战", "基于J2EE的Ajax宝典", "Struts2权威指南", "ROR敏捷开发最佳实践"};

    public void init() throws IOException {
        try {
            // 创建DatagramSocket对象
            socket = new DatagramSocket(PORT);
            // 采用循环接受数据
            for (int i = 0; i < 1000; i++) {
                // 读取Socket中的数据，读到的数据放在inPacket所封装的字节数组里。
                socket.receive(inPacket);
                // 判断inPacket.getData()和inBuff是否是同一个数组
                System.out.println(inBuff == inPacket.getData());
                // 将接收到的内容转成字符串后输出
                System.out.println(new String(inBuff, 0, inPacket.getLength()));
                // 从字符串数组中取出一个元素作为发送的数据
                byte[] sendData = books[i % 4].getBytes();
                // 以指定字节数组作为发送数据、以刚接受到的DatagramPacket的源SocketAddress作为目标SocketAddress创建DatagramPacket。
                outPacket = new DatagramPacket(sendData, sendData.length, inPacket.getSocketAddress());
                // 发送数据
                socket.send(outPacket);
            }
        }
        // 使用finally块保证关闭资源
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new UdpServer().init();
    }
}
