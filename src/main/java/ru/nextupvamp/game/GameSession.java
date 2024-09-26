package ru.nextupvamp.game;

import org.apache.maven.surefire.shared.lang3.tuple.Pair;
import ru.nextupvamp.io.Display;
import ru.nextupvamp.io.InputHandler;
import ru.nextupvamp.words.WordFinder;
import ru.nextupvamp.words.difficulties.Difficulty;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;

@Getter
public class GameSession {
    private String word;
    private String hint;
    private String category;
    private Difficulty difficulty;
    private Player player;
    private boolean isRunning;
    private final Set<Character> wordLetters = new TreeSet<>();
    private final Set<Character> enteredLetters = new TreeSet<>();
    private final Set<Character> guessedLetters = new TreeSet<>();
    private final Display display;
    private final InputHandler inputHandler;
    private final WordFinder wordFinder;

    @SuppressWarnings("unused")
    public GameSession(InputHandler inputHandler, WordFinder wordFinder, Display display) {
        this.inputHandler = inputHandler;
        this.display = display;
        this.wordFinder = wordFinder;
    }

    @SuppressWarnings("unused")
    public GameSession(InputStream inputStream, PrintStream printStream) {
        display = new Display(printStream);
        inputHandler = new InputHandler(inputStream, printStream);
        wordFinder = new WordFinder();
    }

    public GameSession() {
        display = new Display();
        inputHandler = new InputHandler();
        wordFinder = new WordFinder();
    }

    @SuppressWarnings("ConstantConditions")
    public boolean startGame() {
        initGame();
        isRunning = true;

        while (!player.isWin() && player.getLives() > 0) {
            displayGame(new GameSessionState(this));
            char letter = inputHandler.inputLetter(enteredLetters);
            if (letter == InputHandler.PLAYER_NEED_HINT_SIGN) {
                player.setNeedHint(true);
            } else if (wordLetters.contains(letter)) {
                guessedLetters.add(letter);
                player.setWin(isWordGuessed());
            } else {
                player.decrementLives();
            }
        }

        isRunning = false;
        displayGame(new GameSessionState(this));

        return inputHandler.inputContinue();
    }

    public boolean isWordGuessed() {
        for (int i = 0; i != word.length(); ++i) {
            if (!guessedLetters.contains(word.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void initGame() {
        display.displayHeader();

        List<String> categories = wordFinder.getCategories();
        display.displayCategories(categories);
        category = inputHandler.inputCategory(categories);

        display.displayDifficulties();
        difficulty = inputHandler.inputDifficulty();

        Pair<String, String> wordAndHint = wordFinder.findWord(category, difficulty);
        word = wordAndHint.getLeft();
        if (word == null || word.isEmpty()) {
            throw new RuntimeException("Неверная длина слова");
        }
        hint = wordAndHint.getRight();

        for (int i = 0; i != word.length(); ++i) {
            wordLetters.add(word.charAt(i));
        }

        guessedLetters.add(' ');
        guessedLetters.add('-');

        player = new Player(Game.ATTEMPTS);
    }

    private void displayGame(GameSessionState gameState) {
        display.displayHeader();
        display.displayHangman(gameState.player().getLives());
        display.displayAlphabet(gameState.enteredLetters());
        display.displayGameInfo(gameState.category(),
                gameState.difficulty(),
                gameState.player().getLives(),
                gameState.word(),
                gameState.hint(),
                gameState.player().isNeedHint(),
                gameState.guessedLetters()
        );
        if (!isRunning) {
            display.displayGameResult(gameState.player().isWin(), gameState.word());
        }
    }
}
