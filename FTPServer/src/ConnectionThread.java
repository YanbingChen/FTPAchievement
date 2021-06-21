import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

/**
 * 用于处理两条连接
 **/
public class ConnectionThread extends Thread{

    //用于处理初始时不带数据的访问
    private int count = 0;

    //控制连接
    private Socket ctrlSocket;
    //数据连接
    private Socket dataSocket;

    //当前的线程所对应的用户
    public static final ThreadLocal<String> USER = new ThreadLocal<String>();

    //数据连接的ip
    private String dataIp;
    //数据连接的port
    private String dataPort;

    //用于标记用户是否已经登录  
    private boolean isLogin = false;
    //用于标记用户采用的数据传输模式，未设置时为0，port为1，pasv为2
    private int mode = 0;

    //当前目录  
    private String nowDir = Share.rootDir;

    public String getNowDir() {
        return nowDir;
    }

    public boolean getIsLogin() {
        return isLogin;
    }

    public Socket getCtrlSocket() {
        return ctrlSocket;
    }

    public Socket getDataSocket() {
        return dataSocket;
    }

    public String getDataIp() {
        return dataIp;
    }

    public String getDataPort() {
        return dataPort;
    }

    public void setNowDir(String nowDir) {
        this.nowDir = nowDir;
    }

    public void setIsLogin(boolean t) {
        isLogin = t;
    }

    public void setDataIp(String dataIp) {
        this.dataIp = dataIp;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setDataPort(String dataPort) {
        this.dataPort = dataPort;
    }

    public void setDataSocket(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

    //初始最先建立的是控制连接
    public ConnectionThread(Socket ctrlSocket) {
        this.ctrlSocket = ctrlSocket;
    }

    //对控制连接的指令进行处理
    public void run() {
        System.out.println("a new client is connected=== ");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(ctrlSocket.getInputStream()));
            Writer writer = new OutputStreamWriter(ctrlSocket.getOutputStream());
            while(true) {
                //第一次访问，输入流里面是没有东西的，所以会阻塞住
                if(count == 0)
                {
                    writer.write("220 Hello World!\r\n");
                    writer.flush();
                    count++;
                }
                else {
                    //确定控制连接没有关闭
                    if(!ctrlSocket.isClosed()) {
                        //进行命令的选择，然后进行处理，当客户端没有发送数据的时候，将会阻塞
                        String command = reader.readLine();
                        System.out.println(command);
                        if(command !=null) {
                            //command和args之间用空格隔开
                            String[] datas = command.split(" ",2);
                            //读取命令
                            Command commandSolver = CommandFactory.createCommand(datas[0]);
                            //排除不合法命令可能性
                            if(commandSolver == null) {
                                    writer.write("500 该命令不存在，请重新输入");
                            }
                            //登录验证,在没有登录的情况下，无法使用除了user,pass,quit之外的命令
                            if(loginValidate(commandSolver)){
                                //如果在未选择模式试图传输文件的情况，也不能使用传输文件命令
                                if(!((commandSolver instanceof StorCommand || commandSolver instanceof RetrCommand)
                                        &&(mode==0))){
                                    String data = "";
                                    if(datas.length >=2) {
                                        data = datas[1];
                                    }
                                    commandSolver.getResult(data, writer,this);
                                }
                                else{
                                    writer.write("传输文件前需要先选择主被动模式");
                                }
                            }
                            else
                            {
                                writer.write("530 执行该命令需要登录，请登录后再执行相应的操作\r\n");
                                writer.flush();
                            }
                        } else {
                            reader.close();
                            writer.close();
                            dataSocket.close();
                            ctrlSocket.close();
                            System.out.println("连接意外断开");
                        }
                    }
                    else {
                        //连接已经关闭，这个线程不再有存在的必要
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //连接关闭后break则执行
        finally {
            System.out.println("结束连接");
        }

    }


    public boolean loginValidate(Command command) {
        if(command instanceof UserCommand || command instanceof PassCommand || command instanceof QuitCommand) {
            return true;
        }
        else
        {
            return getIsLogin();
        }
    }

}  