package ru.nextupvamp;

import ru.nextupvamp.game.GameSession;

public class Main {
    public static void main(String[] args) {
        try {
            GameSession.getInstance().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
