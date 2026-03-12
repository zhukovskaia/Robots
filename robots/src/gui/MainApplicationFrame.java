package gui;

import java.awt.Dimension;//размер
import java.awt.Toolkit;//инфа об экране
import java.awt.event.KeyEvent;

import javax.swing.*;

import log.Logger;


public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();//рабочий стол

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;   // отступ
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// через сист настройки получаем доступ к окну
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);// заменяет основную панель окна на нашу


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);// на рабочий стол добавили окно логов

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);

        setJMenuBar(generateMenuBar());//меню вверху
        setDefaultCloseOperation(EXIT_ON_CLOSE);// выйти при нажатии на крестик
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());//источник сообщений передаем
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());//главное окно не может стать меньше этого размера
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }



    // добавила класс для выхода
    public void exitApplication(){
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        int result = JOptionPane.showConfirmDialog(
                this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION
        );
        if ( result == JOptionPane.YES_OPTION){
            System.exit(0);
        }

    }


    private JMenuBar generateMenuBar()
    {
        return new MenuBarBuilder(this).buildMenuBar();
    }

    public void setLookAndFeel(String className)//смена оформления
    {
        try
        {
            UIManager.setLookAndFeel(className);//меняет тему
            SwingUtilities.updateComponentTreeUI(this);// updateComponentTreeUI - рекурсивно обновляет все компоненты
            // this - текущее окно (MainApplicationFrame)
        }
        catch (ClassNotFoundException | InstantiationException // Перехват возможных исключений при смене темы
               | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}