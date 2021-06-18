import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.awt.EventQueue;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;

public class MainGUI implements ActionListener{


    //初始化参数--------------------------------
    static String[] file;
    static String[] localFiles;
    static String remotePath;
    static String localPath;
    static String FTP="127.0.0.1";
    static String username="stu1";
    static String password="000000";


    private JFrame frame;
    private JTable ftpTable  = null;
    private JTable localTable  = null;
    private JScrollPane ftpScrollPane = null;
    private JScrollPane localScrollPane  = null;

    private JTextField logField;
    private JTextField localPathText;
    private JTextField remotePathText;

    static Ftp_Client ftp;
    static File localDir;
    public static Ftp_Client getFtp() {
        return ftp;
    }
    public static String[] getFile(){
        return file;
    }
    public static String[] getLocalFile(){
        return localFiles;
    }
    public static String getLocalPath() { return localPath;}

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            try {
                MainGUI window = new MainGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Create the application.
     */
    public MainGUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        initConfig();

        frame = new JFrame();
        frame.setTitle("FTP Client");
        frame.setBounds(0, 0, 600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

        // All Modules
        JTextField ftpMode =  new JTextField("主动模式");   //默认为主动模式

        // Font
        Font font_text =new Font("宋体",Font.PLAIN,16);
        Font font_text_path=new Font("宋体",Font.PLAIN,12);
        Font font_buttom =new Font("宋体",Font.PLAIN,12);

        //显示基本信息(FTP username)-----------------------------------------------
        JLabel label = new JLabel("服务器");
        label.setBounds(20, 7, 55, 24);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(font_text);
        frame.getContentPane().add(label);

        label = new JLabel("用户名");
        label.setBounds(20, 33, 55, 24);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(font_text);
        frame.getContentPane().add(label);

        label = new JLabel("密码");
        label.setBounds(20, 59, 55, 24);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(font_text);
        frame.getContentPane().add(label);

        JTextField url = new JTextField("127.0.0.1");   //FTP服务地址
        url.setBounds(80,7,167,24);
        url.setFont(font_text);
        frame.getContentPane().add(url);

        JTextField usernameField = new JTextField("stu1"); //用户名
        usernameField.setBounds(80,33,167,24);
        usernameField.setFont(font_text);
        frame.getContentPane().add(usernameField);

        JPasswordField passwordField = new JPasswordField("000000");  //密码
        passwordField.setBounds(80,59,167,24);
        passwordField.setFont(font_text);
        frame.getContentPane().add(passwordField);


        // Setting FTP Mode
        ftpMode.setBounds(334,7,96,24);
        ftpMode.setHorizontalAlignment(SwingConstants.CENTER);
        ftpMode.setFont(font_text);
        ftpMode.setEditable(false);
        frame.getContentPane().add(ftpMode);

        //模式切换按钮------------------------------------------------
        JButton modeSwitch = new JButton("切换模式");
        modeSwitch.setFont(font_buttom);
        modeSwitch.setBackground(UIManager.getColor("Button.highlight"));
        modeSwitch.setBounds(334, 33, 96, 24);
        frame.getContentPane().add(modeSwitch);
        modeSwitch.addActionListener(e -> {
            System.out.println("切换模式==============");
            String mode = ftpMode.getText();
            if(mode.equals("主动模式")) {
                ftpMode.setText("被动模式");
            } else {
                ftpMode.setText("主动模式");
            }
        });

        //刷新按钮--------------------------------------------------
        JButton refresh = new JButton("刷新");
        refresh.addActionListener(arg0 -> {
            try{
                setTableInfo();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        refresh.setFont(font_buttom);
        refresh.setBackground(UIManager.getColor("Button.highlight"));
        refresh.setBounds(334, 59, 96, 24);
        refresh.setEnabled(false);
        frame.getContentPane().add(refresh);

        // Init tableHeader
        label = new JLabel("本地");
        label.setBounds(20, 100, 55, 25);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(font_text);
        frame.getContentPane().add(label);

        label = new JLabel("远程");
        label.setBounds(300, 100, 55, 25);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(font_text);
        frame.getContentPane().add(label);

        localPathText = new JTextField("本地路径");   //本地路径
        localPathText.setBounds(80,100,167,25);
        localPathText.setFont(font_text_path);
        localPathText.setEditable(false);
        frame.getContentPane().add(localPathText);

        remotePathText = new JTextField("远程路径"); //远程路径
        remotePathText.setBounds(360,100,167,25);
        remotePathText.setFont(font_text_path);
        remotePathText.setEditable(false);
        frame.getContentPane().add(remotePathText);

        // Last Level Directory for Local
        JButton localLastDir = new JButton();
        localLastDir.setIcon(new ImageIcon("client/gui/LeftArrow.png"));
        localLastDir.setBounds(248, 100, 25, 25);
        localLastDir.setEnabled(false);
        frame.getContentPane().add(localLastDir);
        localLastDir.addActionListener(e -> {
            File localFile = new File(MainGUI.localPath);
            if(localFile.getParentFile().isDirectory()) {
                MainGUI.localPath = localFile.getParentFile().getPath();
            }
            // refresh table
            setTableInfo();
        });

        // Last Level Directory for Local
        JButton remoteLastDir = new JButton();
        remoteLastDir.setIcon(new ImageIcon("client/gui/LeftArrow.png"));
        remoteLastDir.setBounds(528, 100, 25, 25);
        remoteLastDir.setEnabled(false);
        frame.getContentPane().add(remoteLastDir);
        remoteLastDir.addActionListener(e -> {
            try {
                ftp.changeDir("..");
                remotePath = ftp.getRemotePath();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            // refresh table
            setTableInfo();
        });

        //登录按钮------------------------------------------------
        JButton login=new JButton("登录");
        JButton exitButton=new JButton("退出");
        login.setFont(font_buttom);
        login.setBackground(UIManager.getColor("Button.highlight"));
        login.setBounds(260, 7, 64, 50);
        frame.getContentPane().add(login);
        login.addActionListener(e -> {
            System.out.println("登录==============");
            try {
                FTP=url.getText().trim();
                username=usernameField.getText().trim();
                password=passwordField.getText().trim();

                if(ftpMode.getText().equals("主动模式"))
                    ftp=new FtpClient_active(FTP,username,password);
                else
                    ftp=new FtpClient_passive(FTP,username,password);
                if(ftp.isLogined())
                {
                    file=ftp.getAllFile();
                    remotePath = ftp.getRemotePath();
                    localDir = new File(localPath);
                    localFiles = localDir.list();
                    setTableInfo();//显示所有文件信息

                    url.setEditable(false);
                    usernameField.setEditable(false);
                    passwordField.setEditable(false);

                    localPathText.setText(localPath);
                    remotePathText.setText(remotePath);

                    //Set Buttom Mode
                    refresh.setEnabled(true);
                    exitButton.setEnabled(true);
                    modeSwitch.setEnabled(false);
                    localLastDir.setEnabled(true);
                    remoteLastDir.setEnabled(true);
                    login.setEnabled(false);
                }

            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showConfirmDialog(null, "用户名或者密码错误\n username："+username, "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            }
        });

        //退出按钮------------------------------------------------
        exitButton.setFont(font_buttom);
        exitButton.setBackground(UIManager.getColor("Button.highlight"));
        exitButton.setBounds(260, 59, 64, 24);
        exitButton.setEnabled(false);
        frame.getContentPane().add(exitButton);
        exitButton.addActionListener(e -> {
            if(ftp.isLogined() && ftp.logOut()) {
                System.out.println("Logout Success!");
                url.setEditable(true);
                usernameField.setEditable(true);
                passwordField.setEditable(true);
                // TODO More Cleanup Stuff
                frame.getContentPane().remove(ftpScrollPane);
                ftpScrollPane = null;
                frame.getContentPane().remove(localScrollPane);
                localScrollPane = null;
                frame.repaint();

                //Set Buttom Mode
                refresh.setEnabled(false);
                exitButton.setEnabled(false);
                modeSwitch.setEnabled(true);
                localLastDir.setEnabled(false);
                remoteLastDir.setEnabled(false);
                login.setEnabled(true);

            }
        });

    }

    //显示本地信息-----------------------------------------------
    public void setTableInfo()
    {
        if(ftpScrollPane != null) frame.getContentPane().remove(ftpScrollPane);

        try {
            file = ftp.getAllFile();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set PathText
        remotePathText.setText(remotePath);

        //table数据初始化  从FTP读取所有文件
        String[][] remoteData=new String[file.length][4];
        for(int row=0;row<file.length;row++)
        {

            remoteData[row][0]=getName(file[row]);
            if(isDirectory(file[row]))
            {
                remoteData[row][1]="文件夹";
            } else {
                remoteData[row][1]=getSize(file[row])+"";
            }
            remoteData[row][2]="下载";
            remoteData[row][3]="删除";
        }

        //table列名-----------------------------------------------------
        String[] columnNames = {"文件", "大小(B)", "下载", "删除"};
        DefaultTableModel model = new DefaultTableModel();
        model.setDataVector(remoteData, columnNames);

        //加滚动条--------------------------------------------------------
        ftpScrollPane = new JScrollPane();
        ftpScrollPane.setBounds(300, 130, 260, 430);
        frame.getContentPane().add(ftpScrollPane);
        //加滚动条-----------------------------------------------------

        //table功能------------------------------------------------------
        ftpTable = new JTable(model);
        ftpScrollPane.setViewportView(ftpTable);
        ftpTable.setColumnSelectionAllowed(true);
        ftpTable.setCellSelectionEnabled(true);
        ftpTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        ftpTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        ftpTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        ftpTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        ftpTable.setToolTipText("可以点击");

        //table button初始化(最后一列的按键)--------------------
        DownloadButtonColumn downButt = new DownloadButtonColumn(ftpTable, 2, this);
        DeleteButtonColumn deleteButt = new DeleteButtonColumn(ftpTable, 3, this, DeleteButtonColumn.REMOTE);


        if(localScrollPane != null) frame.getContentPane().remove(localScrollPane);

        localDir = new File(localPath);
        localFiles = localDir.list();
        localPathText.setText(localPath);

       //从Local读取所有文件
        String[][] localData=new String[localFiles.length][4];
        for(int row=0;row<localFiles.length;row++)
        {

            localData[row][0]=localFiles[row];

            File f = new File(localPath, localFiles[row]);
            if(f.isDirectory())
            {
                localData[row][1]="文件夹";
            } else {
                localData[row][1]= f.length() + "";
            }

            localData[row][2]="上传";
            localData[row][3]="删除";
        }

        //table列名-----------------------------------------------------

        String[] localColumnNames = {"文件", "大小(B)", "上传", "删除"};
        DefaultTableModel localModel = new DefaultTableModel();
        localModel.setDataVector(localData, localColumnNames);

        //加滚动条--------------------------------------------------------
        localScrollPane = new JScrollPane();
        localScrollPane.setBounds(20, 130, 260, 430);
        frame.getContentPane().add(localScrollPane);


        //table功能------------------------------------------------------
        localTable = new JTable(localModel);
        localScrollPane.setViewportView(localTable);
        localTable.setColumnSelectionAllowed(true);
        localTable.setCellSelectionEnabled(true);
        localTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        localTable.setBorder(new LineBorder(new Color(0, 0, 0)));
        localTable.getColumnModel().getColumn(0).setPreferredWidth(175);
        localTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        localTable.setToolTipText("可以点击");

        //table button初始化(最后一列的按键)--------------------
        UploadButtonColumn upButt = new UploadButtonColumn(localTable, 2, this);
        DeleteButtonColumn localDeleteButt = new DeleteButtonColumn(localTable, 3, this, DeleteButtonColumn.LOCAL);

        // Repaint
        frame.repaint();
    }

    private void initConfig() {
        String path = System.getProperty("user.dir") + "/client/config/client.xml";

        File file = new File(path);
        SAXBuilder builder = new SAXBuilder();
        try {
            //从配置文件里找到当前的默认根目录和用户信息
            Document parse = builder.build(file);
            Element root = parse.getRootElement();
            //配置服务器的默认目录
            localPath = root.getChildText("rootDir");

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getName(String s) {
        String[] splitstr = s.split(" ");
        return splitstr[1];
    }

    private String getSize(String s) {
        String[] splitstr = s.split(" ");
        return splitstr[2];
    }

    private boolean isFile(String s) {
        String[] splitstr = s.split(" ");
        return splitstr[0].equals("文件");
    }

    private boolean isDirectory(String s) {
        String[] splitstr = s.split(" ");
        return splitstr[0].equals("文件夹");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }
}