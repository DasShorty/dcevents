package de.dasshorty.dcevents.api.commands.message;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public interface MessageCommand {

    CommandDataImpl commandData();

    void onExecute(MessageContextInteractionEvent event);

}
