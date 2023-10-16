/**
 * Prints out the chess board and pieces onto the frame
 *
 * @author (Piper Inns Hall)
 * @version (16/07/2023)
 */
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
public class Board extends JPanel implements ActionListener, MouseListener {
    static final int SQR_SIZE = 64;
    static final int SQR_AMOUNT = 8;
    static final int BOARD_SIZE = SQR_SIZE * SQR_AMOUNT;
    static final int PIECE_AMOUNT = 32;
    public static LinkedList<Piece> ps = new LinkedList<>(); //linked list of pieces
    public static Piece selectedPiece = null;
    Images img = new Images();
    private final Image[] images;
    boolean highlight;
    public Board() {
        images = img.loadImages();
        addMouseListener(this);
    }
    public static void pieceList(int pX, int pY, Piece.Color color, boolean pieceMoved, Piece.Name name) {
        new Piece(pX, pY, color, pieceMoved, name, ps);
    }
    public void paint(Graphics g) {
        boolean white = true;
        for (int boardY = 0; boardY < SQR_AMOUNT; boardY++) {
            for (int boardX = 0; boardX < SQR_AMOUNT; boardX++) {
                if(white) {
                    g.setColor(Color.white);
                }
                else {
                    g.setColor(Color.decode("#769656"));
                }
                g.fillRect(boardX * SQR_SIZE, boardY * SQR_SIZE, SQR_SIZE, SQR_SIZE);
                white=!white;
            }
            white=!white;
        }
        if (highlight) {
            g.setColor(Color.yellow);
            if (selectedPiece != null){
                g.fillRect(selectedPiece.pX * SQR_SIZE, selectedPiece.pY * SQR_SIZE, SQR_SIZE, SQR_SIZE);
            }
        }
        for (Piece p : ps) {
            int index = switch (p.name) {
                case QUEEN -> 0;
                case KING -> 1;
                case ROOK -> 2;
                case KNIGHT -> 3;
                case BISHOP -> 4;
                case PAWN -> 5;
            };
            if (p.color == Piece.Color.WHITE) index += 6;
            g.drawImage(images[index], p.x, p.y, this);
        }
        if (Piece.state != Piece.State.ONGOING) {
            int squareX = 300;
            int squareY = 160;
            int centerX = (BOARD_SIZE - squareX) / 2;
            int centerY = (BOARD_SIZE - squareY) / 2;
            int cornerRadius = 10; // Adjust the corner radius as needed

            Color tintedColor = new Color(0, 0, 0, 100); // Adjust the transparency and color as needed
            g.setColor(tintedColor);
            g.fillRect(0, 0, BOARD_SIZE, BOARD_SIZE);// Draw the tinted background rectangle

            g.setColor(Color.white);
            g.fillRoundRect(centerX, centerY, squareX, squareY, cornerRadius, cornerRadius); // rounded corners

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String message =  Piece.state == Piece.State.CHECKMATE ? "Checkmate!" : "Stalemate!";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, (BOARD_SIZE - messageWidth) / 2, centerY + squareY / 2 - 40);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String winner = Piece.getTurn() == Piece.Color.WHITE ? "black" : "white";
            message = Piece.state == Piece.State.CHECKMATE ? winner + " has won" : winner + " has drawn";
            messageWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, (BOARD_SIZE - messageWidth) / 2, centerY + squareY / 2 - 17);

            g.setFont(new Font("Arial", Font.PLAIN, 15));
            message = "'File > New' to play again";
            messageWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, (BOARD_SIZE - messageWidth) / 2, centerY + squareY / 2 + 43);
        }
    }
    public static Piece getPiece(int x, int y) {
        int pX = x / SQR_SIZE;
        int pY = y / SQR_SIZE;
        for (Piece p: ps) {
            if(p.pX == pX && p.pY == pY) return p;
        }
        return null;
    }
    public void actionPerformed(ActionEvent e) {}
    public void mousePressed(MouseEvent e) {
        if (Piece.state == Piece.State.ONGOING) {
            if (selectedPiece == null) { // No piece currently selected, attempt to select a piece
                selectedPiece = getPiece(e.getX(), e.getY());
                if (selectedPiece != null) if (selectedPiece.color != Piece.getTurn()) selectedPiece = null;
                if (selectedPiece != null) highlight = true;
            } else { // A piece is already selected, attempt to move it
                selectedPiece.move(e.getX() / SQR_SIZE, e.getY() / SQR_SIZE);
                highlight = false;
                selectedPiece = null; // Deselect the piece after moving
            }
            this.repaint();
        }
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
}