import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器主进程
 **/
public class Ftp_Server {

    ServerSocket serverSocket;

    public Ftp_Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        //初始化系统信息  
        Share.init();
    }

    public void listen() throws IOException {
        Socket socket = null;
        while(true) {
            //监听到就建立控制连接，然后继续监听
            socket = serverSocket.accept();
            ConnectionThread thread = new ConnectionThread(socket);
            thread.start();
        }
    }

    public static void main(String args[]) throws IOException {
        Ftp_Server ftpServer = new Ftp_Server(21);
        ftpServer.listen();
    }

}  