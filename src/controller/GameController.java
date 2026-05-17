package controller;

import db.DBManager;
import model.GameSession;
import model.VocabularyItem;
import view.GameView;

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

    private List<VocabularyItem> allWords;
    private List<List<VocabularyItem>> levels;
    private int currentLevelIndex;
    private int currentWordInLevel;

    private int levelCorrect, levelWrong;
    private int totalCorrect, totalWrong;
    private int timeLeft;
    private int timeUsed;
    private static final int LEVEL_DURATION = 30;
    private static final int WORDS_PER_LEVEL = 3;

    public GameController(GameView view, DBManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
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
        allWords = dbManager.getAllWords();
        if (allWords.size() < WORDS_PER_LEVEL) {
            JOptionPane.showMessageDialog(view,
                    "Нужно минимум 3 слова для старта уровня. Добавьте слова в словарь!",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Collections.shuffle(allWords);
        levels = new ArrayList<>();
        for (int i = 0; i < allWords.size(); i += WORDS_PER_LEVEL) {
            levels.add(allWords.subList(i, Math.min(i + WORDS_PER_LEVEL, allWords.size())));
        }

        currentLevelIndex = 0;
        totalCorrect = 0;
        totalWrong = 0;
        view.showGameMode();
        startLevel();
    }

    private void startLevel() {
        List<VocabularyItem> currentLevelWords = levels.get(currentLevelIndex);
        currentWordInLevel = 0;
        levelCorrect = 0;
        levelWrong = 0;
        timeLeft = LEVEL_DURATION;
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
        String context = (word.getContextSentence() != null && !word.getContextSentence().isEmpty())
                ? word.getContextSentence()
                : "Контекст не указан";

        view.setWordDisplay(word.getEnglish(), context);
        view.clearAnswer();
        view.enableInput(true);
    }

    private void checkAnswer() {
        String userAnswer = view.getUserAnswer().toLowerCase();
        if (userAnswer.isEmpty()) return;

        VocabularyItem currentWord = levels.get(currentLevelIndex).get(currentWordInLevel);
        String correctAnswer = currentWord.getRussian().toLowerCase();

        boolean isCorrect = correctAnswer.equals(userAnswer) ||
                correctAnswer.contains(userAnswer) ||
                userAnswer.contains(correctAnswer);

        if (isCorrect) {
            levelCorrect++;
            totalCorrect++;
        } else {
            levelWrong++;
            totalWrong++;
            JOptionPane.showMessageDialog(view,
                    "Неверно!\nПравильно: " + currentWord.getRussian(),
                    "Ответ", JOptionPane.INFORMATION_MESSAGE);
        }

        view.updateScore(totalCorrect, totalWrong);
        currentWordInLevel++;
        showCurrentWord();
    }

    private void endLevel(boolean timeOut) {
        if (countdownTimer != null) countdownTimer.stop();

        timeUsed = LEVEL_DURATION - timeLeft;
        if (timeUsed < 0) timeUsed = 0;
        if (timeUsed > LEVEL_DURATION) timeUsed = LEVEL_DURATION;

        view.enableInput(false);
        view.showLevelResults(levelCorrect, levelWrong, timeUsed);
    }

    private void nextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex < levels.size()) {
            startLevel();
        } else {
            JOptionPane.showMessageDialog(view, "Все уровни пройдены!", "Победа", JOptionPane.INFORMATION_MESSAGE);
            finishGame();
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

        view.showInstruction("Игра завершена. Нажмите Старт для новой сессии");
        view.setLevelInfo(0);
        view.updateTimer(LEVEL_DURATION);
        view.updateScore(0, 0);
        view.clearAnswer();
        view.enableInput(false);
    }
}