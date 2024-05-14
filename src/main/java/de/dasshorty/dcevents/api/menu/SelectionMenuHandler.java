package de.dasshorty.pridebot.api.menu;

import de.dasshorty.pridebot.api.menu.entity.EntitySelectionMenu;
import de.dasshorty.pridebot.api.menu.string.StringSelectionMenu;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SelectionMenuHandler extends ListenerAdapter {

    private final List<EntitySelectionMenu> entitySelection = new ArrayList<>();
    private final List<StringSelectionMenu> stringSelection = new ArrayList<>();

    public List<EntitySelectionMenu> getEntitySelection() {
        return entitySelection;
    }

    public List<StringSelectionMenu> getStringSelection() {
        return stringSelection;
    }

    public void addEntity(EntitySelectionMenu menu) {
        this.entitySelection.add(menu);
    }

    public void addString(StringSelectionMenu menu) {
        this.stringSelection.add(menu);
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        stringSelection.forEach(stringSelectionMenu -> {
            if (stringSelectionMenu.id().equals(event.getSelectMenu().getId()))
                stringSelectionMenu.onExecute(event);
        });
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        entitySelection.forEach(entitySelectionMenus -> {
            if (entitySelectionMenus.id().equals(event.getSelectMenu().getId()))
                entitySelectionMenus.onExecute(event);
        });
    }
}
