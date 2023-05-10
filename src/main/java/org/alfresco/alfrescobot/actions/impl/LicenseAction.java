package org.alfresco.alfrescobot.actions.impl;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.alfresco.alfrescobot.actions.api.BotAction;
import org.alfresco.discovery.handler.DiscoveryApi;
import org.alfresco.discovery.model.DiscoveryEntry;
import org.alfresco.discovery.model.LicenseInfo;
import org.springframework.stereotype.Component;

@Component
public class LicenseAction implements BotAction {

  private final DiscoveryApi discoveryApi;

  public LicenseAction(DiscoveryApi discoveryApi) {
    this.discoveryApi = discoveryApi;
  }

  @Override
  public void execute(SlashCommandEvent event) {
    DiscoveryEntry entry = discoveryApi.getRepositoryInformation().getBody();
    LicenseInfo license = entry.getEntry().getRepository().getLicense();
    Integer remainingDays = license.getRemainingDays();
    event.deferReply(true)
            .setContent(String.format("You are good for %s more days", remainingDays))
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
