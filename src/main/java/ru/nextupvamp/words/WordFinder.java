package ru.nextupvamp.words;

import ru.nextupvamp.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * WordFinder class designed to load words from the dictionary
 * and search according to conditions.
 */
public final class WordFinder {
    private final Map<String, List<String>> wordsMap = new HashMap<>();
    private final Random random = new SecureRandom();

    public WordFinder() throws IOException {
        loadWords();
    }

    /**
     * Loads words from the dictionary file. Sets wordsMap in the
     * following way: key of the map is a category of the words,
     * value of the map is the list of words of that category.
     *
     * @throws IOException file reading errors
     */
    private void loadWords() throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("dict.txt")) {
            if (is == null) {
                throw new FileNotFoundException("Словарь не найден");
            }
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
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
    }

    /**
     * Finds the pair of a word and a hint if it exists. If it doesn't then a hint will be null.
     *
     * @param category   category of the found word
     * @param difficulty difficulty of the found word
     * @return Pair of a found word in upper case and a hint in the origin case if it exists
     * @throws IllegalArgumentException in the case of an error that doesn't imply
     *                                  further work of the program
     * @throws IOException              if chosen category doesn't have words at all
     *                                  or doesn't have words of chosen difficulty
     */
    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
    public Pair<String, String> findWord(String category, int difficulty) throws IOException {
        String formatCategory = "<" + category + ">";
        String searchResult;

        List<String> words = wordsMap.get(formatCategory);
        if (words == null) {
            throw new IllegalArgumentException("Такой категории нет.");
        }
        if (words.isEmpty()) {
            throw new IOException("В этой категории нет слов. Попробуйте другую.");
        }

        words = words.stream().filter(s -> {
            int indexOfColon = s.indexOf(':');
            int wordLength = s.substring(0, indexOfColon == -1 ? s.length() : indexOfColon).length();
            if (difficulty == 1) {
                return wordLength < 6;
            } else if (difficulty == 2) {
                return wordLength >= 6 && wordLength < 9;
            } else if (difficulty == 3) {
                return wordLength >= 9;
            } else {
                throw new IllegalArgumentException("Такой сложности нет.");
            }
        }).toList();
        if (words.isEmpty()) {
            throw new IOException("В этой категории нет слов указанной сложности. Попробуйте другую.");
        }

        searchResult = words.get(random.nextInt(words.size()));

        String word;
        String hint = null;
        if (searchResult.contains(":")) {
            word = searchResult.substring(0, searchResult.indexOf(':'));
            hint = searchResult.substring(searchResult.indexOf(':') + 1);
        } else {
            word = searchResult;
        }

        return new Pair<>(word.toUpperCase(), hint);
    }

    public List<String> getCategories() {
        List<String> categoriesList = new ArrayList<>(wordsMap.size());
        for (var category : wordsMap.keySet()) {
            categoriesList.add(category.substring(1, category.length() - 1));
        }
        return categoriesList;
    }
}
