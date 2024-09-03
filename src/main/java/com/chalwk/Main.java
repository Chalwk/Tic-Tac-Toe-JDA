/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk;

import com.chalwk.bot.BotInitializer;
import com.chalwk.game.Board;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

//        for (int i = 0; i < 500; i++) {
//            Board board = getRandomBoardWithSimulatedGame(3);
//            System.out.println(board.printBoard());
//            int progress = board.checkWinner();
//            if (progress == 1) {
//                System.out.println("Player 1 wins!");
//            } else if (progress == 2) {
//                System.out.println("Player 2 wins!");
//            } else if (progress == 0) {
//                System.out.println("It's a draw!");
//            } else {
//                System.out.println("The game is still in progress.");
//            }
//        }

        try {
            new BotInitializer().initializeBot();
        } catch (IOException e) {
            System.err.println("Error reading token or initializing the bot: " + e.getMessage());
        }
    }

    /**
     * Simulates a game board for testing purposes.
     *
     * @return A simulated game board.
     */
    @NotNull
    private static Board getRandomBoardWithSimulatedGame(int boardSize) {
        Board board = new Board(boardSize);
        String[][] simulatedGame = new String[boardSize][boardSize];

        Random random = new Random();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (simulatedGame[row][col] == null) {
                    int playerId = (random.nextInt(2) == 0) ? 1 : 2;
                    board.makeMove(row, col, playerId);

                    if (playerId == 1) {
                        simulatedGame[row][col] = "X";
                    } else {
                        simulatedGame[row][col] = "O";
                    }
                }
            }
        }

        return board;
    }
}
