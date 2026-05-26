package gui;

import utils.i18n.LocaleManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.Locale;

public class RobotsProgram {
    public static void main(String[] args) {

        LocaleManager.init(new Locale("ru"));

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame();
            frame.setVisible(true);
        });
    }
}