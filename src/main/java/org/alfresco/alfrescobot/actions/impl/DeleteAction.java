package org.alfresco.alfrescobot.actions.impl;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.alfresco.alfrescobot.constants.Emojis;
import org.alfresco.core.handler.NodesApi;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

import static net.dv8tion.jda.api.utils.MarkdownUtil.bold;
import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

@Component
public class DeleteAction implements BotAction {

  private static final String DELETE_BUTTON = "delete_btn:confirm__";
  private static final String DENY_BUTTON = "delete_btn:deny";
  private final NodesApi nodesApi;

  public DeleteAction(NodesApi nodesApi) {
    this.nodesApi = nodesApi;
  }

  @Override
  public void execute(SlashCommandEvent event) {
    String nodeId = event.getOption("node_id").getAsString();
    event.deferReply(true)
        .setContent("Are you sure you want to delete the following node: " + monospace(nodeId) + "?")
        .addActionRow(
            Button.danger(DELETE_BUTTON + nodeId, "Yes, I'm sure").withEmoji(Emojis.WASTEBASKET),
            Button.success(DENY_BUTTON, "No, I changed my mind").withEmoji(Emojis.CHECKMARK)
        )
        .queue();
  }

  @Override
  public void internalExecuteOnMenuEvent(SelectionMenuEvent event) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void internalExecuteOnButtonClickEvent(ButtonClickEvent event) {
    String componentId = event.getComponentId();
    if (componentId.equals(DENY_BUTTON)) {
      event.deferEdit().setContent("The node has " + bold("NOT") + " been deleted.").setActionRows().queue();
    } else if (componentId.startsWith(DELETE_BUTTON)) {
      String nodeId = componentId.substring(componentId.indexOf("__") + 2);
      nodesApi.deleteNode(nodeId, true);
      event.deferEdit().setContent("The node with the following id has been deleted: " + monospace(nodeId))
          .setActionRows()
          .queue();
    }
  }

  @Override
  public Predicate<ButtonClickEvent> getButtonClickGuard() {
    return event -> event.getComponentId().equals(DENY_BUTTON) ||
        event.getComponentId().startsWith(DELETE_BUTTON);
  }

}
