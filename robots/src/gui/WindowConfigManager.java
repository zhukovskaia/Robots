package gui;

import java.io.*;
import java.util.Properties;
import log.Logger;

public class WindowConfigManager {
    private static final String FILE_NAME = ".game_window_config.properties";
    private final Properties props = new Properties();
    private final File file;

    public WindowConfigManager() {
        file = new File(System.getProperty("user.home"), FILE_NAME);
    }

    public void load() {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
                Logger.debug("Конфиг загружен");
            } catch (IOException e) {
                Logger.error("Ошибка загрузки конфига: " + e.getMessage());
            }
        } else {
            Logger.debug("Конфиг не найден, используются дефолтные значения");
        }
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "App Config");
            Logger.debug("Конфиг сохранен");
        } catch (IOException e) {
            Logger.error("Ошибка сохранения конфига: " + e.getMessage());
        }
    }

    public int getInt(String key, int def) {
        String val = props.getProperty(key);
        try { return val != null ? Integer.parseInt(val) : def; }
        catch (NumberFormatException e) { Logger.error("Ошибка парсинга " + key); return def; }
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