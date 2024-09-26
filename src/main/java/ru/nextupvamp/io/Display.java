package ru.nextupvamp.io;

import ru.nextupvamp.game.Game;
import ru.nextupvamp.words.difficulties.Difficulty;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Display {
    public static final int DRAW_STAGES = 6; // do not change
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    private static final int GAME_START = 6;
    private static final int ONE_MISTAKE = 5;
    private static final int TWO_MISTAKES = 4;
    private static final int THREE_MISTAKES = 3;
    private static final int FOUR_MISTAKES = 2;
    private static final int FIVE_MISTAKES = 1;
    private static final int GAME_END = 0;
    private static final char FIRST_RUSSIAN_LETTER = 'А';
    private static final char LAST_RUSSIAN_LETTER = 'Я';
    private static final int RUSSIAN_ALPHABET_LENGTH = 33;
    private static final int ROWS_IN_ALPHABET_DISPLAY = 3;

    private final PrintStream printStream;

    public Display() {
        printStream = System.out;
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public void displayHeader() {
        printStream.print(ANSI_RED);
        printStream.println("----------------");
        printStream.println("The Hangman Game");
        printStream.println("----------------");
        printStream.print(ANSI_RESET);

    }

    public void displayCategories(List<String> categories) {
        printStream.println("Категории:");
        for (int i = 0; i != categories.size(); ++i) {
            printStream.printf("%d. %s%n", i + 1, categories.get(i));
        }
    }

    public void displayDifficulties() {
        printStream.println("Уровни сложности:");
        printStream.println("1 -- 2 -- 3");
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public void displayAlphabet(Set<Character> enteredLetters) {
        StringBuilder stringBuilder = new StringBuilder();
        int lettersInRow = RUSSIAN_ALPHABET_LENGTH / ROWS_IN_ALPHABET_DISPLAY;
        for (char i = FIRST_RUSSIAN_LETTER, j = 1; i <= LAST_RUSSIAN_LETTER; ++i, ++j) {
            if (enteredLetters.contains(i)) {
                stringBuilder.append('_');
            } else {
                stringBuilder.append(i);
            }
            if (j % lettersInRow == 0) {
                stringBuilder.append('\n');
            } else {
                stringBuilder.append(' ');
            }
        }
        printStream.println(stringBuilder);
    }

    public void displayGameInfo(
            String category,
            Difficulty difficulty,
            int lives,
            String word,
            String hint,
            boolean showHint,
            Set<Character> guessedLetters
    ) {
        printStream.println("Категория: " + category + ", сложность: " + difficulty.getName());
        printStream.println("Осталось попыток: " + lives);
        if (hint != null) {
            printStream.println("Подсказка: " + (showHint ? hint : ""));
        } else {
            printStream.println("У слова нет подсказки");
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i != word.length(); ++i) {
            if (guessedLetters.contains(word.charAt(i))) {
                stringBuilder.append(Character.toUpperCase(word.charAt(i))).append(' ');
            } else {
                stringBuilder.append("_ ");
            }
        }
        printStream.println("Слово: " + stringBuilder);
    }

    /**
     * Displays the hangman depiction.
     * If the number of attempts distinct from 6
     * the method will skip some draw stages or
     * draw the same stages multiple times.
     *
     * @param lives the number of remaining lives
     */
    @SuppressWarnings({"ConstantConditions", "checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
    public void displayHangman(int lives) {
        int stage;
        if (Game.ATTEMPTS < DRAW_STAGES) {
            if (lives == Game.ATTEMPTS) {
                stage = DRAW_STAGES;
            } else {
                stage = lives * DRAW_STAGES / Game.ATTEMPTS;
            }
        } else if (Game.ATTEMPTS > DRAW_STAGES) {
            stage = (int) Math.round(lives * DRAW_STAGES * 1.0 / Game.ATTEMPTS);
        } else {
            stage = lives;
        }
        switch (stage) {
            case GAME_START:
                printStream.println("  +---+");
                printStream.println("  |   |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("=========");
                break;
            case ONE_MISTAKE:
                printStream.println("  +---+");
                printStream.println("  |   |");
                printStream.println("  O   |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("=========");
                break;
            case TWO_MISTAKES:
                printStream.println("  +---+");
                printStream.println("  |   |");
                printStream.println("  O   |");
                printStream.println("  |   |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("=========");
                break;
            case THREE_MISTAKES:
                printStream.println("  +---+");
                printStream.println("  |   |");
                printStream.println("  O   |");
                printStream.println(" \\|   |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("=========");
                break;
            case FOUR_MISTAKES:
                printStream.println("  +---+");
                printStream.println("  |   |");
                printStream.println("  O   |");
                printStream.println(" \\|/  |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("      |");
                printStream.println("=========");
                break;
            case FIVE_MISTAKES:
                printStream.println("  +---+");
                printStream.println("  |   |");
                printStream.println("  O   |");
                printStream.println(" \\|/  |");
                printStream.println("  |   |");
                printStream.println(" /    |");
                printStream.println("      |");
                printStream.println("=========");
                break;
            case GAME_END:
                printStream.println("  +---+");
                printStream.println("  |   |");
                printStream.println("  O   |");
                printStream.println(" \\|/  |");
                printStream.println("  |   |");
                printStream.println(" / \\  |");
                printStream.println("      |");
                printStream.println("=========");
                break;
            default: // actually this state is impossible for 1 < attempts < 100
                throw new RuntimeException("Неизвестная стадия отрисовки");
        }
    }

    public void displayGameResult(boolean win, String word) {
        if (win) {
            printStream.print(ANSI_GREEN);
            printStream.println("Вы угадали!");
            printStream.print(ANSI_RESET);
        } else {
            printStream.println(ANSI_RED + "Вы проиграли." + ANSI_RESET + " Это было слово " + word);
        }
    }
}
