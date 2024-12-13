package com.gmoledo.mpve;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Map;

public class GameScreen extends ScreenAdapter {
    static Player player;

    GameScreen() {
        // Controller initialization my fail if no controller is connected
        boolean success = Input_System.Initialize_Controller(Controllers.getCurrent());
        if (!success) System.out.println("No controller connected.");

        Shape.Initialize();

        start_game();
    }

    static public void start_game() {
        Board.Instantiate(5);

        player = new Player(Cell.Type.player, Shape.Type.single);
    }

    // Main loop
    public void render(float delta) {
        update(delta);
        draw();
    }

    public void update(float delta) {
        player.update(delta);

        // Reset per-frame inputs
        for (Map.Entry<Integer, Input_System.Button> button : Input_System.buttons.entrySet()) {
            button.getValue().pressed = false;
            button.getValue().released = false;
        }
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        Board.draw();
        player.draw();
    }
}
