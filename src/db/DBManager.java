package db;

import model.GameSession;
import model.VocabularyItem;
import java.time.format.DateTimeFormatter;

import java.sql.*;
import java.time.LocalDate;
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

        String sqlStats = """
            CREATE TABLE IF NOT EXISTS study_statistics (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                study_date TEXT UNIQUE,
                words_added INTEGER DEFAULT 0,
                words_edited INTEGER DEFAULT 0,
                words_deleted INTEGER DEFAULT 0,
                total_words INTEGER DEFAULT 0
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
            stmt.execute(sqlStats);
            stmt.execute(sqlSessions);
        }
    }


    public void recordAction(String actionType) {
        String today = LocalDate.now().toString();
        String sql = """
            INSERT INTO study_statistics (study_date, words_added, words_edited, words_deleted, total_words)
            VALUES (?, 
                CASE WHEN ? = 'added' THEN 1 ELSE 0 END,
                CASE WHEN ? = 'edited' THEN 1 ELSE 0 END,
                CASE WHEN ? = 'deleted' THEN 1 ELSE 0 END,
                (SELECT COUNT(*) FROM vocabulary))
            ON CONFLICT(study_date) DO UPDATE SET
                words_added = words_added + excluded.words_added,
                words_edited = words_edited + excluded.words_edited,
                words_deleted = words_deleted + excluded.words_deleted,
                total_words = excluded.total_words
            """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, today);
            pstmt.setString(2, actionType);
            pstmt.setString(3, actionType);
            pstmt.setString(4, actionType);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> getAllStats() {
        List<Object[]> stats = new ArrayList<>();
        String sql = "SELECT study_date, words_added, words_edited, words_deleted, total_words FROM study_statistics ORDER BY study_date DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.add(new Object[]{
                        rs.getString("study_date"),
                        rs.getInt("words_added"),
                        rs.getInt("words_edited"),
                        rs.getInt("words_deleted"),
                        rs.getInt("total_words")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public void saveGameSession(GameSession session) {
        String sql = "INSERT INTO game_sessions (session_date, duration_sec, total_words, correct, score) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, session.getSessionDate());
            pstmt.setInt(2, 30); // длительность уровня
            pstmt.setInt(3, session.getTotalWords());
            pstmt.setInt(4, session.getCorrectAnswers());
            pstmt.setInt(5, session.getCorrectAnswers() * 10); // очки
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
                    recordAction("added");
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
            boolean success = pstmt.executeUpdate() > 0;
            if (success) recordAction("edited");
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWord(int id) {
        String sql = "DELETE FROM vocabulary WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            boolean success = pstmt.executeUpdate() > 0;
            if (success) recordAction("deleted");
            return success;
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

    public int[] getSRSStatistics() {
        int[] stats = new int[5]; // индексы 0-4 соответствуют уровням 1-5
        String sql = "SELECT box_level, COUNT(*) as count FROM vocabulary GROUP BY box_level";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int level = rs.getInt("box_level");
                int count = rs.getInt("count");
                if (level >= 1 && level <= 5) {
                    stats[level - 1] = count;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<VocabularyItem> getWordsForReview() {
        List<VocabularyItem> list = new ArrayList<>();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String sql = "SELECT * FROM vocabulary WHERE next_review_date <= ? ORDER BY next_review_date";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, today);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalWordsCount() {
        String sql = "SELECT COUNT(*) FROM vocabulary";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<GameSession> getAllSessions() {
        return new ArrayList<>();
    }
}