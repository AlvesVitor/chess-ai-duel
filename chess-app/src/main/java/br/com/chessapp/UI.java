package br.com.chessapp;

import br.com.chessapp.chess.ChessMatch;
import br.com.chessapp.chess.ChessPiece;
import br.com.chessapp.chess.Color;

import java.util.List;
import java.util.stream.Collectors;

public class UI {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String[] numbers = {"➊", "➋", "➌", "➍", "➎", "➏", "➐", "➑"};
    public static final String letters = "  🅰 🅱 🅲 🅳 🅴 🅵 🅶 🅷";

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printMatch(ChessMatch chessMatch, List<ChessPiece> captured,
                                  String whiteAI, String blackAI) {
        printBoard(chessMatch.getPieces());
        System.out.println(" ");
        printCapturedPieces(captured, whiteAI, blackAI);
        int turnoExibido = (chessMatch.getTurn() + 1) / 2;
        System.out.println("⚡ Turno : " + turnoExibido);
        System.out.println("\n");

        String iconW = Main.AI_ICON.getOrDefault(whiteAI, "🤖");
        String iconB = Main.AI_ICON.getOrDefault(blackAI, "🤖");

        if (!chessMatch.getCheckMate()) {
            if (chessMatch.getCurrentPlayer() == Color.BLACK) {
                System.out.println(iconB + " Vez de: ♟ " + blackAI + " (Pretas)");
            } else {
                System.out.println(iconW + " Vez de: ♟ " + whiteAI + " (Brancas)");
            }
            if (chessMatch.getCheck()) {
                System.out.println("⚠️  XEQUE! O rei está em perigo!");
            }
        } else {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║       ♟ XEQUE-MATE! ♟        ║");
            System.out.println("╚══════════════════════════════╝");
            if (chessMatch.getCurrentPlayer() == Color.WHITE) {
                System.out.println("🏆 Vencedor: ♟ " + blackAI + " (Pretas)!");
            } else {
                System.out.println("🏆 Vencedor: ♙ " + whiteAI + " (Brancas)!");
            }
        }
    }

    public static void printBoard(ChessPiece[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print(ANSI_RED + numbers[7 - i] + " " + ANSI_RESET);
            for (int x = 0; x < pieces.length; x++) {
                printPiece(pieces[i][x], false, (i % 2 == 0) ? x : x + 1);
            }
            System.out.println();
        }
        System.out.println(ANSI_RED + letters + ANSI_RESET);
    }

    private static void printPiece(ChessPiece piece, boolean background, int x) {
        if (piece == null) {
            System.out.print(background ? ANSI_GREEN + "■" + ANSI_RESET
                    : (x % 2 == 0 ? ANSI_WHITE : ANSI_BLACK) + "■" + ANSI_RESET);
        } else {
            System.out.print((piece.getColor() == Color.WHITE ? ANSI_WHITE : ANSI_BLACK) + piece + ANSI_RESET);
        }
        System.out.print(" ");
    }

    private static void printCapturedPieces(List<ChessPiece> captured, String whiteAI, String blackAI) {
        List<ChessPiece> white = captured.parallelStream()
                .filter(x -> x.getColor() == Color.WHITE).collect(Collectors.toList());
        List<ChessPiece> black = captured.parallelStream()
                .filter(x -> x.getColor() == Color.BLACK).collect(Collectors.toList());

        System.out.println("⚔️  Baixas da batalha:");
        System.out.print(ANSI_WHITE + "  ♟ " + whiteAI + " capturou : [ ");
        for (ChessPiece p : black) System.out.print(ANSI_BLACK + p + ANSI_WHITE + " ");
        System.out.println("]" + ANSI_RESET);

        System.out.print(ANSI_BLACK + "  ♟ " + blackAI + " capturou : [ ");
        for (ChessPiece p : white) System.out.print(ANSI_WHITE + p + ANSI_BLACK + " ");
        System.out.println("]" + ANSI_RESET);
        System.out.println();
    }
}