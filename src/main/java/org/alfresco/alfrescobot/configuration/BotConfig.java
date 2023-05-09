package org.alfresco.alfrescobot.configuration;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Configuration
@EnableConfigurationProperties(BotProps.class)
public class BotConfig {

  @Bean
  public Map<String, CommandData> botCommandsByName(List<CommandData> botCommands) {
    return botCommands.stream().collect(toMap(CommandData::getName, identity()));
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
  public CommandData downloadCommand() {
    return new CommandData("download", "Download a file")
        .addOption(OptionType.STRING, "node_id", "The nodeId of the file to download", true);
  }

  @Bean
  public CommandData searchCommand() {
    return new CommandData("search", "Look for a file")
        .addOption(OptionType.STRING, "query", "The query to look for specific files", true);
  }

  @Bean
  public CommandData uploadCommand() {
    return new CommandData("upload", "Upload a file to My Files")
        .addOption(OptionType.STRING, "file_name", "The name of the file to upload", true)
        .addOption(OptionType.STRING, "file_url", "The URL of the file to upload", true);
  }

  @Bean
  public CommandData deleteCommand() {
    return new CommandData("delete", "Delete a node by id")
        .addOption(OptionType.STRING, "node_id", "The id of the node to delete", true);
  }

}
