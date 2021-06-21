import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.UnknownHostException;
import java.util.Vector;

/**
 * 获取当前服务器工作目录的文件列表
 * */
public class ListCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        String desDir = t.getNowDir();
        File dir = new File(desDir);
        StringBuilder dirs = new StringBuilder();
        dirs.append("正在获取文件列表.......\n");
        dirs.append("文件目录如下:\n");
        Vector<String> allfiles=new Vector<>();
        String[] lists= dir.list();
        String flag = null;
        for(String name : lists) {
            File temp = new File(desDir+File.separator+name);
            if(temp.isDirectory()) {
                flag = "文件夹:";
            }
            else {
                flag = "文件:";
            }
            String oneinfo=flag+name+":"+temp.length()+":bytes";
            allfiles.add(oneinfo);
        }
        //返回文件列表
        try {
            writer.write(dirs+"\r\n");
            writer.flush();

            for(String oneinfo : allfiles)
            {
                writer.write(oneinfo);
                writer.write("\r\n");
                writer.flush();
            }

            writer.write("213 当前目录下文件列表显示完成\r\n");
            writer.flush();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}  