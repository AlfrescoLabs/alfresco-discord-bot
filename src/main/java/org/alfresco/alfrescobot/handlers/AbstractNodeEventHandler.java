package org.alfresco.alfrescobot.handlers;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.alfresco.event.sdk.handling.filter.IsFolderFilter;
import org.alfresco.event.sdk.handling.filter.NodeTypeFilter;
import org.alfresco.event.sdk.model.v1.model.DataAttributes;
import org.alfresco.event.sdk.model.v1.model.RepoEvent;
import org.alfresco.event.sdk.model.v1.model.Resource;

import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractNodeEventHandler {

  private final AtomicReference<MessageChannel> notificationsChannel = new AtomicReference<>();

  protected String getNodeType(RepoEvent<DataAttributes<Resource>> repoEvent) {
    String nodeType = "unknown";

    if (NodeTypeFilter.of("cm:content").test(repoEvent)) {
      nodeType = "document";
    } else if (IsFolderFilter.get().test(repoEvent)) {
      nodeType = "folder";
    }

    return nodeType;
  }

  public MessageChannel getNotificationsChannel() {
    return notificationsChannel.get();
  }

  public void setNotificationsChannel(MessageChannel messageChannel) {
    notificationsChannel.set(messageChannel);
  }

}
