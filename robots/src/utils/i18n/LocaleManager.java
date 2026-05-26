package utils.i18n;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocaleManager {
    private static ResourceBundle bundle;
    private static final Map<String, MessageFormat> FORMAT_CACHE = new ConcurrentHashMap<>();
    private static final List<LocaleChangeListener> listeners = new ArrayList<>();

    public interface LocaleChangeListener { void onLocaleChanged(); }

    public static void init(Locale locale) {
        bundle = ResourceBundle.getBundle("strings", locale);
        FORMAT_CACHE.clear();
        notifyListeners();
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String format(String key, Object... args) {
        MessageFormat mf = FORMAT_CACHE.computeIfAbsent(key, k ->
                new MessageFormat(bundle.getString(k), bundle.getLocale()));
        return mf.format(args);
    }

    public static void addListener(LocaleChangeListener l) { listeners.add(l); }
    public static void removeListener(LocaleChangeListener l) { listeners.remove(l); }

    private static void notifyListeners() {
        for (LocaleChangeListener l : new ArrayList<>(listeners)) {
            l.onLocaleChanged();
        }
    }
}