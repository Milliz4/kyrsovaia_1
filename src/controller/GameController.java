package controller;

import db.DBManager;
import model.GameSession;
import model.VocabularyItem;
import view.GameView;
import view.VocabularyView;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameController {
    private final GameView view;
    private final DBManager dbManager;
    private Timer countdownTimer;
    private final VocabularyController vocabController;
    private final VocabularyView vocabView;

    private List<VocabularyItem> allWords;
    private List<List<VocabularyItem>> levels;
    private int currentLevelIndex;
    private int currentWordInLevel;

    private int levelCorrect, levelWrong;
    private int totalCorrect, totalWrong;
    private int timeLeft;
    private int timeUsed;
    private static final int MAX_WORDS_PER_LEVEL = 5;
    private static final int TIME_PER_WORD = 10;

    private int calculateLevelDuration(int wordCount) {
        return Math.min(wordCount * TIME_PER_WORD, MAX_WORDS_PER_LEVEL * TIME_PER_WORD);
    }

    public GameController(GameView view, DBManager dbManager, VocabularyController vocabController,
                          VocabularyView vocabView) {
        this.view = view;
        this.dbManager = dbManager;
        this.vocabController = vocabController;
        this.vocabView = vocabView;
        registerListeners();
    }

    private void registerListeners() {
        view.getStartButton().addActionListener(e -> startGame());
        view.getSubmitButton().addActionListener(e -> checkAnswer());
        view.getNextLevelButton().addActionListener(e -> nextLevel());
        view.getFinishButton().addActionListener(e -> finishGame());

        view.getAnswerField().addActionListener(e -> {
            if (view.getSubmitButton().isEnabled()) checkAnswer();
        });
    }

    private void startGame() {
        List<VocabularyItem> wordsForReview = dbManager.getWordsForReview();

        if (wordsForReview.isEmpty()) {
            allWords = dbManager.getAllWords();
            JOptionPane.showMessageDialog(view,
                    "Все слова изучены! Начинаем новый круг.",
                    "Информация", JOptionPane.INFORMATION_MESSAGE);
        } else {
            allWords = wordsForReview;
        }

        if (allWords.size() < 1) {
            JOptionPane.showMessageDialog(view,
                    "Добавьте слова в словарь!",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        totalCorrect = 0;
        totalWrong = 0;
        currentLevelIndex = 0;

        if (view.getHistoryPanel() != null) {
            view.getHistoryPanel().clearHistory();
            List<Object[]> historyData = dbManager.getLevelHistory();
            view.getHistoryPanel().loadHistoryFromDB(historyData);
        }

        Collections.shuffle(allWords);
        levels = new ArrayList<>();
        for (int i = 0; i < allWords.size(); i += MAX_WORDS_PER_LEVEL) {
            levels.add(allWords.subList(i, Math.min(i + MAX_WORDS_PER_LEVEL, allWords.size())));
        }
        view.showGameMode();
        startLevel();
    }

    private void startLevel() {
        List<VocabularyItem> currentLevelWords = levels.get(currentLevelIndex);
        currentWordInLevel = 0;
        levelCorrect = 0;
        levelWrong = 0;

        int levelDuration = calculateLevelDuration(currentLevelWords.size());
        timeLeft = levelDuration;
        timeUsed = 0;

        view.setLevelInfo(currentLevelIndex + 1);
        view.updateTimer(timeLeft);
        view.updateScore(totalCorrect, totalWrong);
        view.clearAnswer();
        view.enableInput(true);
        view.showGameMode();
        view.prepareForGame();

        showCurrentWord();

        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            view.updateTimer(timeLeft);
            if (timeLeft <= 0) {
                endLevel(true);
            }
        });
        countdownTimer.start();
    }

    private void showCurrentWord() {
        if (currentWordInLevel >= levels.get(currentLevelIndex).size()) {
            endLevel(false);
            return;
        }

        VocabularyItem word = levels.get(currentLevelIndex).get(currentWordInLevel);
        String context = (word.getContextSentence() != null && !word.getContextSentence().isEmpty()) ? word.getContextSentence() : "Контекст не указан";

        view.setWordDisplay(word.getEnglish(), context);
        view.clearAnswer();
        view.enableInput(true);
    }

    private void checkAnswer() {
        String userAnswer = view.getUserAnswer().toLowerCase().trim();

        if (userAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Введите ответ!", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentWordInLevel >= levels.get(currentLevelIndex).size()) {
            return;
        }

        VocabularyItem currentWord = levels.get(currentLevelIndex).get(currentWordInLevel);
        String correctAnswer = currentWord.getRussian().toLowerCase().trim();

        boolean isCorrect = correctAnswer.equals(userAnswer);

        if (isCorrect) {
            levelCorrect++;
            totalCorrect++;
            updateSRSLevel(currentWord, true);
        } else {
            levelWrong++;
            totalWrong++;
            updateSRSLevel(currentWord, false);
            JOptionPane.showMessageDialog(view,
                    "Неверно!\nПравильно: " + currentWord.getRussian(), "Ответ", JOptionPane.ERROR_MESSAGE);
        }

        view.updateScore(totalCorrect, totalWrong);
        currentWordInLevel++;
        showCurrentWord();
    }

    private void endLevel(boolean timeOut) {
        if (countdownTimer != null) countdownTimer.stop();

        timeUsed = calculateLevelDuration(levels.get(currentLevelIndex).size()) - timeLeft;
        if (timeUsed < 0) timeUsed = 0;

        boolean isSuccess = (levelCorrect > 0 && levelWrong == 0) || (levelCorrect > levelWrong);
        String sessionStatus = isSuccess ? "success" : "repeat";

        String currentDate = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        dbManager.saveLevelHistory(
                currentLevelIndex + 1,
                levelCorrect,
                levelWrong,
                timeUsed,
                currentDate
        );

        dbManager.saveGameSession(new GameSession(
                0,
                LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                levelCorrect + levelWrong,
                levelCorrect,
                levelWrong,
                sessionStatus
        ));

        if (view.getHistoryPanel() != null) {
            view.getHistoryPanel().addLevelResult(
                    currentLevelIndex + 1,
                    levelCorrect,
                    levelWrong,
                    timeUsed,
                    currentDate
            );
        }
        vocabView.loadAllWords(dbManager.getAllWords());

        view.enableInput(false);
        view.showLevelResults(levelCorrect, levelWrong, timeUsed);
    }

    private void nextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex < levels.size()) {
            startLevel();
        } else {
            int choice = JOptionPane.showConfirmDialog(view,
                    "Все уровни пройдены!\nНачать новую сессию?",
                    "Сессия завершена",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                totalCorrect = 0;
                totalWrong = 0;
                currentLevelIndex = 0;

                dbManager.clearLevelHistory();
                if (view.getHistoryPanel() != null) {
                    view.getHistoryPanel().clearHistory();
                }

                Collections.shuffle(allWords);
                levels.clear();
                for (int i = 0; i < allWords.size(); i += MAX_WORDS_PER_LEVEL) {
                    levels.add(allWords.subList(i, Math.min(i + MAX_WORDS_PER_LEVEL, allWords.size())));
                }

                startLevel();
            } else {
                finishGame();
            }
        }
    }

    private void finishGame() {
        if (countdownTimer != null) countdownTimer.stop();

        int totalWords = totalCorrect + totalWrong;
        double accuracy = totalWords > 0 ? (double) totalCorrect / totalWords : 0;
        String difficulty = accuracy >= 0.8 ? "отлично" : (accuracy >= 0.5 ? "хорошо" : "требует повторения");

        GameSession session = new GameSession(
                0,
                LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                totalWords,
                totalCorrect,
                totalWrong,
                difficulty
        );
        dbManager.saveGameSession(session);
        vocabView.loadAllWords(dbManager.getAllWords());

        int exitChoice = JOptionPane.showConfirmDialog(view,
                "Игра завершена!\n" +
                        "Всего слов: " + totalWords + "\n" +
                        "Правильно: " + totalCorrect + "\n" +
                        "Ошибок: " + totalWrong + "\n" +
                        "Точность: " + String.format("%.1f", accuracy * 100) + "%\n\n" +
                        "Выйти из приложения?",
                "Завершение игры",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (exitChoice == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else {
            view.showInstruction("Игра завершена. Нажмите Старт для новой сессии");
            view.setLevelInfo(0);
            view.updateTimer(30);
            view.updateScore(0, 0);
            view.clearAnswer();
            view.enableInput(false);
        }
    }

    private void updateSRSLevel(VocabularyItem word, boolean isCorrect) {
        int currentLevel = word.getBoxLevel();
        int newLevel = isCorrect ? Math.min(currentLevel + 1, 5) : 1;

        if (newLevel != currentLevel) {
            word.setBoxLevel(newLevel);
            word.setNextReviewDate(vocabController.calculateNextReviewDate(newLevel));
            dbManager.updateWordNoStats(word);
        }
    }
}