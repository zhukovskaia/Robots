package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyVetoException;
import javax.swing.*;
import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private final WindowConfigManager config = new WindowConfigManager();

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int defW = screenSize.width - inset * 2;
        int defH = screenSize.height - inset * 2;

        setBounds(
                config.getInt("main.x", inset),
                config.getInt("main.y", inset),
                config.getInt("main.w", defW),
                config.getInt("main.h", defH)
        );
        setExtendedState(config.getInt("main.state", JFrame.NORMAL));

        setContentPane(desktopPane);

        logWindow = createLogWindow();
        restoreInternalFrame(logWindow, "log");
        addWindow(logWindow);

        gameWindow = new GameWindow();
        restoreInternalFrame(gameWindow, "game");
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                exitApplication();
            }
        });
    }

    private void restoreInternalFrame(JInternalFrame frame, String prefix) {
        int w = config.getInt(prefix + ".w", frame.getPreferredSize().width);
        int h = config.getInt(prefix + ".h", frame.getPreferredSize().height);
        int x = config.getInt(prefix + ".x", 10);
        int y = config.getInt(prefix + ".y", 10);

        frame.setBounds(x, y, w, h);

        try {
            if (config.getBool(prefix + ".max", false)) {
                frame.setMaximum(true);
            } else if (config.getBool(prefix + ".icon", false)) {
                frame.setIcon(true);
            }
        } catch (PropertyVetoException e) {
        }
    }

    protected LogWindow createLogWindow() {
        LogWindow lw = new LogWindow(Logger.getDefaultLogSource());
        setMinimumSize(new Dimension(300, 400));
        Logger.debug("Протокол работает");
        return lw;
    }

    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    public void exitApplication() {
        config.saveMain(getX(), getY(), getWidth(), getHeight(), getExtendedState());
        saveInternalFrame(logWindow, "log");
        saveInternalFrame(gameWindow, "game");
        config.save();

        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void saveInternalFrame(JInternalFrame frame, String prefix) {
        config.saveInternal(prefix, frame.getX(), frame.getY(), frame.getWidth(), frame.getHeight(), frame.isIcon(), frame.isMaximum());
    }

    private JMenuBar generateMenuBar() {
        return new MenuBarBuilder(this).buildMenuBar();
    }

    public void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
        }
    }
}