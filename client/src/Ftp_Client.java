public interface Ftp_Client {
    //获取是否登录
    boolean isLogined();

    //USER&PASS：执行登录指令
    void initftp() throws Exception;

    //QUIT：执行退出指令
    boolean logOut();

    //LIST：获取所有文件和文件夹的名字
    String[] getAllFile() throws Exception;

    //STOR：生成InputStream用于上传本地文件
    void upload(String File_path, String FileName) throws Exception;

    //RETR：下载 from_file_name是下载的文件名,to_path是下载到的路径地址
    void download(String from_file_name, String to_path) throws Exception;

    //DELE：删除文件
    boolean delete(String fileName) throws Exception;

    //PWD：显示服务器的远程工作目录
    String getRemotePath() throws Exception;

    //CWD：更改服务器的远程工作目录
    void changeDir(String dir) throws Exception;
}
