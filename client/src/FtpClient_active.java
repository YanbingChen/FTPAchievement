import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FtpClient_active implements Ftp_Client {

    private Socket commandSocket;
    private Socket dataSocket;

    private BufferedReader controlReader;
    private PrintWriter controlOut;

    private String ftpusername;
    private String ftppassword;


    private static final int PORT = 21;

    public boolean isLogined = false  ;


    public FtpClient_active(String url, String username, String password) {
        try {
            commandSocket = new Socket(url, PORT);//建立与服务器的socket连接

            setUsername(username);
            setPassword(password);

            controlReader = new BufferedReader(new InputStreamReader(commandSocket.getInputStream()));
            controlOut = new PrintWriter(new OutputStreamWriter(commandSocket.getOutputStream()), true);

            initftp();  //登录到ftp服务器
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void initftp() throws Exception {
        String msg;
        do {
            msg = controlReader.readLine();
            System.out.println(msg);
        } while (!msg.startsWith("220 "));

        controlOut.println("USER " + ftpusername);

        String response = controlReader.readLine();
        System.out.println(response);
        //出错处理：用户名错误
        if (!response.startsWith("331 ")) {
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("SimpleFTP received an unknown response after sending the user: " + response);
        }

        controlOut.println("PASS " + ftppassword);

        response = controlReader.readLine();
        System.out.println(response);
        //出错处理：密码错误
        if (!response.startsWith("230 ")) {
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
           throw new IOException("SimpleFTP was unable to log in with the supplied password: "+ response);
        }

        isLogined=true;//登录成功标志
    }

    private void setUsername(String username) {
        this.ftpusername = username;
    }

    private void setPassword(String password) {
        this.ftppassword = password;
    }

    public boolean isLogined() {
        return isLogined;
    }

    public boolean logOut() {
        if(isLogined) {
            controlOut.println("QUIT ");
            try {
                String msg;
                do {
                    msg = controlReader.readLine();
                    System.out.println(msg);
                } while (!msg.startsWith("221 "));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            controlOut.close();
            controlReader.close();
            commandSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    //获取所有文件和文件夹的名字
    public String[] getAllFile() throws Exception {
        String response;
        // Send LIST command
        controlOut.println("LIST");

        // Read command response
        response = controlReader.readLine();
        System.out.println(response);

        // Read data from server
        Vector<String> tempfiles = new Vector<>();

        String line = null;
        while ((line = controlReader.readLine()) != null) {
            if(line.equals("213 当前目录下文件列表显示完成"))
                break;
            System.out.println(line);
            String[] spl = line.split(":");
            if(spl[0].equals("文件") || spl[0].equals("文件夹"))
                tempfiles.add(line);
        }

        // Read command response
        response = line;
        System.out.println(response);

        String[] files = new String[tempfiles.size()];
        tempfiles.copyInto(files);//将vector数据存到数组里

        return files;

    }

    //将服务器设置为port模式
    private void checkIsPortMode() throws Exception {
        // Send PORT command
        String url="127.0.0.1";
        int dataport=(int)(Math.random()*100000%9999)+1024;

        // Open data connection
        ServerSocket dataSocketServer = new ServerSocket(dataport);

        String portCommand="MYPORT "+ url+":"+dataport;
        controlOut.println(portCommand);

        dataSocket=dataSocketServer.accept();

        String response;
        response=controlReader.readLine();
        //出错处理：数据连接无法建立
        if (!response.startsWith("225 ")) {
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("Unable to connect " + response);
        }
        System.out.println(response);
    }

    //生成InputStream用于上传本地文件
    public void upload(String File_path, String FileName) throws Exception {
        //设置主动模式
        checkIsPortMode();
        //本地文件读取-----------------------------------
        System.out.print("File Path :" + File_path + File.separator + FileName);
        File f = new File(File_path, FileName);
        //出错处理：文件不存在
        if (!f.exists()) {
            System.out.println("File not Exists...");
            JOptionPane.showConfirmDialog(null, "File not Exists...", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("Uploading-file not Exists...");
        }
        FileInputStream is = new FileInputStream(f);
        BufferedInputStream input = new BufferedInputStream(is);

        // Send command STOR
        controlOut.println("STOR " + f.getName());

        // Read command response
        String response;
        response = controlReader.readLine();
        System.out.println(response);

        // Read data from server
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        output.flush();
        input.close();
        output.close();
        dataSocket.close();

        response = controlReader.readLine();
        System.out.println(response);

    }


    //下载 from_file_name是下载的文件名,to_path是下载到的路径地址
    public void download(String from_file_name, String to_path) throws Exception {
        //设置主动模式
        checkIsPortMode();
        String response;
        //send RETR command
        controlOut.println("RETR " + from_file_name);

        response = controlReader.readLine();
        System.out.println(response);
        if(response.startsWith("125 ")) {
            // Read data from server
            BufferedOutputStream output = new BufferedOutputStream(
                    new FileOutputStream(new File(to_path, from_file_name)));
            BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            output.close();
            input.close();
            response = controlReader.readLine();
            System.out.println(response);
        }else {
            //出错处理：下载错误
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("Cannot find file " + response);
        }

        dataSocket.close();
    }

    public boolean delete(String fileName) throws Exception {
        String response;
        // Send DELE command
        controlOut.println("DELE " + fileName);

        // Read command response
        response = controlReader.readLine();
        if (!response.equals("开始删除文件")) {
            //出错处理：待删除文件不存在
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("File not exists " + response);
        }
        response = controlReader.readLine();
        System.out.println(response);
        if (!response.startsWith("250 ")) {
            //出错处理：删除错误
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("Unable to delete " + response);
        }
        return response.equals("250 文件删除完成");
    }

    public String getRemotePath() throws Exception {
        String response;
        // Send PWD command
        controlOut.println("PWD ");

        // Read command response
        response = controlReader.readLine();
        response = controlReader.readLine();
        return response;

    }

    public void changeDir(String dir) throws Exception {
        String response;
        // Send CWD command
        controlOut.println("CWD " + dir);
        // Read command response
        response = controlReader.readLine();
        if (!response.startsWith("212 ")) {
            //出错处理：更改路径错误
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("unable to changeDir"+ response);
        }
    }
}