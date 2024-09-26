package ru.nextupvamp.game;

import org.apache.maven.surefire.shared.lang3.tuple.Pair;
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
import ru.nextupvamp.words.WordFinder;
import ru.nextupvamp.words.difficulties.LowDifficulty;

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
    private GameSession gameSession;

    private final String TEST_WORD = "TEST";
    private final String TEST_HINT = "This is a test";

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        if (testInfo.getDisplayName().equals("startGameWithEmptyWord")) {
            return;
        }
        when(wordFinder.findWord(anyString(), any())).thenReturn(Pair.of(TEST_WORD, TEST_HINT));
        when(inputHandler.inputCategory(anyList())).thenReturn("General");
        when(inputHandler.inputDifficulty()).thenReturn(new LowDifficulty());
    }

    @Test
    public void startGameWithEmptyWord() {
        when(wordFinder.findWord(anyString(), any())).thenReturn(Pair.of("", TEST_HINT));
        when(inputHandler.inputCategory(anyList())).thenReturn("General");
        when(inputHandler.inputDifficulty()).thenReturn(new LowDifficulty());

        assertThrows(RuntimeException.class, () -> gameSession.startGame());
    }

    @Test
    public void winGame() {
        when(inputHandler.inputLetter(anySet())).thenReturn('T', 'E', 'S');
        when(inputHandler.inputContinue()).thenReturn(false);

        gameSession.startGame();

        verify(display).displayGameResult(true, TEST_WORD);
    }

    @Test
    public void loseGame() {
        when(inputHandler.inputLetter(anySet())).thenReturn('A', 'B', 'C', 'D', 'E', 'F');
        when(inputHandler.inputContinue()).thenReturn(false);

        gameSession.startGame();

        verify(display).displayGameResult(false, TEST_WORD);
    }

    @Test
    public void guessRightLetter() {
        when(inputHandler.inputLetter(anySet())).thenReturn('T');
        when(inputHandler.inputContinue()).thenReturn(false);
        doReturn(true).when(gameSession).isWordGuessed();

        gameSession.startGame();

        assertEquals(Game.ATTEMPTS, gameSession.getPlayer().getLives());
    }

    @Test
    public void guessWrongLetter() {
        when(inputHandler.inputLetter(anySet())).thenReturn('A', 'T');
        when(inputHandler.inputContinue()).thenReturn(false);
        doReturn(true).when(gameSession).isWordGuessed();

        gameSession.startGame();

        assertEquals(Game.ATTEMPTS - 1, gameSession.getPlayer().getLives());
    }
}