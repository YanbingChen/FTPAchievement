import java.io.IOException;
import java.io.Writer;

/**
 * 检查用户名是否存在
 **/
public class UserCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        String response = "";
        if(Share.users.containsKey(data)) {
            ConnectionThread.USER.set(data);
            response = "331 请输入密码";
        }
        else {
            response = "504 无效的用户名";
        }
        try {
            writer.write(response);
            writer.write("\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
