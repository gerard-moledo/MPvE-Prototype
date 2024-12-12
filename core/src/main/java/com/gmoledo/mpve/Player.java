package com.gmoledo.mpve;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public class Player {
    enum State { troop_selection, board_selection, board_placement }

    State state;
    Troop troop;

    Controller controller;

    // Movement timing fields
    float move_xt = 0.0f;
    float move_yt = 0.0f;
    final float MOVE_DELAY = 0.2f;
    final float SENSITIVITY = 0.7f;

    Player(Cell.Type type, Shape.Type shape) {
        state = State.troop_selection;
        troop = Board.selection.get(Board.selection_index);
        troop.set_enabled(true);

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

        if (Input_System.buttons.get(controller.getMapping().buttonStart).pressed) {
            GameScreen.start_game();
            return;
        }

        if (troop == null) return;

        boolean should_move_x = false;
        boolean should_move_y = false;

        int place_button_code = troop.cell_type == Cell.Type.player ?
            controller.getMapping().buttonA : controller.getMapping().buttonDpadDown;

        Input_System.Button place_button = Input_System.buttons.get(place_button_code);
        if (place_button == null) System.out.println("Button not registered");
        Input_System.Button back_button = Input_System.buttons.get(controller.getMapping().buttonB);

        //==========================
        // GATHER AXIS INPUT
        //==========================
        float x_input = controller.getAxis(controller.getMapping().axisLeftX);
        float y_input = controller.getAxis(controller.getMapping().axisLeftY);

        if (Math.abs(x_input) > SENSITIVITY) {
            move_xt += delta;
            if (move_xt > MOVE_DELAY) {
                move_xt -= MOVE_DELAY;

                should_move_x = true;
            }
        } else { move_xt = MOVE_DELAY; }

        if (Math.abs(y_input) > SENSITIVITY) {
            move_yt += delta;
            if (move_yt > MOVE_DELAY) {
                move_yt -= MOVE_DELAY;

                should_move_y = true;
            }
        } else { move_yt = MOVE_DELAY; }
        //=====GATHER AXIS INPUT=====

        if (state == State.troop_selection) {
            if (should_move_x) {
//                state = State.board_selection;
//                // Hacky "set position" behavior
//                troop.q = 0;
//                troop.r = 0;
//                troop.move(-Board.field_size - 1, Board.field_size / 2 + 1);
                troop.set_enabled(false);

                if (Board.selection_index % 2 == 0 && x_input > 0)
                    Board.selection_index += 1;
                if (Board.selection_index % 2 == 1 && x_input < 0)
                    Board.selection_index -= 1;
                troop = Board.selection.get(Board.selection_index);
                troop.set_enabled(true);
            }
            if (should_move_y) {
                troop.set_enabled(false);

                int di = (int) Math.signum(y_input) * 2;
                Board.selection_index = (Board.selection_index + di + Board.selection.size()) % Board.selection.size();
                troop = Board.selection.get(Board.selection_index);
                troop.set_enabled(true);
            }

            // ====================================
            // TROOP PLACEMENT
            // ====================================
            if (place_button != null && place_button.pressed) {
                troop.set_enabled(false);

                Troop new_troop = new Troop(Cell.Type.player, troop.shape, -6, 4, Cell.CELL_RADIUS);
                troop = new_troop;
                state = State.board_selection;
            }
        }
        else if (state == State.board_selection) {
            if (should_move_x)
                troop.move((int) Math.signum(x_input), 0);
            if (should_move_y)
                troop.move(0, (int) Math.signum(y_input));
            if (place_button != null && place_button.pressed) {
                boolean success = troop.place();

                if (success) {
                    state = State.troop_selection;
                    Board.selection_index = 0;
                    troop = Board.selection.get(Board.selection_index);
                    troop.set_enabled(true);
                }
            }
            if (back_button != null && back_button.pressed) {
                state = State.troop_selection;
                Board.selection_index = 0;
                troop = Board.selection.get(Board.selection_index);
                troop.set_enabled(true);
            }
        }

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

//        // Modify controls of agent depending on player or opponent
//        int x_axis_code = troop.cell_type == Cell.Type.player ?
//                          controller.getMapping().axisLeftX : controller.getMapping().axisRightX;
//        int y_axis_code = troop.cell_type == Cell.Type.player ?
//                          controller.getMapping().axisLeftY : controller.getMapping().axisRightY;
//
//        float x_input = controller.getAxis(x_axis_code);
//        float y_input = controller.getAxis(y_axis_code);
//
//        if (Math.abs(x_input) > SENSITIVITY) {
//            move_xt += delta;
//            if (move_xt > MOVE_DELAY) {
//                move_xt -= MOVE_DELAY;
//
//                troop.move((int) Math.signum(x_input), 0);
//            }
//        } else { move_xt = MOVE_DELAY; }
//
//
//        if (Math.abs(y_input) > SENSITIVITY) {
//            move_yt += delta;
//            if (move_yt > MOVE_DELAY) {
//                move_yt -= MOVE_DELAY;
//
//                troop.move(0, (int) Math.signum(y_input));
//            }
//        } else { move_yt = MOVE_DELAY; }
//        // ========== MOVEMENT LOGIC ==========

    }

    public void draw() {
        if (troop != null) troop.draw();
    }
}
