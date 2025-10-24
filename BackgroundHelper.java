import javax.swing.*;
import java.awt.*;

public class BackgroundHelper {

    public static JLabel getBackground(String imagePath, int width, int height) {
        ImageIcon bgIcon = new ImageIcon(imagePath);
        Image img = bgIcon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);
        JLabel background = new JLabel(scaledIcon);
        background.setBounds(0, 0, width, height);
        return background;
    }
}

