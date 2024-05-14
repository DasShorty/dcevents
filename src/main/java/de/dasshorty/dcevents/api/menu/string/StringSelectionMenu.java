package de.dasshorty.pridebot.api.menu.string;

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public interface StringSelectionMenu {

    String id();

    void onExecute(StringSelectInteractionEvent event);

}
