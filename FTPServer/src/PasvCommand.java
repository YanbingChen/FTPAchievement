import java.io.IOException;
import java.io.Writer;
import java.net.*;

/**
 * 被动模式打开端口监听
 * */
public class PasvCommand implements Command {

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        //System.out.println("execute the PASV command......");
        String response = "";
        try {
            //之前建立过连接就close重新建
            if(t.getDataSocket()!=null){
                t.getDataSocket().close();
            }
            int tempport = -1;
            ServerSocket serverSocket = null;
            while (serverSocket == null) {
                tempport = (int) (Math.random() * 100000) % 60000 + 1024;
                serverSocket = getDataServerSocket(tempport);
            }
            if (tempport != -1 && serverSocket != null) {
                response = "227 进入被动模式 127.0.0.1:" + tempport;
                //System.out.println(response);
            }
            writer.write(response+"\r\n");
            writer.flush();
            //System.out.println("set PASV successful");

            //端口开启等待客户端连接，1分钟未等到则关闭
            serverSocket.setSoTimeout(3000);
            Socket datasocket = serverSocket.accept();
            if(datasocket!=null) {
                t.setDataSocket(datasocket);
                t.setDataIp(datasocket.getInetAddress().getHostAddress());
                t.setDataPort(Integer.toString(datasocket.getPort()));
                t.setMode(2);
                writer.write("225 数据连接已打开\r\n");
            }
            else{
                writer.write("425 等待超时，无法打开数据连接\r\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //用于处理数据连接选择的端口可能已被占用的情况
    public static ServerSocket getDataServerSocket(int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return socket;
    }

}
