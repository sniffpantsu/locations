package net.runelite.client.plugins.playerDataLogger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;


@Extension
@PluginDescriptor(
        name = "Player Data",
        enabledByDefault = false,
        description = "Collect Player Data"
)
public class PlayerDataPlugin extends Plugin
{
    private static final String BASE_DIRECTORY = System.getProperty("user.home") + "/.runelite/playerdata/";

    @Inject
    private PlayerDataConfig config;

    @Inject
    private Client client;

    private Logger playerDataLogger;

    @Provides
    PlayerDataConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PlayerDataConfig.class);
    }

    @Override
    protected void startUp() {
        playerDataLogger = setupLogger();
    }

    @Subscribe
    private void onGametick(GameTick gameTick) throws IOException {
        // create object mapper instance

        if (config.enablePlayerDataCollection()) {
            List<Player> players = client.getPlayers();
            for (Player player : players) {
                PlayerDataEntry data = PlayerDataEntry.from(player);
                Gson gson = new GsonBuilder().create();
                playerDataLogger.info(gson.toJson(data));
            }
        }
    }
    private Logger setupLogger() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.start();

        String directory = BASE_DIRECTORY + "playerdata" + "/";

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setFile(directory + "latest.log");
        appender.setAppend(true);
        appender.setEncoder(encoder);
        appender.setContext(context);

        TimeBasedRollingPolicy<ILoggingEvent> logFilePolicy = new TimeBasedRollingPolicy<>();
        logFilePolicy.setContext(context);
        logFilePolicy.setParent(appender);
        logFilePolicy.setFileNamePattern(directory + "chatlog_%d{yyyy-MM-dd}.log");
        logFilePolicy.setMaxHistory(30);
        logFilePolicy.start();

        appender.setRollingPolicy(logFilePolicy);
        appender.start();

        Logger logger = context.getLogger("PlayerDataLogger");
        logger.detachAndStopAllAppenders();
        logger.setAdditive(false);
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);

        return logger;
    }
}
