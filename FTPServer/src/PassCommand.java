import java.io.IOException;
import java.io.Writer;

/**
 * 检查密码是否正确
 **/
public class PassCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        /*System.out.println("execute the pass command");
        System.out.println("the data is "+data);
         */
        //获得用户名  
        String key = ConnectionThread.USER.get();
        String password = Share.users.get(key);
        String response;
        if(password.equals(data)) {
            //System.out.println("登录成功");
            t.setIsLogin(true);
            response = "230 用户 "+key+" 登录成功";
        }
        else {
            System.out.println("登录失败，密码错误");
            response = "504 登录失败，密码错误";
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