package ru.nextupvamp;

import ru.nextupvamp.game.Game;

public class Main {
    public static void main(String[] args) {
        try {
            Game.getInstance().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
