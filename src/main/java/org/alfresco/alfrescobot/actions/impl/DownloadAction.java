package org.alfresco.alfrescobot.actions.impl;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.alfresco.core.handler.NodesApi;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

@Component
public class DownloadAction implements BotAction {

  private final NodesApi nodesApi;

  public DownloadAction(NodesApi nodesApi) {
    this.nodesApi = nodesApi;
  }

  @Override
  public void execute(SlashCommandEvent event) {
    Resource nodeContent = nodesApi.getNodeContent(event.getOption("node_id").getAsString(), null, null, null)
        .getBody();
    try {
      if (nodeContent != null && nodeContent.getFilename() != null) {
        event.deferReply().addFile(nodeContent.getInputStream(), nodeContent.getFilename()).queue();
      } else {
        event.deferReply(true).setContent("Could not find the specified node.").queue();
      }
    } catch (IOException e) {
      event.deferReply(true).setContent("Error: " + monospace(e.getMessage())).queue();
    }
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
