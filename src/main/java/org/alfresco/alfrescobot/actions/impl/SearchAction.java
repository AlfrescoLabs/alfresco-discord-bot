package org.alfresco.alfrescobot.actions.impl;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.alfresco.core.handler.NodesApi;
import org.alfresco.search.handler.SearchApi;
import org.alfresco.search.model.RequestQuery;
import org.alfresco.search.model.ResultSetPaging;
import org.alfresco.search.model.ResultSetRowEntry;
import org.alfresco.search.model.SearchRequest;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.function.Predicate;

import static net.dv8tion.jda.api.utils.MarkdownUtil.monospace;

@Component
public class SearchAction implements BotAction {

  public static final String SEARCH_MENU = "search_menu:search";
  private final NodesApi nodesApi;
  private final SearchApi searchApi;

  public SearchAction(NodesApi nodesApi, SearchApi searchApi) {
    this.nodesApi = nodesApi;
    this.searchApi = searchApi;
  }

  @Override
  public void execute(SlashCommandEvent event) {
    SelectionMenu.Builder builder = SelectionMenu.create(SEARCH_MENU).setMaxValues(1);
    String query = event.getOption("query").getAsString();
    ResultSetPaging resultSetPaging = searchApi.search(new SearchRequest().query(new RequestQuery().query(query)))
        .getBody();
    if (resultSetPaging != null) {
      resultSetPaging.getList().getEntries().stream().map(ResultSetRowEntry::getEntry)
          .forEach(entry -> builder.addOption(entry.getName(), entry.getId()));

      if (!CollectionUtils.isEmpty(builder.getOptions())) {
        event.deferReply(true).setContent("Select the file to download: ").addActionRow(builder.build()).queue();
      } else {
        noResultsFound(event, query);
      }
    } else {
      noResultsFound(event, query);
    }
  }

  private static void noResultsFound(GenericInteractionCreateEvent event, String query) {
    event.deferReply(true).setContent("No results found for: " + monospace(query)).queue();
  }

  @Override
  public void internalExecuteOnMenuEvent(SelectionMenuEvent event) {
    Resource nodeContent = nodesApi.getNodeContent(event.getInteraction().getSelectedOptions().get(0).getValue(),
        null, null, null).getBody();
    if (nodeContent != null && nodeContent.getFilename() != null) {
      try {
        event.deferEdit()
            .setContent("Your search has been processed.").setActionRows()
            .and(event.getChannel().sendMessage("<@" + event.getMember().getUser().getId() + ">")
                .addFile(nodeContent.getInputStream(), nodeContent.getFilename()))
            .queue();
      } catch (IOException e) {
        event.deferReply(true).setContent("Error: " + monospace(e.getMessage())).queue();
      }
    } else {
      event.deferReply(true).setContent("Could not download the selected node").queue();
    }
  }

  @Override
  public Predicate<SelectionMenuEvent> getSelectionMenuGuard() {
    return event -> event.getComponentId().equals(SEARCH_MENU);
  }

  @Override
  public void internalExecuteOnButtonClickEvent(ButtonClickEvent event) {
    throw new UnsupportedOperationException();
  }

}
