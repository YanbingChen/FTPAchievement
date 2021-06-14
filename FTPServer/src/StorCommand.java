import java.io.*;

/**
 * 存储客户端发来的文件
 **/
public class StorCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        //System.out.println("execute Store command @@@StoreCommand");
        try{
            writer.write("125 打开数据连接开始传输文件\r\n");
            writer.flush();
            FileOutputStream inFile = new FileOutputStream(t.getNowDir()+ File.separator+ data);
            InputStream inSocket = t.getDataSocket().getInputStream();
            byte byteBuffer[] = new byte[1024];
            int amount;
            while((amount =inSocket.read(byteBuffer))!= -1){
                inFile.write(byteBuffer, 0, amount);
            }
            System.out.println("传输完成");
            inFile.close();
            inSocket.close();
            writer.write("250 文件上传完成\r\n");
            writer.flush();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}  