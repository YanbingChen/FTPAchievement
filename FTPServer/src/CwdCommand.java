import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * 改变工作目录
 **/
public class CwdCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        String dir = t.getNowDir()+File.separator+data;
        File file = new File(dir);

        // Assert if working directory exceeded

        try {
            if(file.getAbsolutePath().startsWith(Share.rootDir)) {
                if((file.exists())&&(file.isDirectory())) {
                    t.setNowDir(dir);
                    writer.write("212 路径更改完成");
                }
                else {
                    writer.write("550 目录不存在");
                }
                writer.write("\r\n");
                writer.flush();
            } else {
                writer.write("550 目录不存在");      // ?
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
