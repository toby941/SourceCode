package com.sourcecode.web.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

public class SocketClient {

    private static final Logger log = Logger.getLogger(SocketClient.class);

    public static Integer port;

    public static void notice() throws IOException {
        try {
            Socket server = new Socket(InetAddress.getLocalHost(), port);
            PrintWriter out = new PrintWriter(server.getOutputStream());
            String str = "done"; // 客户端读取（获得）键盘的字符串
            out.println(str); // 客户端向服务器发送信息
            out.flush();
            server.close();
        }
        catch (Exception e) {
            log.error("socket port:" + port, e);
        }
    }

    public static void main(String[] args) throws Exception {
        Socket server = new Socket(InetAddress.getLocalHost(), 5678); // 向主机名为InetAddress.getLocalHost()的服务器申请连接
        BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream())); // 客户端建立输入流并进行封装
        PrintWriter out = new PrintWriter(server.getOutputStream());
        BufferedReader wt = new BufferedReader(new InputStreamReader(System.in)); // 客户端从键盘输入信息

        while (true) {
            String str = wt.readLine(); // 客户端读取（获得）键盘的字符串

            // String str1 = in.readLine(); // 从服务器获得字符串
            out.println(str); // 客户端向服务器发送信息
            out.flush();
            if (str.equals("end")) {
                break;
            }
            System.out.println(in.readLine());
        }
        server.close();
    }
}
