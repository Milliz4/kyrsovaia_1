package gui;

import controller.VocabularyController;
import controller.GameController;  // ← добавлен импорт
import db.DBManager;
import view.VocabularyView;
import view.StatisticsView;
import view.GameView;  // ← добавлен импорт

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Английская лексика");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Вкладка "Уровни" - ИГРА
        GameView gameView = new GameView();
        new GameController(gameView, DBManager.getInstance());
        tabbedPane.addTab("Уровни", gameView);

        // Вкладка "Словарь"
        VocabularyView vocabView = new VocabularyView();
        new VocabularyController(vocabView, DBManager.getInstance());
        tabbedPane.addTab("Словарь", vocabView);

        // Вкладка "Статистика"
        tabbedPane.addTab("Статистика", new StatisticsView());

        add(tabbedPane, BorderLayout.CENTER);

        vocabView.loadAllWords(DBManager.getInstance().getAllWords());
    }

    private JPanel createPlaceholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}