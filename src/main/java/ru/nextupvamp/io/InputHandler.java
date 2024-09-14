package ru.nextupvamp.io;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class InputHandler {
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
        int category = 0;

        printStream.print("Введите номер категории: ");
        while (category <= 0 || category > categories.size()) {
            String input = scanner.nextLine();
            if (input == null || input.isEmpty()) {
                category = random.nextInt(1, categories.size() + 1);
                break;
            }
            try {
                category = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printStream.println("Введите число");
                continue;
            }
            if (category <= 0 || category > categories.size()) {
                printStream.println("Введите корректное значение");
            }
        }

        return categories.get(category - 1);
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    public int inputDifficulty() {
        int difficulty = 0;
        printStream.print("Введите уровень сложности: ");
        while (difficulty <= 0 || difficulty > 3) {
            String input = scanner.nextLine();
            if (input == null || input.isEmpty()) {
                difficulty = random.nextInt(1, 4);
                break;
            }
            try {
                difficulty = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printStream.println("Введите число");
                continue;
            }
            if (difficulty <= 0 || difficulty > 3) {
                printStream.println("Введите корректное значение");
            }
        }

        return difficulty;
    }

    public char inputLetter(Set<Character> enteredLetters) {
        String input;
        char letter = '\u0000';
        boolean correct = false;
        printStream.println("Введите букву (? - подсказка): ");
        do {
            input = scanner.nextLine();
            if ("?".equals(input)) {
                return '?';
            }
            if (input.length() != 1) {
                printStream.println("Введите одну букву");
                continue;
            }

            letter = Character.toUpperCase(input.charAt(0));
            if (letter != 'Ё' && letter < 'А' || letter > 'Я') {
                printStream.println("Введите букву русского алфавита");
                continue;
            }
            if (letter == 'Ё') {
                letter = 'Е';
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
        printStream.println("Продолжить игру? (y/n): ");
        while (true) {
            String input = scanner.nextLine();
            if ("y".equalsIgnoreCase(input)) {
                return true;
            } else if ("n".equalsIgnoreCase(input)) {
                return false;
            } else {
                printStream.println("Введите корректное значение");
            }
        }
    }
}
