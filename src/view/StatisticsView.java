package view;

import db.DBManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StatisticsView extends JPanel {
    private final DefaultTableModel statsModel;
    private final JTable statsTable;
    private final JButton refreshBtn;

    private static final String[] COLUMNS = {
            " Дата", " Добавлено", "Изменено", " Удалено", " Всего слов"
    };

    public StatisticsView() {
        setLayout(new BorderLayout());

        statsModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Таблица только для чтения
            }
        };

        statsTable = new JTable(statsModel);
        statsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        statsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        statsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        statsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        statsTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        add(new JScrollPane(statsTable), BorderLayout.CENTER);

        refreshBtn = new JButton(" Обновить статистику");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadStats());
        loadStats();
    }

    private void loadStats() {
        statsModel.setRowCount(0);
        for (Object[] row : DBManager.getInstance().getAllStats()) {
            statsModel.addRow(row);
        }
    }
}