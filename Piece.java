/**
 * Constructor for all chess pieces
 *
 * @author (Piper Inns Hall)
 * @version (16/07/2023)
 */
import java.util.LinkedList;
public class Piece {
    static final int SQR_SIZE = Board.SQR_SIZE;
    static final int SQR_AMOUNT = Board.SQR_AMOUNT;
    int x, y, pX, pY, clickX, clickY, deltaX, deltaY; // Piece values
    int tempPX, tempPY, tempDX, tempDY , tempCX, tempCY; // Temporary stored values
    boolean isBlack;
    boolean pieceMoved;
    String name;
    LinkedList<Piece> ps;
    boolean castling;
    boolean enPassantEnabled;
    private static boolean isBlackTurn;
    public static boolean checkmated;
    public static boolean winner;
    public static boolean moveMade, checkMade, castleMade, takeMade; // Used for notation
    public static String getName; // Used for notation
    public static int castlingDelta; // Used for notation
    public static Piece enPassantPawn;
    Piece castleRook;
    Piece attackedPiece;
    public Piece(int pX, int pY, boolean isBlack, boolean pieceMoved, String n, LinkedList<Piece> ps) {
        this.x = pX * SQR_SIZE;
        this.y = pY * SQR_SIZE;
        this.pX = pX;
        this.pY = pY;
        this.isBlack = isBlack;
        this.pieceMoved = pieceMoved;
        this.ps = ps;
        this.name = n;
        ps.add(this);
    }
    public void move(int pX, int pY) {
        moveMade = false;
        takeMade = false;
        deltaX = pX - this.pX;
        deltaY = pY - this.pY;
        clickX = pX;
        clickY = pY;
        boolean checking = oppositeKingInCheck();
        attackedPiece = Board.getPiece(this.clickX * SQR_SIZE, this.clickY * SQR_SIZE);
        if (legalMove(true, false,false)) {
            moveMade = true;
            checkMade = checking;
            getName = name;
            castleMade = castling;
            castlingDelta = deltaX;
            moveType();
            this.pieceMoved = true;
            this.attackedPiece = null;
            this.pX = pX;
            this.pY = pY;
            this.x = pX * SQR_SIZE;
            this.y = pY * SQR_SIZE;
            switchTurn();
            if (checking)
                checkmate(this.checkmated(), this.isBlack); // At the bottom of move() so selected piece has moved
        }
    }
    public static void switchTurn() { isBlackTurn = !isBlackTurn; } // Switching turns
    public static void resetTurn() { isBlackTurn = false; } // Reset turns
    public static void loadTurn(boolean blackTurn) { isBlackTurn = blackTurn; } // Loads turns
    public static boolean saveTurn() { return isBlackTurn; } // Saves turns
    public void moveType() {
        pieceTake();
        pawnPromote();
        pawnDouble();
        if (this.castling) castle();
    }
    public void pieceTake() {
        Piece take = Board.getPiece(clickX * SQR_SIZE, clickY * SQR_SIZE);
        if (take != null) {
            take.kill();
            takeMade = true;
        }// Taking pieces
    }
    public void kill() {
        if (enPassantPawn == this) enPassantPawn = null; // Reset en passant pawn if it is killed
        ps.remove(this);
    }
    public void pawnPromote() {
        if (name.equalsIgnoreCase("pawn"))
            if ((this.isBlack && clickY == 7) || (!this.isBlack && clickY == 0)) name = "queen"; // Promoting pawns
    }
    public void pawnDouble() {
        enPassantPawn = null;
        if (name.equalsIgnoreCase("pawn") && Math.abs(deltaY) == 2) {
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
    public boolean legalMove(boolean realMove, boolean takeOwnPiece, boolean checkmating) {
        if (realMove && !checkTurn()) return false;
        if (ownSQRMove()) return false;
        if (!takeOwnPiece && ownPieceMove()) return false;
        if (!queenMove()) return false;
        if (!kingMove(checkmating, realMove)) return false;
        if (realMove && !resolveCheck()) return false;
        if (!rookMove()) return false;
        if (!knightMove()) return false;
        if (!bishopMove()) return false;
        if (!pawnMove()) return false;
        if (!boardBoundary()) return false;
        if (!kingTakeProtectedPiece()) return false;
        return noObstruction();
    }
    public boolean checkTurn() { return (isBlackTurn && this.isBlack) || (!isBlackTurn && !this.isBlack); }
    public boolean ownSQRMove() { return this.deltaX == 0 && this.deltaY == 0; } // Cannot move to own SQR
    public boolean ownPieceMove() {
        Piece clickedPiece = Board.getPiece(clickX * SQR_SIZE, clickY * SQR_SIZE);
        return clickedPiece != null && clickedPiece.isBlack == isBlack; // Cannot take own pieces
    }
    public boolean queenMove() {
        if (!name.equalsIgnoreCase("queen")) return true;
        return Math.abs(this.deltaX) == Math.abs(this.deltaY) || deltaX == 0 || deltaY == 0;
    } // Queen moves
    public boolean kingMove(boolean checkmating, boolean realMove) {
        if (!name.equalsIgnoreCase("king")) return true;
        if (!pieceMoved && deltaY == 0 && Math.abs(deltaX) == 2) {
            int rookX = (deltaX > 0) ? 7 : 0; // Determine the rook's starting position
            int rookY = pY; // Rook stays in the same row
            castleRook = Board.getPiece(rookX * SQR_SIZE, rookY * SQR_SIZE);
            Piece bFile = Board.getPiece(SQR_SIZE, pY * SQR_SIZE);
            boolean bFilePiece = bFile != null && deltaX < 0;
            boolean squareProtected = squareProtected(pX + Integer.signum(deltaX), pY);
            if (castleRook != null && castleRook.name.equalsIgnoreCase("rook") && !castleRook.pieceMoved
                    && !bFilePiece && !squareProtected && !myKingInCheck()) {
                if(!checkmating && realMove) castling = true;
                return true; // Castling successful
            }
            return false; // Castling is not valid
        }

        return Math.abs(deltaX) <= 1 && Math.abs(deltaY) <= 1; // King moves
    }
    public boolean resolveCheck() {
        this.tempSave();
        Piece attacker = getCheckingPiece();
        boolean canDefend;
        if (myKingInCheck()) {
            canDefend = (attacker == attackedPiece && attacker != null);
        } else {
            canDefend = (attackedPiece != null);
        }
        this.pX = this.clickX;
        this.pY = this.clickY;
        if (myKingInCheck() && !canDefend) {
            this.tempLoad();
            return false; // King cannot escape check with this move
        }
        this.tempLoad();
        return true; // King can escape check with this move
    }
    public boolean myKingInCheck() {
        Piece myKing = myKingPos();
        return check(myKing, myKing.pX, myKing.pY);
    }
    public boolean oppositeKingInCheck() {
        Piece oppositeKing = oppositeKingPos();
        return checking(oppositeKing, oppositeKing.pX, oppositeKing.pY);
    }
    public Piece getCheckingPiece() {
        Piece myKing = myKingPos();
        return getPieceCheckingKing(myKing.pX, myKing.pY);
    }
    public Piece myKingPos() {
        for (Piece p : ps) {
            if (p.name.equalsIgnoreCase("king") && p.isBlack == this.isBlack) {
                return p;
            }
        }
        return null; // Return null if the king is not found
    }
    public Piece oppositeKingPos() {
        for (Piece p : ps) {
            if (p.name.equalsIgnoreCase("king") && p.isBlack != this.isBlack) {
                return p;
            }
        }
        return null; // Return null if the king is not found
    }
    public boolean check(Piece myKing, int kingPosX, int kingPosY) {
        for (Piece p : ps) {
            if (p.isBlack != isBlackTurn) {
                p.tempSave();
                if (myKing != null) {
                    p.deltaX = kingPosX - p.pX;
                    p.deltaY = kingPosY - p.pY;
                    p.clickX = kingPosX;
                    p.clickY = kingPosY;
                }
                if (p.legalMove(false,false,false)) {
                    p.tempLoad();
                    return true;
                }
                p.tempLoad();
            }
        }
        return false;
    }
    public boolean checking(Piece oppositeKing, int kingPosX, int kingPosY) {
        for (Piece p : ps) {
            if (p.isBlack == isBlackTurn) {
                p.tempSave();
                if (p == Board.selectedPiece) {
                    p.pX = Board.selectedPiece.clickX;
                    p.pY = Board.selectedPiece.clickY;
                }
                if (oppositeKing != null) {
                    p.deltaX = kingPosX - p.pX;
                    p.deltaY = kingPosY - p.pY;
                    p.clickX = kingPosX;
                    p.clickY = kingPosY;
                }
                if (p.legalMove(false,false,false)) {
                    p.tempLoad();
                    return true;
                }
                p.tempLoad();
            }
        }
        return false;
    }
    public Piece getPieceCheckingKing(int kingPosX, int kingPosY) {
        for (Piece p : ps) {
            if (p.isBlack != isBlackTurn) {
                p.tempSave();
                p.deltaX = kingPosX - p.pX;
                p.deltaY = kingPosY - p.pY;
                p.clickX = kingPosX;
                p.clickY = kingPosY;
                if (p.legalMove(false, false,false)) {
                    p.tempLoad();
                    return p;
                }
                p.tempLoad();
            }
        }
        return null;
    }
    public boolean kingTakeProtectedPiece() {
        if (!name.equalsIgnoreCase("king")) return true;
        if (attackedPiece != null && attackedPiece.isBlack != this.isBlack) {
            return !pieceProtected(attackedPiece, attackedPiece.pX, attackedPiece.pY); // King cannot take protected piece
        }
        return true;
    }
    public boolean pieceProtected(Piece attacker, int attackerPosX, int attackerPosY) {
        for (Piece p : ps) {
            if (p.isBlack != isBlackTurn) {
                p.tempSave();
                if (attacker != null) {
                    p.deltaX = attackerPosX - p.pX;
                    p.deltaY = attackerPosY - p.pY;
                    p.clickX = attackerPosX;
                    p.clickY = attackerPosY;
                }
                if (p.legalMove(false, true,false)) {
                    p.tempLoad();
                    return true;
                }
                p.tempLoad();
            }
        }
        return false;
    }
    public boolean squareProtected(int squarePosX, int squarePosY) {
        for (Piece p : ps) {
            if (p.isBlack != isBlackTurn) {
                p.tempSave();
                p.deltaX = squarePosX - p.pX;
                p.deltaY = squarePosY - p.pY;
                p.clickX = squarePosX;
                p.clickY = squarePosY;
                if (p.legalMove(false, false,false)) {
                    p.tempLoad();
                    return true;
                }
                p.tempLoad();
            }
        }
        return false;
    }
    public static void checkmate(boolean checkmated, boolean winner) {
        Piece.checkmated = checkmated;
        Piece.winner = winner;
    }
    public boolean checkmated() {
        for (Piece p : ps) {
            if (p.isBlack == isBlackTurn) { // Check moves for the current player's pieces (turn has already changed)
                for (int x = 0; x < SQR_AMOUNT; x++) {
                    for (int y = 0; y < SQR_AMOUNT; y++) {
                        p.tempSave();
                        p.deltaX = x - p.pX;
                        p.deltaY = y - p.pY;
                        p.clickX = x;
                        p.clickY = y;
                        p.attackedPiece = Board.getPiece(x * SQR_SIZE, y * SQR_SIZE); // Set the attackedPiece before calling resolveCheck
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
    public boolean rookMove() {
        if (!name.equalsIgnoreCase("rook")) return true;
        return deltaX == 0 || deltaY == 0;
    } // Rook moves
    public boolean knightMove() {
        if (name.equalsIgnoreCase("knight")) {
            return Math.abs(this.deltaX) == 2 && Math.abs(this.deltaY) == 1 ||
                    Math.abs(this.deltaX) == 1 && Math.abs(this.deltaY) == 2;
        }
        return true;
    } // Knight moves
    public boolean bishopMove() {
        if (!name.equalsIgnoreCase("bishop")) return true;
        return deltaX == deltaY || deltaX == -deltaY;
    } // Bishop moves
    public boolean pawnMove() {
        if (!name.equalsIgnoreCase("pawn")) return true;
        if (Board.getPiece(clickX * SQR_SIZE, clickY * SQR_SIZE) != null) {
            if (deltaX < -1 || deltaX > 1) return false; // Diagonal taking
            if (deltaX == 0) return false; // Can't take non-diagonally
        } else {
            if (deltaX != 0) {
                if (deltaY == 0 || Math.abs(deltaX) > 1) return false; // Can't en passant sideways
                int signIsBlack = this.isBlack ? -1 : 1;
                Piece leftPawn = Board.getPiece(clickX * SQR_SIZE,(clickY + signIsBlack) * SQR_SIZE);
                Piece rightPawn = Board.getPiece(clickX * SQR_SIZE,(clickY + signIsBlack) * SQR_SIZE);
                if ((leftPawn != null && leftPawn.enPassantEnabled && leftPawn == enPassantPawn) ||
                        (rightPawn != null && rightPawn.enPassantEnabled && rightPawn == enPassantPawn)) {
                    enPassantPawn.kill();
                    return true;
                }
                return false; // Can't move to empty square without en passant
            }
        }
        if (!isBlack) { // If piece is white
            if ((!pieceMoved && deltaY < -2) || (pieceMoved && deltaY < -1)) return false; // Can't move forward far
            return deltaY < 0; // Can't move backward
        } else { // If piece is black
            if ((!pieceMoved && deltaY > 2) || (pieceMoved && deltaY > 1)) return false; // Can't move forward far
            return deltaY > 0; // Can't move backward
        }
    }
    public boolean boardBoundary() { return clickX <= 7 && clickY <= 7 && clickX >= 0 && clickY >= 0; }
    public boolean noObstruction() {
        if (name.equalsIgnoreCase("knight")) return true;
        int currentX = this.pX + Integer.signum(this.deltaX);
        int currentY = this.pY + Integer.signum(this.deltaY);
        while (Math.abs(currentX - clickX) > 0 || Math.abs(currentY - clickY) > 0) {
            if (Board.getPiece(currentX * SQR_SIZE, currentY * SQR_SIZE) != null) return false;
            currentX += Integer.signum(this.deltaX);
            currentY += Integer.signum(this.deltaY);
        }
        return true; // No obstruction found
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