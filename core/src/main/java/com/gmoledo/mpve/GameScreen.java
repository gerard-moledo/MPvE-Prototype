package com.gmoledo.mpve;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Map;

public class GameScreen extends ScreenAdapter {
    Player player;
    Player opponent;

    GameScreen() {
        Board.Instantiate(5);
        // Controller initialization my fail if no controller is connected
        boolean success = Player.Initialize_Controller(Controllers.getCurrent());
        if (!success) System.out.println("No controller connected.");

        player = new Player(Cell.Type.player, Troop.Shape.single);
        opponent = new Player(Cell.Type.opponent, Troop.Shape.single);
    }

    // Main loop
    public void render(float delta) {
        update(delta);
        draw();
    }

    public void update(float delta) {
        player.update(delta);
        opponent.update(delta);

        // Reset per-frame inputs
        for (Map.Entry<Integer, Player.Button> button : Player.buttons.entrySet()) {
            button.getValue().pressed = false;
            button.getValue().released = false;
        }
    }

    public void draw() {
        ScreenUtils.clear(Color.BLACK);

        Board.draw();
        player.draw();
        opponent.draw();
    }
}
