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
    private static final int DEFAULT_INTERNAL_WIDTH = 600;
    private static final int DEFAULT_INTERNAL_HEIGHT = 400;
    private static final int DEFAULT_GAME_X = 10;
    private static final int DEFAULT_GAME_Y = 10;
    private static final int DEFAULT_LOG_X = 220;
    private static final int DEFAULT_LOG_Y = 10;

    public MainApplicationFrame() {
        stateManager.load();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defW = screenSize.width - DEFAULT_INSET * 2;
        int defH = screenSize.height - DEFAULT_INSET * 2;

        stateManager.restoreMain(this, DEFAULT_INSET, DEFAULT_INSET, defW, defH);

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        stateManager.restoreInternalFrame(logWindow, "log", DEFAULT_LOG_X, DEFAULT_LOG_Y, DEFAULT_INTERNAL_WIDTH, DEFAULT_INTERNAL_HEIGHT);
        addWindow(logWindow);

        gameWindow = new GameWindow();
        stateManager.restoreInternalFrame(gameWindow, "game", DEFAULT_GAME_X, DEFAULT_GAME_Y, DEFAULT_INTERNAL_WIDTH, DEFAULT_INTERNAL_HEIGHT);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });

        Logger.debug("Главное окно инициализировано");
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

        Logger.debug("Конфигурация сохранена перед выходом");

        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            Logger.debug("Приложение закрыто пользователем");
            System.exit(0);
        }
    }

    private JMenuBar generateMenuBar() {
        return new MenuBarBuilder(this).buildMenuBar();
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
            Logger.debug("Тема оформления изменена: " + className);
        } catch (Exception e) {
            Logger.error("Не удалось установить тему оформления: " + e.getMessage());
        }
    }
}