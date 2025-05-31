package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

    private Board board;

    public ChessMatch() {
        this.board = new Board(8, 8);
        initialSetup();
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
        Piece capturedPiece = makeMove(sourcePosition, targetPosition);
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
