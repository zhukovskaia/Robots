package utils.i18n;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class LocaleManager {
    private static final Logger LOG = Logger.getLogger(LocaleManager.class.getName());
    private static ResourceBundle bundle;
    private static final Map<String, MessageFormat> FORMAT_CACHE = new ConcurrentHashMap<>();
    private static final List<LocaleChangeListener> listeners = new ArrayList<>();

    public interface LocaleChangeListener {
        void onLocaleChanged();
    }


    public static void init(Locale locale) {
        bundle = ResourceBundle.getBundle("strings", locale);
        FORMAT_CACHE.clear();
        notifyListeners();
    }


    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            LOG.warning("⚠️ LocaleManager: Ключ не найден в бандле -> '" + key +
                    "' (Locale: " + (bundle != null ? bundle.getLocale() : "null") + ")");
            return "[" + key + "]"; // Fallback: покажет в UI, какой ключ отсутствует
        }
    }


    public static String format(String key, Object... args) {
        try {

            MessageFormat mf = FORMAT_CACHE.computeIfAbsent(key, k -> {
                String pattern = bundle.getString(k);
                return new MessageFormat(pattern, bundle.getLocale());
            });
            return mf.format(args);
        } catch (MissingResourceException e) {
            LOG.warning("️ LocaleManager: Ключ не найден в бандле -> '" + key +
                    "' (Locale: " + (bundle != null ? bundle.getLocale() : "null") + ")");
            return "[" + key + "]";
        }
    }

    public static void addListener(LocaleChangeListener l) {
        listeners.add(l);
    }

    public static void removeListener(LocaleChangeListener l) {
        listeners.remove(l);
    }

    private static void notifyListeners() {
        for (LocaleChangeListener l : new ArrayList<>(listeners)) {
            l.onLocaleChanged();
        }
    }
}