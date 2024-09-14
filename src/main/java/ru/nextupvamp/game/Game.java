package ru.nextupvamp.game;

import ru.nextupvamp.io.Display;
import ru.nextupvamp.io.InputHandler;
import ru.nextupvamp.util.Pair;
import ru.nextupvamp.words.WordFinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Game {
    public static final int ATTEMPTS = 6;

    private String word;
    private String hint;
    private boolean showHint;
    private String category;
    private int difficulty;
    private int lives;
    private final Set<Character> wordLetters = new TreeSet<>();
    private final Set<Character> enteredLetters = new TreeSet<>();
    private final Set<Character> guessedLetters = new TreeSet<>();
    private final Display display;
    private final InputHandler inputHandler;
    private final WordFinder wordFinder;

    @SuppressWarnings("unused")
    public Game(InputHandler inputHandler, WordFinder wordFinder, Display display) {
        this.inputHandler = inputHandler;
        this.display = display;
        this.wordFinder = wordFinder;
    }

    @SuppressWarnings("unused")
    public Game(InputStream inputStream, PrintStream printStream) throws IOException {
        display = new Display(printStream);
        inputHandler = new InputHandler(inputStream, printStream);
        wordFinder = new WordFinder();
    }

    public Game() throws IOException {
        display = new Display();
        inputHandler = new InputHandler();
        wordFinder = new WordFinder();
    }

    public boolean startGame() throws IOException {
        initGame();

        lives = ATTEMPTS;
        boolean win = false;
        while (!win && lives > 0) {
            displayGame();
            char letter = inputHandler.inputLetter(enteredLetters);
            if (letter == '?') {
                showHint = true;
            } else if (wordLetters.contains(letter)) {
                guessedLetters.add(letter);
                win = checkIfWin();
            } else {
                --lives;
            }
        }

        displayGame();
        display.displayGameResult(win, word);

        return inputHandler.inputContinue();
    }

    public boolean checkIfWin() {
        for (int i = 0; i != word.length(); ++i) {
            if (!guessedLetters.contains(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void initGame() throws IOException {
        display.displayHeader();

        List<String> categories = wordFinder.getCategories();
        display.displayCategories(categories);
        category = inputHandler.inputCategory(categories);

        display.displayDifficulties();
        difficulty = inputHandler.inputDifficulty();

        Pair<String, String> wordAndHint = wordFinder.findWord(category, difficulty);
        word = wordAndHint.left();
        if (word == null || word.isEmpty()) {
            throw new IOException("Неверная длина слова");
        }
        hint = wordAndHint.right();

        wordLetters.clear();
        for (int i = 0; i != word.length(); ++i) {
            wordLetters.add(word.charAt(i));
        }

        guessedLetters.clear();
        guessedLetters.add(' ');
        guessedLetters.add('-');
        enteredLetters.clear();

        showHint = false;
    }

    private void displayGame() {
        display.displayHeader();
        display.displayHangman(lives);
        display.displayAlphabet(enteredLetters);
        display.displayGameInfo(category, difficulty, lives, word, hint, showHint, guessedLetters);
    }

    public int getLives() {
        return lives;
    }
}
