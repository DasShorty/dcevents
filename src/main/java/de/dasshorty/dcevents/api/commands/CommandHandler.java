package de.dasshorty.dcevents.api.commands;

import de.dasshorty.dcevents.api.commands.message.MessageCommand;
import de.dasshorty.dcevents.api.commands.slash.SlashCommand;
import de.dasshorty.dcevents.api.commands.user.UserCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends ListenerAdapter {

    private final List<UserCommand> userCommands = new ArrayList<>();
    private final List<SlashCommand> slashCommands = new ArrayList<>();
    private final List<MessageCommand> messageCommands = new ArrayList<>();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        this.slashCommands.forEach(slashCommand -> {
            if (slashCommand.commandData().getName().equals(event.getFullCommandName().split(" ")[0]))
                slashCommand.onExecute(event);
        });
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        this.userCommands.forEach(userCommand -> {
            if (userCommand.commandData().getName().equals(event.getFullCommandName()))
                userCommand.onExecute(event);
        });
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        this.messageCommands.forEach(messageCommand -> {
            if (messageCommand.commandData().getName().equals(event.getName()))
                messageCommand.onExecute(event);
        });
    }

    public void addSlash(SlashCommand command) {
        this.slashCommands.add(command);
    }

    public void addMessage(MessageCommand command) {
        this.messageCommands.add(command);
    }

    public void addUser(UserCommand command) {
        this.userCommands.add(command);
    }

    public List<UserCommand> getUserCommands() {
        return userCommands;
    }

    public List<SlashCommand> getSlashCommands() {
        return slashCommands;
    }

    public List<MessageCommand> getMessageCommands() {
        return messageCommands;
    }

    public void updateCommands(Guild guild) {
        CommandListUpdateAction commandListUpdateAction = guild.updateCommands();

        System.out.println("Updating commands...");

        this.slashCommands.forEach(slashCommand -> {
            System.out.println("Updating: " + slashCommand.commandData().getName());
            commandListUpdateAction.addCommands(slashCommand.commandData()).queue();
        });

        this.messageCommands.forEach(messageCommand -> {
            System.out.println("Updating: " + messageCommand.commandData().getName());
            commandListUpdateAction.addCommands(messageCommand.commandData()).queue();
        });
        this.userCommands.forEach(userCommand -> {
            System.out.println("Updating: " + userCommand.commandData().getName());
            commandListUpdateAction.addCommands(userCommand.commandData()).queue();
        });

        commandListUpdateAction.queue();
        System.out.println("Update finished!");
    }
}
