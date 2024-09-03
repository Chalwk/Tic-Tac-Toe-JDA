/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.bot;

import com.chalwk.CommandManager.CommandListener;
import com.chalwk.commands.*;
import com.chalwk.game.GameManager;
import com.chalwk.util.authentication;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.IOException;

/**
 * A class responsible for initializing and setting up the bot for the Virtual Pets game project.
 */
public class BotInitializer {

    /**
     * An instance of the PetDataHandler class to manage pet data.
     */
    public static ShardManager shardManager;

    public static GameManager gameManager;

    /**
     * The bot's authentication token.
     */
    private final String token;

    /**
     * Constructs a BotInitializer instance and retrieves the bot's authentication token.
     *
     * @throws IOException if there's an error reading the token file.
     */
    public BotInitializer() throws IOException {
        gameManager = new GameManager();
        this.token = authentication.getToken();
    }

    public static GameManager getGameManager() {
        return gameManager;
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    /**
     * Initializes the bot and sets up event listeners and commands.
     */
    public void initializeBot() {

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(this.token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Tic-Tac-Toe"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.MESSAGE_CONTENT);

        shardManager = builder.build();
        registerCommands(shardManager);
    }

    /**
     * Registers the available commands for the bot.
     *
     * @param shardManager The ShardManager instance used to manage the bot.
     */
    private void registerCommands(ShardManager shardManager) {
        CommandListener commands = new CommandListener();
        commands.add(new invite(gameManager));
        commands.add(new accept(gameManager));
        commands.add(new decline(gameManager));
        commands.add(new channel(gameManager));
        commands.add(new cancel(gameManager));
        commands.add(new makeMove(gameManager));
        shardManager.addEventListener(commands);
    }
}
