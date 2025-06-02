package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private Board board;
    private int turn;
    private Color currentPlayer;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

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

    public ChessPiece getPromoted() {
        return promoted;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck() {
        return check;
    }
    public boolean getCheckMate() {
        return checkMate;
    }
    public ChessPiece getEnPassantVulnerable() {
        return enPassantVulnerable;
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
        ChessPiece movedPiece = (ChessPiece) board.piece(targetPosition);

        // Promotion
        promoted = null;
        if (movedPiece instanceof Pawn) {
            if ((movedPiece.getColor() == Color.WHITE && targetPosition.getRow() == 0) ||
                (movedPiece.getColor() == Color.BLACK && targetPosition.getRow() == 7)) {
                promoted = (ChessPiece) board.piece(targetPosition);
                promoted = replacePromotedPiece("Q"); // Default promotion to Queen

            }
        }

        check = (testCheck(getOpponentColor(currentPlayer))) ? true : false;

        if (testCheckMate(getOpponentColor(currentPlayer))) {
            checkMate = true;
        } else {
            nextTurn();
        }

        // Special move: en passant
        if (movedPiece instanceof Pawn &&
            (targetPosition.getRow() == sourcePosition.getRow() - 2 || targetPosition.getRow() == sourcePosition.getRow() + 2)) {
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null; // Reset en passant vulnerability
        }

        return (ChessPiece) capturedPiece;
    }

    private Piece makeMove(Position sourcePosition, Position targetPosition) {
        ChessPiece p = (ChessPiece) board.removePiece(sourcePosition);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(targetPosition);
        board.placePiece(p, targetPosition);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add((ChessPiece) capturedPiece);
        }

        // Special move: castling
        if (p instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2) {
            Position rookSourcePosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            Position rookTargetPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(rookSourcePosition);
            rook.increaseMoveCount();
            board.placePiece(rook, rookTargetPosition);
        } else if (p instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2) {
            Position rookSourcePosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            Position rookTargetPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(rookSourcePosition);
            rook.increaseMoveCount();
            board.placePiece(rook, rookTargetPosition);
        }

        // Special move: en passant
        if (p instanceof Pawn) {
            if (sourcePosition.getColumn() != targetPosition.getColumn() && capturedPiece == null) {
                Position pawnPosition;
                if (p.getColor() == Color.WHITE) {
                    pawnPosition = new Position(targetPosition.getRow() + 1, targetPosition.getColumn());
                } else {
                    pawnPosition = new Position(targetPosition.getRow() - 1, targetPosition.getColumn());
                }
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add((ChessPiece) capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }

        return capturedPiece;
    }

    private void undoMove(Position sourcePosition, Position targetPosition, Piece capturedPiece) {
        ChessPiece p = (ChessPiece)board.removePiece(targetPosition);
        p.decreaseMoveCount();
        board.placePiece(p, sourcePosition);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, targetPosition);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add((ChessPiece) capturedPiece);
        }

        // Special move: castling
        if (p instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() + 2) {
            Position rookSourcePosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 1);
            Position rookTargetPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() + 3);
            ChessPiece rook = (ChessPiece) board.removePiece(rookSourcePosition);
            rook.decreaseMoveCount();
            board.placePiece(rook, rookTargetPosition);
        } else if (p instanceof King && targetPosition.getColumn() == sourcePosition.getColumn() - 2) {
            Position rookSourcePosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 1);
            Position rookTargetPosition = new Position(sourcePosition.getRow(), sourcePosition.getColumn() - 4);
            ChessPiece rook = (ChessPiece) board.removePiece(rookSourcePosition);
            rook.decreaseMoveCount();
            board.placePiece(rook, rookTargetPosition);
        }

        // Special move: en passant
        if (p instanceof Pawn) {
            if (sourcePosition.getColumn() != targetPosition.getColumn() && capturedPiece == null) {
                ChessPiece pawn = (ChessPiece) board.removePiece(targetPosition);
                Position pawnPosition;
                if (p.getColor() == Color.WHITE) {
                    pawnPosition = new Position(3, targetPosition.getColumn());
                } else {
                    pawnPosition = new Position(4, targetPosition.getColumn());
                }
                board.placePiece(pawn, pawnPosition);

            }
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
        List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == getOpponentColor(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
                return true;
            }
        }
        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) {
            return false; // Not in check, so not checkmate
        }
        List<Piece> list = piecesOnTheBoard.stream().filter(x ->((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (mat[i][j]) {
                        Position sourcePosition = ((ChessPiece)p).getChessPosition().toPosition();
                        Position targetPosition = new Position(i, j);
                        Piece capturedPiece = makeMove(sourcePosition, targetPosition);
                        boolean isCheck = testCheck(color);
                        undoMove(sourcePosition, targetPosition, capturedPiece);
                        if (!isCheck) {
                            return false; // Found a valid move that does not result in check
                        }
                    }


                }
            }
        }
        return true; // No valid moves, so it's checkmate
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    public ChessPiece replacePromotedPiece(String type) {
        if (promoted == null) {
            throw new ChessException("There is no piece to be promoted.");
        }

        if (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
            return promoted; // Default to Queen if invalid type
        }
        Position pos = promoted.getChessPosition().toPosition();
        Piece p =board.removePiece(pos);
        piecesOnTheBoard.remove(p);

        ChessPiece newPiece = newPiece(type, promoted.getColor());
        board.placePiece(newPiece, pos);
        piecesOnTheBoard.add(newPiece);

        return newPiece;

    }

    private ChessPiece newPiece(String type, Color color) {
        if (type.equals("B")) return new Bishop(board, color);
        if (type.equals("N")) return new Knight(board, color);
        if (type.equals("R")) return new Rook(board, color);
        if (type.equals("Q")) return new Queen(board, color);
        return new Queen(board, color);
    }

    private void initialSetup() {
        // Initial setup of the chess pieces on the board
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));


    }


}
