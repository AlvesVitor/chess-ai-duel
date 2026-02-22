package br.com.chessapp;

import br.com.chessapp.chess.ChessMatch;
import br.com.chessapp.chess.ChessPiece;
import br.com.chessapp.chess.ChessPosition;
import br.com.chessapp.chess.Color;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UI {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String[] numbers = {"➊", "➋", "➌", "➍", "➎", "➏", "➐", "➑"};
    public static final String letters = "  🅰 🅱 🅲 🅳 🅴 🅵 🅶 🅷";

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static ChessPosition readChessPosition(Scanner sc) {
        try {
            String s = sc.nextLine();
            char column = s.charAt(0);
            int row = Integer.parseInt(s.substring(1));

            return new ChessPosition(column, row);
        } catch (RuntimeException e) {
            throw new InputMismatchException("Erro na leitura da posição. Valores válidos são de a1 até h8.");
        }

    }

    public static void printMatch(ChessMatch chessMatch, List<ChessPiece> captured) {
        printBoard(chessMatch.getPieces());
        System.out.println(" ");
        printCapturedPieces(captured);
        System.out.println("⚡ Turno : " + chessMatch.getTurn());

        if (!chessMatch.getCheckMate()) {
            if (chessMatch.getCurrentPlayer() == Color.BLACK) {
                System.out.println("🤖 Vez de : ♟ CLAUDE (Pretas)");
            } else {
                System.out.println("🤖 Vez de : ♙ GPT (Brancas)");
            }

            if (chessMatch.getCheck()) {
                System.out.println("⚠️  CHECK! O rei está em perigo!");
            }

        } else {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║        ♟ CHEQUE-MATE! ♟       ║");
            System.out.println("╚══════════════════════════════╝");
            if (chessMatch.getCurrentPlayer() == Color.BLACK) {
                System.out.println("🏆 Vencedor: ♙ GPT (Brancas)!");
            } else {
                System.out.println("🏆 Vencedor: ♟ CLAUDE (Pretas)!");
            }
        }
    }

    public static void printBoard(ChessPiece[][] pieces) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print(ANSI_RED + numbers[7 - i] + " " + ANSI_RESET);
            if (i % 2 == 0) {
                for (int x = 0; x < pieces.length; x++) {
                    printPiece(pieces[i][x], false, x);
                }

            } else {
                for (int x = 0; x < pieces.length; x++) {
                    printPiece(pieces[i][x], false, x + 1);
                }
            }
            System.out.println();
        }

        System.out.println(ANSI_RED + letters + ANSI_RESET);

    }

    public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves, Color color) {
        for (int i = 0; i < pieces.length; i++) {
            System.out.print(ANSI_RED + numbers[7 - i] + " " + ANSI_RESET);
            if (i % 2 == 0) {
                for (int x = 0; x < pieces.length; x++) {
                    printPiece(pieces[i][x], possibleMoves[i][x], x, color);
                }
            } else {

                for (int x = 0; x < pieces.length; x++) {
                    printPiece(pieces[i][x], possibleMoves[i][x], x + 1, color);
                }
            }
            System.out.println();
        }

        System.out.println(ANSI_RED + letters + ANSI_RESET);

    }

    private static void printPiece(ChessPiece piece, boolean background, int x, Color cor) {

        if (piece == null) {
            if (background) {
                System.out.print(ANSI_GREEN + "■" + ANSI_RESET);
            } else {
                if (x % 2 == 0) {
                    System.out.print(ANSI_WHITE + "■" + ANSI_RESET);
                } else {
                    System.out.print(ANSI_BLACK + "■" + ANSI_RESET);
                }
            }
        } else {
            if (cor != piece.getColor() && background) {
                System.out.print(ANSI_GREEN + piece + ANSI_RESET);
            } else {

                if (piece.getColor() == Color.WHITE) {
                    System.out.print(ANSI_WHITE + piece + ANSI_RESET);
                } else {
                    System.out.print(ANSI_BLACK + piece + ANSI_RESET);
                }
            }

        }
        System.out.print(" ");
    }

    private static void printPiece(ChessPiece piece, boolean background, int x) {

        if (piece == null) {
            if (background) {
                System.out.print(ANSI_GREEN + "■" + ANSI_RESET);
            } else {
                if (x % 2 == 0) {
                    System.out.print(ANSI_WHITE + "■" + ANSI_RESET);
                } else {
                    System.out.print(ANSI_BLACK + "■" + ANSI_RESET);
                }
            }
        } else {

            if (piece.getColor() == Color.WHITE) {
                System.out.print(ANSI_WHITE + piece + ANSI_RESET);
            } else {
                System.out.print(ANSI_BLACK + piece + ANSI_RESET);
            }

        }
        System.out.print(" ");
    }

    private static void printCapturedPieces(List<ChessPiece> captured) {
        List<ChessPiece> white = captured.parallelStream().filter(x -> x.getColor() == Color.WHITE)
                .collect(Collectors.toList());
        List<ChessPiece> black = captured.parallelStream().filter(x -> x.getColor() == Color.BLACK)
                .collect(Collectors.toList());

        System.out.println("⚔️  Baixas da batalha:");
        System.out.print(ANSI_WHITE + "  ♙ GPT capturou    : [ ");
        for (ChessPiece p : black) System.out.print(p + " ");
        System.out.println("]" + ANSI_RESET);

        System.out.print(ANSI_BLACK + "  ♟ CLAUDE capturou : [ ");
        for (ChessPiece p : white) System.out.print(p + " ");
        System.out.println("]" + ANSI_RESET);
        System.out.println();
    }
}
