package gui;

import controller.VocabularyController;
import controller.GameController;
import db.DBManager;
import view.VocabularyView;
import view.StatisticsView;
import view.GameView;

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
        tabbedPane.addTab("Словарь", vocabView);
        vocabView.loadAllWords(DBManager.getInstance().getAllWords());

        GameView gameView = new GameView();
        new GameController(gameView, DBManager.getInstance(), vocabController, vocabView);
        tabbedPane.addTab("Уровни", gameView);

        tabbedPane.addTab("Статистика", new StatisticsView());

        add(tabbedPane, BorderLayout.CENTER);

        vocabView.loadAllWords(DBManager.getInstance().getAllWords());
    }
}