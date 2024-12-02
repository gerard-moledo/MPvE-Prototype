package com.gmoledo.mpve;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;

import java.util.HashMap;
import java.util.Map;

public class Player {
    static class Button {
        boolean pressed = false;
        boolean down = false;
        boolean released = false;
        boolean up = true;
    }
    static Map<Integer, Button> buttons = new HashMap<>();
    final static int L_TRIGGER = 4;
    final static int R_TRIGGER = 5;
    static Controller controller;
    static ControllerListener controller_listener = new ControllerAdapter() {
        @Override
        public void connected(Controller controller) {
            System.out.println("Controller connected.");

            Player.Initialize_Controller(controller);
        }

        @Override
        public void disconnected(Controller controller) {
            System.out.println("Controller disconnected.");
        }

        @Override
        public boolean buttonDown(Controller controller, int buttonCode) {
            Button button = Player.buttons.get(buttonCode);

            if (button.up) {
                button.pressed = true;
                button.down = true;
            }

            Player.buttons.replace(buttonCode, button);
            return false;
        }

        @Override
        public boolean buttonUp(Controller controller, int buttonCode) {
            Button button = Player.buttons.get(buttonCode);
            if (button == null) return false;

            if (button.down) {
                button.down = false;

                button.released = true;
                button.up = true;
            }

            Player.buttons.replace(buttonCode, button);
            return false;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            Button button = Player.buttons.get(axisIndex);
            if (button == null) return false;

            if (value > 0.6f) {
                if (!button.down) button.pressed = true;
                button.down = true;
            }
            if (value < 0.01f){
                button.down = false;

                button.released = true;
                button.up = true;
            }

            Player.buttons.replace(axisIndex, button);
            return super.axisMoved(controller, axisIndex, value);
        }
    };

    Troop troop;

    // Movement timing fields
    float move_xt = 0.0f;
    float move_yt = 0.0f;
    float move_delay = 0.2f;
    float sensitivity = 0.7f;

    Player(Cell.Type type, Troop.Shape shape) {
        int q = type == Cell.Type.player ? -Board.field_size - 1 : Board.field_size + 1;
        int r = type == Cell.Type.player ? Board.field_size / 2 + 1 : -Board.field_size / 2 - 1;
        troop = new Troop(type, shape, q, r);
    }

    static public boolean Initialize_Controller(Controller controller) {
        Player.controller = controller;
        if (controller != null) {
            Player.controller.addListener(controller_listener);

            Player.buttons.put(controller.getMapping().buttonL1, new Button());
            Player.buttons.put(controller.getMapping().buttonR1, new Button());
            Player.buttons.put(L_TRIGGER, new Button());
            Player.buttons.put(R_TRIGGER, new Button());
        }

        return controller != null;
    }

    public void update(float delta) {
        if (controller == null) {
            boolean success = Initialize_Controller(Controllers.getCurrent());

            if (!success) return;
        }

        // ==================================
        // SHAPE TOGGLING
        // ==================================
        int L_button_code = troop.cell_type == Cell.Type.player ?
                                     controller.getMapping().buttonL1 : L_TRIGGER;
        int R_button_code = troop.cell_type == Cell.Type.player ?
                                     controller.getMapping().buttonR1 : R_TRIGGER;

        Button L_button = Player.buttons.get(L_button_code);
        Button R_button = Player.buttons.get(R_button_code);
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
