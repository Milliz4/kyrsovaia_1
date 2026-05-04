package model;

public class GameSession {
    private int id;
    private String sessionDate;
    private int totalWords;
    private int correctAnswers;
    private int wrongAnswers;
    private String difficulty;

    public GameSession() {
    }

    public GameSession(int id, String sessionDate, int totalWords, int correctAnswers,
                       int wrongAnswers, String difficulty) {
        this.id = id;
        this.sessionDate = sessionDate;
        this.totalWords = totalWords;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.difficulty = difficulty;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSessionDate() { return sessionDate; }
    public void setSessionDate(String sessionDate) { this.sessionDate = sessionDate; }

    public int getTotalWords() { return totalWords; }
    public void setTotalWords(int totalWords) { this.totalWords = totalWords; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public int getWrongAnswers() { return wrongAnswers; }
    public void setWrongAnswers(int wrongAnswers) { this.wrongAnswers = wrongAnswers; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public double getSuccessRate() {
        if (totalWords == 0) return 0.0;
        return (double) correctAnswers / totalWords * 100;
    }
}