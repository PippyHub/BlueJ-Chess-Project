/**
 * Starts new chess game
 *
 * @author (Piper)
 * @version (16/07/2023)
 */
import java.io.File;
public class newGame {
    public newGame() {
        clearing();
        resetPieces();
        Menu.panel.repaint();
    }
    public static void clearing() {
        while (!Board.ps.isEmpty()) {
            Board.ps.removeFirst();
        }
        Piece.checkmated = false;
        Piece.resetTurn();
    }
    public void resetPieces(){
        File file = new File("defaultPosition.txt");
        Loader loader = new Loader();
        loader.loadFile(file);
        for (int piece = 0; piece < Loader.PIECE_AMOUNT; piece++) {
            Board.pieceList(Loader.pX[piece], Loader.pY[piece], Loader.isBlack[piece], false, Loader.name[piece]);
        } // Access the loaded values from the arrays
    }
}