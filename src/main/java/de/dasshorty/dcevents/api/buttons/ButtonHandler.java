package de.dasshorty.dcevents.api.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ButtonHandler extends ListenerAdapter {
    private final List<Button> buttons = new ArrayList<>();

    public List<Button> getButtons() {
        return buttons;
    }

    public void addButton(Button button) {
        this.buttons.add(button);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        buttons.forEach(button -> {
            String id = event.getButton().getId();

            if (id == null) {
                return;
            }

            if (id.contains("_")) {
                id = id.split("_")[0];
            }

            if (button.id().equals(id)) {
                button.onExecute(event);
            }
        });

    }
}
