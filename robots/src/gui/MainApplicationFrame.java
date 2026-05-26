package gui;

import model.RobotModel;
import log.Logger;
import utils.i18n.LocaleManager;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;

public class MainApplicationFrame extends JFrame implements LocaleManager.LocaleChangeListener {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private final AppStateManager stateManager = new AppStateManager();
    private final RobotModel robotModel;

    public MainApplicationFrame() {
        robotModel = new RobotModel();

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

        gameWindow = new GameWindow(robotModel);
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
        LocaleManager.addListener(this);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
        Logger.debug(LocaleManager.get("log.main_init"));
    }

    @Override
    public void onLocaleChanged() {
        SwingUtilities.invokeLater(() -> {
            setJMenuBar(new MenuBarBuilder(this).buildMenuBar());
            if (gameWindow != null) {
                gameWindow.setVisible(true);
                try { gameWindow.setSelected(true); gameWindow.toFront(); } catch (Exception ignored) {}
            }
            if (logWindow != null) logWindow.setVisible(true);
            if (gameWindow != null && gameWindow.getInfoWindow() != null) {
                gameWindow.getInfoWindow().setVisible(true);
            }
            desktopPane.repaint();
        });
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void exitApplication() {

        Logger.debug(LocaleManager.get("log.exit_app"));


        Object[] options = {
                LocaleManager.get("dialog.exit.yes"),
                LocaleManager.get("dialog.exit.no")
        };

        int res = JOptionPane.showOptionDialog(
                this,
                LocaleManager.get("dialog.exit.confirm.msg"),
                LocaleManager.get("dialog.exit.confirm.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );


        if (res == 0) {
            if (gameWindow != null) {
                gameWindow.shutdown();
            }


            stateManager.saveMain(this);
            stateManager.saveAllFrames(desktopPane);
            stateManager.save();

            Logger.debug(LocaleManager.get("log.app_closed"));
            System.exit(0);
        }

    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
            Logger.debug(LocaleManager.format("log.theme_changed", className));
        } catch (Exception e) {
            Logger.error(LocaleManager.format("log.theme_error", e.getMessage()));
        }
    }
}