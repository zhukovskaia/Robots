package gui;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private final AppStateManager stateManager = new AppStateManager();

    private static final int DEFAULT_INSET = 50;
    private static final int DEFAULT_INTERNAL_W = 600;
    private static final int DEFAULT_INTERNAL_H = 400;
    private static final int DEFAULT_LOG_X = 220, DEFAULT_LOG_Y = 10;
    private static final int DEFAULT_GAME_X = 10, DEFAULT_GAME_Y = 10;
    private static final int DEFAULT_INFO_X = 430, DEFAULT_INFO_Y = 10;
    private static final int DEFAULT_INFO_W = 230, DEFAULT_INFO_H = 110;

    public MainApplicationFrame() {
        stateManager.load();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defW = screenSize.width - DEFAULT_INSET * 2;
        int defH = screenSize.height - DEFAULT_INSET * 2;

        stateManager.restoreMain(this, DEFAULT_INSET, DEFAULT_INSET, defW, defH);
        setContentPane(desktopPane);

        logWindow = createLogWindow();
        // Генерируем префикс из заголовка, чтобы он совпадал с сохранением
        restoreWindowWithAutoPrefix(logWindow, DEFAULT_LOG_X, DEFAULT_LOG_Y, DEFAULT_INTERNAL_W, DEFAULT_INTERNAL_H);
        addWindow(logWindow);

        gameWindow = new GameWindow();
        restoreWindowWithAutoPrefix(gameWindow, DEFAULT_GAME_X, DEFAULT_GAME_Y, DEFAULT_INTERNAL_W, DEFAULT_INTERNAL_H);
        addWindow(gameWindow);

        RobotInfoWindow infoWindow = gameWindow.getInfoWindow();
        restoreWindowWithAutoPrefix(infoWindow, DEFAULT_INFO_X, DEFAULT_INFO_Y, DEFAULT_INFO_W, DEFAULT_INFO_H);
        addWindow(infoWindow);

        setJMenuBar(new MenuBarBuilder(this).buildMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { exitApplication(); }
        });
        Logger.debug("Главное окно инициализировано");
    }

    // Вспомогательный метод: формирует ключ из заголовка окна и восстанавливает состояние
    private void restoreWindowWithAutoPrefix(JInternalFrame frame, int defX, int defY, int defW, int defH) {
        String prefix = frame.getTitle().replaceAll("\\s+", "_").toLowerCase();
        stateManager.restoreInternalFrame(frame, prefix, defX, defY, defW, defH);
    }

    protected LogWindow createLogWindow() {
        LogWindow lw = new LogWindow(Logger.getDefaultLogSource());
        Logger.debug("Протокол работает");
        return lw;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void exitApplication() {
        stateManager.saveMain(this);
        stateManager.saveAllFrames(desktopPane);
        stateManager.save();
        Logger.debug("Конфигурация сохранена");

        int res = JOptionPane.showConfirmDialog(this, "Вы действительно хотите выйти?", "Выход", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            Logger.debug("Приложение закрыто");
            System.exit(0);
        }
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
            Logger.debug("Тема изменена: " + className);
        } catch (Exception e) {
            Logger.error("Ошибка темы: " + e.getMessage());
        }
    }
}