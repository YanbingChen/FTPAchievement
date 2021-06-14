import java.io.IOException;
import java.io.Writer;

/**
 * 显示当前工作目录
 **/
public class PwdCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        String pwd=t.getNowDir();
        try{
            writer.write("212 当前路径如下：\r\n");
            writer.write(pwd+"\r\n");
            writer.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
