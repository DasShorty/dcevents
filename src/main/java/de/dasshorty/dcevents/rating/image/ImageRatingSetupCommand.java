package de.dasshorty.dcevents.rating.image;

import de.dasshorty.dcevents.api.commands.slash.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.util.Optional;

public class ImageRatingSetupCommand implements SlashCommand {

    private final ImageRepository imageRepo;

    public ImageRatingSetupCommand(ImageRepository imageRepo) {
        this.imageRepo = imageRepo;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("image-rating", "Setup Image Rating")
                .addSubcommands(
                        new SubcommandData("create", "Create an Image Rating Competition")
                                .addOption(OptionType.CHANNEL, "input", "Where can users send images to the competition", true)
                                .addOption(OptionType.CHANNEL, "vote", "Where should users rate the images", true),
                        new SubcommandData("start", "Start an image rating")
                                .addOption(OptionType.STRING, "id", "Image Rating id", true)
                                .addOption(OptionType.ROLE, "mention", "A role that is been pinged by the bot"),
                        new SubcommandData("stop", "Stop an image rating")
                                .addOption(OptionType.STRING, "id", "Image Rating id", true)
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (guild == null) {
            event.reply("Something went wrong but the bot couldn't get the guild :(").setEphemeral(true).queue();
            return;
        }

        switch (event.getSubcommandName().toLowerCase()) {
            case "create" -> {

                event.deferReply(true).queue();
                GuildChannelUnion inputChannel = event.getOption("input", OptionMapping::getAsChannel);
                GuildChannelUnion voteChannel = event.getOption("vote", OptionMapping::getAsChannel);

                if (inputChannel == null) {
                    event.getHook().editOriginal("The **input** channel can't be found by discord. Try it again later...").queue();
                    return;
                }

                if (voteChannel == null) {
                    event.getHook().editOriginal("The **vote** channel can't be found by discord. Try it again later...").queue();
                    return;
                }

                if (inputChannel.getType() == ChannelType.CATEGORY || voteChannel.getType() == ChannelType.CATEGORY) {
                    event.getHook().editOriginal("One of the inputs (**input**, **vote**) is an category. We can't send messages to this type. Please select an different channel!").queue();
                    return;
                }

                ImageRatingDto dto = ImageRatingDto.createDto();
                dto.setServerId(guild.getId());
                dto.setSubmitChannelId(inputChannel.getId());
                dto.setVotingChannelId(voteChannel.getId());

                imageRepo.save(dto);

                event.getHook().editOriginal("Image Rating (**" + dto.getId() + "**) has been created.").queue();

            }
            case "start" -> {

                event.deferReply(true).queue();
                String id = event.getOption("id", OptionMapping::getAsString);
                Role role = event.getOption("mention", OptionMapping::getAsRole);

                if (id == null) {
                    event.getHook().editOriginal("The provided id can't be serialized!").queue();
                    return;
                }

                if (role == null) {
                    event.getHook().editOriginal("The provided role can't be serialized!").queue();
                    return;
                }

                Optional<ImageRatingDto> optional = this.imageRepo.findById(id);

                if (optional.isEmpty()) {
                    event.getHook().editOriginal("Image Rating can't be found by id: " + id).queue();
                    return;
                }

                ImageRatingDto dto = optional.get();

                String submitChannelId = dto.getSubmitChannelId();
                TextChannel submitChannel = event.getGuild().getTextChannelById(submitChannelId);

                if (submitChannel == null) {
                    event.getHook().editOriginal("Something went wrong with the submitChannelId: " + submitChannelId).queue();
                    return;
                }

                submitChannel.sendMessage(role.getAsMention())
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setTitle("Image Rating")
                                        .setDescription("Send your images into this channel and enter the competition!")
                                        .addField("How can i win?", "At first you submit your image here then the community can vote your image and the image with the highest voting gets the price.", false)
                                        .setColor(Color.GREEN)
                                        .setFooter("Event presented by DasShorty")
                                        .build()
                        ).queue(message -> {
                            event.getHook().editOriginal("Message(" + message.getJumpUrl() + ") has been send and rating has started in phase 1!").queue();
                        });

            }
            case "stop" -> {

            }
        }

    }
}
