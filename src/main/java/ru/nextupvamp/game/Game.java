package ru.nextupvamp.game;

import lombok.Setter;

/**
 * Singleton with the main game loop
 */
@Setter
public class Game {
    public static final int ATTEMPTS = 6;

    private static volatile Game instance;

    private GameSession game;

    private Game() {
    }

    public void init() {
        boolean continuePlaying = true;
        while (continuePlaying) {
            setGame(new GameSession()); // it was made to avoid spotbugs warning
            continuePlaying = game.startGame();
        }
    }

    public static Game getInstance() {
        Game localInstance = instance;
        if (localInstance == null) {
            synchronized (Game.class) {
                localInstance = instance;
                if (localInstance == null) {
                    localInstance = new Game();
                    instance = localInstance;
                }
            }
        }
        return localInstance;
    }
}
