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

    public MainApplicationFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defW = screenSize.width - 100;
        int defH = screenSize.height - 100;

        stateManager.restoreMain(this, 50, 50, defW, defH);
        setContentPane(desktopPane);

        logWindow = new LogWindow(Logger.getDefaultLogSource());
        stateManager.restoreInternalFrame(logWindow, LogWindow.CONFIG_KEY,
                LogWindow.getDefaultX(), LogWindow.getDefaultY(),
                LogWindow.getDefaultWidth(), LogWindow.getDefaultHeight());
        addWindow(logWindow);

        gameWindow = new GameWindow();
        stateManager.restoreInternalFrame(gameWindow, GameWindow.CONFIG_KEY,
                GameWindow.getDefaultX(), GameWindow.getDefaultY(),
                GameWindow.getDefaultWidth(), GameWindow.getDefaultHeight());
        addWindow(gameWindow);

        RobotInfoWindow infoWindow = gameWindow.getInfoWindow();
        stateManager.restoreInternalFrame(infoWindow, RobotInfoWindow.CONFIG_KEY,
                RobotInfoWindow.getDefaultX(), RobotInfoWindow.getDefaultY(),
                RobotInfoWindow.getDefaultWidth(), RobotInfoWindow.getDefaultHeight());
        addWindow(infoWindow);

        setJMenuBar(new MenuBarBuilder(this).buildMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { exitApplication(); }
        });
        Logger.debug("Главное окно инициализировано");
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