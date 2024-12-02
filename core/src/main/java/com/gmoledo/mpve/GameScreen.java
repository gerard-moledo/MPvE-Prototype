package com.gmoledo.mpve;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Map;

public class GameScreen extends ScreenAdapter {
    Player player;
    Player opponent;

    ShapeRenderer renderer;

    GameScreen() {
        Board.Instantiate(5);
        Player.Initialize_Controller(Controllers.getCurrent());

        player = new Player(Cell.Type.player, Troop.Shape.single);
        opponent = new Player(Cell.Type.opponent, Troop.Shape.single);

        renderer = new ShapeRenderer();
    }

    public void render(float delta) {
        player.update(delta);
        opponent.update(delta);

        for (Map.Entry<Integer, Player.Button> button : Player.buttons.entrySet()) {
            button.getValue().pressed = false;
            button.getValue().released = false;
        }

        ScreenUtils.clear(Color.BLACK);

        renderer.setAutoShapeType(true);
        renderer.begin();

        Board.draw();
        player.draw();
        opponent.draw();

        renderer.end();
    }
}
