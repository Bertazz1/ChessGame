package boardgame;

public class Piece {

    protected Position position;
    private Board board;

    public Piece(Board board) {
        this.board = board;
        this.position = null; // Position will be set later
    }

    public Board getBoard() {
        return board;
    }
}
