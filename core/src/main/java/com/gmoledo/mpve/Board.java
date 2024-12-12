package com.gmoledo.mpve;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

final public class Board {
    static List<List<Cell>> board;
    static int field_size;
    static final int HOME_SIZE = 3;

    static List<Troop> selection;
    static int selection_index;

    static public void Instantiate(int field_size) {
        Board.field_size = field_size;
        Board.selection_index = 0;

        create_board();
        create_selection();
    }

    static private void create_selection() {
        selection = new ArrayList<>();
        Shape.Type[] shape_types = Shape.SHAPE_MAP.keySet().toArray(new Shape.Type[0]);
        for (int t = 0; t < Shape.SHAPE_MAP.size(); ++t) {
            Shape.Type type = shape_types[t];
            float cell_radius = Cell.CELL_RADIUS / 2.0f;
            Troop troop_select = new Troop(Cell.Type.player, type, 0, 0, cell_radius);
            troop_select.set_absolute_position(100 + 3 * 2 * cell_radius * (t % 2), Gdx.graphics.getHeight() - (45 + 4 * 2 * cell_radius * (t / 2)));
            troop_select.set_enabled(false);
            selection.add(troop_select);
        }
    }

    static private void create_board() {
        // Allocate space for entire board and fill with empty contents
        final int MAX_SIZE = (field_size + HOME_SIZE * 2) * 2;
        final int ARRAY_OFFSET = MAX_SIZE / 2;
        board = new ArrayList<>();
        for (int i = 0; i < MAX_SIZE; ++i) {
            board.add(new ArrayList<>());
            for (int j = 0; j < MAX_SIZE; ++j) {
                board.get(i).add(null);
            }
        }

        // Create board: Field (hex-shaped grid with side length this.field_size)
        int field_index = field_size - 1;
        for (int q = -field_index; q <= field_index; ++q) { // Generate grid based on qr-coordinates
            for (int r = Math.max(-field_index - q, -field_index); r <= Math.min(field_index - q, field_index); ++r) {
                // q and r must be converted to positive indices for array access
                board.get(q + ARRAY_OFFSET).set(r + ARRAY_OFFSET, new Cell(Cell.Type.field, q, r, 0.0f));
            }
        }

        // Create board: Bases (player and opponent home bases, HOME_SIZE wide)
        int base_index = field_size + 2;
        for (int dq = -3; dq <= 3; ++dq) {
            if (dq == 0) continue; // Bases are only indexed left and right (negative and positive)

            int q = (field_size - 1) * (int) Math.signum(dq) + dq; // Do the math
            for (int r = 0; r * -Math.signum(dq) < base_index; r += (int) -Math.signum(dq)) {
                if (Math.abs(dq) == 3 && r == 0) continue;
                if (Math.abs(dq) == 1 && Math.abs(r) == base_index - 1) continue;

                // q and r must be converted to positive indices for array access
                Cell.Type base_type = dq < 0 ? Cell.Type.player_territory : Cell.Type.opponent_territory;
                board.get(q + ARRAY_OFFSET).set(r + ARRAY_OFFSET, new Cell(base_type, q, r, 0.0f));
            }
        }
    }

    // Convert raw q and r coordinates to array-accessible coordinates
    static public Cell get(int q, int r) {
        Cell cell = null;
        final int MAX_SIZE = board.size();
        if (board.get(q + MAX_SIZE / 2) != null) {
            cell = board.get(q + MAX_SIZE / 2).get(r + MAX_SIZE / 2);
        }
        return cell;
    }

    static public void draw() {
        for (List<Cell> cells : Board.board) {
            for (Cell cell : cells) {
                if (cell != null) {
                    cell.draw();
                }
            }
        }
        for (Troop troop_select : selection) {
            troop_select.draw();
        }
    }
}
