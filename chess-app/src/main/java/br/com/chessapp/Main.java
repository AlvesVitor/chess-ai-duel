package br.com.chessapp;

import br.com.chessapp.chess.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
    private static final ChessAIClient aiClient = new ChessAIClient();

    static final String[] ALL_AIS = {"GPT", "CLAUDE", "GROK", "GEMINI"};
    static final Map<String, String> AI_ICON = Map.of(
            "GPT", "🟢",
            "CLAUDE", "🟠",
            "GROK", "🔵",
            "GEMINI", "🟣"
    );

    static String champion = null;
    static String runnerUp = null;
    static String third = null;
    static String fourth = null;

    public static void main(String[] args) throws Exception {
        String[] players = ALL_AIS.clone();
        shuffleArray(players);

        TournamentUI.printBracket(players);
        Thread.sleep(4000);

        TournamentUI.printPhaseHeader("⚔️  SEMIFINAIS", 1);
        String semi1Winner = playMatch(players[0], players[1], "Semifinal 1");
        String semi1Loser = semi1Winner.equals(players[0]) ? players[1] : players[0];

        TournamentUI.printPhaseHeader("⚔️  SEMIFINAIS", 2);
        String semi2Winner = playMatch(players[2], players[3], "Semifinal 2");
        String semi2Loser = semi2Winner.equals(players[2]) ? players[3] : players[2];

        TournamentUI.printPhaseHeader("🥉  DISPUTA DE 3º LUGAR", 0);
        String thirdPlace = playMatch(semi1Loser, semi2Loser, "Disputa 3º Lugar");
        fourth = thirdPlace.equals(semi1Loser) ? semi2Loser : semi1Loser;
        third = thirdPlace;

        TournamentUI.printPhaseHeader("🏆  GRANDE FINAL", 0);
        champion = playMatch(semi1Winner, semi2Winner, "Final");
        runnerUp = champion.equals(semi1Winner) ? semi2Winner : semi1Winner;

        TournamentUI.printPodium(champion, runnerUp, third, fourth);
    }

    private static String playMatch(String whiteAI, String blackAI, String matchLabel) throws Exception {
        ChessMatch chessMatch = new ChessMatch();
        List<ChessPiece> captured = new ArrayList<>();
        List<String> moveHistory = new ArrayList<>();

        TournamentUI.printMatchHeader(whiteAI, blackAI, matchLabel);
        Thread.sleep(2000);

        while (!chessMatch.getCheckMate()) {
            UI.clearScreen();
            TournamentUI.printMatchHeader(whiteAI, blackAI, matchLabel);
            UI.printMatch(chessMatch, captured, whiteAI, blackAI);

            String currentAI = chessMatch.getCurrentPlayer() == Color.WHITE ? whiteAI : blackAI;
            String color = chessMatch.getCurrentPlayer() == Color.WHITE ? "brancas" : "pretas";
            String icon = AI_ICON.getOrDefault(currentAI, "🤖");

            System.out.println("\n───────────────────────────────────────────");
            System.out.println("   " + icon + " " + currentAI + " (" + color + ") está calculando...");
            System.out.println("───────────────────────────────────────────");

            boolean moveMade = false;
            int attempts = 0;

            while (!moveMade && attempts < 5) {
                try {
                    String board = boardToString(chessMatch.getPieces());
                    List<String> valid = getValidMoves(chessMatch);
                    boolean inCheck = chessMatch.getCheck();
                    String[] move = aiClient.getMove(board, color, currentAI, moveHistory, valid, inCheck);

                    ChessPosition source = parsePosition(move[0]);
                    ChessPosition target = parsePosition(move[1]);
                    ChessPiece captured2 = chessMatch.performChessMove(source, target);

                    if (captured2 != null) captured.add(captured2);

                    UI.clearScreen();
                    TournamentUI.printMatchHeader(whiteAI, blackAI, matchLabel);
                    UI.printMatch(chessMatch, captured, whiteAI, blackAI);

                    if (captured2 != null)
                        System.out.println("⚔️  " + icon + " " + currentAI + " capturou: " + captured2 + " do adversário!");

                    System.out.println("✅ " + currentAI + " (" + color + ") jogou: " + move[0] + " → " + move[1]);
                    moveHistory.add(color + ": " + move[0] + " " + move[1]);
                    Thread.sleep(2500);
                    moveMade = true;

                } catch (ChessException e) {
                    attempts++;
                    System.out.println("⚠️  Jogada inválida — tentativa " + attempts + "/5: " + e.getMessage());
                    Thread.sleep(500);
                } catch (Exception e) {
                    attempts++;
                    System.out.println("❌ Falha na API de " + currentAI + ": " + e.getMessage());
                    Thread.sleep(1000);
                }
            }

            if (!moveMade) {
                String loser = currentAI;
                String winner = currentAI.equals(whiteAI) ? blackAI : whiteAI;
                System.out.println("💀 " + loser + " não conseguiu mover — " + winner + " vence por W.O.!");
                Thread.sleep(3000);
                return winner;
            }
        }

        String loser = chessMatch.getCurrentPlayer() == Color.WHITE ? whiteAI : blackAI;
        String winner = loser.equals(whiteAI) ? blackAI : whiteAI;

        UI.clearScreen();
        TournamentUI.printMatchHeader(whiteAI, blackAI, matchLabel);
        UI.printMatch(chessMatch, captured, whiteAI, blackAI);
        TournamentUI.printMatchResult(winner, loser);
        Thread.sleep(5000);
        return winner;
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
                                    char tc = (char) ('a' + mj);
                                    int tr = 8 - mi;
                                    validMoves.add(column + "" + row + " " + tc + tr);
                                }
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return validMoves;
    }

    private static ChessPosition parsePosition(String pos) {
        pos = pos.trim().toLowerCase();
        return new ChessPosition(pos.charAt(0), Integer.parseInt(String.valueOf(pos.charAt(1))));
    }

    private static String boardToString(ChessPiece[][] pieces) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] == null) sb.append(". ");
                else if (pieces[i][j].getColor() == Color.WHITE)
                    sb.append(pieceToLetter(pieces[i][j]).toUpperCase()).append(" ");
                else
                    sb.append(pieceToLetter(pieces[i][j]).toLowerCase()).append(" ");
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

    private static void shuffleArray(String[] arr) {
        Random rnd = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            String tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
        }
    }
}