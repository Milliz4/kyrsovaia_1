package controller;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class VocabularyControllerTest {

    @Test
    void calculateNextReviewDate_level1_returnsTomorrow() {
        String expected = LocalDate.now().plusDays(1).toString();
        assertEquals(expected, VocabularyController.calculateNextReviewDate(1));
    }

    @Test
    void calculateNextReviewDate_level5_returnsPlus30Days() {
        String expected = LocalDate.now().plusDays(30).toString();
        assertEquals(expected, VocabularyController.calculateNextReviewDate(5));
    }

    @Test
    void calculateNextReviewDate_invalidLevel_clamps() {
        assertEquals(
                VocabularyController.calculateNextReviewDate(1),
                VocabularyController.calculateNextReviewDate(0)
        );
        assertEquals(
                VocabularyController.calculateNextReviewDate(5),
                VocabularyController.calculateNextReviewDate(10)
        );
    }
}