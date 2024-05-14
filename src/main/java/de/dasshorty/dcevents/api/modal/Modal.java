package de.dasshorty.pridebot.api.modal;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public interface Modal {

    String id();

    void onExecute(ModalInteractionEvent event);

}
