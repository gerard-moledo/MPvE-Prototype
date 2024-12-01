package com.gmoledo.mpve;

import java.util.ArrayList;
import java.util.List;

public class Board {
    List<List<Boolean>> board;
    int size;

    Board(int size) {
        this.size = size;

        create_board();
    }

    private void create_board() {
        // Allocate space for entire board and fill with empty contents
        board = new ArrayList<>();
        for (int i = 0; i < this.size * 2; ++i) {
            board.add(new ArrayList<>());
            for (int j = 0; j < this.size * 2; ++j) {
                board.get(i).add(false);
            }
        }

        // Create board (hex-shaped, hexagonal grid with side length this.size)
        int board_index = this.size - 1;
        for (int q = -board_index; q <= board_index; ++q) { // Generate grid based on qr-coordinates
            for (int r = Math.max(-board_index - q, -board_index); r <= Math.min(board_index - q, board_index); ++r) {
                System.out.format("(%d, %d), ", q, r);

                // q and r must be converted to positive indices for array access
                board.get(q + this.size).set(r + this.size, true);
            }
            System.out.println();
        }
    }
}
