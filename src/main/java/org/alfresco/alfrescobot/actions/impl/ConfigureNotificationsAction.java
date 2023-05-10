package org.alfresco.alfrescobot.actions.impl;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.alfresco.alfrescobot.handlers.AbstractNodeEventHandler;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

@Component
public class ConfigureNotificationsAction implements BotAction {

  private final List<AbstractNodeEventHandler> nodeEventHandlers;

  public ConfigureNotificationsAction(List<AbstractNodeEventHandler> nodeEventHandlers) {
    this.nodeEventHandlers = nodeEventHandlers;
  }

  @Override
  public void execute(SlashCommandEvent event) {
    MessageChannel channel = event.getOption("channel").getAsMessageChannel();
    nodeEventHandlers.forEach(handler -> handler.setNotificationsChannel(channel));

    event.deferReply(true)
        .setContent(String.format("%s has been configured to receive ACS notifications.", monospace(channel.getName())))
        .queue();
  }

  @Override
  public void internalExecuteOnMenuEvent(SelectionMenuEvent event) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void internalExecuteOnButtonClickEvent(ButtonClickEvent event) {
    throw new UnsupportedOperationException();
  }

}
