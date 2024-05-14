package de.dasshorty.dcevents;

import de.dasshorty.dcevents.api.commands.CommandHandler;
import de.dasshorty.dcevents.rating.image.ImageRatingSetupCommand;
import de.dasshorty.dcevents.rating.image.ImageRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordBot {

    private final JDA jda;

    @Autowired
    public DiscordBot(ImageRepository imageRepository) {

        JDABuilder builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"));


        builder.setAutoReconnect(true);

        CommandHandler commandHandler = new CommandHandler();

        builder.addEventListeners(commandHandler);

        try {
            this.jda = builder.build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        commandHandler.addSlash(new ImageRatingSetupCommand(imageRepository));
        commandHandler.updateCommands(this.jda.getGuilds().getFirst());
    }
}
