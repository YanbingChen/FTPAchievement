import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public interface Ftp_Client {

    public boolean isLogined();

    public boolean logOut();

    public void initftp() throws Exception;

    //获取所有文件和文件夹的名字
    public String[] getAllFile() throws Exception;

    //生成InputStream用于上传本地文件
    public void upload(String File_path) throws Exception;

    //下载 from_file_name是下载的文件名,to_path是下载到的路径地址
    public void download(String from_file_name, String to_path) throws Exception;

    // delete
    public boolean delete(String fileName) throws Exception;
}
