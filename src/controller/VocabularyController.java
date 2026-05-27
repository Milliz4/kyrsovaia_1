package controller;

import db.DBManager;
import model.VocabularyItem;
import view.VocabularyView;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VocabularyController {
    private final VocabularyView view;
    private final DBManager dbManager;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public VocabularyController(VocabularyView view, DBManager dbManager) {
        this.view = view;
        this.dbManager = dbManager;
        registerListeners();
    }

    private void registerListeners() {
        view.getAddButton().addActionListener(e -> addWord());
        view.getEditButton().addActionListener(e -> editWord());
        view.getDeleteButton().addActionListener(e -> deleteWord());
        view.getImportButton().addActionListener(e -> importFromCsv());
    }

    private void addWord() {
        String english = JOptionPane.showInputDialog(view, "Слово на английском языке:");
        if (english == null || english.trim().isEmpty()) return;

        String russian = JOptionPane.showInputDialog(view, "Русский перевод:");
        if (russian == null || russian.trim().isEmpty()) return;

        String context = JOptionPane.showInputDialog(view, "Пример предложения (контекст):");

        VocabularyItem newWord = new VocabularyItem(
                0,
                english.trim(),
                russian.trim(),
                context != null ? context.trim() : "",
                1,
                LocalDate.now().format(DATE_FORMAT)
        );

        VocabularyItem saved = dbManager.addWord(newWord);
        if (saved != null) {
            view.addWordToTable(saved);
            JOptionPane.showMessageDialog(view, "Слово добавлено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(view, "Ошибка при сохранении!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editWord() {
        VocabularyItem selected = view.getSelectedWord();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Выберите слово для редактирования!",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField engField = new JTextField(selected.getEnglish(), 20);
        JTextField rusField = new JTextField(selected.getRussian(), 20);
        JTextField ctxField = new JTextField(selected.getContextSentence(), 30);
        JSpinner levelSpinner = new JSpinner(new SpinnerNumberModel(selected.getBoxLevel(), 1, 5, 1));
        JTextField dateField = new JTextField(selected.getNextReviewDate(), 10);

        JPanel editPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        editPanel.add(new JLabel("Слово на английском языке:"));
        editPanel.add(engField);
        editPanel.add(new JLabel("Русский перевод:"));
        editPanel.add(rusField);
        editPanel.add(new JLabel("Контекст:"));
        editPanel.add(ctxField);
        editPanel.add(new JLabel("Уровень SRS:"));
        editPanel.add(levelSpinner);
        editPanel.add(new JLabel("След. повтор (YYYY-MM-DD):"));
        editPanel.add(dateField);

        int result = JOptionPane.showConfirmDialog(view, editPanel,
                "Редактирование слова", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (engField.getText().trim().isEmpty() || rusField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(view, "Поля перевода слов не могут быть пустыми!",
                        "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int newLevel = (int) levelSpinner.getValue();
            String nextReview = dateField.getText().trim();

            if (nextReview.equals(selected.getNextReviewDate()) || nextReview.isEmpty()) {
                nextReview = calculateNextReviewDate(newLevel);
            }

            selected.setEnglish(engField.getText().trim());
            selected.setRussian(rusField.getText().trim());
            selected.setContextSentence(ctxField.getText().trim());
            selected.setBoxLevel(newLevel);
            selected.setNextReviewDate(nextReview);

            if (dbManager.updateWord(selected)) {
                view.updateWordInTable(selected);
                JOptionPane.showMessageDialog(view, "Слово обновлено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Ошибка при обновлении!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteWord() {
        VocabularyItem selected = view.getSelectedWord();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Выберите слово для удаления!",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                "Удалить слово \"" + selected.getEnglish() + "\"?",
                "Подтверждение удаления", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dbManager.deleteWord(selected.getId())) {
                view.removeWordFromTable(selected.getId());
                JOptionPane.showMessageDialog(view, "Слово удалено!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Ошибка при удалении!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static String calculateNextReviewDate(int boxLevel) {
        int[] intervals = {1, 3, 7, 14, 30};
        if (boxLevel < 1) boxLevel = 1;
        if (boxLevel > 5) boxLevel = 5;
        return LocalDate.now().plusDays(intervals[boxLevel - 1])
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private void importFromCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("txt файлы", "txt"));

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            java.util.List<VocabularyItem> words = new java.util.ArrayList<>();
            int lineCount = 0;
            int errorCount = 0;

            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(new java.io.FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {

                String line;
                br.readLine();

                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split(",", -1);
                    if (parts.length >= 2) {
                        String eng = parts[0].trim();
                        String rus = parts[1].trim();
                        String ctx = parts.length > 2 ? parts[2].trim() : "";

                        if (!eng.isEmpty() && !rus.isEmpty()) {
                            words.add(new VocabularyItem(eng, rus, ctx));
                            lineCount++;
                        } else {
                            errorCount++;
                        }
                    } else {
                        errorCount++;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Ошибка чтения файла:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (words.isEmpty()) {
                JOptionPane.showMessageDialog(view, "В файле не найдено корректных слов.", "Внимание", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (dbManager.importWords(words)) {
                view.loadAllWords(dbManager.getAllWords());
                JOptionPane.showMessageDialog(view,
                        "Успешно импортировано: " + lineCount + " слов.\nПропущено строк: " + errorCount,
                        "Импорт завершён", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(view, "Ошибка при сохранении в базу данных!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}