package controller;

import db.DBManager;
import view.StatisticsView;

import java.util.List;

public class StatisticsController {
    private final StatisticsView view;
    private final DBManager dbManager;

    public StatisticsController(StatisticsView view, DBManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
        registerListeners();
        loadStatistics();
    }

    private void registerListeners() {
        view.getRefreshButton().addActionListener(e -> loadStatistics());
    }

    public void loadStatistics() {
        int totalWords = dbManager.getTotalWordsCount();
        int[] srsStats = dbManager.getSRSStatistics();
        int wordsForReview = dbManager.getWordsForReview().size();
        List<Object[]> history = dbManager.getAllStats();

        view.updateStatistics(totalWords, srsStats, wordsForReview, history);
    }
}