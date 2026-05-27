package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    @Test
    void getSuccessRate_normalCase() {
        GameSession s = new GameSession(0, "2026-05-25", 10, 8, 2, "хорошо");
        assertEquals(80.0, s.getSuccessRate());
    }

    @Test
    void getSuccessRate_perfectScore() {
        GameSession s = new GameSession();
        s.setTotalWords(5);
        s.setCorrectAnswers(5);
        assertEquals(100.0, s.getSuccessRate());
    }

    @Test
    void getSuccessRate_zeroWords() {
        GameSession s = new GameSession();
        s.setTotalWords(0);
        assertEquals(0.0, s.getSuccessRate());
    }
}