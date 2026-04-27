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
                Logger.debug("Конфигурация загружена успешно");
            } catch (IOException e) {
                Logger.error("Не удалось загрузить конфигурацию: " + e.getMessage());
            }
        } else {
            Logger.debug("Файл конфигурации не найден, используются значения по умолчанию");
        }
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "Application Window Configuration");

        } catch (IOException e) {
            Logger.error("Не удалось сохранить конфигурацию: " + e.getMessage());
        }
    }

    public int getInt(String key, int def) {
        String val = props.getProperty(key);
        try {
            return val != null ? Integer.parseInt(val) : def;
        } catch (NumberFormatException e) {
            Logger.error("Неверный формат числа для ключа '" + key + "': " + val);
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

    public void saveInternal(String prefix, int x, int y, int w, int h, boolean iconified, boolean maximized) {
        props.setProperty(prefix + ".x", String.valueOf(x));
        props.setProperty(prefix + ".y", String.valueOf(y));
        props.setProperty(prefix + ".w", String.valueOf(w));
        props.setProperty(prefix + ".h", String.valueOf(h));
        props.setProperty(prefix + ".icon", String.valueOf(iconified));
        props.setProperty(prefix + ".max", String.valueOf(maximized));
    }
}