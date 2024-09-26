package ru.nextupvamp.words;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.maven.surefire.shared.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import ru.nextupvamp.words.difficulties.HighDifficulty;
import ru.nextupvamp.words.difficulties.MediumDifficulty;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Getter
public class WordFinderTest {
    private final WordFinder wordFinder = new WordFinder();

    @SneakyThrows
    public WordFinderTest() {
        Field field = WordFinder.class.getDeclaredField("wordsMap");
        field.setAccessible(true);
        Map<String, List<String>> testWordMap = Map.of(
                "<category1>", List.of("word1:difficulty1", "wordwor2:difficulty2", "wordwordword3:difficulty3"),
                "<category2>", List.of("wordWithoutHint"),
                "<emptyCategory>", Collections.emptyList()
        );
        field.set(wordFinder, testWordMap);
    }

    @Test
    public void findWordWithHint() {
        Pair<String, String> wordAndHint = wordFinder.findWord("category1", new HighDifficulty());
        String word = wordAndHint.getLeft();
        String hint = wordAndHint.getRight();
        // method returns a word in upper case and a hint in the origin case
        assertAll(
                () -> assertEquals("WORDWORDWORD3", word),
                () -> assertEquals("difficulty3", hint)
        );
    }

    @Test
    public void findWordWithoutHint() {
        Pair<String, String> wordAndHint = wordFinder.findWord("category2", new HighDifficulty());
        String word = wordAndHint.getLeft();
        String hint = wordAndHint.getRight();
        assertAll(
                () -> assertEquals("WORDWITHOUTHINT", word),
                () -> assertNull(hint)
        );
    }

    @Test
    public void findWordWithAbsentDifficulty() {
        assertThrows(IOException.class, () -> wordFinder.findWord("category2", new MediumDifficulty()));
    }

    @Test
    public void findWordWithAbsentCategory() {
        assertThrows(IllegalArgumentException.class, () -> wordFinder.findWord("something", new MediumDifficulty()));
    }

    @Test
    public void findWordInEmptyCategory() {
        assertThrows(IOException.class, () -> wordFinder.findWord("emptyCategory", new MediumDifficulty()));
    }
}