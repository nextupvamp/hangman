package ru.nextupvamp.game;

import java.io.IOException;

/**
 * Singleton with the main game loop
 */
public class GameSession {
    private static GameSession instance;

    private final Game game = new Game();

    private GameSession() throws IOException {
    }

    public void init() throws IOException {
        boolean continuePlaying = true;
        while (continuePlaying) {
            continuePlaying = game.startGame();
        }
    }

    public static synchronized GameSession getInstance() throws IOException {
        if (instance == null) {
            instance = new GameSession();
        }

        return instance;
    }
}
