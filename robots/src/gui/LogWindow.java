package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;
import utils.i18n.LocaleManager;

public class LogWindow extends JInternalFrame implements LogChangeListener, LocaleManager.LocaleChangeListener {
    private final LogWindowSource source;
    private final TextArea content;
    public static final String CONFIG_KEY = "log";

    public static int getDefaultWidth() { return 350; }
    public static int getDefaultHeight() { return 600; }
    public static int getDefaultX() { return 640; }
    public static int getDefaultY() { return 20; }

    public LogWindow(LogWindowSource source) {
        super("", true, true, true, true);
        this.source = source;
        source.registerListener(this);
        LocaleManager.addListener(this);


        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        content = new TextArea("");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(content, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        updateTitle();
        updateContent();
        setVisible(true);
    }

    private void updateTitle() {
        setTitle(LocaleManager.get("log.title"));
    }

    private void updateContent() {
        StringBuilder sb = new StringBuilder();
        for (LogEntry e : source.all()) sb.append(e.getMessage()).append('\n');
        content.setText(sb.toString());
        content.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateContent);
    }

    @Override
    public void onLocaleChanged() {
        EventQueue.invokeLater(this::updateTitle);
    }

    public void close() {
        source.unregisterListener(this);
        LocaleManager.removeListener(this);
        dispose();
    }
}