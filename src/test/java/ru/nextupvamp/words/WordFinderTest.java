package ru.nextupvamp.words;

import org.junit.jupiter.api.Test;
import ru.nextupvamp.util.Pair;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WordFinderTest {
    private final WordFinder wordFinder = new WordFinder();

    public WordFinderTest() throws IOException, NoSuchFieldException, IllegalAccessException {
        Field field = WordFinder.class.getDeclaredField("wordsMap");
        field.setAccessible(true);
        Map<String, List<String>> testWordMap = Map.of(
                "<category1>", List.of("word1:difficulty1", "wordwor2:difficulty2", "wordwordword3:difficulty3"),
                "<category2>", List.of("wordWithoutHint"),
                "<emptyCategory>", Collections.emptyList()
        );
        field.set(wordFinder, testWordMap);
    }

    public WordFinder getWordFinder() {
        return wordFinder;
    }

    @Test
    public void findWordWithHint() throws IOException {
        Pair<String, String> wordAndHint = wordFinder.findWord("category1", 3);
        String word = wordAndHint.left();
        String hint = wordAndHint.right();
        // method returns a word in upper case and a hint in the origin case
        assertAll(
                () -> assertEquals("WORDWORDWORD3", word),
                () -> assertEquals("difficulty3", hint)
        );
    }

    @Test
    public void findWordWithoutHint() throws IOException {
        Pair<String, String> wordAndHint = wordFinder.findWord("category2", 3);
        String word = wordAndHint.left();
        String hint = wordAndHint.right();
        assertAll(
                () -> assertEquals("WORDWITHOUTHINT", word),
                () -> assertNull(hint)
        );
    }

    @Test
    public void findWordWithAbsentDifficulty() {
        assertThrows(IOException.class, () -> wordFinder.findWord("category2", 2));
    }

    @Test
    public void findWordWithAbsentCategory() {
        assertThrows(IllegalArgumentException.class, () -> wordFinder.findWord("something", 2));
    }

    @Test
    public void findWordInEmptyCategory() {
        assertThrows(IOException.class, () -> wordFinder.findWord("emptyCategory", 2));
    }
}
