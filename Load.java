/**
 * Loads saved chess game
 *
 * @author (Piper)
 * @version (16/07/2023)
 */
import java.io.File;
public class Load {
    public Load(boolean loadTurn, File file) {
        clearing();
        resetPieces(file);
        if (loadTurn) loadTurn();
        else Piece.setTurn(Piece.Color.WHITE);
    }
    public static void clearing() {
        Menu.panel.repaint();
        Menu.clearTextArea();
        Board.selectedPiece = null;
        Piece.state = Piece.State.ONGOING;
        while (!Board.ps.isEmpty()) {
            Board.ps.removeFirst();
        }
    }
    public static void resetPieces(File file) {
        Loader loader = new Loader();
        loader.loadFile(file);
        for (int piece = 0; piece < Loader.PIECE_AMOUNT; piece++) {
            if (Loader.name[piece] != null) {
                Board.pieceList(Loader.pX[piece], Loader.pY[piece],
                        Loader.color[piece], Loader.pieceMoved[piece], Loader.name[piece]);
            }
        } // Access the loaded values from the arrays
    }
    public void loadTurn() {
        File file = new File("saveTurn.txt");
        Loader loader = new Loader();
        loader.loadTurn(file);
        Piece.setTurn(Loader.turn);
    }
}