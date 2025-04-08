package src;

import java.awt.Component;
import java.util.EventObject;
import java.util.function.BiConsumer;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;


public class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;
    private JTable table;
    private int row;
    private BiConsumer<JTable, Integer> onClick;

    public ButtonEditor(JCheckBox checkBox, JTable table, BiConsumer<JTable, Integer> onClick) {
        super(checkBox);
        this.table = table;
        this.onClick = onClick;

        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> {
            fireEditingStopped();
            if (onClick != null) {
                onClick.accept(table, row);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table, 
        Object value,
        boolean isSelected,
        int row,
        int column
    ) {
        this.label = (value == null) ? "" : value.toString();
        this.row = row;
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        return true;
    }
}
