/**
 * Prints the notation into the text area
 *
 * @author (Piper Inns Hall)
 * @version (16/07/2023)
 */
public class Notation {
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    public Notation(boolean castle, int deltaX, Piece.Name name, int pX, int pY, boolean take, Piece.State state, boolean checking) {
        notate(castle, deltaX, name, pX, pY, take, state, checking);
    }
    public void notate(boolean castle, int deltaX, Piece.Name name, int pX, int pY, boolean take, Piece.State state, boolean checking) {
        String turn = Piece.getTurn() == Piece.Color.WHITE ? "White" : "Black" + "\u200A";
        Menu.updateTextArea(turn + "   ");
        if (castle) {
            if (deltaX > 0) Menu.updateTextArea("O-O");
            else Menu.updateTextArea("O-O-O");
        } else {
            String firstChar;
            if (name == Piece.Name.KNIGHT) {
                firstChar = "N";
            } else if (name == Piece.Name.PAWN) {
                firstChar = "";
            } else {
                firstChar = String.valueOf(name.name().charAt(0));
            }
            Menu.updateTextArea(firstChar); //Print piece name e.g. K for king
            char pieceX = (char) (pX + 'a');
            String pieceY = String.valueOf(SQR_AMOUNT - pY);
            if (take) {
                if (name == Piece.Name.PAWN) {
                    Menu.updateTextArea(String.valueOf(pieceX));
                } //If pawn print what square its taking from
                Menu.updateTextArea("x"); //Print out if taking
            }
            Menu.updateTextArea(pieceX + pieceY); // Print rank and file e.g. e4
            if (state == Piece.State.CHECKMATE) Menu.updateTextArea("#"); //Print # if mate
            else if (checking) Menu.updateTextArea("+"); //Print + if check
        }
        Menu.updateTextArea("\n");
    } //Add notation after moving a piece
}
