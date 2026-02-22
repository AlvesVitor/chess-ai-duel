package br.com.chessapp.boardgame;

public abstract class Piece {

    protected Position position;
    private Board board;

    public Piece(Board board) {
        this.board = board;
        position = null;
    }

    protected Board getBoard() {
        return board;
    }

    public abstract boolean[][] possibleMoves();

    public boolean possibleMovie(Position position) {
        return possibleMoves()[position.getRow()][position.getComlumn()];
    }

    public boolean isThereAnyPossibleMove() {
        boolean[][] mat = possibleMoves();

        for (boolean[] booleans : mat) {

            for (int y = 0; y < mat.length; y++) {
                if (booleans[y]) {
                    return true;
                }
            }
        }
        return false;
    }

}