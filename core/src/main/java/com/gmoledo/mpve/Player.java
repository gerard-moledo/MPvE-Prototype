package com.gmoledo.mpve;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class Player {
    enum State { troop_selection, board_selection, board_placement }

    class Cursor {
        int q;
        int r;
        int selection_index;
        Cell highlight;

        Cursor() {
            this.q = 0;
            this.r = 0;
            this.selection_index = 0;
            this.highlight = new Cell(Cell.Type.cursor, this.q, this.r, Cell.CELL_RADIUS * 0.5f);
        }

        public void move(int dq, int dr) {
            Cell new_cell = Board.get(this.q + dq, this.r + dr);
            if (new_cell != null) {
                this.q += dq;
                this.r += dr;
                highlight.translate(dq, dr);
            }
        }
    }

    State state;

    Cursor cursor;
    Troop active_troop;

    Controller controller;

    // Movement timing fields
    Map<Integer, Float> move_timing = new HashMap<>();
    final float MOVE_DELAY = 0.2f;
    final float SENSITIVITY = 0.7f;

    Player() {
        state = State.board_selection;
        cursor = new Cursor();
        active_troop = Board.selection.get(Board.selection_index);
        active_troop.set_enabled(true);

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

    public void update(float delta) {
        if (!try_connect_controller()) return;

        if (Input_System.buttons.get(controller.getMapping().buttonStart).pressed) {
            GameScreen.start_game();
            return;
        }

        // 12/12: Currently Player requires an active Troop
        if (active_troop == null) return;

        //==========================
        // GATHER INPUT
        //==========================
        Input_System.Button select_button = Input_System.buttons.get(controller.getMapping().buttonA);
        Input_System.Button back_button = Input_System.buttons.get(controller.getMapping().buttonB);

        float x_input = calculate_axis_input(controller.getMapping().axisLeftX, delta);
        float y_input = calculate_axis_input(controller.getMapping().axisLeftY, delta);
        //=====GATHER INPUT=====

        switch (state) {
        case troop_selection: {
            // TROOP SELECTION NAVIGATION
            if (x_input != 0.0f) {
                active_troop.set_enabled(false);

                if (Board.selection_index % 2 == 0 && x_input > 0)
                    Board.selection_index += 1;
                if (Board.selection_index % 2 == 1 && x_input < 0)
                    Board.selection_index -= 1;
                cursor.selection_index = Board.selection_index;

                active_troop.set_enabled(false);
                active_troop = Board.selection.get(cursor.selection_index);
                active_troop.set_enabled(true);
            }
            if (y_input != 0.0f) {
                int di = (int) Math.signum(y_input) * 2;
                Board.selection_index = (Board.selection_index + di + Board.selection.size()) % Board.selection.size();

                cursor.selection_index = Board.selection_index;

                active_troop.set_enabled(false);
                active_troop = Board.selection.get(cursor.selection_index);
                active_troop.set_enabled(true);
            }

            // TROOP SELECTION PLACEMENT
            if (select_button != null && select_button.pressed) {
                active_troop.set_enabled(false);

                active_troop = new Troop(Cell.Type.player, active_troop.shape, cursor.q, cursor.r, Cell.CELL_RADIUS);

                state = State.board_placement;
                this.cursor.highlight.sprite.setColor(Color.BLACK);
            }
            if (back_button != null && back_button.pressed) {
                state = State.board_selection;
                this.cursor.highlight.sprite.setColor(Color.BLACK);
            }
        } break;
        case board_selection: {
            if (x_input != 0.0f) {
                cursor.move((int) Math.signum(x_input), 0);
            }
            if (y_input != 0.0f) {
                cursor.move(0, (int) Math.signum(y_input));
            }

            if (select_button.pressed) {
                if (Board.get(cursor.q, cursor.r).type != Cell.Type.player_troop) {
                    state = State.troop_selection;
                    this.cursor.highlight.sprite.setColor(Color.GRAY);
                    active_troop.set_enabled(true);
                }
            }

            if (back_button != null && back_button.pressed) {
                state = State.troop_selection;
                this.cursor.highlight.sprite.setColor(Color.GRAY);

                active_troop = Board.selection.get(cursor.selection_index);
                active_troop.set_enabled(true);
            }
        } break;
        case board_placement: {
            // BOARD MOVEMENT
            if (x_input != 0.0f) {
                active_troop.move((int) Math.signum(x_input), 0);
                cursor.move((int) Math.signum(x_input), 0);
            }
            if (y_input != 0.0f) {
                active_troop.move(0, (int) Math.signum(y_input));
                cursor.move(0, (int) Math.signum(y_input));
            }

            // BOARD TROOP ROTATION
            Input_System.Button L_button = Input_System.buttons.get(controller.getMapping().buttonL1);
            Input_System.Button R_button = Input_System.buttons.get(controller.getMapping().buttonR1);
            if (L_button.pressed) {
                active_troop.rotate(-1);
            }
            if (R_button.pressed) {
                active_troop.rotate(1);
            }

            // BOARD PLACEMENT
            if (select_button != null && select_button.pressed) {
                boolean success = active_troop.place();

                if (success) {
                    state = State.troop_selection;
                    this.cursor.highlight.sprite.setColor(Color.GRAY);

                    active_troop = Board.selection.get(cursor.selection_index);
                    active_troop.set_enabled(true);
                }
            }

            // BOARD RETURN-TO-SELECTION
            if (back_button != null && back_button.pressed) {
                state = State.troop_selection;
                this.cursor.highlight.sprite.setColor(Color.GRAY);

                active_troop = Board.selection.get(cursor.selection_index);
                active_troop.set_enabled(true);
            }
        } break;
        } // switch
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

    public void draw() {
        if (active_troop != null) active_troop.draw();
        cursor.highlight.draw();
    }
}
