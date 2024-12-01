package com.gmoledo.mpve;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public class Player {
    Controller controller;
    Troop troop;

    // Movement timing fields
    float move_xt = 0.0f;
    float move_yt = 0.0f;
    float move_delay = 0.2f;
    float sensitivity = 0.7f;

    Player(Cell.Type type, Troop.Shape shape) {
        controller = Controllers.getCurrent();

        int q = type == Cell.Type.player ? -Board.field_size - 1 : Board.field_size + 1;
        int r = type == Cell.Type.player ? Board.field_size / 2 + 1 : -Board.field_size / 2 - 1;
        troop = new Troop(type, shape, q, r);
    }

    public void update(float delta) {
        // Modify controls of agent depending on player or opponent
        if (controller != null && controller.isConnected()) {
            int x_axis_code = troop.cell_type == Cell.Type.player ?
                              controller.getMapping().axisLeftX : controller.getMapping().axisRightX;
            int y_axis_code = troop.cell_type == Cell.Type.player ?
                              controller.getMapping().axisLeftY : controller.getMapping().axisRightY;

            float x_input = controller.getAxis(x_axis_code);
            if (Math.abs(x_input) > sensitivity) {
                move_xt += delta;
                if (move_xt > move_delay) {
                    move_xt -= move_delay;

                    troop.move((int) Math.signum(x_input), 0);
                }
            } else { move_xt = move_delay; }

            float y_input = controller.getAxis(y_axis_code);
            if (Math.abs(y_input) > sensitivity) {
                move_yt += delta;
                if (move_yt > move_delay) {
                    move_yt -= move_delay;

                    troop.move(0, (int) Math.signum(y_input));
                }
            } else { move_yt = move_delay; }
        }
        else {
            controller = Controllers.getCurrent();
        }
    }

    public void draw() {
        troop.draw();
    }
}
