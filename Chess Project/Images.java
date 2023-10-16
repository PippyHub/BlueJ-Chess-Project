/**
 * Loads all image assets
 *
 * @author (Piper Inns Hall)
 * @version (16/07/2023)
 */
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Images {
    private final Image[] images;
    public Images() {
        images = new Image[12];
    }
    public Image[] loadImages() {
        try {
            BufferedImage all = ImageIO.read(new File("ChessPiecesArray.png"));
            int index = 0;
            for (int y = 0; y < 120; y += 60) {
                for (int x = 0; x < 360; x += 60) {
                    images[index] = all.getSubimage(x, y, 60, 60)
                            .getScaledInstance(Board.SQR_SIZE, Board.SQR_SIZE, BufferedImage.SCALE_SMOOTH);
                    index++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images;
    }
}