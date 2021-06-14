import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * 将文件发送给客户端
 **/
public class RetrCommand implements Command{
    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        String desDir = t.getNowDir()+File.separator+data;
        File file = new File(desDir);
        //System.out.println(desDir);
        if(file.exists())
        {
            try {
                writer.write("125 打开数据连接开始传输文件\r\n");
                writer.flush();
                BufferedOutputStream dataOut = new BufferedOutputStream(t.getDataSocket().getOutputStream());
                byte[] byteBuffer = new byte[1024];
                InputStream is = new FileInputStream(file);
                int amount;
                while((amount =is.read(byteBuffer))!= -1){
                    dataOut.write(byteBuffer, 0, amount);
                }
                dataOut.flush();
                dataOut.close();
                writer.write("250 文件回传完成\r\n");
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                writer.write("450 该文件不存在\r\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}