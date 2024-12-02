package com.gmoledo.mpve;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public class Player {
    Troop troop;

    Controller controller;

    // Movement timing fields
    float move_xt = 0.0f;
    float move_yt = 0.0f;
    float move_delay = 0.2f;
    float sensitivity = 0.7f;

    Player(Cell.Type type, Troop.Shape shape) {
        // Start in appropriate agent's base
        int q = type == Cell.Type.player ? -Board.field_size - 1 : Board.field_size + 1;
        int r = type == Cell.Type.player ? Board.field_size / 2 + 1 : -Board.field_size / 2 - 1;
        troop = new Troop(type, shape, q, r);

        try_connect_controller();
    }

    // Helper function to check if controller is connected
    private boolean try_connect_controller() {
        boolean success = true;

        if (controller == null) {
            success = Input_System.Initialize_Controller(Controllers.getCurrent());

            if (success) controller = Input_System.controller;
        }

        return success;
    }

    public void update(float delta) {
        if (!try_connect_controller()) return;

        // ==================================
        // SHAPE TOGGLING
        // ==================================
        int L_button_code = troop.cell_type == Cell.Type.player ?
                                     controller.getMapping().buttonL1 : Input_System.L_TRIGGER;
        int R_button_code = troop.cell_type == Cell.Type.player ?
                                     controller.getMapping().buttonR1 : Input_System.R_TRIGGER;

        Input_System.Button L_button = Input_System.buttons.get(L_button_code);
        Input_System.Button R_button = Input_System.buttons.get(R_button_code);
        if (L_button.pressed) {
            troop.toggle_shape(-1);
        }
        if (R_button.pressed) {
            troop.toggle_shape(1);
        }
        // ========== SHAPE TOGGLING ==========

        // ==================================
        // MOVEMENT LOGIC
        // ==================================

        // Modify controls of agent depending on player or opponent
        int x_axis_code = troop.cell_type == Cell.Type.player ?
                          controller.getMapping().axisLeftX : controller.getMapping().axisRightX;
        int y_axis_code = troop.cell_type == Cell.Type.player ?
                          controller.getMapping().axisLeftY : controller.getMapping().axisRightY;

        float x_input = controller.getAxis(x_axis_code);
        float y_input = controller.getAxis(y_axis_code);

        if (Math.abs(x_input) > sensitivity) {
            move_xt += delta;
            if (move_xt > move_delay) {
                move_xt -= move_delay;

                troop.move((int) Math.signum(x_input), 0);
            }
        } else { move_xt = move_delay; }


        if (Math.abs(y_input) > sensitivity) {
            move_yt += delta;
            if (move_yt > move_delay) {
                move_yt -= move_delay;

                troop.move(0, (int) Math.signum(y_input));
            }
        } else { move_yt = move_delay; }
        // ========== MOVEMENT LOGIC ==========
    }

    public void draw() {
        troop.draw();
    }
}
