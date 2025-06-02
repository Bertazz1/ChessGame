package chess.pieces;

import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;
import boardgame.Board;

public class King extends ChessPiece {

    private ChessMatch chessMatch;

    public King(Board board ,Color color,ChessMatch chessMatch) {
        super(board, color);
        this.chessMatch = chessMatch;
    }


    @Override
    public String toString() {
        return "K";
    }

    private boolean canMove(int row, int column) {
       ChessPiece p = (ChessPiece) getBoard().piece(new Position(row, column));
       return p == null || p.getColor() != getColor();
    }
    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        // above
        p.setValues(position.getRow() - 1, position.getColumn());
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        // below
        p.setValues(position.getRow() + 1, position.getColumn());
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        // left
        p.setValues(position.getRow(), position.getColumn() - 1);
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        // right
        p.setValues(position.getRow(), position.getColumn() + 1);
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        // northwest
        p.setValues(position.getRow() - 1, position.getColumn() - 1);
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        // northeast
        p.setValues(position.getRow() - 1, position.getColumn() + 1);
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        // southwest
        p.setValues(position.getRow() + 1, position.getColumn() - 1);
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }
        // southeast
        p.setValues(position.getRow() + 1, position.getColumn() + 1);
        if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
            mat[p.getRow()][p.getColumn()] = true;
        }

        // Castling
        if (getMoveCount() == 0 && !chessMatch.getCheck()) {
            // Kingside castling
            Position rookPosition = new Position(position.getRow(), position.getColumn() + 3);
            if (testRookCastling(rookPosition)) {
                Position p1 = new Position(position.getRow(), position.getColumn() + 1);
                Position p2 = new Position(position.getRow(), position.getColumn() + 2);
                if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2)) {
                    mat[position.getRow()][position.getColumn() + 2] = true;
                }
            }
            // Queenside castling
            rookPosition.setValues(position.getRow(), position.getColumn() - 4);
            if (testRookCastling(rookPosition)) {
                Position p1 = new Position(position.getRow(), position.getColumn() - 1);
                Position p2 = new Position(position.getRow(), position.getColumn() - 2);
                Position p3 = new Position(position.getRow(), position.getColumn() - 3);
                if (!getBoard().thereIsAPiece(p1) && !getBoard().thereIsAPiece(p2) && !getBoard().thereIsAPiece(p3)) {
                    mat[position.getRow()][position.getColumn() - 2] = true;
                }
            }
        }
        return mat;
    }

    private boolean testRookCastling(Position p) {
        ChessPiece piece = (ChessPiece) getBoard().piece(p);
        return piece != null && piece instanceof Rook && piece.getColor() == getColor() && piece.getMoveCount() == 0;
    }
}

