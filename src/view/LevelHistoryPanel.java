package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LevelHistoryPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable historyTable;

    private static final String[] COLUMNS = {
            "Уровень", "✓", "✕", "Время", "Результат"
    };

    public LevelHistoryPanel() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(260, 100));
        setPreferredSize(new Dimension(260, 0));
        setBorder(BorderFactory.createTitledBorder("История уровней"));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(28);
        historyTable.setFillsViewportHeight(true);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        historyTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(25);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(25);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(55);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(historyTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4));
        add(scroll, BorderLayout.CENTER);
    }

    public void addLevelResult(int levelNum, int correct, int wrong, int timeSec, String date) {
        tableModel.addRow(new Object[]{
                levelNum,
                correct,
                wrong,
                timeSec + " сек",
                date
        });
    }

    public void clearHistory() {
        tableModel.setRowCount(0);
    }

    public void loadHistoryFromDB(List<Object[]> historyData) {
        tableModel.setRowCount(0);
        for (Object[] row : historyData) {
            tableModel.addRow(row);
        }
    }
}