import java.io.IOException;
import java.io.Writer;

/**
 * 断开连接
 **/
public class QuitCommand implements Command{

    @Override
    public void getResult(String data, Writer writer, ConnectionThread t) {
        try {
            writer.write("221 goodbye.\r\n");
            writer.flush();
            writer.close();
            t.getCtrlSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}  