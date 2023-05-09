package org.alfresco.alfrescobot.actions.api;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.function.Predicate;

/**
 * Implementations of this class should be named exactly as the expected Slash Command, plus the Action suffix.
 * E.g.: Slash command = /delete_node, Expected implementation = DeleteNodeAction
 */
public interface BotAction {

  /**
   * Performs the expected action linked to a specific SlashCommandEvent
   *
   * @param event
   */
  void execute(SlashCommandEvent event);

  default void executeOnMenuEvent(SelectionMenuEvent selectionMenuEvent) {
    if (getSelectionMenuGuard() != null && getSelectionMenuGuard().test(selectionMenuEvent)) {
      internalExecuteOnMenuEvent(selectionMenuEvent);
    }
  }

  /**
   * @return the predicate that should be tested to determine whether
   * a SelectionMenuEvent should be acted upon
   */
  default Predicate<SelectionMenuEvent> getSelectionMenuGuard() {
    return null;
  }

  /**
   * Determines the behaviour on SelectionMenuEvents related to this Action.
   * You should also override the related {@link BotAction#getSelectionMenuGuard()} method.
   * @param event
   */
  void internalExecuteOnMenuEvent(SelectionMenuEvent event);

  default void executeOnButtonClickEvent(ButtonClickEvent buttonClickEvent) {
    if (getButtonClickGuard() != null && getButtonClickGuard().test(buttonClickEvent)) {
      internalExecuteOnButtonClickEvent(buttonClickEvent);
    }
  }

  /**
   * @return the predicate that should be tested to determine whether
   * a ButtonClickEvent should be acted upon
   */
  default Predicate<ButtonClickEvent> getButtonClickGuard() {
    return null;
  }

  /**
   * Determines the behaviour on ButtonClickEvents related to this Action.
   * You should also override the related {@link BotAction#getButtonClickGuard()} method.
   * @param event
   */
  void internalExecuteOnButtonClickEvent(ButtonClickEvent event);

}

