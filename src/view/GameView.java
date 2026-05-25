package view;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {
    private JLabel levelLabel, timerLabel, scoreLabel;
    private JLabel instructionLabel;
    private JLabel wordLabel, contextLabel;
    private JTextField answerField;
    private JButton startButton, submitButton, nextLevelButton, finishButton;
    private JPanel gamePanel, resultPanel, topPanel;
    private JPanel cardPanel;

    private JLabel valCorrect;
    private JLabel valWrong;
    private JLabel valTime;
    private LevelHistoryPanel historyPanel;

    public GameView() {
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        historyPanel = new LevelHistoryPanel();

        topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        levelLabel = new JLabel("Уровень: 0", SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 20));

        timerLabel = new JLabel("Время: 30 сек", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        scoreLabel = new JLabel("0 | 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        topPanel.add(levelLabel);
        topPanel.add(timerLabel);
        topPanel.add(scoreLabel);

        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        instructionLabel = new JLabel("Нажмите 'Старт' чтобы начать уровень!", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 22));
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wordLabel.setVisible(false);

        contextLabel = new JLabel("", SwingConstants.CENTER);
        contextLabel.setFont(new Font("Georgia", Font.ITALIC, 15));
        contextLabel.setForeground(new Color(50, 50, 150));
        contextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contextLabel.setVisible(false);
        contextLabel.setMaximumSize(new Dimension(500, 60));

        answerField = new JTextField(25);
        answerField.setFont(new Font("Arial", Font.PLAIN, 16));
        answerField.setMaximumSize(new Dimension(350, 35));
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerField.setVisible(false);
        answerField.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        startButton = new JButton("Старт уровня");
        startButton.setFont(new Font("Arial", Font.BOLD, 13));
        submitButton = new JButton("Ответить");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 13));
        submitButton.setVisible(false);
        submitButton.setEnabled(false);

        buttonPanel.add(startButton);
        buttonPanel.add(submitButton);

        gamePanel.add(Box.createVerticalGlue());
        gamePanel.add(instructionLabel);
        gamePanel.add(Box.createVerticalStrut(15));
        gamePanel.add(wordLabel);
        gamePanel.add(Box.createVerticalStrut(10));
        gamePanel.add(contextLabel);
        gamePanel.add(Box.createVerticalStrut(20));
        gamePanel.add(answerField);
        gamePanel.add(Box.createVerticalStrut(20));
        gamePanel.add(buttonPanel);
        gamePanel.add(Box.createVerticalGlue());

        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel resTitle = new JLabel("Уровень завершён!", SwingConstants.CENTER);
        resTitle.setFont(new Font("Arial", Font.BOLD, 24));
        resTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        resultPanel.add(resTitle, BorderLayout.NORTH);

        JPanel statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
        statsContainer.setMaximumSize(new Dimension(400, 250));

        valCorrect = new JLabel("0", SwingConstants.LEFT);
        valCorrect.setFont(new Font("Arial", Font.BOLD, 18));
        valCorrect.setForeground(Color.GREEN);

        valWrong = new JLabel("0", SwingConstants.LEFT);
        valWrong.setFont(new Font("Arial", Font.BOLD, 18));
        valWrong.setForeground(Color.RED);

        valTime = new JLabel("30 сек", SwingConstants.LEFT);
        valTime.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel correctPanel = createStatRow("Правильно:", valCorrect);
        JPanel wrongPanel = createStatRow("Ошибок:", valWrong);
        JPanel timePanel = createStatRow("Время:", valTime);

        statsContainer.add(Box.createVerticalStrut(20));
        statsContainer.add(correctPanel);
        statsContainer.add(Box.createVerticalStrut(15));
        statsContainer.add(wrongPanel);
        statsContainer.add(Box.createVerticalStrut(15));
        statsContainer.add(timePanel);
        statsContainer.add(Box.createVerticalStrut(20));

        resultPanel.add(statsContainer, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setOpaque(false);
        nextLevelButton = new JButton("Следующий уровень");
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 13));
        nextLevelButton.setPreferredSize(new Dimension(200, 35));
        finishButton = new JButton("Завершить игру");
        finishButton.setFont(new Font("Arial", Font.PLAIN, 13));
        finishButton.setPreferredSize(new Dimension(180, 35));

        buttonsPanel.add(nextLevelButton);
        buttonsPanel.add(finishButton);
        resultPanel.add(buttonsPanel, BorderLayout.SOUTH);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(gamePanel, "game");
        cardPanel.add(resultPanel, "result");

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(cardPanel, BorderLayout.CENTER);

        add(historyPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "game");
    }

    public LevelHistoryPanel getHistoryPanel() { return historyPanel; }

    private JPanel createStatRow(String labelText, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(400, 40));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText, SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        panel.add(label, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);

        return panel;
    }

    public void prepareForGame() {
        instructionLabel.setVisible(false);
        startButton.setVisible(false);

        wordLabel.setVisible(true);
        contextLabel.setVisible(true);
        answerField.setVisible(true);
        submitButton.setVisible(true);
    }

    public void showInstruction(String text) {
        instructionLabel.setVisible(true);
        instructionLabel.setText(text);
        startButton.setVisible(true);

        wordLabel.setVisible(false);
        contextLabel.setVisible(false);
        answerField.setVisible(false);
        submitButton.setVisible(false);
    }

    public void showGameMode() {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "game");
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public void showResultMode() {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "result");
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    public void setLevelInfo(int level) { levelLabel.setText("Уровень: " + level); }
    public void setWordDisplay(String english, String context) {
        wordLabel.setText(english); contextLabel.setText(context);
    }
    public void clearAnswer() { answerField.setText(""); }
    public void enableInput(boolean enable) {
        answerField.setEnabled(enable); submitButton.setEnabled(enable);
        if (enable) answerField.requestFocusInWindow();
    }
    public void updateTimer(int seconds) {
        timerLabel.setText("Время: " + seconds + " сек");
        timerLabel.setForeground(seconds <= 10 ? Color.RED : Color.BLACK);
    }
    public void updateScore(int correct, int wrong) { scoreLabel.setText(correct + " | " + wrong); }
    public void showLevelResults(int correct, int wrong, int timeUsed) {
        showResultMode();
        valCorrect.setText(String.valueOf(correct));
        valWrong.setText(String.valueOf(wrong));
        valTime.setText(timeUsed + " сек");
    }
    public String getUserAnswer() { return answerField.getText().trim(); }

    public JButton getStartButton() { return startButton; }
    public JButton getSubmitButton() { return submitButton; }
    public JButton getNextLevelButton() { return nextLevelButton; }
    public JButton getFinishButton() { return finishButton; }
    public JTextField getAnswerField() { return answerField; }
}