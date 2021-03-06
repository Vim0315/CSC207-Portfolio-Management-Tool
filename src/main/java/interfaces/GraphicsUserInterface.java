package interfaces;

import javax.imageio.ImageIO;
import javax.swing.*;

import controller.DataAccessInterfaceControllerRelay;
import controller.GraphicInterfaceRelay;
import presenters.PanelFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class GraphicsUserInterface implements GraphicInterfaceRelay {
    /**
     * Contains methods for generating graph suite for visualizing portfolio information.
     */

    public static JFrame generateJFrame(DataAccessInterfaceControllerRelay api) {
        JFrame frame = new JFrame();
        PanelFactory pf = new PanelFactory(api);

        frame.add(pf.makePanel("Text", 0, 0, 500, 250));
        frame.add(pf.makePanel("Portfolio Value Chart", 0, 500, 500, 500));
        frame.add(pf.makePanel("Portfolio Composition Chart", 500, 250, 500, 500));
        frame.add(pf.makePanel("Asset Growth Chart", 500, 750, 500, 250));
        frame.add(pf.makePanel("User Leaderboard", 500, 0, 500, 250));
        frame.add(pf.makePanel("Portfolio Growth Chart", 0, 250, 500, 250));

        return frame;

    }

    public void generateGraphics(DataAccessInterfaceControllerRelay api) {

        JFrame frame = generateJFrame(api);

        frame.setLayout(null);
        frame.setSize(1000, 1000);
        frame.setVisible(true);

    }

    public void generateImage(DataAccessInterfaceControllerRelay api) throws IOException {
    //Save an image representation to .\images\image.png
        JFrame frame = generateJFrame(api);
        frame.setLayout(null);
        frame.setSize(1000, 1000);
        frame.setUndecorated(true);
        frame.setVisible(true);
        BufferedImage image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        frame.paint(image.getGraphics());
        ImageIO.write(image, "PNG", new File(".\\images\\image.png"));
        frame.dispose();
    }
}
