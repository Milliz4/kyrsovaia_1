package model;

public class VocabularyItem {
    private int id;
    private String english;
    private String russian;
    private String contextSentence;
    private int boxLevel;
    private String nextReviewDate;

    public VocabularyItem() {}

    public VocabularyItem(int id, String english, String russian, String contextSentence,
                          int boxLevel, String nextReviewDate) {
        this.id = id;
        this.english = english;
        this.russian = russian;
        this.contextSentence = contextSentence;
        this.boxLevel = boxLevel;
        this.nextReviewDate = nextReviewDate;
    }

    public VocabularyItem(String english, String russian, String contextSentence) {
        this.english = english;
        this.russian = russian;
        this.contextSentence = contextSentence;
        this.boxLevel = 1;
        this.nextReviewDate = java.time.LocalDate.now().toString();
    }

    public static String validate(String english, String russian) {
        if (english == null || english.trim().isEmpty()) {
            return "Слово на английском не может быть пустым";
        }
        if (russian == null || russian.trim().isEmpty()) {
            return "Перевод не может быть пустым";
        }
        if (english.length() > 100 || russian.length() > 100) {
            return "Слово не должно превышать 100 символов";
        }
        if (!english.matches("^[a-zA-Z\\s\\-']+$")) {
            return "Английское слово должно содержать только буквы";
        }
        return null;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getRussian() { return russian; }
    public void setRussian(String russian) { this.russian = russian; }

    public String getContextSentence() { return contextSentence; }
    public void setContextSentence(String contextSentence) { this.contextSentence = contextSentence; }

    public int getBoxLevel() { return boxLevel; }
    public void setBoxLevel(int boxLevel) { this.boxLevel = boxLevel; }

    public String getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(String nextReviewDate) { this.nextReviewDate = nextReviewDate; }
}