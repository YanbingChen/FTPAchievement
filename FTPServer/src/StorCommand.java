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
            //确认原文件名在服务器端是否存在，若存在则文件名增加后缀(1)
            String desDir=t.getNowDir()+ File.separator+ data;
            File file = new File(desDir);
            while(file.exists()){
                desDir+="(1)";
                file= new File(desDir);
            }
            FileOutputStream inFile = new FileOutputStream(desDir);
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