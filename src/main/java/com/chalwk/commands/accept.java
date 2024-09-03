/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.GameManager;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class accept implements CommandInterface {

    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();

    private final GameManager gameManager;

    public accept(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public String getDescription() {
        return "Accept an invite to play a game";
    }

    @Override
    public List<OptionData> getOptions() {
        return new ArrayList<>();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.isOnCooldown(event)) return;

        if (settings.notCorrectChannel(event)) return;

        User acceptingPlayer = event.getUser();

        if (gameManager.isInGame(acceptingPlayer)) {
            event.reply("## You are already in a game.").setEphemeral(true).queue();
            return;
        } else if (gameManager.getPendingInvites().containsKey(acceptingPlayer)) {
            gameManager.acceptInvite(acceptingPlayer, event);
        } else {
            event.reply("## You don't have any pending invites.").setEphemeral(true).queue();
        }

        COOLDOWN_MANAGER.setCooldown(getName(), event.getUser());
    }
}
