package ru.nextupvamp.io;

import ru.nextupvamp.words.difficulties.Difficulty;
import ru.nextupvamp.words.difficulties.HighDifficulty;
import ru.nextupvamp.words.difficulties.LowDifficulty;
import ru.nextupvamp.words.difficulties.MediumDifficulty;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class InputHandler {
    public static final char PLAYER_NEED_HINT_SIGN = '?';

    private static final char DEFAULT_INITIALIZING_CHAR_VALUE = '\u0000';
    private static final String PLAYER_NEED_HINT_STRING = "?";
    private static final String PLAYER_AGREEMENT = "y";
    private static final String PLAYER_DISAGREEMENT = "n";
    private static final String INPUT_CORRECT_VALUE_REQUEST = "Введите корректное значение";
    private static final char FIRST_RUSSIAN_LETTER = 'А';
    private static final char LAST_RUSSIAN_LETTER = 'Я';
    private static final char PROBLEMATIC_RUSSIAN_LETTER = 'Ё';
    private static final char PROBLEMATIC_RUSSIAN_LETTER_SOLUTION = 'Е';

    private final Scanner scanner;
    private final PrintStream printStream;
    private final Random random = new SecureRandom();

    public InputHandler() {
        scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        printStream = System.out;
    }

    public InputHandler(InputStream inputStream, PrintStream printStream) {
        scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        this.printStream = printStream;
    }

    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public String inputCategory(List<String> categories) {
        printStream.print("Введите номер категории: ");
        int category = inputNumber(1, categories.size());

        return categories.get(category - 1);
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    public Difficulty inputDifficulty() {
        printStream.print("Введите уровень сложности: ");
        int difficulty = inputNumber(1, Difficulty.DIFFICULTIES);

        return switch (difficulty) {
            case 1 -> new LowDifficulty();
            case 2 -> new MediumDifficulty();
            case 3 -> new HighDifficulty();
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        };
    }

    @SuppressWarnings("SameParameterValue")
    private int inputNumber(int from, int to) {
        while (true) {
            String input = scanner.nextLine();
            if (input == null || input.isEmpty()) {
                return random.nextInt(from, to + 1);
            } else {
                Scanner intScanner = new Scanner(input);
                if (intScanner.hasNextInt()) {
                    int readInt = intScanner.nextInt();
                    if (readInt < from || readInt > to) {
                        printStream.println(INPUT_CORRECT_VALUE_REQUEST);
                    } else {
                        return readInt;
                    }
                } else {
                    printStream.println("Введите число");
                }
            }
        }
    }

    public char inputLetter(Set<Character> enteredLetters) {
        String input;
        char letter = DEFAULT_INITIALIZING_CHAR_VALUE;
        boolean correct = false;
        printStream.println("Введите букву (? - подсказка): ");
        do {
            input = scanner.nextLine();
            if (PLAYER_NEED_HINT_STRING.equals(input)) {
                return PLAYER_NEED_HINT_STRING.charAt(0);
            }
            if (input.length() != 1) {
                printStream.println("Введите одну букву");
                continue;
            }

            letter = Character.toUpperCase(input.charAt(0));
            if (letter != PROBLEMATIC_RUSSIAN_LETTER && letter < FIRST_RUSSIAN_LETTER || letter > LAST_RUSSIAN_LETTER) {
                printStream.println("Введите букву русского алфавита");
                continue;
            }
            if (letter == PROBLEMATIC_RUSSIAN_LETTER) {
                letter = PROBLEMATIC_RUSSIAN_LETTER_SOLUTION;
            }
            if (enteredLetters.contains(letter)) {
                printStream.println("Эта буква уже была введена. Введите другую");
                continue;
            }
            correct = true;
        } while (!correct);
        enteredLetters.add(letter);
        return letter;
    }

    public boolean inputContinue() {
        printStream.println("Продолжить игру? ("
                + PLAYER_AGREEMENT + "/"
                + PLAYER_DISAGREEMENT + "): ");
        while (true) {
            String input = scanner.nextLine();
            if (PLAYER_AGREEMENT.equalsIgnoreCase(input)) {
                return true;
            } else if (PLAYER_DISAGREEMENT.equalsIgnoreCase(input)) {
                return false;
            } else {
                printStream.println(INPUT_CORRECT_VALUE_REQUEST);
            }
        }
    }
}
