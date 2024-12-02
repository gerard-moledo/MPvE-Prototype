package com.gmoledo.mpve;

import java.util.ArrayList;
import java.util.List;

final public class Board {
    static List<List<Cell>> board;
    static int field_size;
    static final int HOME_SIZE = 3;

    static public void Instantiate(int field_size) {
        Board.field_size = field_size;

        create_board();
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
                board.get(q + ARRAY_OFFSET).set(r + ARRAY_OFFSET, new Cell(Cell.Type.field, q, r));
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
                Cell.Type base_type = dq < 0 ? Cell.Type.player_base : Cell.Type.opponent_base;
                board.get(q + ARRAY_OFFSET).set(r + ARRAY_OFFSET, new Cell(base_type, q, r));
            }
        }
    }

    // Convert raw q and r coordinates to array-accessible coordinates
    static public boolean get_index(int q, int r) {
        final int MAX_SIZE = board.size();

        return board.get(q + MAX_SIZE / 2) != null && board.get(q + MAX_SIZE / 2).get(r + MAX_SIZE / 2) != null;
    }

    static public void draw() {
        for (List<Cell> cells : Board.board) {
            for (Cell cell : cells) {
                if (cell != null) {
                    cell.draw();
                }
            }
        }
    }
}
