package ui;

import javax.swing.*;
import java.awt.*;

public class BackgroundForecastPanel extends JPanel {
    private ImageIcon backgroundImage;

    // Used to create the background for the current weather info panel.
    // imagePath is path to that background image.
    public BackgroundForecastPanel(String imagePath) {
        backgroundImage = new ImageIcon(getClass().getResource(imagePath));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // image to fill entire panel
            g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}
