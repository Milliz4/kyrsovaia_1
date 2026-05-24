package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LevelHistoryPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable historyTable;

    private static final String[] COLUMNS = {
            "Уровень", "Правильно", "Ошибок", "Время", "Результат"
    };

    public LevelHistoryPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0)); // Ширина 250px
        setBorder(BorderFactory.createTitledBorder("История уровней"));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(25);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        add(new JScrollPane(historyTable), BorderLayout.CENTER);
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