package net.runelite.client.plugins.playerDataLogger;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import static net.runelite.client.plugins.playerDataLogger.PlayerDataConfig.*;

@ConfigGroup(GROUP_NAME)
public interface PlayerDataConfig extends Config {

    String GROUP_NAME = "playerData";

    @ConfigItem(
            keyName = "collect",
            name = "Collect",
            description = "Enable Player Data Collection",
            position = 0
    )
    default boolean enablePlayerDataCollection() {
        return true;
    }

}