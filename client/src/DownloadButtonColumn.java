import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DownloadButtonColumn extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor, ActionListener {
    private JTable table;
    private JButton renderButton;
    private JButton editButton;
    private String text;
    private MainGUI guiThread;

    public DownloadButtonColumn(JTable table, int column, MainGUI guiThread) {
        super();
        this.table = table;
        this.guiThread = guiThread;
        renderButton = new JButton();
        editButton = new JButton();
        editButton.setFocusPainted(false);
        editButton.addActionListener(this);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(column).setCellRenderer(this);
        columnModel.getColumn(column).setCellEditor(this);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (hasFocus) {
            renderButton.setForeground(table.getForeground());
            renderButton.setBackground(UIManager.getColor("Button.background"));
        } else if (isSelected) {
            renderButton.setForeground(table.getSelectionForeground());
            renderButton.setBackground(table.getSelectionBackground());
        } else {
            renderButton.setForeground(table.getForeground());
            renderButton.setBackground(UIManager.getColor("Button.background"));
        }

        renderButton.setText((value == null) ? " " : value.toString());
        return renderButton;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        text = (value == null) ? " " : value.toString();
        editButton.setText(text);
        return editButton;
    }

    public Object getCellEditorValue() {
        return text;
    }

    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();

        String[] file1 = new String[0];
        try {
            file1 = MainGUI.getFile(); //得到所有的文件

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String from_file_name = getName(file1[table.getSelectedRow()]);

        // Check if SelectedRow is a Directory.
        if(file1[table.getSelectedRow()].startsWith("文件夹")) {
            try {
                MainGUI.getFtp().changeDir(from_file_name);
                MainGUI.remotePath = MainGUI.getFtp().getRemotePath();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        } else {
            String path = MainGUI.getLocalPath();
            try {
                MainGUI.getFtp().download(from_file_name, path);
                System.out.println("下载成功! ");



            }catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        // Refresh Tables
        guiThread.setTableInfo();
    }


    private String getName(String s) {
        String[] splitstr = s.split(" ");
        return splitstr[1];
    }

}