/* Copyright (c) 2024 Jericho Crosby <jericho.crosby227@gmail.com>. Licensed under GNU General Public License v3.0.
   See the LICENSE file or visit https://www.gnu.org/licenses/gpl-3.0.en.html for details. */

package com.chalwk.util;

import com.chalwk.game.GameManager;
import com.chalwk.util.Logging.Logger;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class fileIO {

    private static final String CONFIG_FILE = "/config.txt";

    /**
     * Loads the channel ID from the configuration file.
     *
     * @return The channel ID or an empty string if not found.
     */
    public static String loadChannelID() {
        try (Stream<String> lines = Files.lines(Paths.get(fileIO.class.getResource(CONFIG_FILE).toURI()))) {
            return lines.filter(line -> !line.trim().isEmpty())
                    .findFirst()
                    .map(String::trim)
                    .orElse("");
        } catch (IOException | URISyntaxException e) {
            Logger.info("Failed to load Channel ID: " + e.getMessage());
            return ""; // return an empty string for better handling in callers
        }
    }

    /**
     * Saves the given channel ID to the configuration file.
     *
     * @param channelID      The channel ID to save.
     * @param isAddOperation True if adding the channel ID, false if removing it.
     * @param event          The interaction event to respond to the user.
     * @param gameManager    The game manager instance.
     */
    public static void saveChannelID(String channelID, boolean isAddOperation, SlashCommandInteractionEvent event, GameManager gameManager) {
        try {
            URI fileUri = fileIO.class.getResource(CONFIG_FILE).toURI();
            Path filePath = Paths.get(fileUri);

            // Ensure the file exists
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            List<String> lines = Files.readAllLines(filePath);

            if (isAddOperation) {
                lines.add(channelID);
                event.reply("## Channel ID saved!").setEphemeral(true).queue();
                gameManager.setChannelID(channelID);
            } else {
                lines.removeIf(line -> line.trim().equals(channelID));
                event.reply("## Channel ID removed!").setEphemeral(true).queue();
                gameManager.setChannelID("");
            }

            Files.writeString(filePath, String.join("\n", lines));
        } catch (URISyntaxException e) {
            Logger.info("Failed to parse file URI: " + e.getMessage());
            event.reply("## Failed to read data from the config file!").setEphemeral(true).queue();
        } catch (IOException e) {
            Logger.info("Failed to read or write data: " + e.getMessage());
            event.reply("## Failed to save channel ID!").setEphemeral(true).queue();
        }
    }

    /**
     * Checks if the given channel ID is configured in the file.
     *
     * @param channelID The channel ID to check.
     * @return True if the channel ID is configured, false otherwise.
     */
    public static boolean isChannelIdConfigured(String channelID) {
        if (channelID == null || channelID.isEmpty()) {
            return false;
        } else if (!GameManager.getChannelID().isEmpty()) {
            return true;
        }

        try (Stream<String> lines = Files.lines(Paths.get(fileIO.class.getResource(CONFIG_FILE).toURI()))) {
            return lines.anyMatch(line -> line.trim().equals(channelID));
        } catch (IOException | URISyntaxException e) {
            Logger.info("Failed to read data: " + e.getMessage());
            return false;
        }
    }
}
