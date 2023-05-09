package org.alfresco.alfrescobot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.alfresco.alfrescobot.configuration.BotProps;
import org.alfresco.alfrescobot.listeners.CommandsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.security.auth.login.LoginException;
import java.util.List;

@SpringBootApplication
public class AlfrescoBotApplication implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoBotApplication.class);
  private final BotProps botProps;
  private final CommandsListener commandsListener;
  private final List<CommandData> botCommands;

  public AlfrescoBotApplication(BotProps botProps, CommandsListener commandsListener,
                                List<CommandData> botCommands) {
    this.botProps = botProps;
    this.commandsListener = commandsListener;
    this.botCommands = botCommands;
  }

  public static void main(String[] args) {
    SpringApplication.run(AlfrescoBotApplication.class, args);
  }

  @Override
  public void run(String... args) {
    JDABuilder jdaBuilder = JDABuilder.createDefault(botProps.getToken());
    jdaBuilder.setStatus(OnlineStatus.ONLINE);
    jdaBuilder.setAutoReconnect(true);
    jdaBuilder.setActivity(Activity.watching("for commands"));
    jdaBuilder.addEventListeners(commandsListener);

    try {
      JDA jda = jdaBuilder.build();
      botCommands.stream().forEach(command -> jda.upsertCommand(command).queue());
      jda.awaitReady();
    } catch (LoginException | IllegalArgumentException | InterruptedException e) {
      LOGGER.error("Error while trying to start Alfresco Bot: ", e);
    }
  }
}
