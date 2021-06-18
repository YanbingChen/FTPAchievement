import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FtpClient_active implements Ftp_Client {

    private Socket commandConn;

    private BufferedReader controlReader;
    private PrintWriter controlOut;

    private String ftpusername;
    private String ftppassword;


    private static final int PORT = 21;

    public boolean isLogined = false  ;


    public FtpClient_active(String url, String username, String password) {
        try {
            commandConn = new Socket(url, PORT);//建立与服务器的socket连接

            setUsername(username);
            setPassword(password);

            controlReader = new BufferedReader(new InputStreamReader(commandConn.getInputStream()));
            controlOut = new PrintWriter(new OutputStreamWriter(commandConn.getOutputStream()), true);

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

        if (!response.startsWith("331 ")) {
            JOptionPane.showConfirmDialog(null, response, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            throw new IOException("SimpleFTP received an unknown response after sending the user: " + response);

        }

        controlOut.println("PASS " + ftppassword);

        response = controlReader.readLine();
        System.out.println(response);
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
            commandConn.close();
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
            String[] spl = line.split(" ");
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


    //生成InputStream用于上传本地文件
    public void upload(String File_path, String FileName) throws Exception {
        //本地文件读取-----------------------------------
        System.out.print("File Path :" + File_path + File.separator + FileName);
        File f = new File(File_path, FileName);
        if (!f.exists()) {
            System.out.println("File not Exists...");
            return;
        }
        FileInputStream is = new FileInputStream(f);
        BufferedInputStream input = new BufferedInputStream(is);
        //-----------------------------------------------

        // Send PORT command
        String url="127.0.0.1";
        int dataport=(int)(Math.random()*100000%9999)+1024;
        String portCommand="MYPORT "+ url+":"+dataport;
        // Open data connection
        ServerSocket dataSocketServ = new ServerSocket(dataport);

        controlOut.println(portCommand);

        Socket dataSocket=dataSocketServ.accept();

        String response;
        response=controlReader.readLine();
        System.out.println(response);


        // Send command STOR
        controlOut.println("STOR " + f.getName());

        // Read command response
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
        // Send PORT command
        String url="127.0.0.1";
        int dataport=(int)(Math.random()*100000%9999)+1024;

        // Open data connection
        ServerSocket dataSocketServ = new ServerSocket(dataport);

        String portCommand="MYPORT "+ url+":"+dataport;
        controlOut.println(portCommand);

        Socket dataSocket=dataSocketServ.accept();

        String response;
        response=controlReader.readLine();
        System.out.println(response);

        //send RETR command
        controlOut.println("RETR " + from_file_name);

        // Read data from server
        BufferedOutputStream output = new BufferedOutputStream(
                new FileOutputStream(new File(to_path, from_file_name)));
        BufferedInputStream input = new BufferedInputStream(
                dataSocket.getInputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        output.flush();
        output.close();
        input.close();
        dataSocket.close();

        response = controlReader.readLine();
        System.out.println(response);

        response = controlReader.readLine();
        System.out.println(response);
    }

    public boolean delete(String fileName) throws Exception {
        String response;
        // Send LIST command
        controlOut.println("DELE " + fileName);

        // Read command response
        response = controlReader.readLine();
        response = controlReader.readLine();
        System.out.println(response);
        return response.equals("250 文件删除完成");
    }

    public String getRemotePath() throws Exception {
        String response;
        // Send LIST command
        controlOut.println("PWD ");

        // Read command response
        response = controlReader.readLine();
        response = controlReader.readLine();
        return response;
    }

    public void changeDir(String dir) throws Exception {
        String response;
        // Send LIST command
        controlOut.println("CWD " + dir);

        // Read command response
        response = controlReader.readLine();

    }
}