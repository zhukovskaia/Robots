package gui;

import java.awt.BorderLayout; // BorderLayout делит область на 5 частей: NORTH, SOUTH, EAST, WEST, CENTER
import java.awt.EventQueue;// Позволяет выполнять код в потоке обработки событий
import java.awt.TextArea;// Импорт класса TextArea - многострочное текстовое поле (устаревшее, но простое)

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import log.LogChangeListener; // Импорт интерфейса LogChangeListener - для получения уведомлений об изменениях в логе
import log.LogEntry;// Импорт класса LogEntry - представляет одну запись в логе (одно сообщение)
import log.LogEntry;
import log.LogWindowSource;// Импорт класса LogWindowSource - источник логов (где хранятся сообщения)

public class LogWindow extends JInternalFrame implements LogChangeListener
{
    private LogWindowSource m_logSource;// ссылка на источник логов
    private TextArea m_logContent; // текст с сообщением

    public LogWindow(LogWindowSource logSource) 
    {
        super("Протокол работы", true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);// Регистрируем это окно как слушателя изменений в источнике логов
        // registerListener - добавляет подписчика, который будет уведомляться об изменениях
        m_logContent = new TextArea("");// новое текстовое поле
        m_logContent.setSize(200, 500);
        
        JPanel panel = new JPanel(new BorderLayout());  // Создаем панель с менеджером компоновки BorderLayout
        panel.add(m_logContent, BorderLayout.CENTER);// Добавляем текстовое поле в центр панели
        getContentPane().add(panel); // Получаем панель содержимого внутреннего окна и добавляем нашу панель
        // getContentPane() - возвращает контейнер для содержимого окна
        pack();
        updateLogContent(); // Загружаем существующее содержимое лога в текстовое поле
    }

    private void updateLogContent()
    {
        StringBuilder content = new StringBuilder();// Создаем StringBuilder для эффективного построения строки
        // StringBuilder лучше, чем простая конкатенация строк через +
        for (LogEntry entry : m_logSource.all()) // Проходим по всем записям в источнике лога

        {
            content.append(entry.getMessage()).append("\n"); // Добавляем сообщение из записи и перевод строки
        }
        m_logContent.setText(content.toString()); // Устанавливаем полученный текст в текстовое поле
        m_logContent.invalidate();
    }
    
    @Override  // Выполняем обновление содержимого в потоке обработки событий
    public void onLogChanged()
    {
        EventQueue.invokeLater(this::updateLogContent);
    }
}
