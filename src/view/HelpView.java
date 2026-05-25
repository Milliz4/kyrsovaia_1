package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HelpView extends JPanel {

    public HelpView() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(20, 40, 20, 40));

        JScrollPane scrollPane = new JScrollPane(createContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Справка и инструкции");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(30));

        panel.add(createSection("О приложении",
                "Это приложение для изучения английской лексики методом интервального повторения (SRS).\n" +
                        "Оно помогает эффективно запоминать слова, автоматически рассчитывая оптимальные интервалы повторения."));

        panel.add(createSection("Как добавить слова",
                "1. Перейдите на вкладку «Словарь»\n" +
                        "2. Нажмите кнопку «Добавить»\n" +
                        "3. Введите слово на английском\n" +
                        "4. Введите перевод на русском\n" +
                        "5. Добавьте пример использования (контекст) - необязательно\n" +
                        "6. Нажмите OK\n\n" +
                        "Совет: Добавляйте слова с примерами предложений - это улучшает запоминание!"));

        panel.add(createSection("Как проходить уровни",
                "1. Перейдите на вкладку «Уровни»\n" +
                        "2. Нажмите кнопку «Старт уровня»\n" +
                        "3. Переведите показанное английское слово\n" +
                        "4. Введите ответ в поле и нажмите «Ответить» (или Enter)\n" +
                        "5. После завершения уровня просмотрите результаты\n" +
                        "6. Нажмите «Следующий уровень» или «Завершить игру»\n\n" +
                        "На каждое слово даётся 10 секунд\n" +
                        "Уровень считается пройденным, если правильных ответов больше, чем ошибок"));

        panel.add(createSection("Как работает система SRS",
                "SRS (Spaced Repetition System) - система интервального повторения:\n\n" +
                        "Уровень 1 (красный) - слово нужно повторять ежедневно\n" +
                        "Уровень 2 (оранжевый) - каждые 3 дня\n" +
                        "Уровень 3 (жёлтый) - каждую неделю\n" +
                        "Уровень 4 (светло-зелёный) - каждые 2 недели\n" +
                        "Уровень 5 (зелёный) - каждый месяц\n\n" +
                        "При правильном ответе слово переходит на следующий уровень\n" +
                        "При ошибке слово возвращается на 1 уровень"));

        panel.add(createSection("Просмотр статистики",
                "Перейдите на вкладку «Статистика» чтобы увидеть:\n\n" +
                        "• Общее количество слов в словаре\n" +
                        "• Распределение слов по уровням SRS\n" +
                        "• Количество слов, требующих повторения\n" +
                        "• Историю активности (добавлено/изменено/удалено за каждый день)\n" +
                        "• График распределения слов по уровням"));

        panel.add(createSection("Напоминания",
                "Приложение автоматически проверяет слова, требующие повторения,\n" +
                        "и отправляет уведомления (иконка в системном трее).\n\n" +
                        "Если вы видите уведомление - зайдите в приложение и пройдите уровень!"));

        panel.add(createSection("Полезные советы",
                "• Добавляйте 5-10 новых слов в день\n" +
                        "• Проходите повторения ежедневно\n" +
                        "• Не пропускайте слова с красным уровнем (Уровень 1)\n" +
                        "• Используйте контекстные примеры\n" +
                        "• Следите за статистикой прогресса\n" +
                        "• Регулярность важнее количества!"));

        panel.add(Box.createVerticalGlue());

        JLabel footerLabel = new JLabel("Успехов в изучении!");
        footerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        footerLabel.setForeground(new Color(59, 130, 246));
        footerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        panel.add(footerLabel);

        return panel;
    }

    private JPanel createSection(String title, String content) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(new EmptyBorder(15, 0, 15, 0));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);
        section.add(Box.createVerticalStrut(10));

        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));
        textArea.setForeground(new Color(75, 85, 99));
        textArea.setBackground(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setBorder(new EmptyBorder(0, 0, 0, 0));
        textArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        section.add(textArea);
        section.add(Box.createVerticalStrut(5));

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(229, 231, 235));
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        section.add(separator);

        return section;
    }
}