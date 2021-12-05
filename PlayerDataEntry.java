package net.runelite.client.plugins.playerDataLogger;

import lombok.ToString;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;
import net.runelite.api.Player;

@ToString
public class PlayerDataEntry {
    private final String timestamp;
    private final long _id;
    private final int x;
    private final int y;
    private final int combatLevel;
    private final int primaryAnimation;
    private final int secondaryAnimation;
    private final boolean isFemale;
    private final boolean isMoving;
    private final String name;

    private PlayerDataEntry(Player player) {
        this.timestamp = ZonedDateTime.now(Clock.systemUTC()).toString();
        this._id = Objects.requireNonNull(player.getName()).hashCode();
        this.x = player.getWorldArea().getX();
        this.y = player.getWorldArea().getY();
        this.combatLevel = player.getCombatLevel();
        this.primaryAnimation = player.getAnimation();
        this.secondaryAnimation = player.getPoseAnimation();
        this.isFemale = Objects.requireNonNull(player.getPlayerComposition()).isFemale();
        this.isMoving = player.isMoving();
        this.name = player.getName();
    }

    public static PlayerDataEntry from (Player player) {
        return new PlayerDataEntry(player);
    }
}
