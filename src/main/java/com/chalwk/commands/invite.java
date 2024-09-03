/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */
package com.chalwk.commands;

import com.chalwk.CommandManager.CommandCooldownManager;
import com.chalwk.CommandManager.CommandInterface;
import com.chalwk.game.GameManager;
import com.chalwk.util.settings;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class invite implements CommandInterface {

    private static final CommandCooldownManager COOLDOWN_MANAGER = new CommandCooldownManager();
    private final GameManager gameManager;

    public invite(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invite a player to a game";
    }

    @Override
    public List<OptionData> getOptions() {

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "opponent", "The user to invite", true));
        OptionData option = new OptionData(OptionType.INTEGER, "size", "The size of the game board", true);

        option.addChoice("3x3", 3);
        option.addChoice("4x4", 4);
        option.addChoice("5x5", 5);
        option.addChoice("6x6", 6);
        option.addChoice("7x7", 7);
        option.addChoice("8x8", 8);
        option.addChoice("9x9", 9);

        options.add(option);
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (COOLDOWN_MANAGER.isOnCooldown(event)) return;

        if (settings.notCorrectChannel(event)) return;

        OptionMapping sizeOption = event.getOption("size");
        User userToInvite = event.getOption("opponent").getAsUser();
        User invitingPlayer = event.getUser();

        //if (isSelf(event, userToInvite, invitingPlayer)) return;

        int size = sizeOption.getAsInt();

        gameManager.invitePlayer(invitingPlayer, userToInvite, size, event);
        COOLDOWN_MANAGER.setCooldown(getName(), event.getUser());
    }

    private boolean isSelf(SlashCommandInteractionEvent event, User userToInvite, User invitingPlayer) {
        if (userToInvite.getId().equals(invitingPlayer.getId())) {
            event.reply("## You can't invite yourself to a game!").setEphemeral(true).queue();
            return true;
        }
        return false;
    }
}