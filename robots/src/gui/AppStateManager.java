package gui;

import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import log.Logger;
import utils.i18n.LocaleManager;

public class AppStateManager {
    private final WindowConfigManager configManager;

    public AppStateManager() {
        this.configManager = WindowConfigManager.create();
        Logger.debug(LocaleManager.get("log.app_state_loaded"));
    }

    public void save() {
        configManager.save();
        Logger.debug(LocaleManager.get("log.app_state_saved"));
    }

    public void restoreMain(JFrame mainFrame, int defX, int defY, int defW, int defH) {
        mainFrame.setBounds(
                configManager.getInt("main.x", defX),
                configManager.getInt("main.y", defY),
                configManager.getInt("main.w", defW),
                configManager.getInt("main.h", defH)
        );
        mainFrame.setExtendedState(configManager.getInt("main.state", JFrame.NORMAL));
    }

    public void restoreInternalFrame(JInternalFrame frame, String stableKey, int defX, int defY, int defW, int defH) {
        int w = configManager.getInt(stableKey + ".w", defW);
        int h = configManager.getInt(stableKey + ".h", defH);
        int x = configManager.getInt(stableKey + ".x", defX);
        int y = configManager.getInt(stableKey + ".y", defY);

        frame.setBounds(x, y, w, h);

        try {
            if (configManager.getBool(stableKey + ".max", false)) {
                frame.setMaximum(true);
            } else if (configManager.getBool(stableKey + ".icon", false)) {
                frame.setIcon(true);
            }
        } catch (PropertyVetoException e) {
            Logger.error("Failed to restore frame: " + stableKey);
        }
    }

    public void saveMain(JFrame mainFrame) {
        configManager.saveMain(
                mainFrame.getX(), mainFrame.getY(),
                mainFrame.getWidth(), mainFrame.getHeight(),
                mainFrame.getExtendedState()
        );
    }

    public void saveAllFrames(JDesktopPane desktopPane) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            String key = getStableKey(frame);
            if (key != null) {
                configManager.saveInternal(key,
                        frame.getX(), frame.getY(),
                        frame.getWidth(), frame.getHeight(),
                        frame.isIcon(), frame.isMaximum());
            }
        }
    }

    private String getStableKey(JInternalFrame frame) {
        if (frame instanceof GameWindow) return GameWindow.CONFIG_KEY;
        if (frame instanceof LogWindow) return LogWindow.CONFIG_KEY;
        if (frame instanceof RobotInfoWindow) return RobotInfoWindow.CONFIG_KEY;
        return null;
    }
}