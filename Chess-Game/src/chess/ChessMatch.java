package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {

    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;

    private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
    private List <ChessPiece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8, 8);
        this.turn = 1;
        this.currentPlayer = Color.WHITE; // White starts the game
        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public ChessPiece [][] getPieces() {
        ChessPiece[][] pieces = new ChessPiece[board.getRows()][board.getColumns()];
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                pieces[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return pieces;
    }


    public ChessPiece performChessMove(ChessPosition source, ChessPosition target) {
        Position sourcePosition = source.toPosition();
        Position targetPosition = target.toPosition();
        validateSourcePosition(sourcePosition);
        validateTargetPosition(sourcePosition, targetPosition);
        Piece capturedPiece = makeMove(sourcePosition, targetPosition);

        if (testCheck(currentPlayer)) {
            undoMove(sourcePosition, targetPosition, capturedPiece);
            throw new ChessException("You cannot put yourself in check.");
        }
        check = testCheck(getOpponentColor(currentPlayer));
        nextTurn();
        return (ChessPiece)capturedPiece;

    }

    private Piece makeMove(Position sourcePosition, Position targetPosition) {
        Piece p = board.removePiece(sourcePosition);
        Piece capturedPiece = board.removePiece(targetPosition);
        board.placePiece(p, targetPosition);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add((ChessPiece) capturedPiece);
        }
        return capturedPiece;
    }

    private void undoMove(Position sourcePosition, Position targetPosition, Piece capturedPiece) {
        Piece p = board.removePiece(targetPosition);
        board.placePiece(p, sourcePosition);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, targetPosition);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add((ChessPiece) capturedPiece);
        }
    }

    public void validateSourcePosition(Position position){
        if (!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on source position " + position);
        }
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
            throw new ChessException("The chosen piece is not yours.");
        }
        if (!board.piece(position).isThereAnyPossibleMove()){
            throw new ChessException("There are no possible moves for the piece on source position " + position);
        }
    }

    public boolean[][] possibleMoves(ChessPosition source) {
        Position position = source.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public void validateTargetPosition(Position sourcePosition, Position targetPosition) {
        if (!board.piece(sourcePosition).possibleMove(targetPosition)) {
            throw new ChessException("The chosen piece cannot move to target position " + targetPosition);
        }
    }

    private Color getOpponentColor(Color currentPlayer) {
        return (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<ChessPiece> list = piecesOnTheBoard.stream()
                .filter(x -> x.getColor() == color)
                .filter(x -> x instanceof King)
                .toList();
        for (Piece p : list) {
            if (p instanceof  King)
            return (ChessPiece) p;
        }
        throw new IllegalStateException("There is no " + color + " king on the board.");
    }

    private boolean testCheck(Color color) {
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<ChessPiece> opponentPieces = piecesOnTheBoard.stream()
                .filter(x -> x.getColor() == getOpponentColor(color))
                .toList();
        for (ChessPiece piece : opponentPieces) {
            boolean[][] possibleMoves = piece.possibleMoves();
            if (possibleMoves[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true; // The king is in check
            }
        }
        return false; // The king is safe
    }


    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    private void initialSetup() {
        // Initial setup of the chess pieces on the board
        placeNewPiece('a', 8,new Rook(board, Color.WHITE));
        placeNewPiece('e' ,8 ,new Rook(board, Color.BLACK));
        placeNewPiece('a',6,new King(board, Color.WHITE));

    }


}
