package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

    private Board board;
    private int turn;
    private Color currentPlayer;

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
        nextTurn();
        return (ChessPiece)capturedPiece;

    }

    private Piece makeMove(Position sourcePosition, Position targetPosition) {
        Piece p = board.removePiece(sourcePosition);
        Piece capturedPiece = board.removePiece(targetPosition);
        board.placePiece(p, targetPosition);
        return capturedPiece;
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

        private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
    }

    private void initialSetup() {
        // Initial setup of the chess pieces on the board
        placeNewPiece('a', 8,new Rook(board, Color.WHITE));
        placeNewPiece('e' ,8 ,new Rook(board, Color.BLACK));
        placeNewPiece('a',6,new King(board, Color.WHITE));

    }


}
