package org.alfresco.alfrescobot.listeners;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommandsListener extends ListenerAdapter {

  private final Map<String, BotAction> botCommandsByName;

  public CommandsListener(@Qualifier("botCommandsByName") Map<String, BotAction> botCommandsByName) {
    this.botCommandsByName = botCommandsByName;
  }


  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    botCommandsByName.get(event.getName()).execute(event);
  }

  @Override
  public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
    botCommandsByName.values().forEach(botAction -> botAction.executeOnMenuEvent(event));

  }

  @Override
  public void onButtonClick(@NotNull ButtonClickEvent event) {
    botCommandsByName.values().forEach(botAction -> botAction.executeOnButtonClickEvent(event));
  }
}
