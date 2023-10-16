/**
 * Constructor for all chess pieces
 *
 * @author (Piper Inns Hall)
 * @version (16/07/2023)
 */
import java.util.LinkedList;
public class Piece {
    public enum Color {
        WHITE, BLACK
    }
    public enum Name {
        QUEEN, KING, ROOK, KNIGHT, BISHOP, PAWN
    }
    public enum State {
        ONGOING, CHECKMATE, STALEMATE
    }
    static final int SQR_SIZE = Board.SQR_SIZE;
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    int x, y, pX, pY, clickX, clickY, deltaX, deltaY; // Piece values
    Color color;
    boolean pieceMoved;
    Name name;
    LinkedList<Piece> ps;
    int tempPX, tempPY, tempDX, tempDY , tempCX, tempCY; // Temporary piece values
    private static Color turn = Color.WHITE;
    static State state = State.ONGOING;
    boolean enPassantEnabled;
    boolean castling;
    boolean take;
    private static Piece enPassantPawn;
    Piece castleRook;
    Piece attackedPiece;
    public Piece(int pX, int pY, Color color , boolean pieceMoved, Name name, LinkedList<Piece> ps) {
        this.x = pX * SQR_SIZE;
        this.y = pY * SQR_SIZE;
        this.pX = pX;
        this.pY = pY;
        this.color = color;
        this.pieceMoved = pieceMoved;
        this.name = name;
        this.ps = ps;
        ps.add(this);
    }
    public void move(int pX, int pY) {
        deltaX = pX - this.pX;
        deltaY = pY - this.pY;
        clickX = pX;
        clickY = pY;
        take = false;
        attackedPiece = Board.getPiece(this.clickX * SQR_SIZE, this.clickY * SQR_SIZE);
        castling = false;
        boolean checking = check(false, false);
        if (legalMove(true, false, false)) {
            boolean castle = castling;
            moveType();
            this.pieceMoved = true;
            this.pX = pX;
            this.pY = pY;
            this.x = pX * SQR_SIZE;
            this.y = pY * SQR_SIZE;
            new Notation(castle, deltaX, name, this.pX, this.pY, take, state, checking);
            switchTurn();
            gameState(checking);
        }
    }
    public boolean legalMove(boolean realMove, boolean ownMove, boolean mateMove) {
        boolean validMove = switch (name) {
            case QUEEN -> queenMove();
            case KING -> kingMove(realMove, mateMove);
            case ROOK -> rookMove();
            case KNIGHT -> knightMove();
            case BISHOP -> bishopMove();
            case PAWN -> pawnMove();
        };
        if (!validMove) return false;
        if (!ownMove && ownPieceMove()) return false;
        if (ownSquareMove()) return false;
        if (outOfBounds()) return false;
        if (realMove && !resolveCheck()) return false;
        if (protectedPiece()) return false;
        return !obstruction();
    }
    public boolean queenMove() {
        return Math.abs(this.deltaX) == Math.abs(this.deltaY) || deltaX == 0 || deltaY == 0;
    } // Queen moves
    public boolean kingMove(boolean realMove, boolean mateMove) {
        if (!pieceMoved && deltaY == 0 && Math.abs(deltaX) == 2) {
            int rookX = (deltaX > 0) ? 7 : 0; // Determine the rook's starting position
            int rookY = pY; // Rook stays in the same row
            castleRook = Board.getPiece(rookX * SQR_SIZE, rookY * SQR_SIZE);
            Piece bFile = Board.getPiece(SQR_SIZE, pY * SQR_SIZE);
            boolean bFilePiece = bFile != null && deltaX < 0;
            boolean checkCastle;
            checkCastle = protectedSquare(pX + Integer.signum(deltaX), pY,
                    false, false, false);
            if (castleRook != null && castleRook.name == Name.ROOK && !castleRook.pieceMoved
                    && !bFilePiece && !checkCastle) {
                if (realMove && !mateMove) castling = true;
                return true; // Castling successful
            }
            return false; // Castling is not valid
        }
        return Math.abs(deltaX) <= 1 && Math.abs(deltaY) <= 1; // King moves
    } // King moves
    public boolean rookMove() {
        return this.deltaX == 0 || this.deltaY == 0;
    } // Rook moves
    public boolean knightMove() {
        return Math.abs(this.deltaX) == 2 && Math.abs(this.deltaY) == 1 ||
                Math.abs(this.deltaX) == 1 && Math.abs(this.deltaY) == 2;
    } // Knight moves
    public boolean bishopMove() {
        return deltaX == deltaY || deltaX == -deltaY;
    } // Bishop moves
    public boolean pawnMove() {
        if (Board.getPiece(clickX * SQR_SIZE, clickY * SQR_SIZE) != null) {
            if (deltaX < -1 || deltaX > 1) return false; // Diagonal taking
            if (deltaX == 0) return false; // Can't take non-diagonally
        } else {
            if (deltaX != 0) { // If moving diagonally
                if (deltaY == 0 || Math.abs(deltaX) > 1) return false; // Can't en passant sideways
                int signColor = color == Color.WHITE ? 1 : -1;
                Piece pawn = Board.getPiece(clickX * SQR_SIZE,(clickY + signColor) * SQR_SIZE);
                boolean sameRank = false;
                if (pawn != null) sameRank = this.pY == pawn.pY;
                if (pawn != null && pawn.enPassantEnabled && pawn == enPassantPawn && sameRank) {
                    enPassantPawn.kill();
                    return true;
                }
                return false; // Can't move to empty square without en passant
            }
        }
        if (color == Color.WHITE) { // If piece is white
            if ((!pieceMoved && deltaY < -2) || (pieceMoved && deltaY < -1)) return false; // Can't move forward too far
        } else { // If piece is black
            if ((!pieceMoved && deltaY > 2) || (pieceMoved && deltaY > 1)) return false; // Can't move forward too far
        }
        return color == Color.WHITE ? deltaY < 0 : deltaY > 0; //Can't move backwards
    }
    public boolean ownPieceMove() {
        Piece clickedPiece = Board.getPiece(clickX * SQR_SIZE, clickY * SQR_SIZE);
        return clickedPiece != null && clickedPiece.color == this.color;
    } // Can't take own piece
    public boolean ownSquareMove() {
        return this.deltaX == 0 && this.deltaY == 0;
    } // Can't move to own square
    public boolean outOfBounds() {
        return !(clickX <= 7 && clickY <= 7 && clickX >= 0 && clickY >= 0);
    } // Can't move out of bounds
    public boolean resolveCheck() {
        this.tempSave();
        this.pX = this.clickX;
        this.pY = this.clickY;
        if (check(true, true)) {
            this.tempLoad();
            return false; // King cannot escape check with this move
        }
        this.tempLoad();
        return true; // King can escape check with this move
    }
    public boolean check(boolean myKing, boolean resolveCheckMove) {
        Piece king = kingPos(myKing);
        return protectedSquare(king.pX, king.pY,
                !myKing, resolveCheckMove,false);
    }
    public boolean protectedPiece() {
        if (name != Name.KING) return false;
        if (attackedPiece != null && attackedPiece.color != color) {
            return protectedSquare(attackedPiece.pX,attackedPiece.pY,
                    false,false,true);
        }
        return false;
    }
    public boolean obstruction() {
        if (name == Name.KNIGHT) return false;
        int currentX = this.pX + Integer.signum(this.deltaX);
        int currentY = this.pY + Integer.signum(this.deltaY);
        while (Math.abs(currentX - clickX) > 0 || Math.abs(currentY - clickY) > 0) {
            if (Board.getPiece(currentX * SQR_SIZE, currentY * SQR_SIZE) != null) return true;
            currentX += Integer.signum(this.deltaX);
            currentY += Integer.signum(this.deltaY);
        }
        return false; // No obstruction found
    }
    public void moveType() {
        pieceTake();
        pawnPromote();
        pawnTwoSquare();
        if (castling) castle();
    }
    public void pieceTake() {
        Piece take = Board.getPiece(clickX * SQR_SIZE, clickY * SQR_SIZE);
        if (take != null) {
            take.kill();
            this.take = true;
        }
    }
    public void kill() {
        ps.remove(this);
    }
    public void pawnPromote() {
        if (name == Name.PAWN && ((color == Color.BLACK && clickY == 7) || (color == Color.WHITE && clickY == 0))) {
            name = Name.QUEEN;
        }
    }
    public void pawnTwoSquare() {
        enPassantPawn = null;
        if (name == Name.PAWN && Math.abs(deltaY) == 2) {
            enPassantEnabled = true;
            enPassantPawn = this;
        }
    }
    public void castle() {
        castleRook.pX = (deltaX > 0) ? this.pX + 1 : this.pX - 1;
        castleRook.x = castleRook.pX * SQR_SIZE;
        castleRook.y = castleRook.pY * SQR_SIZE;
        castling = false;
    }
    public static void switchTurn() {
        turn = (turn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    public static Color getTurn() {
        return turn;
    }
    public static void setTurn(Color color) {
        turn = color;
    }
    public Piece kingPos(boolean myKing) {
        for (Piece p : ps) {
            if (p.name == Name.KING) {
                if (myKing && p.color == turn || !myKing && p.color != turn) {
                    return p; // return king
                }
            }
        }
        return null; // Return null if the king is not found
    }
    public boolean protectedSquare(int pX, int pY, boolean checkOwnPieces, boolean resolveCheckMove, boolean ownMove) {
        for (Piece p : ps) {
            if (checkOwnPieces && p.color == turn || !checkOwnPieces && p.color != turn) {
                if (!resolveCheckMove || p != attackedPiece) { // Attacked piece cant put king in check
                    p.tempSave();
                    if (checkOwnPieces && p == this){
                        p.pX = this.clickX;
                        p.pY = this.clickY;
                    }
                    p.deltaX = pX - p.pX;
                    p.deltaY = pY - p.pY;
                    p.clickX = pX;
                    p.clickY = pY;
                    if (p.legalMove(false, ownMove, false)) {
                        p.tempLoad();
                        return true;
                    }
                    p.tempLoad();
                }
            }
        }
        return false;
    }
    public boolean checkmate() {
        for (Piece p : ps) {
            if (p.color == turn) { // Check moves for the current player's pieces
                for (int x = 0; x < SQR_AMOUNT; x ++) {
                    for (int y = 0; y < SQR_AMOUNT; y ++) {
                        p.tempSave();
                        p.deltaX = x - p.pX;
                        p.deltaY = y - p.pY;
                        p.clickX = x;
                        p.clickY = y;
                        p.attackedPiece = Board.getPiece(x * SQR_SIZE, y * SQR_SIZE);
                        if (p.legalMove(true, false,true)) {
                            p.tempLoad();
                            return false; // At least one legal move is available
                        }
                        p.attackedPiece = null;
                        p.tempLoad();
                    }
                }
            }
        }
        return true; // No legal moves available, it's checkmate
    }
    public void gameState(boolean checking) {
        if (checking && checkmate()) state = State.CHECKMATE;
        else if (checkmate()) state = State.STALEMATE;
    }
    public void tempSave() {
        this.tempPX = this.pX;
        this.tempPY = this.pY;
        this.tempDX = this.deltaX;
        this.tempDY = this.deltaY;
        this.tempCX = this.clickX;
        this.tempCY = this.clickY;
    }
    public void tempLoad() {
        this.pX = this.tempPX;
        this.pY = this.tempPY;
        this.deltaX = this.tempDX;
        this.deltaY = this.tempDY;
        this.clickX = this.tempCX;
        this.clickY = this.tempCY;
    }
}