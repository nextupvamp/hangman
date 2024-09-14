package ru.nextupvamp.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.nextupvamp.words.WordFinderTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputHandlerTest {
    // using the same word map as in the WordFinderTest class
    private final WordFinderTest testWordFinder = new WordFinderTest();

    private final ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
    private final PrintStream PRINT_STREAM = new PrintStream(BAOS);

    public InputHandlerTest() throws IOException, NoSuchFieldException, IllegalAccessException {
    }

    @Test
    public void inputCategory() {
        ByteArrayInputStream in = new ByteArrayInputStream("2".getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        String category = inputHandler.inputCategory(testWordFinder.getWordFinder().getCategories());
        assertEquals(testWordFinder.getWordFinder().getCategories().get(1), category);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "4"})
    public void inputWrongCategory(String input) {
        // input + System.lineSeparator() + "2" is done so that the method works properly
        // w/o another value it will eternally wait for correct input
        ByteArrayInputStream in = new ByteArrayInputStream((input + System.lineSeparator() + "2").getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        inputHandler.inputCategory(testWordFinder.getWordFinder().getCategories());
        String output = BAOS.toString();
        assertTrue(output.contains("Введите корректное значение"));
    }

    @Test
    public void inputDifficulty() {
        // unfortunately idk how to make parameterized @beforeeach
        // so there's a lot of such boilerplate code :(
        ByteArrayInputStream in = new ByteArrayInputStream("2".getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        int difficulty = inputHandler.inputDifficulty();
        assertEquals(2, difficulty);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "4"})
    public void inputWrongDifficulty(String input) {
        ByteArrayInputStream in = new ByteArrayInputStream((input + System.lineSeparator() + "2").getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        inputHandler.inputDifficulty();
        String output = BAOS.toString();
        assertTrue(output.contains("Введите корректное значение"));
    }

    @Test
    public void inputStringAsDifficulty() {
        ByteArrayInputStream in = new ByteArrayInputStream(("something" + System.lineSeparator() + "2").getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        inputHandler.inputDifficulty();
        String output = BAOS.toString();
        assertTrue(output.contains("Введите число"));
    }

    @Test
    public void inputNonRussianLetter() {
        ByteArrayInputStream in = new ByteArrayInputStream(("S" + System.lineSeparator() + "а").getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        inputHandler.inputLetter(new TreeSet<>());
        String output = BAOS.toString();
        assertTrue(output.contains("Введите букву русского алфавита"));
    }

    @Test
    public void inputLetterInDifferentCases() {
        ByteArrayInputStream in =
                new ByteArrayInputStream(("А" + System.lineSeparator() + "а" + System.lineSeparator() + "б").getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        Set<Character> enteredLetters = new TreeSet<>();
        inputHandler.inputLetter(enteredLetters);
        inputHandler.inputLetter(enteredLetters);
        String output = BAOS.toString();
        // this message says that letters are considered the same
        assertTrue(output.contains("Эта буква уже была введена. Введите другую"));
    }

    @Test
    public void inputSeveralLettersAtOnce() {
        ByteArrayInputStream in = new ByteArrayInputStream(("аб" + System.lineSeparator() + "а").getBytes());
        InputHandler inputHandler = new InputHandler(in, PRINT_STREAM);
        inputHandler.inputLetter(new TreeSet<>());
        String output = BAOS.toString();
        assertTrue(output.contains("Введите одну букву"));
    }
}
