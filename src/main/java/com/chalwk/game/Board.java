/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.game;

public class Board {

    private final int rows;
    private final int cols;
    private final int[][] board;

    public Board(int size) {
        this.board = new int[size][size];
        this.rows = size;
        this.cols = size;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                board[row][col] = 0;
            }
        }
    }

    public int makeMove(int row, int col, int player) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return 0; // invalid move
        }

        if (board[row][col] == 0) {
            board[row][col] = player;
        } else {
            return 1; // cell already occupied
        }

        return 2; // move successful
    }

    public int checkWinner() {
        // Check rows
        for (int row = 0; row < rows; row++) {
            if (checkRow(row)) {
                return board[row][0]; // Player 1 or 2 wins
            }
        }
        // Check columns
        for (int col = 0; col < cols; col++) {
            if (checkColumn(col)) {
                return board[0][col]; // Player 1 or 2 wins
            }
        }
        // Check diagonals
        if (checkDiagonal(true)) {
            return board[0][0]; // Player 1 or 2 wins
        }
        if (checkDiagonal(false)) {
            return board[0][cols - 1]; // Player 1 or 2 wins
        }
        // Check if the board is full
        if (isBoardFull()) {
            return 0; // Tie
        }
        return -1; // Game still in progress
    }

    private boolean checkRow(int row) {
        int check = board[row][0];
        if (check != 0) {
            for (int col = 1; col < cols; col++) {
                if (board[row][col] != check) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkColumn(int col) {
        int check = board[0][col];
        if (check != 0) {
            for (int row = 1; row < rows; row++) {
                if (board[row][col] != check) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkDiagonal(boolean forward) {
        if (forward) {
            int check = board[0][0];
            for (int i = 1; i < Math.min(rows, cols); i++) {
                if (board[i][i] != check) {
                    return false;
                }
            }
        } else {
            int check = board[0][cols - 1];
            for (int i = 1; i < Math.min(rows, cols); i++) {
                if (board[i][cols - i - 1] != check) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (board[row][col] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getROWS() {
        return rows;
    }

    public String printBoard() {
        StringBuilder sb = new StringBuilder();

        int maxColNumberLength = ("" + cols).length();
        int maxRowNumberLength = ("" + rows).length();

        // Print column numbers
        sb.append(System.lineSeparator());
        for (int col = 0; col < cols; col++) {
            if (col == 0) {
                sb.append("    ").append(String.format("%" + maxColNumberLength + "d", col)).append("   "); // Added 4 spaces before the first column number
            } else {
                sb.append(String.format("%" + maxColNumberLength + "d", col)).append("   ");
            }
        }
        sb.append(System.lineSeparator());

        // Print board
        for (int row = 0; row < rows; row++) {

            // Print row number
            String rowStr = String.format("%" + maxRowNumberLength + "d", row);
            sb.append(rowStr).append(" ");

            // Print row content
            for (int col = 0; col < cols; col++) {
                sb.append("| ");
                if (board[row][col] == 1) {
                    sb.append("X");
                } else if (board[row][col] == 2) {
                    sb.append("O");
                } else {
                    sb.append(" ");
                }
                sb.append(" ");
            }
            sb.append("|").append(System.lineSeparator());

            // Print separator
            if (row < rows - 1) {
                sb.append("  |");
                sb.append("---+".repeat(Math.max(0, cols)));
                sb.deleteCharAt(sb.length() - 1);
                sb.append("|").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }
}