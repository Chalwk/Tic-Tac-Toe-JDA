/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.game;

import com.chalwk.util.settings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.chalwk.bot.BotInitializer.getShardManager;

/**
 * Represents a game between two players, managing game-related operations such as starting a game and scheduling game end tasks.
 */
public class Game {

    private final User invitingPlayer;
    private final User invitedPlayer;
    private final GameManager gameManager;
    private final Board board;
    private String embedID;
    private int whos_turn;
    private Date startTime;
    private TimerTask gameEndTask;

    /**
     * Creates a new game with the specified players and event.
     *
     * @param invitingPlayer the player who initiated the game
     * @param invitedPlayer  the player who was invited to join the game
     * @param event          the event that triggered the game start
     * @param gameManager    the game manager
     * @param size           the size of the game board
     */
    public Game(User invitingPlayer, User invitedPlayer, SlashCommandInteractionEvent event, GameManager gameManager, int size) {
        this.invitingPlayer = invitingPlayer;
        this.invitedPlayer = invitedPlayer;
        this.whos_turn = getStartingPlayer();
        this.gameManager = gameManager;
        this.board = new Board(size);
        startGame(event);
    }

    public EmbedBuilder createGameEmbed() {
        User player = this.whos_turn == 1 ? invitingPlayer : invitedPlayer;
        return new EmbedBuilder()
                .setTitle("⭕❌ TIC TAC TOE ❌⭕")
                .addField("Players: ", this.getInvitingPlayer().getAsMention() + " VS " + this.getInvitedPlayer().getAsMention(), true)
                .addField("Board: ", "```" + board.printBoard() + "```", false)
                .setFooter("Turn: " + player.getEffectiveName() + " (" + (this.whos_turn == 1 ? "❌" : "⭕") + ")")
                .setColor(Color.BLUE);
    }

    /**
     * Updates the game embed with the current game state.
     *
     * @param event the event that triggered the game update
     * @param state the current state of the game
     */
    public void updateGameEmbed(SlashCommandInteractionEvent event, BoardState state) {

        EmbedBuilder embed = createGameEmbed();

        if (state == BoardState.IN_PROGRESS) {
            embed.setColor(Color.BLUE);
        } else if (state == BoardState.PLAYER1_WINS) {
            embed.setColor(Color.GREEN);
            embed.setFooter("Game Over! " + invitingPlayer.getAsMention() + " wins!");
            endGame();
        } else if (state == BoardState.PLAYER2_WINS) {
            embed.setColor(Color.RED);
            embed.setFooter("Game Over! " + invitedPlayer.getAsMention() + " wins!");
            endGame();
        } else if (state == BoardState.DRAW) {
            embed.setColor(Color.YELLOW);
            embed.setFooter("Game Over! It's a draw!");
            endGame();
        }

        event.getChannel().deleteMessageById(getEmbedID()).queue();
        event.replyEmbeds(embed.build()).queue();
        setMessageID(event);
    }

    /**
     * Gets the game board.
     *
     * @return the game board
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Gets the ID of the message embed for the game.
     *
     * @return the ID of the message embed for the game
     */
    public String getEmbedID() {
        return this.embedID;
    }

    /**
     * Sets the ID of the message embed for the game.
     *
     * @param embedID the ID of the message embed for the game
     */
    private void setEmbedID(String embedID) {
        this.embedID = embedID;
    }

    /**
     * Sets the player whose turn it is to play.
     */
    public void setWhosTurn() {
        this.whos_turn = this.whos_turn == 1 ? 2 : 1;
    }

    /**
     * Gets the player whose turn it is to play.
     *
     * @return the player whose turn it is to play
     */
    public int getWhosTurn() {
        return this.whos_turn;
    }

    /**
     * Starts the game, sends a notification to both players, and schedules the game end task.
     *
     * @param event the event that triggered the game start
     */
    public void startGame(SlashCommandInteractionEvent event) {
        this.startTime = new Date();
        scheduleGameEndTask();
        event.replyEmbeds(createGameEmbed().build()).queue();
        setMessageID(event);
    }

    /**
     * Ends the game
     */
    public void endGame() {
        cancelGameEndTask();
        gameManager.removeGame(invitingPlayer, invitedPlayer);
    }

    /**
     * Schedules a task to end the game when the default time limit is reached.
     */
    private void scheduleGameEndTask() {
        if (gameEndTask != null) {
            gameEndTask.cancel();
        }
        gameEndTask = new TimerTask() {
            @Override
            public void run() {
                if (isTimeUp()) {
                    this.cancel();
                    String channelID = GameManager.getChannelID();
                    TextChannel channel = getShardManager().getTextChannelById(channelID);
                    channel.sendMessage("Times up! Game between " + invitingPlayer.getAsMention() + " and " + invitedPlayer.getAsMention() + " has ended!").queue();
                    gameManager.removeGame(invitingPlayer, invitedPlayer);
                }
            }
        };

        Timer gameEndTimer = new Timer();
        gameEndTimer.scheduleAtFixedRate(gameEndTask, 0, 1000);
    }

    /**
     * Sets the ID of the message embed for the game after a delay.
     *
     * @param event the event associated with the command execution
     */
    private void setMessageID(SlashCommandInteractionEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setEmbedID(event.getChannel().getLatestMessageId());
            }
        }, 700);
    }

    /**
     * Checks if the default time limit for the game has been exceeded.
     *
     * @return true if the time limit has been exceeded, false otherwise
     */
    private boolean isTimeUp() {
        long elapsedTime = System.currentTimeMillis() - startTime.getTime();
        return elapsedTime > settings.getDefaultTimeLimit() * 1000L;
    }

    /**
     * Gets the player who initiated the game.
     *
     * @return the player who initiated the game
     */
    public User getInvitingPlayer() {
        return this.invitingPlayer;
    }

    /**
     * Gets the player who was invited to join the game.
     *
     * @return the player who was invited to join the game
     */
    public User getInvitedPlayer() {
        return this.invitedPlayer;
    }

    /**
     * Gets the player who starts the game.
     *
     * @return the player who starts the game
     */
    public int getStartingPlayer() {
        return new Random().nextInt(2) + 1;
    }

    /**
     * Checks if the specified player is in the game.
     *
     * @param player the player to check
     * @return true if the player is in the game, false otherwise
     */
    public boolean isPlayer(User player) {
        return player.equals(invitingPlayer) || player.equals(invitedPlayer);
    }

    /**
     * Cancels the game end task.
     */
    private void cancelGameEndTask() {
        if (gameEndTask != null) {
            gameEndTask.cancel();
            gameEndTask = null;
        }
    }
}