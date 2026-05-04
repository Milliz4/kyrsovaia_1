package db;

import model.GameSession;
import model.VocabularyItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private static final String URL = "jdbc:sqlite:english_vocabulary.db";
    private static DBManager instance;
    private Connection connection;

    private DBManager() {
        try {
            connection = DriverManager.getConnection(URL);
            createTables();
            insertTestData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBManager getInstance() {
        if (instance == null) instance = new DBManager();
        return instance;
    }

    private void createTables() throws SQLException {
        String sqlVocab = """
            CREATE TABLE IF NOT EXISTS vocabulary (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                english TEXT NOT NULL,
                russian TEXT NOT NULL,
                context_sentence TEXT,
                box_level INTEGER DEFAULT 1,
                next_review_date TEXT
            );
            """;

        String sqlSessions = """
            CREATE TABLE IF NOT EXISTS game_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                session_date TEXT NOT NULL,
                duration_sec INTEGER,
                total_words INTEGER,
                correct INTEGER,
                score INTEGER
            );
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlVocab);
            stmt.execute(sqlSessions);
        }
    }

    private void insertTestData() {
        if (getAllWords().isEmpty()) {
            addWord(new VocabularyItem(0, "hello", "привет", "Hello, how are you?", 1, "2025-04-22"));
            addWord(new VocabularyItem(0, "world", "мир", "The world is beautiful.", 1, "2025-04-22"));
            addWord(new VocabularyItem(0, "borrow", "одолжить", "She borrowed a book.", 2, "2025-04-25"));
        }
    }

    public VocabularyItem addWord(VocabularyItem word) {
        String sql = "INSERT INTO vocabulary (english, russian, context_sentence, box_level, next_review_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, word.getEnglish());
            pstmt.setString(2, word.getRussian());
            pstmt.setString(3, word.getContextSentence());
            pstmt.setInt(4, word.getBoxLevel());
            pstmt.setString(5, word.getNextReviewDate());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    word.setId(generatedKeys.getInt(1));
                    return word;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<VocabularyItem> getAllWords() {
        List<VocabularyItem> list = new ArrayList<>();
        String sql = "SELECT * FROM vocabulary";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new VocabularyItem(
                        rs.getInt("id"),
                        rs.getString("english"),
                        rs.getString("russian"),
                        rs.getString("context_sentence"),
                        rs.getInt("box_level"),
                        rs.getString("next_review_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateWord(VocabularyItem word) {
        String sql = "UPDATE vocabulary SET english=?, russian=?, context_sentence=?, box_level=?, next_review_date=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, word.getEnglish());
            pstmt.setString(2, word.getRussian());
            pstmt.setString(3, word.getContextSentence());
            pstmt.setInt(4, word.getBoxLevel());
            pstmt.setString(5, word.getNextReviewDate());
            pstmt.setInt(6, word.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWord(int id) {
        String sql = "DELETE FROM vocabulary WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public VocabularyItem getWordById(int id) {
        String sql = "SELECT * FROM vocabulary WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new VocabularyItem(
                            rs.getInt("id"),
                            rs.getString("english"),
                            rs.getString("russian"),
                            rs.getString("context_sentence"),
                            rs.getInt("box_level"),
                            rs.getString("next_review_date")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<GameSession> getAllSessions() {
        return new ArrayList<>();
    }
}