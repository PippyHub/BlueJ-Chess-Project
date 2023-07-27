/**
 * Loads saved chess game
 *
 * @author (Piper)
 * @version (16/07/2023)
 */
import java.io.File;
public class Load {
    public Load() {
        emptyBoardList();
        resetVariables();
        resetPieces();
        loadTurn();
        Menu.panel.repaint();
    }
    public void emptyBoardList() {
        while (!Board.ps.isEmpty()) {
            Board.ps.removeFirst();
        }
    }
    public void resetVariables() {
        Piece.checkmated = false;
        Piece.resetTurn();
    }
    public static void resetPieces() {
        File file = new File("savePosition.txt");
        Loader loader = new Loader();
        loader.loadFile(file);
        for (int piece = 0; piece < Loader.PIECE_AMOUNT; piece++) {
            if (Loader.name[piece] != null) {
                Board.pieceList(Loader.pX[piece], Loader.pY[piece],
                        Loader.isBlack[piece], false, Loader.name[piece]);
            }
        } // Access the loaded values from the arrays
    }
    public void loadTurn() {
        File file = new File("saveTurn.txt");
        Loader loader = new Loader();
        loader.loadTurn(file);
        Piece.loadTurn(Loader.turn);
    }
}