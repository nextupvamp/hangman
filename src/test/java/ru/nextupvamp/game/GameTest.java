package ru.nextupvamp.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nextupvamp.io.Display;
import ru.nextupvamp.io.InputHandler;
import ru.nextupvamp.util.Pair;
import ru.nextupvamp.words.WordFinder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameTest {
    @Mock
    private WordFinder wordFinder;
    @Mock
    private InputHandler inputHandler;
    @Mock
    private Display display;

    @InjectMocks
    @Spy
    private Game game;

    private final String TEST_WORD = "TEST";
    private final String TEST_HINT = "This is a test";

    @BeforeEach
    public void setUp(TestInfo testInfo) throws IOException {
        if (testInfo.getDisplayName().equals("startGameWithEmptyWord")) {
            return;
        }
        when(wordFinder.findWord(anyString(), anyInt())).thenReturn(new Pair<>(TEST_WORD, TEST_HINT));
        when(inputHandler.inputCategory(anyList())).thenReturn("General");
        when(inputHandler.inputDifficulty()).thenReturn(1);
    }

    @Test
    public void startGameWithEmptyWord() throws IOException {
        when(wordFinder.findWord(anyString(), anyInt())).thenReturn(new Pair<>("", TEST_HINT));
        when(inputHandler.inputCategory(anyList())).thenReturn("General");
        when(inputHandler.inputDifficulty()).thenReturn(1);

        assertThrows(IOException.class, () -> game.startGame());
    }

    @Test
    public void winGame() throws IOException {
        when(inputHandler.inputLetter(anySet())).thenReturn('T', 'E', 'S');
        when(inputHandler.inputContinue()).thenReturn(false);

        game.startGame();

        verify(display).displayGameResult(true, TEST_WORD);
    }

    @Test
    public void loseGame() throws IOException {
        when(inputHandler.inputLetter(anySet())).thenReturn('A', 'B', 'C', 'D', 'E', 'F');
        when(inputHandler.inputContinue()).thenReturn(false);

        game.startGame();

        verify(display).displayGameResult(false, TEST_WORD);
    }

    @Test
    public void guessRightLetter() throws IOException {
        when(inputHandler.inputLetter(anySet())).thenReturn('T');
        when(inputHandler.inputContinue()).thenReturn(false);
        doReturn(true).when(game).checkIfWin();

        game.startGame();

        assertEquals(Game.ATTEMPTS, game.getLives());
    }

    @Test
    public void guessWrongLetter() throws IOException {
        when(inputHandler.inputLetter(anySet())).thenReturn('A', 'T');
        when(inputHandler.inputContinue()).thenReturn(false);
        doReturn(true).when(game).checkIfWin();

        game.startGame();

        assertEquals(Game.ATTEMPTS - 1, game.getLives());
    }
}
