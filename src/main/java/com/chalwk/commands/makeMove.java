/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.Board;
import com.chalwk.game.BoardState;
import com.chalwk.game.Game;
import com.chalwk.game.GameManager;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class makeMove implements CommandInterface {

    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();
    private final GameManager gameManager;

    public makeMove(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "move";
    }

    @Override
    public String getDescription() {
        return "Accept an invite to play a game";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "row", "The row number", true),
                new OptionData(OptionType.INTEGER, "col", "The col number", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.isOnCooldown(event)) return;

        if (settings.notCorrectChannel(event)) return;

        User player = event.getUser();

        if (!gameManager.isInGame(player)) {
            event.reply("## You are not in a game.").setEphemeral(true).queue();
            return;
        }

        Game game = gameManager.getGame(player);
        if (!game.isPlayer(player)) return; // only the players in this specific game can play

        int row = event.getOption("row").getAsInt();
        int col = event.getOption("col").getAsInt();

        Board board = game.getBoard();

        int makeMove = board.makeMove(row, col, game.getWhosTurn());
        if (makeMove == 0) {
            event.reply("Invalid move. Row and column numbers must be between 0 and " + (board.getROWS() - 1) + ".").setEphemeral(true).queue();
            return;
        } else if (makeMove == 1) {
            event.reply("Invalid move. The cell is already occupied.").setEphemeral(true).queue();
            return;
        }

        // Check the game state after the move
        BoardState state = determineGameState(board);
        if (state != BoardState.IN_PROGRESS) {
            event.reply("The game has already ended!").setEphemeral(true).queue();
            return;
        }

        game.setWhosTurn();
        game.updateGameEmbed(event, state);
        COOLDOWN_MANAGER.setCooldown(getName(), event.getUser());
    }

    /**
     * Determines the current game state based on the board.
     *
     * @param board the current game board
     * @return the current state of the game
     */
    private BoardState determineGameState(Board board) {
        int progress = board.checkWinner();
        return switch (progress) {
            case 1 -> BoardState.PLAYER1_WINS;
            case 2 -> BoardState.PLAYER2_WINS;
            case 0 -> BoardState.DRAW;
            default -> BoardState.IN_PROGRESS;
        };
    }
}
