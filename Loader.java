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
    private static File load;
    static final int PIECE_AMOUNT = Board.SQR_SIZE / 2;
    static int[] pX;
    static int[] pY;
    static boolean[] isBlack;
    static boolean[] pieceMoved;
    static String[] name;
    static boolean turn;
    public Loader() {
        pX = new int[PIECE_AMOUNT];
        pY = new int[PIECE_AMOUNT];
        isBlack = new boolean[PIECE_AMOUNT];
        pieceMoved = new boolean[PIECE_AMOUNT];
        name = new String[PIECE_AMOUNT];
    }
    public void loadFile(File file) {
        load = file;
        fileRead();
    }
    public void loadTurn(File file) {
        try {
            Scanner readFile = new Scanner(file);
            if (readFile.hasNextLine()) {
                String lineRead = readFile.nextLine();
                turn = Boolean.parseBoolean(lineRead);
            }
            readFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void fileRead() {
        try {
            Scanner readFile = new Scanner(load);
            for (int piece = 0; piece < PIECE_AMOUNT; piece++) {
                if (readFile.hasNextLine()) {
                    String lineRead = readFile.nextLine();
                    String[] parts = lineRead.split(",");
                    pX[piece] = Integer.parseInt(parts[0]);
                    pY[piece] = Integer.parseInt(parts[1]);
                    isBlack[piece] = Boolean.parseBoolean(parts[2]);
                    pieceMoved[piece] = Boolean.parseBoolean(parts[3]);
                    name[piece] = parts[4];
                } else break;
            }
            readFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}