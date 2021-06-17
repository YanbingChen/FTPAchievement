import java.io.*;

/**
 * 删除服务器文件
 **/
public class DeleCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        if(data == null || data.equals("")) {
            try {
                writer.write("350 文件删除出错\r\n");
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String deleDir = t.getNowDir()+File.separator+data;
        File file = new File(deleDir);
        //System.out.println(desDir);
        if(file.exists())
        {
            try {
                writer.write("开始删除文件\r\n");
                writer.flush();
                deleteFile(file);
                if(!file.exists()){
                    writer.write("250 文件删除完成\r\n");
                }
                else{
                    writer.write("350 文件删除出错\r\n");
                }
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

    //递归删除文件
    private void deleteFile(File file) {
            if (file.isFile()) {//判断是否是文件
                file.delete();//删除文件
            } else if (file.isDirectory()) {//否则如果它是一个目录
                File[] files = file.listFiles();//声明目录下所有的文件 files[];
                for (int i=0; i< files.length; i++) {//遍历目录下所有的文件
                    this.deleteFile(files[i]);//把每个文件用这个方法进行迭代
                }
                file.delete();//删除文件夹
            }
    }
}
