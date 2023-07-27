/**
 * Saves chess savePosition
 *
 * @author (Piper)
 * @version (16/07/2023)
 */
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
public class Save {
    public Save(LinkedList<Piece> ps) {
        File position = new File("savePosition.txt");
        File turn = new File("saveTurn.txt");
        try {
            FileWriter positionWrite = new FileWriter(position);
            for (Piece piece : ps) {
                String line = piece.pX + "," + piece.pY + "," +
                        piece.isBlack + "," + piece.pieceMoved + "," + piece.name + "\n";
                positionWrite.write(line);
            }
            positionWrite.flush();
            positionWrite.close();

            FileWriter turnWrite = new FileWriter(turn);
            turnWrite.write( Piece.saveTurn() + "");
            turnWrite.flush();
            turnWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}