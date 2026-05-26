package gui;

import java.io.*;
import java.util.Properties;
import log.Logger;
import utils.i18n.LocaleManager;

public class WindowConfigManager {
    private static final String FILE_NAME = ".game_window_config.properties";
    private final Properties props = new Properties();
    private final File file;

    private WindowConfigManager() {
        file = new File(System.getProperty("user.home"), FILE_NAME);
    }

    public static WindowConfigManager create() {
        WindowConfigManager config = new WindowConfigManager();
        config.load();
        return config;
    }

    private void load() {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                Logger.debug(LocaleManager.get("log.config_loaded"));
            } catch (IOException e) {
                Logger.error(LocaleManager.format("log.config_load_error", e.getMessage()));
            }
        } else {
            Logger.debug(LocaleManager.get("log.config_not_found"));
        }
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "App Config");
            Logger.debug(LocaleManager.get("log.config_saved"));
        } catch (IOException e) {
            Logger.error(LocaleManager.format("log.config_save_error", e.getMessage()));
        }
    }

    public int getInt(String key, int def) {
        String val = props.getProperty(key);
        try { return val != null ? Integer.parseInt(val) : def; }
        catch (NumberFormatException e) {
            Logger.error(LocaleManager.format("log.parse_error", key));
            return def;
        }
    }

    public boolean getBool(String key, boolean def) {
        String val = props.getProperty(key);
        return val != null ? Boolean.parseBoolean(val) : def;
    }

    public void saveMain(int x, int y, int w, int h, int state) {
        props.setProperty("main.x", String.valueOf(x));
        props.setProperty("main.y", String.valueOf(y));
        props.setProperty("main.w", String.valueOf(w));
        props.setProperty("main.h", String.valueOf(h));
        props.setProperty("main.state", String.valueOf(state));
    }

    public void saveInternal(String prefix, int x, int y, int w, int h, boolean icon, boolean max) {
        props.setProperty(prefix + ".x", String.valueOf(x));
        props.setProperty(prefix + ".y", String.valueOf(y));
        props.setProperty(prefix + ".w", String.valueOf(w));
        props.setProperty(prefix + ".h", String.valueOf(h));
        props.setProperty(prefix + ".icon", String.valueOf(icon));
        props.setProperty(prefix + ".max", String.valueOf(max));
    }
}