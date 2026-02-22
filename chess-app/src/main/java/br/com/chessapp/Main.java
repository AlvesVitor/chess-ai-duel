package br.com.chessapp;

import br.com.chessapp.chess.*;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final ChessAIClient aiClient = new ChessAIClient();

    public static void main(String[] args) throws Exception {

        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();
        List<String> moveHistory = new ArrayList<>();

        while (!chessMatch.getCheckMate()) {

            UI.clearScreen();
            UI.printMatch(chessMatch, captured);

//            String currentModel = "GPT";
            String currentModel = chessMatch.getCurrentPlayer() == Color.WHITE ? "GPT" : "CLAUDE";
            String color = chessMatch.getCurrentPlayer() == Color.WHITE ? "brancas" : "pretas";
            String icon = chessMatch.getCurrentPlayer() == Color.WHITE ? "♙" : "♟";

            System.out.println("\n┌───────────────────────────────────────────");
            System.out.println("│  🧠 " + icon + " " + currentModel + " (" + color + ") está calculando...  ");
            System.out.println("└───────────────────────────────────────────");

            boolean moveMade = false;
            int attempts = 0;

            while (!moveMade && attempts < 5) {
                try {

                    String board = boardToString(chessMatch.getPieces());
                    List<String> validMoves = getValidMoves(chessMatch);
                    String[] move = aiClient.getMove(board, color, currentModel, moveHistory, validMoves);

                    ChessPosition source = parsePosition(move[0]);
                    ChessPosition target = parsePosition(move[1]);

                    ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

                    if (capturedPiece != null) {
                        captured.add(capturedPiece);
                    }

                    UI.clearScreen();
                    UI.printMatch(chessMatch, captured);

                    if (capturedPiece != null) {
                        System.out.println("⚔️  " + icon + " " + currentModel + " sacrificou: " + capturedPiece + " do adversário!");
                    }

                    System.out.println("✅ " + currentModel.toUpperCase() + " (" + color + ") jogou: " + move[0] + " → " + move[1]);
                    moveHistory.add(color + ": " + move[0] + " " + move[1]);
                    Thread.sleep(3000);
                    moveMade = true;

                } catch (ChessException e) {
                    attempts++;
                    System.out.println("⚠️  Jogada inválida de " + icon + " " + currentModel + " — tentativa " + attempts + "/5");
                    Thread.sleep(500);
                } catch (Exception e) {
                    attempts++;
                    System.out.println("❌ Falha na API de " + currentModel + ": " + e.getMessage() + " (tentativa " + attempts + "/5)");
                    Thread.sleep(1000);
                }
            }

            if (!moveMade) {
                System.out.println("💀 " + currentModel.toUpperCase() + " não conseguiu fazer um movimento válido após 5 tentativas.");
                break;
            }
        }

        UI.clearScreen();
        UI.printMatch(chessMatch, captured);

        if (chessMatch.getCheckMate()) {
            String winner = chessMatch.getCurrentPlayer() == Color.WHITE
                    ? "♟ CLAUDE (Pretas)"
                    : "♙ GPT (Brancas)";
            System.out.println("\n╔═══════════════════════════════════════════");
            System.out.println("║  🏆 VENCEDOR DA BATALHA: " + winner);
            System.out.println("╚═══════════════════════════════════════════");
        }
    }

    private static List<String> getValidMoves(ChessMatch chessMatch) {
        List<String> validMoves = new ArrayList<>();
        ChessPiece[][] pieces = chessMatch.getPieces();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = pieces[i][j];
                if (piece != null && piece.getColor() == chessMatch.getCurrentPlayer()) {
                    try {

                        char column = (char) ('a' + j);
                        int row = 8 - i;
                        ChessPosition source = new ChessPosition(column, row);

                        boolean[][] moves = chessMatch.possibleMoves(source);
                        for (int mi = 0; mi < 8; mi++) {
                            for (int mj = 0; mj < 8; mj++) {
                                if (moves[mi][mj]) {
                                    char targetColumn = (char) ('a' + mj);
                                    int targetRow = 8 - mi;
                                    validMoves.add(column + "" + row + " " + targetColumn + targetRow);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignore errors in parts that cannot move.
                    }
                }
            }
        }
        return validMoves;
    }

    private static ChessPosition parsePosition(String pos) {
        pos = pos.trim().toLowerCase();
        char column = pos.charAt(0);
        int row = Integer.parseInt(String.valueOf(pos.charAt(1)));
        return new ChessPosition(column, row);
    }

    private static String boardToString(ChessPiece[][] pieces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] == null) {
                    sb.append(".");
                } else if (pieces[i][j].getColor() == Color.WHITE) {
                    sb.append(pieceToLetter(pieces[i][j]).toUpperCase());
                } else {
                    sb.append(pieceToLetter(pieces[i][j]).toLowerCase());
                }
                sb.append(" ");
            }
            sb.append("\\n");
        }
        return sb.toString();
    }

    private static String pieceToLetter(ChessPiece piece) {
        switch (piece.getClass().getSimpleName()) {
            case "King":
                return "K";
            case "Queen":
                return "Q";
            case "Rook":
                return "R";
            case "Bishop":
                return "B";
            case "Knight":
                return "N";
            case "Pawn":
                return "P";
            default:
                return "?";
        }
    }
}