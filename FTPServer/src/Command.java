import java.io.Writer;

//所有控制命令所共用的接口
interface Command {

    /**
     * @param data    从ftp客户端接收的除ftp命令之外的参数
     * @param writer  网络输出流 
     * @param thread       控制连接所对应的处理线程
     **/
    public void getResult(String data, Writer writer, ConnectionThread thread);

}  