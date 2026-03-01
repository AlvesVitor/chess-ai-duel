package br.com.chessapp;

public class TournamentUI {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String WHITE = "\u001B[37m";

    public static void printBracket(String[] players) {
        UI.clearScreen();
        System.out.println(YELLOW + BOLD);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║      🏆  CAMPEONATO DE XADREZ DE IAs  🏆         ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);

        System.out.println(CYAN + "  🎲 SORTEIO DE CONFRONTOS:" + RESET);
        System.out.println();
        System.out.println("        SEMIFINAL 1              SEMIFINAL 2");
        System.out.println();

        String icon0 = Main.AI_ICON.getOrDefault(players[0], "🤖");
        String icon1 = Main.AI_ICON.getOrDefault(players[1], "🤖");
        String icon2 = Main.AI_ICON.getOrDefault(players[2], "🤖");
        String icon3 = Main.AI_ICON.getOrDefault(players[3], "🤖");

        System.out.println("  " + icon0 + " " + padR(players[0], 8) + " ──┐        ┌── " + icon2 + " " + players[2]);
        System.out.println("                ├── VS ──┤");
        System.out.println("  " + icon1 + " " + padR(players[1], 8) + " ──┘        └── " + icon3 + " " + players[3]);
        System.out.println();
        System.out.println("              ↓   FINAL   ↓");
        System.out.println();
        System.out.println(WHITE + "  (partida começa em breve...)" + RESET);
    }

    public static void printPhaseHeader(String phaseName, int gameNumber) {
        UI.clearScreen();
        System.out.println(YELLOW + BOLD);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.printf("║   %-48s║%n", "  " + phaseName + (gameNumber > 0 ? "  Jogo " + gameNumber : ""));
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    public static void printMatchHeader(String whiteAI, String blackAI, String label) {
        String iconW = Main.AI_ICON.getOrDefault(whiteAI, "🤖");
        String iconB = Main.AI_ICON.getOrDefault(blackAI, "🤖");
        System.out.println(BOLD + CYAN);
        System.out.println("┌───────────────────────────────────────────────────┐");
        System.out.printf("│  %-49s│%n", "  📋 " + label);
        System.out.printf("│  %-49s│%n", "  " + iconW + " " + whiteAI + " (brancas)  vs  " + iconB + " " + blackAI + " (pretas)");
        System.out.println("└───────────────────────────────────────────────────┘");
        System.out.println(RESET);
    }

    public static void printMatchResult(String winner, String loser) {
        String iconW = Main.AI_ICON.getOrDefault(winner, "🤖");
        System.out.println(GREEN + BOLD);
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.printf("║  %-49s║%n", "  🏆 VENCEDOR: " + iconW + " " + winner + "!");
        System.out.printf("║  %-49s║%n", "  ❌ Eliminado: " + loser);
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    public static void printPodium(String champion, String runnerUp, String third, String fourth) {
        UI.clearScreen();
        String ic1 = Main.AI_ICON.getOrDefault(champion, "🤖");
        String ic2 = Main.AI_ICON.getOrDefault(runnerUp, "🤖");
        String ic3 = Main.AI_ICON.getOrDefault(third, "🤖");
        String ic4 = Main.AI_ICON.getOrDefault(fourth, "🤖");

        System.out.println(YELLOW + BOLD);
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║      🏆  RESULTADO FINAL DO CAMPEONATO  🏆       ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.printf("║  %-49s║%n", "  🥇 1º LUGAR — " + ic1 + " " + champion + "   ← CAMPEÃO!");
        System.out.printf("║  %-49s║%n", "  🥈 2º LUGAR — " + ic2 + " " + runnerUp);
        System.out.printf("║  %-49s║%n", "  🥉 3º LUGAR — " + ic3 + " " + third);
        System.out.printf("║  %-49s║%n", "  4️⃣  4º LUGAR — " + ic4 + " " + fourth);
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private static String padR(String s, int len) {
        if (s.length() >= len) return s;
        return s + " ".repeat(len - s.length());
    }
}