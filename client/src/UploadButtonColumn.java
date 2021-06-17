
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class UploadButtonColumn extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor, ActionListener {
    private JTable table;
    private JButton renderButton;
    private JButton editButton;
    private String text;
    private MainGUI guiThread;

    public UploadButtonColumn(JTable table, int column, MainGUI guiThread) {
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

        //上传点击按钮触发------------------------------------
        System.out.println("上传！！！！！");

        String localFileName = MainGUI.localFiles[table.getSelectedRow()];
        String path = MainGUI.localPath + "/" +localFileName;
        try {
            //上传
            MainGUI.getFtp().upload(path);
            System.out.println("文件上传成功");
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        //上传点击按钮触发------------------------------------

        // refresh tables
        guiThread.setTableInfo();
    }

    private String getName(String s) {
        String[] splitstr = s.split(" ");
        return splitstr[1];
    }

}