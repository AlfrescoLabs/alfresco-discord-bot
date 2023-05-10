package org.alfresco.alfrescobot.configuration;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toMap;

@Configuration
@EnableConfigurationProperties(BotProps.class)
public class BotConfig {

  @Bean
  public Map<String, BotAction> botCommandsByName(List<CommandData> commandData, List<BotAction> botActions) {
    return commandData.stream().collect(
        toMap(CommandData::getName, data -> botActions.stream().filter(matchesCommand(data)).findFirst().get()));
  }

  private Predicate<? super BotAction> matchesCommand(CommandData commandData) {
    String sanitizedCommandName = commandData.getName().replace("_", "");
    return botAction -> {
      String className = botAction.getClass().getSimpleName();
      String sanitizedClassName = className.substring(0, className.lastIndexOf("Action"));
      return sanitizedCommandName.equalsIgnoreCase(sanitizedClassName);
    };
  }

  @Bean
  public CommandData helpCommand() {
    return new CommandData("help", "Display the available commands");
  }

  @Bean
  public CommandData pingCommand() {
    return new CommandData("ping", "Get a pong");
  }

  @Bean
  public CommandData licenseCommand() {
    return new CommandData("license", "When the license will expire");
  }

  @Bean
  public CommandData downloadCommand() {
    return new CommandData("download", "Download a file").addOption(OptionType.STRING, "node_id",
        "The nodeId of the file to download", true);
  }

  @Bean
  public CommandData searchCommand() {
    return new CommandData("search", "Look for a file").addOption(OptionType.STRING, "query",
        "The query to look for specific files", true);
  }

  @Bean
  public CommandData uploadCommand() {
    return new CommandData("upload", "Upload a file to My Files").addOption(OptionType.STRING, "file_name",
            "The name of the file to upload", true)
        .addOption(OptionType.STRING, "file_url", "The URL of the file to upload", true);
  }

  @Bean
  public CommandData deleteCommand() {
    return new CommandData("delete", "Delete a node by id").addOption(OptionType.STRING, "node_id",
        "The id of the node to delete", true);
  }

}
