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
    private SRSChartPanel chartPanel;

    private static final Color[] SRS_COLORS = {
            new Color(220, 38, 38),
            new Color(250, 109, 8),
            new Color(210, 199, 6),
            new Color(34, 197, 94),
            new Color(22, 163, 74)
    };

    private static final String[] COLUMNS = {
            "Дата", "Добавлено", "Изменено", "Удалено", "Всего слов"
    };

    public StatisticsView() {
        setBackground(new Color(248, 250, 252));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(createSRSPanel(), BorderLayout.NORTH);

        statsModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        statsTable = new JTable(statsModel);
        styleTable(statsTable);

        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("История активности"),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        add(scrollPane, BorderLayout.CENTER);

        refreshBtn = new JButton("Обновить статистику");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(219, 234, 254));
    }

    public void updateStatistics(int totalWords, int[] srsStats, int wordsForReview, List<Object[]> history) {
        totalWordsLabel.setText("Всего слов: " + totalWords);
        wordsForReviewLabel.setText("Требуют повторения: " + wordsForReview);

        for (int i = 0; i < 5; i++) {
            srsLevelLabels[i].setText(srsStats[i] + " слов");
            srsLevelLabels[i].setForeground(SRS_COLORS[i]);
        }

        if (chartPanel != null) {
            chartPanel.updateData(srsStats);
        }

        statsModel.setRowCount(0);
        for (Object[] row : history) {
            statsModel.addRow(row);
        }
        statsModel.fireTableDataChanged();
    }

    public JButton getRefreshButton() { return refreshBtn; }

    private JPanel createSRSPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Статистика SRS"),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        totalWordsLabel = new JLabel("Всего слов: 0");
        totalWordsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalWordsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(totalWordsLabel);
        leftPanel.add(Box.createVerticalStrut(16));

        srsLevelLabels = new JLabel[5];
        String[] levelNames = {
                "Уровень 1 (ежедневно):",
                "Уровень 2 (каждые 3 дня):",
                "Уровень 3 (каждую неделю):",
                "Уровень 4 (каждые 2 недели):",
                "Уровень 5 (каждый месяц):"
        };

        for (int i = 0; i < 5; i++) {
            JPanel levelRow = new JPanel(new BorderLayout());
            levelRow.setBackground(Color.WHITE);
            levelRow.setMaximumSize(new Dimension(250, 28));
            levelRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel nameLabel = new JLabel(levelNames[i]);
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            levelRow.add(nameLabel, BorderLayout.WEST);

            srsLevelLabels[i] = new JLabel("0 слов");
            srsLevelLabels[i].setFont(new Font("Arial", Font.BOLD, 13));
            srsLevelLabels[i].setForeground(SRS_COLORS[i]);
            levelRow.add(srsLevelLabels[i], BorderLayout.EAST);

            leftPanel.add(levelRow);
            leftPanel.add(Box.createVerticalStrut(6));
        }

        leftPanel.add(Box.createVerticalGlue());

        wordsForReviewLabel = new JLabel("Требуют повторения: 0");
        wordsForReviewLabel.setFont(new Font("Arial", Font.BOLD, 14));
        wordsForReviewLabel.setForeground(new Color(220, 38, 38));
        wordsForReviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(wordsForReviewLabel);

        chartPanel = new SRSChartPanel();
        chartPanel.setPreferredSize(new Dimension(300, 220));

        mainPanel.add(leftPanel);
        mainPanel.add(chartPanel);

        return mainPanel;
    }

    private class SRSChartPanel extends JPanel {
        private int[] data = {0, 0, 0, 0, 0};
        private String[] labels = {"Ур. 1", "Ур. 2", "Ур. 3", "Ур. 4", "Ур. 5"};

        public SRSChartPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        }

        public void updateData(int[] newData) {
            this.data = newData.clone();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 30;
            int barWidth = (width - 2 * padding) / data.length - 10;
            int maxVal = 1;
            for (int val : data) {
                if (val > maxVal) maxVal = val;
            }

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(new Color(31, 41, 55));
            g2d.drawString("Распределение по уровням", padding, 18);

            for (int i = 0; i < data.length; i++) {
                int barHeight = (int) ((double) data[i] / maxVal * (height - padding - 40));
                int x = padding + i * (barWidth + 10) + 5;
                int y = height - padding - barHeight;

                g2d.setColor(SRS_COLORS[i]);
                g2d.fillRoundRect(x, y, barWidth, barHeight, 4, 4);
                g2d.setColor(SRS_COLORS[i].darker());
                g2d.drawRoundRect(x, y, barWidth, barHeight, 4, 4);

                g2d.setColor(new Color(31, 41, 55));
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                String valText = String.valueOf(data[i]);
                int textWidth = g2d.getFontMetrics().stringWidth(valText);
                g2d.drawString(valText, x + (barWidth - textWidth) / 2, y - 5);

                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                int labelWidth = g2d.getFontMetrics().stringWidth(labels[i]);
                g2d.drawString(labels[i], x + (barWidth - labelWidth) / 2, height - 12);
            }
        }
    }
}