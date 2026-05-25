package view;

import model.VocabularyItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VocabularyView extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable vocabularyTable;
    private final JButton addBtn, editBtn, deleteBtn, importBtn;

    private static final String[] COLUMNS = {
            "ID", "English", "Russian", "Контекст", "Уровень SRS", "След. повтор"
    };

    public VocabularyView() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vocabularyTable = new JTable(tableModel);
        vocabularyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vocabularyTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        vocabularyTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        vocabularyTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        vocabularyTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        vocabularyTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        vocabularyTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        add(new JScrollPane(vocabularyTable), BorderLayout.CENTER);

        addBtn = new JButton(" Добавить");
        editBtn = new JButton(" Редактировать");
        deleteBtn = new JButton("️Удалить");
        importBtn = new JButton("Импорт из txt");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(importBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addWordToTable(VocabularyItem word) {
        tableModel.addRow(toTableRow(word));
    }

    public void updateWordInTable(VocabularyItem word) {
        int modelRow = findRowByWordId(word.getId());
        if (modelRow != -1) {
            for (int col = 0; col < COLUMNS.length; col++) {
                tableModel.setValueAt(toTableRow(word)[col], modelRow, col);
            }
            tableModel.fireTableRowsUpdated(modelRow, modelRow);
        }
    }

    public void removeWordFromTable(int wordId) {
        int modelRow = findRowByWordId(wordId);
        if (modelRow != -1) {
            tableModel.removeRow(modelRow);
        }
    }

    public void loadAllWords(List<VocabularyItem> words) {
        tableModel.setRowCount(0);
        for (VocabularyItem word : words) {
            tableModel.addRow(toTableRow(word));
        }
        tableModel.fireTableDataChanged();
    }

    private Object[] toTableRow(VocabularyItem word) {
        return new Object[]{
                word.getId(),
                word.getEnglish(),
                word.getRussian(),
                word.getContextSentence(),
                word.getBoxLevel(),
                word.getNextReviewDate()
        };
    }

    private int findRowByWordId(int wordId) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if ((int) tableModel.getValueAt(row, 0) == wordId) {
                return row;
            }
        }
        return -1;
    }

    public JTable getTable() { return vocabularyTable; }
    public JButton getAddButton() { return addBtn; }
    public JButton getEditButton() { return editBtn; }
    public JButton getDeleteButton() { return deleteBtn; }

    public VocabularyItem getSelectedWord() {
        int row = vocabularyTable.getSelectedRow();
        if (row == -1) return null;

        return new VocabularyItem(
                (int) tableModel.getValueAt(row, 0),
                (String) tableModel.getValueAt(row, 1),
                (String) tableModel.getValueAt(row, 2),
                (String) tableModel.getValueAt(row, 3),
                (int) tableModel.getValueAt(row, 4),
                (String) tableModel.getValueAt(row, 5)
        );
    }
    public JButton getImportButton() { return importBtn; }

}