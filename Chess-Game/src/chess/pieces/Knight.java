package chess.pieces;

import boardgame.Position;
import chess.ChessPiece;
import chess.Color;
import boardgame.Board;

public class Knight extends ChessPiece {

    public Knight(Board board, Color color) {
        super(board, color);
    }

    @Override
    public String toString() {
        return "N";
    }

    private boolean canMove(int row, int column) {
        ChessPiece p = (ChessPiece) getBoard().piece(new Position(row, column));
        return p == null || p.getColor() != getColor();
    }

    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

        Position p = new Position(0, 0);

        // L-shaped moves
        int[][] moves = {
            {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
            {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
        };

        for (int[] move : moves) {
            p.setValues(position.getRow() + move[0], position.getColumn() + move[1]);
            if (getBoard().positionExists(p) && canMove(p.getRow(), p.getColumn())) {
                mat[p.getRow()][p.getColumn()] = true;
            }
        }

        return mat;
    }
}