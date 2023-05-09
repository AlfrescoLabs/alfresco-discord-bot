package org.alfresco.alfrescobot.actions.impl;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.core.model.NodeEntry;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

@Component
public class UploadAction implements BotAction {

  private final NodesApi nodesApi;

  public UploadAction(NodesApi nodesApi) {
    this.nodesApi = nodesApi;
  }

  @Override
  public void execute(SlashCommandEvent event) {
    NodeEntry nodeEntry = nodesApi.createNode("-my-",
        new NodeBodyCreate().nodeType("cm:content").name(event.getOption("file_name").getAsString()), null, null,
        null, null, null).getBody();

    if (nodeEntry != null) {
      Node node = nodeEntry.getEntry();
      String fileUrl = event.getOption("file_url").getAsString();
      URL url;
      try {
        url = new URL(fileUrl);
      } catch (MalformedURLException e) {
        event.deferReply(true).setContent("The requested URL is invalid: " + monospace(fileUrl)).queue();
        throw new IllegalArgumentException(e);
      }

      try (InputStream is = url.openStream()) {
        byte[] fileAsBytes = IOUtils.toByteArray(is);
        NodeEntry updatedNodeEntry = nodesApi.updateNodeContent(node.getId(), fileAsBytes, true, null, null, null, null)
            .getBody();
        if (updatedNodeEntry != null) {
          Node updatedNode = updatedNodeEntry.getEntry();
          event.reply("Content uploaded successfully with nodeId: " + monospace(updatedNode.getId())).queue();
        } else {
          event.deferReply(true).setContent("Failed to upload the desired document.").queue();
        }
      } catch (IOException e) {
        event.deferReply(true).setContent("Error: " + monospace(e.getMessage())).queue();
      }
    } else {
      event.deferReply(true).setContent("Could not create the desired node.").queue();
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
