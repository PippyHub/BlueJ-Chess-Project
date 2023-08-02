/**
 * Loads chess savePosition
 *
 * @author (Piper)
 * @version (16/07/2023)
 */
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
public class Loader {
    static final int PIECE_AMOUNT = Board.PIECE_AMOUNT;
    static int[] pX;
    static int[] pY;
    static Piece.Color[] color;
    static boolean[] pieceMoved;
    static Piece.Name[] name;
    static Piece.Color turn;
    public Loader() {
        pX = new int[PIECE_AMOUNT];
        pY = new int[PIECE_AMOUNT];
        color = new Piece.Color[PIECE_AMOUNT];
        pieceMoved = new boolean[PIECE_AMOUNT];
        name = new Piece.Name[PIECE_AMOUNT];
    }
    public void loadFile(File file) {
        fileRead(file);
    }
    public void loadTurn(File file) {
        try {
            Scanner readFile = new Scanner(file);
            if (readFile.hasNextLine()) {
                String lineRead = readFile.nextLine();
                turn = Piece.Color.valueOf(lineRead);
            }
            readFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void fileRead(File file) {
        try {
            Scanner readFile = new Scanner(file);
            for (int piece = 0; piece < PIECE_AMOUNT; piece++) {
                if (readFile.hasNextLine()) {
                    String lineRead = readFile.nextLine();
                    String[] parts = lineRead.split(",");
                    pX[piece] = Integer.parseInt(parts[0]);
                    pY[piece] = Integer.parseInt(parts[1]);
                    color[piece] = Piece.Color.valueOf(parts[2]);
                    pieceMoved[piece] = Boolean.parseBoolean(parts[3]);
                    name[piece] = Piece.Name.valueOf(parts[4]);
                } else break;
            }
            readFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}