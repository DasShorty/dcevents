package de.dasshorty.dcevents;

import de.dasshorty.dcevents.api.buttons.ButtonHandler;
import de.dasshorty.dcevents.api.commands.CommandHandler;
import de.dasshorty.dcevents.rating.image.ImageRatingListeners;
import de.dasshorty.dcevents.rating.image.ImageRatingSetupCommand;
import de.dasshorty.dcevents.rating.image.ImageRepository;
import de.dasshorty.dcevents.rating.image.rating.RatingButton;
import de.dasshorty.dcevents.rating.image.submit.SubmitRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DiscordBot {

    private final JDA jda;

    @Autowired
    public DiscordBot(ImageRepository imageRepository, SubmitRepository submitRepository) {

        JDABuilder builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"));

        builder.addEventListeners(new ImageRatingListeners(imageRepository, submitRepository));
        builder.setAutoReconnect(true);
        builder.enableIntents(Arrays.stream(GatewayIntent.values()).toList());
        builder.enableCache(Arrays.stream(CacheFlag.values()).toList());

        CommandHandler commandHandler = new CommandHandler();
        ButtonHandler buttonHandler = new ButtonHandler();

        builder.addEventListeners(commandHandler, buttonHandler);

        try {
            this.jda = builder.build().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        commandHandler.addSlash(new ImageRatingSetupCommand(imageRepository, submitRepository));
        commandHandler.updateCommands(this.jda.getGuilds().getFirst());
        buttonHandler.addButton(new RatingButton(submitRepository));
    }
}
