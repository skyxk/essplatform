package com.chen.core.util;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * @author chen
 * @version V1.0
 * @Description 用java.net.Socket进行Socket操作工具类
 */
public class SocketUtils {

    private static final String ENCODING = "UTF-8";

    public static void listen(final int port, String content, SocketFunction function) throws IOException{
        ServerSocket serverSocket = null ;
        Socket socket = null;
        try {
            serverSocket = createServer(port);
            socket = createServerSocket(serverSocket);
            //通常获取到socket连接后，都是分配一个线程来处理客户端
            WorkClass workClass = new WorkClass(socket, content, function);
            workClass.work();
        }catch(IOException ioe){
            ioe.printStackTrace();
            throw ioe;
        }finally{
            clientClose(socket);
            //关闭服务端
            serverClose(serverSocket);
        }
    }

    static class WorkClass{
        private Socket socket ;
        private String content;
        private SocketFunction function;
        public WorkClass(Socket socket, String content, SocketFunction function){
            this.socket = socket;
            this.content = content;
            this.function = function;
        }

        public void work() throws IOException {
            String msg = null;
            InputStream in = null;
            OutputStream out = null;
            try {
                in = socket.getInputStream();
                msg = input(in);
                out = socket.getOutputStream();
                output(out, content);
                function.callback(msg);
            }finally{
                //关闭输入输出流
                streamClose(in, out);
            }
        }
    }

    public static void send(String host, int port, String content, SocketFunction function) throws IOException{
        Socket socket = null;
        String msg = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            socket = createClientSocket(host, port);
            out = socket.getOutputStream();
            output(out, content);
            socket.shutdownOutput();//输出完后，需要关闭socket的输出通道，表示不存向服务端输出内容
            in = socket.getInputStream();
            msg = input(in);
            function.callback(msg);
        }catch(UnknownHostException uhe){
            uhe.printStackTrace();
            throw new IOException("主机连接创建异常：" + uhe.getMessage());
        }catch(IOException ioe){
            ioe.printStackTrace();
            throw ioe;
        }finally{
            streamClose(in, out);
            clientClose(socket);
        }
    }

    public static void streamClose(InputStream in, OutputStream out){
        //IOUtils.closeQuietly(in); 可用IOUtils工具类关闭流
        if (in != null){
            try {
                in.close();
            }catch(IOException ioe){
                System.out.println("关闭输入流异常：" + ioe.getMessage());
            }
        }
        if (out != null){
            try {
                out.flush();
                out.close();
            }catch(IOException ioe){
                System.out.println("关闭输出流异常：" + ioe.getMessage());
            }
        }
    }

    private static ServerSocket createServer(int port) throws IOException {
        System.out.println("监听端口号：" + port);
        return new ServerSocket(port);
    }

    private static Socket createServerSocket(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        System.out.println("获取到客户端连接：" + socket.getInetAddress().getHostAddress());
        return socket;
    }

    private static Socket createClientSocket(String host, int port) throws UnknownHostException, IOException {
        return new Socket(host, port);
    }

    private static void serverClose(ServerSocket server) {
        if (server != null && !server.isClosed()){
            try{
                server.close();
            }catch(IOException ioe){
                System.out.println("服务关闭异常：" + ioe.getMessage());
            }
        }
    }

    private static void clientClose(Socket socket){
        if (socket != null && !socket.isClosed()){
            try{
                socket.close();
            }catch(IOException ioe){
                System.out.println("Socket关闭异常：" + ioe.getMessage());
            }
        }
    }

    public static OutputStream output(OutputStream out, String content) throws IOException {
        try{
            out.write(content.getBytes(ENCODING));
        }finally{
            return out;
        }
    }

    public static String input(InputStream in) throws IOException {
        int len;
        char[] b = new char[1024];
        StringBuilder sb = new StringBuilder();
        BufferedReader reader ;
        try {
            //以字符流为主，如需字节流，则不需要BufferedReader和InputStreamReader，可以直接从InputStream中获取或采用对应缓冲包装类
            reader = new BufferedReader(new InputStreamReader(in, ENCODING));
            while ((len = reader.read(b)) != -1) {
                sb.append(b, 0, len);
            }
            //reader.close();
        }finally{
        }
        return sb.toString();
    }

    public interface SocketFunction{
        void callback(String msg);
    }

    public static void main(String[] args) throws IOException{
        String data = "这是测试数据：";
        //测试时，请分别单独启动send和listen方法
        //客户端
        String replaceUU = UUID.randomUUID().toString().replace("-", "");
        String s = "{COM:\"1\",DOCID:\""+replaceUU+"\",HASH:\"qweqweqweqweqwe\"}";
        send("123.56.11.240",7788, s, new SocketFunction(){
            @Override
            public void callback(String msg) {
                System.out.println(data + msg);
            }
        });

//        //服务端
//        listen(8111, "this is server test", new SocketFunction() {
//            @Override
//            public void callback(String msg) {
//                System.out.println(data + msg);
//            }
//        });
    }

}