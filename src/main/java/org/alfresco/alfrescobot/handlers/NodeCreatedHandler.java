package org.alfresco.alfrescobot.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.alfresco.event.sdk.handling.handler.OnNodeCreatedEventHandler;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.NodeResource;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;
import org.springframework.stereotype.Component;

import java.awt.*;

import static net.dv8tion.jda.api.utils.MarkdownUtil.*;

@Component
public class NodeCreatedHandler extends AbstractNodeEventHandler implements OnNodeCreatedEventHandler {

  @Override
  public void handleEvent(RepoEvent<DataAttributes<Resource>> repoEvent) {
    MessageChannel channel = getNotificationsChannel();
    if (channel != null) {
      NodeResource nodeResource = (NodeResource) repoEvent.getData().getResource();
      String nodeType = getNodeType(repoEvent);
      String notification = String.format("A new %s has been created by %s: %s" + italics("\n(nodeId: %s)"), nodeType,
          bold(nodeResource.getCreatedByUser().getDisplayName()), monospace(nodeResource.getName()),
          monospace(nodeResource.getId()));
      channel.sendMessageEmbeds(new EmbedBuilder().setDescription(notification).setColor(Color.green).build()).queue();
    }
  }

}
