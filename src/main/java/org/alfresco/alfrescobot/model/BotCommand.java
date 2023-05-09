package org.alfresco.alfrescobot.model;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.alfresco.alfrescobot.actions.api.BotAction;

public class BotCommand {

  private final CommandData commandData;
  private final BotAction action;

  public BotCommand(CommandData commandData, BotAction action) {
    this.commandData = commandData;
    this.action = action;
  }

  public CommandData getCommandData() {
    return commandData;
  }

  public BotAction getAction() {
    return action;
  }

}
