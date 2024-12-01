package com.gmoledo.mpve;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen extends ScreenAdapter {
    Board board;
    Troop troop;
    Troop enemy;
    Controller controller;

    ShapeRenderer renderer;

    boolean moved_x = false;
    boolean moved_y = false;
    boolean opp_moved_x = false;
    boolean opp_moved_y = false;

    GameScreen() {
        board = new Board(5);
        troop = new Troop(Cell.Type.player, 1, 2);
        enemy = new Troop(Cell.Type.opponent, 5, -2);
        controller = Controllers.getCurrent();

        renderer = new ShapeRenderer();
    }

    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (controller != null && controller.isConnected()) {
            float move_x = controller.getAxis(controller.getMapping().axisLeftX);
            if (Math.abs(move_x) >= 0.2f && !moved_x) {
                moved_x = true;
                troop.move((int) Math.signum(move_x), 0);
            }
            if (Math.abs(move_x) <= 0.2f) {
                moved_x = false;
            }

            float move_y = controller.getAxis(controller.getMapping().axisLeftY);
            if (Math.abs(move_y) >= 0.5f && !moved_y) {
                moved_y = true;
                troop.move(0, (int) Math.signum(move_y));
            }
            if (Math.abs(move_y) <= 0.5f) {
                moved_y = false;
            }

            float opp_move_x = controller.getAxis(controller.getMapping().axisRightX);
            if (Math.abs(opp_move_x) >= 0.2f && !opp_moved_x) {
                opp_moved_x = true;
                enemy.move((int) Math.signum(opp_move_x), 0);
            }
            if (Math.abs(opp_move_x) <= 0.2f) {
                opp_moved_x = false;
            }

            float opp_move_y = controller.getAxis(controller.getMapping().axisRightY);
            if (Math.abs(opp_move_y) >= 0.5f && !opp_moved_y) {
                opp_moved_y = true;
                enemy.move(0, (int) Math.signum(opp_move_y));
            }
            if (Math.abs(opp_move_y) <= 0.5f) {
                opp_moved_y = false;
            }
        }
        else {
            controller = Controllers.getCurrent();
        }

        board.draw(renderer);
        troop.draw(renderer);
        enemy.draw(renderer);
    }
}
