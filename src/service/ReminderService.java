package service;

import db.DBManager;
import model.VocabularyItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderService {
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private Timer timer;
    private final DBManager dbManager;

    public ReminderService() {
        this.dbManager = DBManager.getInstance();
    }

    public void start(JFrame mainWindow) {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray не поддерживается");
            return;
        }

        systemTray = SystemTray.getSystemTray();

        Image image = createIconImage();

        trayIcon = new TrayIcon(image, "Английская лексика");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Приложение для изучения слов");

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    mainWindow.setExtendedState(Frame.NORMAL);
                    mainWindow.toFront();
                }
            }
        });

        try {
            systemTray.add(trayIcon);
            startPeriodicCheck();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private Image createIconImage() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        g2d.setColor(new Color(59, 130, 246)); // Синий фон
        g2d.fillOval(5, 5, 54, 54);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        g2d.drawString("A", 18, 45); // Буква A

        g2d.dispose();
        return img;
    }

    private void startPeriodicCheck() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndNotify();
            }
        }, 0, 60000);
    }

    private void checkAndNotify() {
        List<VocabularyItem> dueWords = dbManager.getWordsForReview();

        if (!dueWords.isEmpty()) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            String message = String.format(
                    "Время: %s\n" +
                            "Нужно повторить: %d слов(а)\n\n" +
                            "Не откладывайте!",
                    time, dueWords.size()
            );

            trayIcon.displayMessage(
                    "Напоминание",
                    message,
                    TrayIcon.MessageType.INFO
            );

            System.out.println("Отправлено системное уведомление: " + dueWords.size() + " слов");
        }
    }

    public void stop() {
        if (timer != null) timer.cancel();
        if (systemTray != null) systemTray.remove(trayIcon);
    }
}