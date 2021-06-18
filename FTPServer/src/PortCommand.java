import java.io.IOException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 主动模式建立数据连接
 **/
public class PortCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        try {
            String[] Split =  data.split(":");
            String ip = Split[0];
            String port = Integer.toString(Integer.parseInt(Split[1]));
            //System.out.println("ip is "+ip);
            //System.out.println("port is "+port);
            t.setDataIp(ip);
            t.setDataPort(port);
            //之前建立过连接就close重新建
            if(t.getDataSocket()!=null){
                t.getDataSocket().close();
            }
            Socket temp=new Socket();
            //指定高位端口进行连接
            while (!temp.isBound()) {
                int tempport = (int) (Math.random() * 100000) % 60000 + 1024;
                temp.bind(new InetSocketAddress("127.0.0.1", tempport));
            }
            temp.connect(new InetSocketAddress(t.getDataIp(),Integer.parseInt(t.getDataPort())));
            if(temp.isConnected()){
                t.setDataSocket(temp);
                t.setMode(1);
                writer.write("225 数据连接已打开\r\n");
            }
            else{
                writer.write("425 无法打开数据连接\r\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}  