package ru.nextupvamp.words;

import org.apache.maven.surefire.shared.lang3.tuple.ImmutablePair;
import org.apache.maven.surefire.shared.lang3.tuple.Pair;
import ru.nextupvamp.words.difficulties.Difficulty;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import lombok.SneakyThrows;

/**
 * WordFinder class designed to load words from the dictionary
 * and search according to conditions.
 */
public final class WordFinder {
    private final static Properties PROPERTIES = new Properties();

    private final Map<String, List<String>> wordsMap = new HashMap<>();
    private final Random random = new SecureRandom();

    public WordFinder() {
        loadProperties();
        loadWords();
    }

    @SneakyThrows
    private static void loadProperties() {
        PROPERTIES.load(WordFinder.class.getClassLoader().getResourceAsStream("config.properties"));
    }

    /**
     * Loads words from the dictionary file. Sets wordsMap in the
     * following way: key of the map is a category of the words,
     * value of the map is the list of words of that category.
     */
    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    private void loadWords() {
        try (InputStream is = this.getClass().getClassLoader()
                .getResourceAsStream(PROPERTIES.getProperty("dictionary.file.name"));
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            String line = bufferedReader.readLine();

            while (line != null) {
                if (line.startsWith("<")) {
                    String category = line;
                    List<String> words = new ArrayList<>();

                    line = bufferedReader.readLine();
                    while (line != null && !line.startsWith("<")) {
                        line = line.replaceAll("[Ёё]", "е");
                        words.add(line);
                        line = bufferedReader.readLine();
                    }

                    wordsMap.put(category, words);
                }
            }
        }
    }

    /**
     * Finds the pair of a word and a hint if it exists. If it doesn't then a hint will be null.
     *
     * @param category   category of the found word
     * @param difficulty difficulty of the found word
     * @return Pair of a found word in upper case and a hint in the origin case if it exists
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    @SneakyThrows
    public Pair<String, String> findWord(String category, Difficulty difficulty) {
        String formatCategory = String.format(PROPERTIES.getProperty("category.format"), category);

        List<String> words = wordsMap.get(formatCategory);
        if (words == null) {
            throw new IllegalArgumentException("Такой категории нет.");
        }
        if (words.isEmpty()) {
            throw new IOException("В этой категории нет слов. Попробуйте другую.");
        }

        words = words.stream().filter(difficulty.getWordsFilteringPredicate()).toList();

        if (words.isEmpty()) {
            throw new IOException("В этой категории нет слов указанной сложности. Попробуйте другую.");
        }

        String searchResult = words.get(random.nextInt(words.size()));

        String word;
        String hint = null;
        if (searchResult.contains(":")) {
            word = searchResult.substring(0, searchResult.indexOf(':'));
            hint = searchResult.substring(searchResult.indexOf(':') + 1);
        } else {
            word = searchResult;
        }

        return new ImmutablePair<>(word.toUpperCase(), hint);
    }

    public List<String> getCategories() {
        List<String> categoriesList = new ArrayList<>(wordsMap.size());
        for (var category : wordsMap.keySet()) {
            categoriesList.add(category.substring(1, category.length() - 1));
        }
        return categoriesList;
    }
}
