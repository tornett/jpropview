import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class JPropView extends JFrame {

    private static final long serialVersionUID = 1L;

    public JPropView() {

        Container cp = getContentPane();
        setSize(800, 550);
        setLocation(50, 50);
        setTitle("System Information");
        JTable thisTable = new JTable(new SystemPropertiesTableModel());
        JScrollPane sp = new JScrollPane(thisTable);
        JTabbedPane tabbedPane = new JTabbedPane();
        cp.add(tabbedPane);
        tabbedPane.add("System Properties", sp);

        thisTable = new JTable(new FontsTableModel());
        thisTable.setRowHeight(26);
        thisTable.setDefaultRenderer(Font.class, new FontSampleRenderer(
                (float) 15.0, "The quick brown fox jumps over the lazy dog."));
        sp = new JScrollPane(thisTable);
        tabbedPane.add("Fonts", sp);

        UIManagerPropertiesTableModel model = new UIManagerPropertiesTableModel();
        thisTable = new JTable(model);
        sp = new JScrollPane(thisTable);
        tabbedPane.add("UIManager Properties", sp);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        (new JPropView()).setVisible(true);
    }

}

@SuppressWarnings("unchecked")
class UIManagerPropertiesTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    static Object[][] dataArray;

    static Object[] columnNames = { "Property", "Value" };

    static {

        TreeMap<String, String> v = new TreeMap<String, String>();
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            String name = keys.nextElement().toString();
            String prop = UIManager.getString(name);
            v.put(name, prop);
        }

        dataArray = new Object[v.size()][2];
        int j = 0;
        for (Iterator it = v.keySet().iterator(); it.hasNext();) {
            String name = (String)it.next();
            String prop = (String)v.get(name);
            Object[] row = {name, prop};
            dataArray[j++] = row;
        }
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public UIManagerPropertiesTableModel() {
        super((Object[][]) dataArray, columnNames);
    }
}

@SuppressWarnings("unchecked")
class SystemPropertiesTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    static Object[][] dataArray;

    static Object[] columnNames = { "Property", "Value" };

    static {

        Vector<Object[]> v = new Vector<Object[]>();
        Properties props = System.getProperties();

        Enumeration enm = props.propertyNames();
        while (enm.hasMoreElements()) {
            String name = (String) enm.nextElement();
            String prop = props.getProperty(name);
            Object[] row = { name, prop };
            v.addElement(row);
        }

        dataArray = new Object[v.size()][2];
        for (int j = 0; j < v.size(); j++)
            dataArray[j] = (Object[]) v.elementAt(j);
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public SystemPropertiesTableModel() {
        super((Object[][]) dataArray, columnNames);
    }

}

class FontsTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    static Object[][] dataArray;

    static Object[] columnNames = { "Font", "Sample" };

    static {
        Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAllFonts();
        dataArray = new Object[fonts.length][1];
        for (int j = 0; j < fonts.length; j++) {
            Object[] row = { fonts[j].getName(), fonts[j] };
            dataArray[j] = row;
        }
    }

    public FontsTableModel() {
        super(dataArray, columnNames);
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
        case 0:
            return String.class;
        case 1:
            return Font.class;
        }
        return Object.class;
    }

}

class FontSampleRenderer implements TableCellRenderer {

    String sampleString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    float size = 12;

    Hashtable<Object, JLabel> cache = new Hashtable<Object, JLabel>(50, 10);

    protected static Border noFocusBorder = new EmptyBorder(1, 2, 1, 2);

    public FontSampleRenderer() {

    }

    public FontSampleRenderer(float size, String sampleString) {
        this();
        this.size = size;
        if (sampleString != null)
            this.sampleString = sampleString;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel textField = (JLabel) cache.get(value);
        if (textField == null) {

            java.awt.Font f = (java.awt.Font) value;
            textField = new JLabel(sampleString);
            textField.setOpaque(true);
            Font font = f.deriveFont(size);
            textField.setFont(font);
            cache.put(value, textField);
        }

        if (isSelected) {
            textField.setForeground(table.getSelectionForeground());
            textField.setBackground(table.getSelectionBackground());
        } else {
            textField.setForeground(table.getForeground());
            textField.setBackground(table.getBackground());
        }

        if (hasFocus) {
            textField.setBorder(UIManager
                    .getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                textField.setForeground(UIManager
                        .getColor("Table.focusCellForeground"));
                textField.setBackground(UIManager
                        .getColor("Table.focusCellBackground"));
            }
        } else {
            textField.setBorder(noFocusBorder);
        }

        return textField;
    }
}
