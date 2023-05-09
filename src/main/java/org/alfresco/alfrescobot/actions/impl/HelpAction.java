package org.alfresco.alfrescobot.actions.impl;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

@Component
public class HelpAction implements BotAction {

  private final List<CommandData> commands;

  public HelpAction(List<CommandData> commands) {
    this.commands = commands;
  }

  @Override
  public void execute(SlashCommandEvent event) {
    event.deferReply(true)
        .setContent("The following commands are available: " + monospace(
            commands.stream().map(CommandData::getName).toList().toString()))
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
