package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.TextArea;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindow extends JInternalFrame implements LogChangeListener {
    private final LogWindowSource source;
    private final TextArea content;

    public LogWindow(LogWindowSource source) {
        super("Протокол работы", true, true, true, true);
        this.source = source;
        source.registerListener(this);
        content = new TextArea("");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(content, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateContent();
    }

    private void updateContent() {
        StringBuilder sb = new StringBuilder();
        for (LogEntry e : source.all()) sb.append(e.getMessage()).append('\n');
        content.setText(sb.toString());
        content.invalidate();
    }

    @Override public void onLogChanged() { EventQueue.invokeLater(this::updateContent); }

    public void close() {
        source.unregisterListener(this);
        dispose();
    }
}