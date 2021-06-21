import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DeleteButtonColumn extends AbstractCellEditor implements
        TableCellRenderer, TableCellEditor, ActionListener {

    public static final int LOCAL = 1;
    public static final int REMOTE = 2;

    private JTable table;
    private JButton renderButton;
    private JButton editButton;
    private String text;
    private MainGUI guiThread;
    private int mode;

    public DeleteButtonColumn(JTable table, int column, MainGUI guiThread, int mode) {
        super();
        this.table = table;
        this.guiThread = guiThread;
        this.mode = mode;
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

        if(mode == LOCAL) {
            String localFileName = MainGUI.localFiles[table.getSelectedRow()];
            File f = new File(MainGUI.localPath, localFileName);
            f.delete();
        } else {
            try {
                String[] file1 = MainGUI.getFile(); //得到所有的文件
                String from_file_name = getName(file1[table.getSelectedRow()]);
                if(!MainGUI.getFtp().delete(from_file_name)){
                    JOptionPane.showConfirmDialog(null, "文件删除出错", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
                };
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        // refresh table
        guiThread.setTableInfo();
    }

    private String getName(String s) {
        String[] splitstr = s.split(" ");
        return splitstr[1];
    }

}