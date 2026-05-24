package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StatisticsView extends JPanel {
    private final DefaultTableModel statsModel;
    private final JTable statsTable;
    private final JButton refreshBtn;

    private JLabel totalWordsLabel;
    private JLabel[] srsLevelLabels;
    private JLabel wordsForReviewLabel;

    private static final String[] COLUMNS = {
            "Дата", "Добавлено", "Изменено", "Удалено", "Всего слов"
    };

    public StatisticsView() {
        setLayout(new BorderLayout());
        add(createSRSPanel(), BorderLayout.NORTH);

        statsModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        statsTable = new JTable(statsModel);
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("История активности"));
        add(scrollPane, BorderLayout.CENTER);

        refreshBtn = new JButton("Обновить статистику");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    public void updateStatistics(int totalWords, int[] srsStats, int wordsForReview, List<Object[]> history) {
        totalWordsLabel.setText("Всего слов: " + totalWords);
        wordsForReviewLabel.setText("Требуют повторения: " + wordsForReview);

        for (int i = 0; i < 5; i++) {
            srsLevelLabels[i].setText(srsStats[i] + " слов");
        }

        statsModel.setRowCount(0);
        for (Object[] row : history) {
            statsModel.addRow(row);
        }
        statsModel.fireTableDataChanged();
    }

    public JButton getRefreshButton() { return refreshBtn; }

    private JPanel createSRSPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Статистика SRS"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        totalWordsLabel = new JLabel("Всего слов: 0");
        totalWordsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalWordsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(totalWordsLabel);
        panel.add(Box.createVerticalStrut(10));

        JPanel levelsPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        srsLevelLabels = new JLabel[5];

        String[] levelNames = {"Уровень 1 (ежедневно)",
                "Уровень 2 (каждые 3 дня)",
                "Уровень 3 (каждую неделю)",
                "Уровень 4 (каждые 2 недели)",
                "Уровень 5 (каждый месяц)"};

        for (int i = 0; i < 5; i++) {
            JLabel nameLabel = new JLabel(levelNames[i] + ":");
            srsLevelLabels[i] = new JLabel("0 слов");
            levelsPanel.add(nameLabel);
            levelsPanel.add(srsLevelLabels[i]);
        }

        panel.add(levelsPanel);
        panel.add(Box.createVerticalStrut(10));

        wordsForReviewLabel = new JLabel("Требуют повторения: 0");
        wordsForReviewLabel.setFont(new Font("Arial", Font.BOLD, 14));
        wordsForReviewLabel.setForeground(Color.RED);
        wordsForReviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(wordsForReviewLabel);

        return panel;
    }
}