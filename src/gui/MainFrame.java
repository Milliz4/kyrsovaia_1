package gui;

import controller.VocabularyController;
import db.DBManager;
import view.VocabularyView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Английская лексика");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Уровни", createPlaceholderPanel("Игровые уровни (в разработке)"));

        VocabularyView vocabView = new VocabularyView();
        new VocabularyController(vocabView, DBManager.getInstance());
        tabbedPane.addTab(" Словарь", vocabView);

        tabbedPane.addTab(" Статистика", createPlaceholderPanel("Статистика прогресса (в разработке)"));

        add(tabbedPane, BorderLayout.CENTER);

        vocabView.loadAllWords(DBManager.getInstance().getAllWords());
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}