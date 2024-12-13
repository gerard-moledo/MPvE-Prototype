package com.gmoledo.mpve;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

import java.util.HashMap;
import java.util.Map;

public class Player {
    enum State { troop_selection, board_selection, board_placement }

    State state;
    Troop troop;

    Controller controller;

    // Movement timing fields
    Map<Integer, Float> move_timing = new HashMap<>();
    final float MOVE_DELAY = 0.2f;
    final float SENSITIVITY = 0.7f;

    Player() {
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

            if (success) {
                controller = Input_System.controller;

                move_timing.put(controller.getMapping().axisLeftX, 0.0f);
                move_timing.put(controller.getMapping().axisLeftY, 0.0f);
            }
        }

        return success;
    }

    public float calculate_axis_input(int axis, float delta) {
        float calculated_input = 0.0f;
        float raw_input = controller.getAxis(axis);
        float move_t = move_timing.get(axis);
        if (Math.abs(raw_input) > SENSITIVITY) {
            move_t += delta;
            if (move_t > MOVE_DELAY) {
                move_t -= MOVE_DELAY;

                calculated_input = raw_input;
            }
        } else { move_t = MOVE_DELAY; }

        move_timing.put(axis, move_t);

        return calculated_input;
    }

    public void update(float delta) {
        if (!try_connect_controller()) return;

        if (Input_System.buttons.get(controller.getMapping().buttonStart).pressed) {
            GameScreen.start_game();
            return;
        }

        // 12/12: Currently Player requires an active Troop
        if (troop == null) return;

        //==========================
        // GATHER INPUT
        //==========================
        Input_System.Button place_button = Input_System.buttons.get(controller.getMapping().buttonA);
        Input_System.Button back_button = Input_System.buttons.get(controller.getMapping().buttonB);

        float x_input = calculate_axis_input(controller.getMapping().axisLeftX, delta);
        float y_input = calculate_axis_input(controller.getMapping().axisLeftY, delta);
        //=====GATHER INPUT=====

        if (state == State.troop_selection) {
            // TROOP SELECTION NAVIGATION
            if (x_input != 0.0f) {
                troop.set_enabled(false);

                if (Board.selection_index % 2 == 0 && x_input > 0)
                    Board.selection_index += 1;
                if (Board.selection_index % 2 == 1 && x_input < 0)
                    Board.selection_index -= 1;
                troop = Board.selection.get(Board.selection_index);
                troop.set_enabled(true);
            }
            if (y_input != 0.0f) {
                troop.set_enabled(false);

                int di = (int) Math.signum(y_input) * 2;
                Board.selection_index = (Board.selection_index + di + Board.selection.size()) % Board.selection.size();
                troop = Board.selection.get(Board.selection_index);
                troop.set_enabled(true);
            }

            // TROOP SELECTION PLACEMENT
            if (place_button != null && place_button.pressed) {
                troop.set_enabled(false);

                Troop new_troop = new Troop(Cell.Type.player, troop.shape, -6, 4, Cell.CELL_RADIUS);
                troop = new_troop;
                state = State.board_placement;
            }
        }
        else if (state == State.board_placement) {
            // BOARD MOVEMENT
            if (x_input != 0.0f)
                troop.move((int) Math.signum(x_input), 0);
            if (y_input != 0.0f)
                troop.move(0, (int) Math.signum(y_input));

            // BOARD PLACEMENT
            if (place_button != null && place_button.pressed) {
                boolean success = troop.place();

                if (success) {
                    state = State.troop_selection;
                    Board.selection_index = 0;
                    troop = Board.selection.get(Board.selection_index);
                    troop.set_enabled(true);
                }
            }

            // BOARD RETURN-TO-SELECTION
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
        Input_System.Button L_button = Input_System.buttons.get(controller.getMapping().buttonL1);
        Input_System.Button R_button = Input_System.buttons.get(controller.getMapping().buttonR1);
        if (L_button.pressed) {
            troop.toggle_shape(-1);
        }
        if (R_button.pressed) {
            troop.toggle_shape(1);
        }
        // ========== SHAPE TOGGLING ==========
    }

    public void draw() {
        if (troop != null) troop.draw();
    }
}
