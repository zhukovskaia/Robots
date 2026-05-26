package gui;

import java.awt.event.KeyEvent;
import javax.swing.*;
import log.Logger;
import utils.i18n.LocaleManager;

public class MenuBarBuilder {
    private final MainApplicationFrame frame;
    public MenuBarBuilder(MainApplicationFrame frame) { this.frame = frame; }

    public JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createLanguageMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu(LocaleManager.get("menu.file"));
        menu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exit = new JMenuItem(LocaleManager.get("menu.file.exit"));
        exit.setMnemonic(KeyEvent.VK_X);
        exit.addActionListener(e -> {
            Logger.debug(LocaleManager.get("log.exit_app"));
            frame.exitApplication();
        });
        menu.add(exit);
        return menu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu(LocaleManager.get("menu.view"));
        menu.setMnemonic(KeyEvent.VK_V);
        JMenuItem sys = new JMenuItem(LocaleManager.get("menu.view.system"), KeyEvent.VK_S);
        sys.addActionListener(e -> frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        JMenuItem cross = new JMenuItem(LocaleManager.get("menu.view.cross"), KeyEvent.VK_U);
        cross.addActionListener(e -> frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        menu.add(sys); menu.add(cross);
        return menu;
    }

    private JMenu createLanguageMenu() {
        JMenu menu = new JMenu(LocaleManager.get("menu.lang"));
        menu.setMnemonic(KeyEvent.VK_L);

        JMenuItem ru = new JMenuItem(LocaleManager.get("menu.lang.ru"));
        ru.addActionListener(e -> {
            LocaleManager.init(new java.util.Locale("ru"));
            Logger.debug(LocaleManager.get("log.lang_changed"));
        });

        JMenuItem en = new JMenuItem(LocaleManager.get("menu.lang.en"));
        en.addActionListener(e -> {
            LocaleManager.init(new java.util.Locale("en"));
            Logger.debug(LocaleManager.get("log.lang_changed"));
        });

        menu.add(ru);
        menu.add(en);
        return menu;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu(LocaleManager.get("menu.test"));
        menu.setMnemonic(KeyEvent.VK_T);
        JMenuItem logMsg = new JMenuItem(LocaleManager.get("menu.test.log"), KeyEvent.VK_L);
        logMsg.addActionListener(e -> Logger.debug(LocaleManager.get("log.test_msg")));
        menu.add(logMsg);
        return menu;
    }
}