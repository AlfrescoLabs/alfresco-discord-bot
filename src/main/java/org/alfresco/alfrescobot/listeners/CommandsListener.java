package org.alfresco.alfrescobot.listeners;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.alfresco.alfrescobot.constants.Emojis;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.core.model.Node;
import org.alfresco.core.model.NodeBodyCreate;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.RequestQuery;
import org.alfresco.search.model.ResultSetPaging;
import org.alfresco.search.model.ResultSetRowEntry;
import org.alfresco.search.model.SearchRequest;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

@Component
public class CommandsListener extends ListenerAdapter {

  private final Map<String, CommandData> botCommandsByName;
  private final NodesApi nodesApi;
  private final SearchApi searchApi;

  public CommandsListener(@Qualifier("botCommandsByName") Map<String, CommandData> botCommandsByName, NodesApi nodesApi,
                          SearchApi searchApi) {
    this.botCommandsByName = botCommandsByName;
    this.nodesApi = nodesApi;
    this.searchApi = searchApi;
  }


  @Override
  public void onSlashCommand(@NotNull SlashCommandEvent event) {
    switch (event.getName()) {
      case "help" ->
          event.reply("The following commands are available: " + monospace(botCommandsByName.keySet().toString()))
              .setEphemeral(true).queue();

      case "ping" -> event.reply("Pong").setEphemeral(true).queue();

      case "download" -> {
        Resource nodeContent = nodesApi.getNodeContent(event.getOption("node_id").getAsString(), null, null, null)
            .getBody();
        try {
          event.deferReply().addFile(nodeContent.getInputStream(), nodeContent.getFilename()).queue();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      case "upload" -> {
        Node fileNode = nodesApi.createNode("-my-",
            new NodeBodyCreate().nodeType("cm:content").name(event.getOption("file_name").getAsString()), null, null,
            null, null, null).getBody().getEntry();

        String fileUrl = event.getOption("file_url").getAsString();
        URL url;
        try {
          url = new URL(fileUrl);
        } catch (MalformedURLException e) {
          throw new RuntimeException(e);
        }

        try (InputStream is = url.openStream()) {
          byte[] fileAsBytes = IOUtils.toByteArray(is);
          Node updatedNode = nodesApi.updateNodeContent(fileNode.getId(), fileAsBytes, true, null, null, null, null)
              .getBody().getEntry();
          event.reply("Content uploaded successfully with nodeId: " + monospace(updatedNode.getId())).queue();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      case "delete" -> {
        String nodeId = event.getOption("node_id").getAsString();
        event.deferReply(true)
            .setContent("Are you sure you want to delete the following node: " + monospace(nodeId) + "?")
            .addActionRow(
                Button.danger("button:delete_" + nodeId, "Yes, I'm sure").withEmoji(Emojis.WASTEBASKET),
                Button.success("button:deny", "No, I changed my mind").withEmoji(Emojis.CHECKMARK)
            )
            .queue();
      }

      case "search" -> {
        SelectionMenu.Builder builder = SelectionMenu.create("menu:search").setMaxValues(1);
        String query = event.getOption("query").getAsString();
        ResultSetPaging resultSetPaging = searchApi.search(new SearchRequest().query(new RequestQuery().query(query)))
            .getBody();
        resultSetPaging.getList().getEntries().stream().map(ResultSetRowEntry::getEntry)
            .forEach(entry -> builder.addOption(entry.getName(), entry.getId()));

        if (!CollectionUtils.isEmpty(builder.getOptions())) {
          event.deferReply(true).setContent("Select the file to download: ").addActionRow(builder.build()).queue();
        } else {
          event.deferReply(true).setContent("No results found for: " + monospace(query)).queue();
        }
      }

      default -> event.reply("Unrecognized or not implemented command: " + monospace(event.getName())).queue();
    }
  }

  @Override
  public void onSelectionMenu(@NotNull SelectionMenuEvent event) {
    if (event.getComponentId().equals("menu:search")) {
      Resource nodeContent = nodesApi.getNodeContent(event.getInteraction().getSelectedOptions().get(0).getValue(),
          null, null, null).getBody();
      try {
        event.deferEdit()
            .setContent("Your search has been processed.").setActionRows()
            .and(event.getChannel().sendMessage("<@" + event.getMember().getUser().getId() + ">")
                .addFile(nodeContent.getInputStream(), nodeContent.getFilename()))
            .queue();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void onButtonClick(@NotNull ButtonClickEvent event) {
    String componentId = event.getComponentId();
    if (componentId.equals("button:deny")) {
      event.deferEdit().setContent("The node has " + bold("NOT") + " been deleted.").setActionRows().queue();
    } else if (componentId.startsWith("button:delete_")) {
      String nodeId = componentId.substring(componentId.indexOf("_") + 1);
      nodesApi.deleteNode(nodeId, true);
      event.deferEdit().setContent("The node with the following id has been deleted: " + monospace(nodeId))
          .setActionRows()
          .queue();
    }
  }
}
