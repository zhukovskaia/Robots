package gui;

import java.awt.event.KeyEvent;
import javax.swing.*;
import log.Logger;

public class MenuBarBuilder {
    private final MainApplicationFrame frame;
    public MenuBarBuilder(MainApplicationFrame frame) { this.frame = frame; }

    public JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu("Файл");
        menu.setMnemonic(KeyEvent.VK_F);
        JMenuItem exit = new JMenuItem("Выход");
        exit.setMnemonic(KeyEvent.VK_X);
        exit.addActionListener(e -> { Logger.debug("Выход из приложения"); frame.exitApplication(); });
        menu.add(exit);
        return menu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);
        JMenuItem sys = new JMenuItem("Системная схема", KeyEvent.VK_S);
        sys.addActionListener(e -> frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        JMenuItem cross = new JMenuItem("Универсальная схема", KeyEvent.VK_U);
        cross.addActionListener(e -> frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        menu.add(sys); menu.add(cross);
        return menu;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);
        JMenuItem logMsg = new JMenuItem("Сообщение в лог", KeyEvent.VK_L);
        logMsg.addActionListener(e -> Logger.debug("Тестовое сообщение"));
        menu.add(logMsg);
        return menu;
    }
}