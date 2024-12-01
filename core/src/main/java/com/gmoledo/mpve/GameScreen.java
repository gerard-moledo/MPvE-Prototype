package com.gmoledo.mpve;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {
    Player player;
    Player opponent;

    ShapeRenderer renderer;

    GameScreen() {
        Board.Instantiate(5);

        player = new Player(Cell.Type.player, Troop.Shape.cluster);
        opponent = new Player(Cell.Type.opponent, Troop.Shape.triple);

        renderer = new ShapeRenderer();
    }

    public void render(float delta) {
        player.update(delta);
        opponent.update(delta);

        ScreenUtils.clear(Color.BLACK);

        renderer.setAutoShapeType(true);
        renderer.begin();

        Board.draw();
        player.draw();
        opponent.draw();

        renderer.end();
    }
}
