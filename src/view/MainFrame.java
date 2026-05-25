package view;

import controller.VocabularyController;
import controller.GameController;
import controller.StatisticsController;
import db.DBManager;
import service.ReminderService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Английская лексика");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        VocabularyView vocabView = new VocabularyView();
        VocabularyController vocabController = new VocabularyController(vocabView, DBManager.getInstance());

        GameView gameView = new GameView();
        new GameController(gameView, DBManager.getInstance(), vocabController, vocabView);
        tabbedPane.addTab("Уровни", gameView);

        tabbedPane.addTab("Словарь", vocabView);
        vocabView.loadAllWords(DBManager.getInstance().getAllWords());

        StatisticsView statsView = new StatisticsView();
        new StatisticsController(statsView, DBManager.getInstance());
        tabbedPane.addTab("Статистика", statsView);

        tabbedPane.addTab("Справка", new HelpView());

        add(tabbedPane, BorderLayout.CENTER);
    }
}