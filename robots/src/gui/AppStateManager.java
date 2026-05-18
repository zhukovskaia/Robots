package gui;

import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import log.Logger;

public class AppStateManager {
    private final WindowConfigManager configManager;

    public AppStateManager() {
        this.configManager = new WindowConfigManager();
    }

    public void load() {
        configManager.load();
        Logger.debug("Менеджер состояний: загрузка конфигурации запущена");
    }

    public void save() {
        configManager.save();
    }

    public void restoreMain(JFrame mainFrame, int defaultX, int defaultY, int defaultW, int defaultH) {
        mainFrame.setBounds(
                configManager.getInt("main.x", defaultX),
                configManager.getInt("main.y", defaultY),
                configManager.getInt("main.w", defaultW),
                configManager.getInt("main.h", defaultH)
        );
        mainFrame.setExtendedState(configManager.getInt("main.state", JFrame.NORMAL));
    }

    public void restoreInternalFrame(JInternalFrame frame, String prefix, int defaultX, int defaultY, int defaultW, int defaultH) {
        int w = configManager.getInt(prefix + ".w", defaultW);
        int h = configManager.getInt(prefix + ".h", defaultH);
        int x = configManager.getInt(prefix + ".x", defaultX);
        int y = configManager.getInt(prefix + ".y", defaultY);

        frame.setBounds(x, y, w, h);

        try {
            if (configManager.getBool(prefix + ".max", false)) {
                frame.setMaximum(true);
            } else if (configManager.getBool(prefix + ".icon", false)) {
                frame.setIcon(true);
            }
        } catch (PropertyVetoException e) {
            Logger.error("Не удалось восстановить состояние окна '" + prefix + "': " + e.getMessage());
        }
    }

    public void saveMain(JFrame mainFrame) {
        configManager.saveMain(
                mainFrame.getX(), mainFrame.getY(),
                mainFrame.getWidth(), mainFrame.getHeight(),
                mainFrame.getExtendedState()
        );
    }

    public void saveInternalFrame(JInternalFrame frame, String prefix) {
        configManager.saveInternal(prefix,
                frame.getX(), frame.getY(),
                frame.getWidth(), frame.getHeight(),
                frame.isIcon(), frame.isMaximum()
        );
    }

    public void saveAllFrames(JDesktopPane desktopPane) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String prefix = frame.getTitle().replaceAll("\\s+", "_").toLowerCase();
            saveInternalFrame(frame, prefix);
        }
    }
}